package com.example.billapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.billapp.MainActivity
import com.example.billapp.R
import com.example.billapp.models.DebtRelation
import com.example.billapp.models.Group
import com.example.billapp.models.GroupTransaction
import com.example.billapp.models.PersonalTransaction
import com.example.billapp.models.User
import com.example.billapp.utils.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.messaging.RemoteMessage

object FirebaseRepository {

    private fun getFirestoreInstance() = FirebaseFirestore.getInstance()
    private fun getAuthInstance() = FirebaseAuth.getInstance()

    suspend fun signIn(email: String, password: String): User = suspendCoroutine { continuation ->
        getAuthInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getFirestoreInstance().collection(Constants.USERS)
                        .document(getAuthInstance().currentUser!!.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            val user = document.toObject(User::class.java)!!
                            continuation.resume(user)
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                    updateUserFCMToken(getAuthInstance().currentUser!!.uid)
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Sign in failed"))
                }
            }
    }

    fun signOut() {
        getAuthInstance().signOut()
    }

    suspend fun signUp(name: String, email: String, password: String): User = suspendCoroutine { continuation ->
        getAuthInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = getAuthInstance().currentUser
                    val user = User(firebaseUser!!.uid, name, email)
                    getFirestoreInstance().collection(Constants.USERS)
                        .document(user.id)
                        .set(user)
                        .addOnSuccessListener {
                            continuation.resume(user)
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                    updateUserFCMToken(firebaseUser.uid)
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Sign up failed"))
                }
            }
    }

    fun updateUserBudget(budget: Int) {
        val currentUser = getAuthInstance().currentUser
        if (currentUser != null) {
            getFirestoreInstance().collection(Constants.USERS)
                .document(currentUser.uid)
                .update("budget", budget)
        }
    }

    private const val GROUPS_COLLECTION = "groups"
    private const val DEBT_RELATIONS_COLLECTION = "debtRelations"

    suspend fun updateDebtLastPenaltyDate(groupId: String, debtId: String, timestamp: Long) {
        try {
            val db = FirebaseFirestore.getInstance()
            val debtRelationRef = db.collection(GROUPS_COLLECTION)
                .document(groupId)
                .collection(DEBT_RELATIONS_COLLECTION)
                .document(debtId)

            // 使用 transaction 確保數據一致性
            db.runTransaction { transaction ->
                val snapshot = transaction.get(debtRelationRef)
                if (snapshot.exists()) {
                    Log.d("FirebaseRepository", "找到債務關係文檔，準備更新懲罰日期。債務ID: $debtId") // 確認是否找到匹配的文檔

                    val updates = hashMapOf<String, Any>(
                        "lastPenaltyDate" to Timestamp(timestamp / 1000, 0) // 將毫秒轉換為秒
                    )
                    transaction.update(debtRelationRef, updates)
                } else {
                    Log.w("FirebaseRepository", "未找到債務關係文檔，無法更新。債務ID: $debtId")
                }
            }.await()

            Log.d("FirebaseRepository", "成功更新債務最後懲罰日期。群組ID: $groupId, 債務ID: $debtId")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "更新債務最後懲罰日期時發生錯誤", e)
            throw e
        }
    }

    fun updateUserFCMToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                getFirestoreInstance().collection(Constants.USERS).document(userId)
                    .update("fcmToken", token)
            }
        }
    }

    //////

    suspend fun sendDebtReminder(context: Context, debtRelation: DebtRelation) = withContext(Dispatchers.IO) {
        val currentDate = Timestamp.now()

        if (debtRelation.lastRemindTimestamp == null || canSendReminder(currentDate, debtRelation.lastRemindTimestamp)) {
            // 發送推送通知
            sendPushNotification(
                debtRelation.from,
                "債務提醒",
                "您有一筆 ${debtRelation.amount} 元的債務需要償還給 ${debtRelation.to}"
            )

            // 發送應用內通知
            sendInAppNotification(context, debtRelation)

            // 更新用戶經驗值和信任等級
            updateUserExperience(debtRelation.to, 5)
            updateUserTrustLevel(debtRelation.from, -5)

            return@withContext true
        } else {
            return@withContext false
        }
    }

    private fun canSendReminder(currentDate: Timestamp, lastReminderDate: Timestamp): Boolean {
        val oneDayInMillis = 24 * 60 * 60 * 1000
        return currentDate.toDate().time - lastReminderDate.toDate().time >= oneDayInMillis
    }

    private fun sendInAppNotification(context: Context, debtRelation: DebtRelation) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "debt_reminder_channel"

        // 創建通知通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "債務提醒", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 創建意圖以響應通知點擊
        val intent = Intent(context, MainActivity::class.java)  // 替換為你的活動
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // 創建通知
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)  // 替換為你的圖標
            .setContentTitle("債務提醒")
            .setContentText("您有一筆 ${debtRelation.amount} 元的債務需要償還給 ${debtRelation.name}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // 發送通知
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private suspend fun sendPushNotification(userId: String, title: String, message: String) {
        try {
            // 獲取用戶的 FCM token
            val userToken = getUserFCMToken(userId)

            if (userToken != null) {
                // Create data payload
                val remoteMessage = RemoteMessage.Builder(userToken)
                    .setMessageId(System.currentTimeMillis().toString())  // Unique message ID
                    .setData(mapOf(
                        "title" to title,
                        "message" to message,
                        "userId" to userId
                    ))  // Add custom data
                    .build()

                // Send the message
                FirebaseMessaging.getInstance().send(remoteMessage)
                Log.d("FCM", "Successfully sent message to user: $userId")
            } else {
                // 用戶 FCM token 不存在
                Log.e("FCM", "User token not found for userId: $userId")
            }
        } catch (e: Exception) {
            // 捕捉發送推送消息時的異常
            Log.e("FCM", "Error sending FCM message", e)
        }
    }


    private suspend fun getUserFCMToken(userId: String): String? {
        return try {
            val user = getFirestoreInstance().collection("users").document(userId).get().await()
            user.getString("fcmToken")
        } catch (e: Exception) {
            Log.e("FCM", "Error getting user FCM token", e)
            null
        }
    }

    //////


    suspend fun updateUserExperience(userId: String, amount: Int) {
        getFirestoreInstance().collection("users").document(userId).update("experience", FieldValue.increment(amount.toLong()))
    }

    suspend fun updateUserTrustLevel(userId: String, amount: Int) {
        getFirestoreInstance().collection("users").document(userId).update("trustLevel", FieldValue.increment(amount.toLong()))
    }

    suspend fun createGroup(group: Group): String = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val groupId = getFirestoreInstance().collection(Constants.GROUPS).document().id
        val groupData = group.copy(
            createdBy = currentUser.uid,
            id = groupId,
            createdTime = Timestamp.now()
        )
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .set(groupData, SetOptions.merge())
            .await()
        return@withContext groupId
    }

    suspend fun getUserName(userId: String): String = withContext(Dispatchers.IO) {
        val user = getFirestoreInstance().collection(Constants.USERS)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)
        return@withContext user?.name ?: "Unknown User"
    }


    suspend fun getCurrentUser(): User = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        getFirestoreInstance().collection("users").document(currentUser.uid).get().await().toObject(User::class.java)
            ?: throw IllegalStateException("User data not found")
    }

    suspend fun getUserData(userId: String): User = withContext(Dispatchers.IO) {
        try {
            val userDocument = getFirestoreInstance().collection(Constants.USERS)
                .document(userId)
                .get()
                .await()

            if (userDocument.exists()) {
                return@withContext userDocument.toObject(User::class.java)
                    ?: throw Exception("Failed to convert document to User object")
            } else {
                throw Exception("User not found")
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error getting user data", e)
            throw e
        }
    }

    suspend fun fetchGroupWithDebtRelations(groupId: String): Group? {
        return withContext(Dispatchers.IO) {
            try {
                // 获取主要的 group 文档
                val groupDoc = getFirestoreInstance()
                    .collection(Constants.GROUPS)
                    .document(groupId)
                    .get()
                    .await()

                val group = groupDoc.toObject(Group::class.java) ?: return@withContext null

                // 获取 debtRelations 子集合
                val debtRelationsSnapshot = groupDoc.reference
                    .collection("debtRelations")
                    .get()
                    .await()

                // 将子集合数据转换为 DebtRelation 对象列表
                val debtRelations = debtRelationsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(DebtRelation::class.java)
                }

                // 更新 group 对象的 debtRelations
                group.debtRelations.clear()
                group.debtRelations.addAll(debtRelations)

                Log.d("FirebaseRepository", "Group ${group.id} fetched with ${debtRelations.size} debt relations")

                return@withContext group
            } catch (e: Exception) {
                Log.e("FirebaseRepository", "Error fetching group with debt relations", e)
                return@withContext null
            }
        }
    }

    suspend fun getUserGroups(): List<Group> = withContext(Dispatchers.IO) {
        try {
            val currentUser =
                getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
            val userId = currentUser.uid
            Log.d("FirebaseRepository", "開始獲取用戶群組，用戶ID: $userId")

            // Query for groups where the user is in assignedTo
            Log.d("FirebaseRepository", "查詢用戶被分配的群組")
            val assignedGroupsSnapshot = getFirestoreInstance()
                .collection(Constants.GROUPS)
                .whereArrayContains("assignedTo", userId)
                .orderBy("createdTime", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(
                "FirebaseRepository",
                "找到 ${assignedGroupsSnapshot.documents.size} 個被分配的群組"
            )
            val assignedGroups = assignedGroupsSnapshot.documents.mapNotNull { doc ->
                try {
                    // 首先輸出原始文檔數據
                    Log.d("FirebaseRepository", "群組 ${doc.id} 原始數據: ${doc.data}")

                    // 特別檢查 debtRelations 字段
                    val rawDebtRelations = doc.get("debtRelations")
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 原始債務關係數據類型: ${rawDebtRelations?.javaClass?.name}"
                    )
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 原始債務關係數據內容: $rawDebtRelations"
                    )

                    val group = doc.toObject(Group::class.java)
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 轉換後的債務關係數量: ${group?.debtRelations?.size ?: 0}"
                    )

                    group
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "轉換群組 ${doc.id} 時出錯", e)
                    null
                }
            }

            // Query for groups where the user is the creator
            Log.d("FirebaseRepository", "查詢用戶創建的群組")
            val createdGroupsSnapshot = getFirestoreInstance()
                .collection(Constants.GROUPS)
                .whereEqualTo("createdBy", userId)
                .orderBy("createdTime", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("FirebaseRepository", "找到 ${createdGroupsSnapshot.documents.size} 個創建的群組")
            val createdGroups = createdGroupsSnapshot.documents.mapNotNull { doc ->
                try {
                    val group = doc.toObject(Group::class.java)
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 轉換結果: ${group != null}, 債務關係數量: ${group?.debtRelations?.size ?: 0}"
                    )

                    // 檢查原始數據
                    val rawDebtRelations = doc.get("debtRelations")
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 原始債務關係數據: $rawDebtRelations"
                    )

                    group
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "轉換群組 ${doc.id} 時出錯", e)
                    null
                }
            }

            // Combine both lists
            val allGroups = (assignedGroups + createdGroups)
                .distinctBy { it.id }
                .sortedByDescending { it.createdTime }

            // 为每个组获取完整的数据，包括 debtRelations
            val completeGroups = allGroups.mapNotNull { group ->
                fetchGroupWithDebtRelations(group.id)
            }

            Log.d("FirebaseRepository", "最終獲取到 ${completeGroups.size} 個完整的群组")
            completeGroups.forEachIndexed { index, group ->
                Log.d(
                    "FirebaseRepository",
                    "群组 ${index + 1}: ${group.name}, 債務關係數量: ${group.debtRelations.size}"
                )
            }

            updateUserGroupsID(userId, completeGroups.map { it.id })

            return@withContext completeGroups
        }catch (e: Exception) {
            Log.e("FirebaseRepository", "獲取用戶群組時發生錯誤", e)
            throw e
        }
    }

    private fun updateUserGroupsID(userId: String, map: List<String>) {
        getFirestoreInstance().collection("users").document(userId).update("groupsID", map)
    }

    suspend fun deleteGroup(groupId: String) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection("groups").document(groupId).delete().await()
    }

    suspend fun deletePersonalTransaction(transactionId: String, transactionType: String, transactionAmount: Double) = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val userId = currentUser.uid

        // Reference to the transaction document
        val transactionRef = getFirestoreInstance().collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .document(transactionId)

        // Delete the transaction document
        transactionRef.delete().await()

        // Update the user's total income or expense
        val userRef = getFirestoreInstance().collection(Constants.USERS).document(userId)
        when (transactionType) {
            "收入" -> userRef.update("income", FieldValue.increment(-transactionAmount)).await()
            "支出" -> userRef.update("expense", FieldValue.increment(-transactionAmount)).await()
            else -> Log.e("deletePersonalTransaction", "Invalid transaction type: $transactionType")
        }
    }

    suspend fun updateGroup(groupId: String, group: Group) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection(Constants.GROUPS).document(groupId).set(group).await()
    }

    suspend fun assignUserToGroup(groupId: String, userId: String) = withContext(Dispatchers.IO) {
        val groupRef = getFirestoreInstance().collection("groups").document(groupId)
        val group = groupRef.get().await().toObject(Group::class.java)
        val currentUser = getCurrentUser()
        currentUser.groupsID.add(groupId)
        group?.assignedTo?.add(userId)
        groupRef.set(group!!).await()
    }

    suspend fun getGroup(groupId: String): Group = withContext(Dispatchers.IO) {
        return@withContext getFirestoreInstance()
            .collection(Constants.GROUPS)
            .document(groupId)
            .get()
            .await()
            .toObject(Group::class.java) ?: throw IllegalStateException("Group not found")
    }

    suspend fun getGroupTransactions(groupId: String): List<GroupTransaction> = withContext(Dispatchers.IO) {
        return@withContext getFirestoreInstance()
            .collection(Constants.GROUPS)
            .document(groupId)
            .collection("transactions")
            .get()
            .await()
            .toObjects(GroupTransaction::class.java)
    }


    // 新增一筆個人交易紀錄
    suspend fun addPersonalTransaction(transaction: PersonalTransaction) = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val userId = currentUser.uid

        // Generate a unique transactionId using Firestore's document ID
        val transactionId = getFirestoreInstance().collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .document()
            .id

        // Create a transaction object with the generated transactionId
        val transactionWithId = transaction.copy(
            transactionId = transactionId,
            userId = userId
        )

        // Add the transaction to the user's transactions subcollection
        getFirestoreInstance().collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .document(transactionId)
            .set(transactionWithId)
            .await()

        // Update the user's total income or expense
        val userRef = getFirestoreInstance().collection(Constants.USERS).document(userId)
        when (transaction.type) {
            "收入" -> userRef.update("income", FieldValue.increment(transaction.amount)).await()
            "支出" -> userRef.update("expense", FieldValue.increment(transaction.amount)).await()
            else -> Log.e("addPersonalTransaction", "Invalid transaction type: ${transaction.type}")
        }
    }

    // 取得個人交易紀錄
    suspend fun getUserTransactions(userId: String): List<PersonalTransaction> = withContext(Dispatchers.IO) {
        return@withContext getFirestoreInstance()
            .collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .get()
            .await()
            .toObjects(PersonalTransaction::class.java)
    }

    suspend fun getTransaction(transactionId: String): PersonalTransaction = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val userId = currentUser.uid
        return@withContext getFirestoreInstance()
            .collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .document(transactionId)
            .get()
            .await()
            .toObject(PersonalTransaction::class.java)!!
    }

    // 新增一筆群組交易紀錄
    suspend fun addGroupTransaction(groupId: String, transaction: GroupTransaction, debtRelations: List<DebtRelation>) = withContext(Dispatchers.IO) {
        val transactionId = getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("transactions")
            .document()
            .id

        val transactionWithId = transaction.copy(id = transactionId)

        // 添加交易
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("transactions")
            .document(transactionId)
            .set(transactionWithId)
            .await()

        // 添加債務關係
        for (debtRelation in debtRelations) {
            debtRelation.groupTransactionId = transactionId
            getFirestoreInstance().collection(Constants.GROUPS)
                .document(groupId)
                .collection("debtRelations")
                .document(debtRelation.id)
                .set(debtRelation)
                .await()
        }
    }

    suspend fun getGroupDebtRelations(groupId: String): Map<String, List<DebtRelation>> = withContext(Dispatchers.IO) {
        val debtRelations = getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("debtRelations")
            .get()
            .await()
            .toObjects(DebtRelation::class.java)

        return@withContext debtRelations.groupBy { it.groupTransactionId }
    }

    // 取得群組成員
    suspend fun getGroupMembers(groupId: String): List<User> = withContext(Dispatchers.IO) {
        val group = getGroup(groupId)
        val memberIds = group.assignedTo + group.createdBy
        return@withContext memberIds.mapNotNull { userId ->
            getFirestoreInstance().collection(Constants.USERS)
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)
        }
    }

    suspend fun updateGroupDebtRelations(groupId: String, debtRelations: List<DebtRelation>) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .update("debtRelations", debtRelations)
            .await()
    }

    suspend fun deleteDebtRelation(groupId: String, debtRelationId: String) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("debtRelations")
            .document(debtRelationId)
            .delete()
    }

}