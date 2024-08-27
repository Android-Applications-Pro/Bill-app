package com.example.billapp.firebase

import android.util.Log
import com.example.billapp.models.DeptRelation
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirebaseRepository {

    private fun getFirestoreInstance() = FirebaseFirestore.getInstance()
    private fun getAuthInstance() = FirebaseAuth.getInstance()

    suspend fun createGroup(group: Group) = withContext(Dispatchers.IO) {
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
    }

    suspend fun getCurrentUser(): User = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        getFirestoreInstance().collection("users").document(currentUser.uid).get().await().toObject(User::class.java)
            ?: throw IllegalStateException("User data not found")
    }

    suspend fun getUserGroups(): List<Group> = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val userId = currentUser.uid

        // Query for groups where the user is in assignedTo
        val assignedGroups = getFirestoreInstance()
            .collection(Constants.GROUPS)
            .whereArrayContains("assignedTo", userId)
            .orderBy("createdTime", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Group::class.java)

        // Query for groups where the user is the creator
        val createdGroups = getFirestoreInstance()
            .collection(Constants.GROUPS)
            .whereEqualTo("createdBy", userId)
            .orderBy("createdTime", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Group::class.java)

        // Combine both lists, removing any duplicates if necessary, and sort by createdTime
        val allGroups = (assignedGroups + createdGroups)
            .distinctBy { it.id }
            .sortedByDescending { it.createdTime }

        updateUserGroupsID(userId, allGroups.map { it.id })

        return@withContext allGroups
    }

    private fun updateUserGroupsID(userId: String, map: List<String>) {
        getFirestoreInstance().collection("users").document(userId).update("groupsID", map)
    }

    suspend fun deleteGroup(groupId: String) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection("groups").document(groupId).delete().await()
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
    suspend fun addGroupTransaction(groupId: String, transaction: GroupTransaction) = withContext(Dispatchers.IO) {
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

        // 計算並添加債務關係
        val deptRelations = calculateDeptRelations(transactionWithId)
        for (deptRelation in deptRelations) {
            getFirestoreInstance().collection(Constants.GROUPS)
                .document(groupId)
                .collection("deptRelations")
                .document(deptRelation.id)
                .set(deptRelation)
                .await()
        }
    }

    private fun calculateDeptRelations(transaction: GroupTransaction): List<DeptRelation> {
        val deptRelations = mutableListOf<DeptRelation>()

        // 計算每個付款人應付的金額
        val amountPerPayer = transaction.amount / transaction.payer.size

        // 對於每個付款人
        transaction.payer.forEach { payerId ->
            // 計算每個分擔者應付給這個付款人的金額
            val amountPerDivider = amountPerPayer / transaction.divider.size

            // 對於每個分擔者
            transaction.divider.forEach { dividerId ->
                // 如果分擔者不是付款人，創建一個債務關係
                if (dividerId != payerId) {
                    deptRelations.add(
                        DeptRelation(
                            id = java.util.UUID.randomUUID().toString(),
                            from = dividerId,
                            to = payerId,
                            amount = amountPerDivider,
                            groupTransactionId = transaction.id
                        )
                    )
                }
            }
        }

        return deptRelations
    }

    suspend fun getGroupDeptRelations(groupId: String): Map<String, List<DeptRelation>> = withContext(Dispatchers.IO) {
        val deptRelations = getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("deptRelations")
            .get()
            .await()
            .toObjects(DeptRelation::class.java)

        return@withContext deptRelations.groupBy { it.groupTransactionId }
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

    suspend fun updateGroupDeptRelations(groupId: String, deptRelations: List<DeptRelation>) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .update("deptRelations", deptRelations)
            .await()
    }

}