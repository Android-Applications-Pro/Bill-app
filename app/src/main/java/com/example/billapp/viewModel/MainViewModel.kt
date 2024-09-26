package com.example.billapp.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billapp.firebase.FirebaseRepository
import com.example.billapp.models.DeptRelation
import com.example.billapp.models.Group
import com.example.billapp.models.GroupTransaction
import com.example.billapp.models.PersonalTransaction
import com.example.billapp.models.TransactionCategory
import com.example.billapp.models.User
import com.example.billapp.utils.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.linecorp.linesdk.LineProfile
import com.linecorp.linesdk.auth.LineLoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class MainViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _userGroups = MutableStateFlow<List<Group>>(emptyList())
    val userGroups: StateFlow<List<Group>> = _userGroups.asStateFlow()

    private val _groupCreationStatus = MutableStateFlow<GroupCreationStatus>(GroupCreationStatus.IDLE)
    val groupCreationStatus: StateFlow<GroupCreationStatus> = _groupCreationStatus.asStateFlow()

    // 個人交易紀錄(List)
    private val _userTransactions = MutableStateFlow<List<PersonalTransaction>>(emptyList())
    val userTransactions: StateFlow<List<PersonalTransaction>> = _userTransactions.asStateFlow()

    // 當前群組交易紀錄(List)
    private val _groupTransactions = MutableStateFlow<List<GroupTransaction>>(emptyList())
    val groupTransactions: StateFlow<List<GroupTransaction>> = _groupTransactions.asStateFlow()

    // Dept relations (List)
    private val _deptRelations = MutableStateFlow<List<DeptRelation>>(emptyList())
    val deptRelations: StateFlow<List<DeptRelation>> = _deptRelations.asStateFlow()

    // Dept relations (Map) grouped by Transaction ID
    private val _groupIdDeptRelations = MutableStateFlow<Map<String, List<DeptRelation>>>(emptyMap())
    val groupIdDeptRelations: StateFlow<Map<String, List<DeptRelation>>> = _groupIdDeptRelations.asStateFlow()

    // Transaction fields
    private val _transactionType = MutableStateFlow("支出")
    val transactionType: StateFlow<String> = _transactionType.asStateFlow()

    private val _amount = MutableStateFlow(0.0)
    val amount: StateFlow<Double> get() = _amount

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> get() = _note

    private val _date = MutableStateFlow(Timestamp.now())
    val date: StateFlow<Timestamp> = _date.asStateFlow()

    private val _category = MutableStateFlow<String>("") // Assuming _category is a String
    val category: StateFlow<String> get() = _category

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _shareMethod = MutableStateFlow("")
    val shareMethod: StateFlow<String> = _shareMethod

    private val _dividers = MutableStateFlow<List<String>>(emptyList())
    val dividers: StateFlow<List<String>> = _dividers

    private val _payers = MutableStateFlow<List<String>>(emptyList())
    val payers: StateFlow<List<String>> = _payers

    private val _groupMembers = MutableStateFlow<List<User>>(emptyList())
    val groupMembers: StateFlow<List<User>> = _groupMembers.asStateFlow()

    private val _transaction = MutableStateFlow<PersonalTransaction?>(null)
    val transaction: StateFlow<PersonalTransaction?> = _transaction

    private var _updatetime = MutableStateFlow(Timestamp.now())
    val updatetime: StateFlow<Timestamp> = _updatetime.asStateFlow()

    private var currentGroup = MutableStateFlow<Group?>(null)
    val group: StateFlow<Group?> = currentGroup.asStateFlow()

    // 用於登入和註冊的狀態流
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(false) // 初始值為 false
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _lineLoginResult = MutableStateFlow<LineLoginResult?>(null)
    val lineLoginResult: StateFlow<LineLoginResult?> = _lineLoginResult.asStateFlow()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()

    private val _groupAvatarUrl = MutableStateFlow<String?>(null)
    val groupAvatarUrl: StateFlow<String?> = _groupAvatarUrl.asStateFlow()

    init {
        checkCurrentUser()
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        data class Authenticated(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun handleLineLogin(result: LineLoginResult) {
        _lineLoginResult.value = result
        if (result.isSuccess) {
            val lineProfile = result.lineProfile
            // Handle successful login, e.g., create or update user in your database
            if (lineProfile != null) {
                createOrUpdateUser(lineProfile)
            }
        } else {
            _error.value = result.errorData?.toString() ?: "Line login failed"
        }
    }

    private fun createOrUpdateUser(lineProfile: LineProfile) {
        viewModelScope.launch {
            try {
                val user = User(
                    id = lineProfile.userId,
                    name = lineProfile.displayName,
                    email = "", // Remove this line if User doesn't require an email
                    image = lineProfile.pictureUrl?.toString() ?: ""
                )
                FirebaseRepository.createOrUpdateUser(user)
                _user.value = user
                _authState.value = AuthState.Authenticated(user)
                loadUserData(user.id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


    private fun checkCurrentUser() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                loadUserData(currentUser.uid)
                loadUserGroups()
                loadUserTransactions()
            }
            _isUserLoggedIn.value = currentUser != null
        }
    }

    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userData = FirebaseRepository.getUserData(userId)
                _user.value = userData
                loadUserGroups()
                loadUserTransactions()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logOut() {
        FirebaseRepository.signOut()
        clearData()
        _authState.value = AuthState.Initial
        _isUserLoggedIn.value = false
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthState.Loading
            try {
                val user = FirebaseRepository.signIn(email, password)
                _user.value = user
                _authState.value = AuthState.Authenticated(user)
                loadUserData(user.id)
            } catch (e: Exception) {
                _error.value = e.message
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthState.Loading
            try {
                val user = FirebaseRepository.signUp(name, email, password)
                _user.value = user
                _authState.value = AuthState.Authenticated(user)
                loadUserData(user.id)
            } catch (e: Exception) {
                _error.value = e.message
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun clearData() {
        _groupCreationStatus.value = GroupCreationStatus.IDLE
        _groupIdDeptRelations.value = emptyMap()
        currentGroup.value = null
        _user.value = null
        _userTransactions.value = emptyList()
        _groupTransactions.value = emptyList()
        _deptRelations.value = emptyList()
        _isLoading.value = false
        _error.value = null
        _dividers.value = emptyList()
        _payers.value = emptyList()
        _transactionType.value = "支出"
        _amount.value = 0.0
        _note.value = ""
        _date.value = Timestamp.now()
        _category.value = ""
        _name.value = ""
        _shareMethod.value = ""
        _groupMembers.value = emptyList()
        _transaction.value = null
        _updatetime.value = Timestamp.now()
        _userGroups.value = emptyList()
        _userPercentages.value = emptyMap()
        _userAdjustments.value = emptyMap()
        _userExactAmounts.value = emptyMap()
        _userShares.value = emptyMap()
        _groupName.value = ""
    }

    fun reloadUserData() {
        viewModelScope.launch {
            try {
                val updatedUser = FirebaseRepository.getCurrentUser()
                _user.value = updatedUser
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateUserProfile(updatedUser: User) {
        viewModelScope.launch {
            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(getCurrentUserID())
                .set(updatedUser)
                .addOnSuccessListener {
                    _user.value = updatedUser
                }
                .addOnFailureListener { e ->
                    Log.e("updateUserProfile", "Error updating user profile", e)
                }
        }
    }

    private fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }

    fun getUserAmount(): Float {
        val user = _user.value
        return (user?.income?.toFloat() ?: 0.0f) - (user?.expense?.toFloat() ?: 0.0f)
    }

    fun getUserIncome(): Float {
        return _user.value?.income?.toFloat() ?: 0.0f
    }

    fun getUserExpense(): Float {
        return _user.value?.expense?.toFloat() ?: 0.0f
    }

    // Dept Functions //
    fun getDeptRelations(groupId: String): MutableStateFlow<List<DeptRelation>> {
        return _deptRelations
    }

    suspend fun getUserName(userId: String): String {
        return FirebaseRepository.getUserName(userId)
    }



    fun uploadAvatar(imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            Log.d("MainViewModel", "Uploading avatar for user: $currentUserId")

            try {
                val url = currentUserId?.let { FirebaseRepository.uploadAvatar(imageUri, it) }
                _avatarUrl.value = url
                Log.d("MainViewModel", "Avatar uploaded successfully: $url")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error uploading avatar: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadGroupAvatar(imageUri: Uri, groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("MainViewModel", "Uploading group avatar for group: $groupId")

            try {
                val url = FirebaseRepository.uploadGroupAvatar(imageUri, groupId)
                _groupAvatarUrl.value = url
                Log.d("MainViewModel", "Group avatar uploaded successfully: $url")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error uploading group avatar: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun usePresetAvatar(presetResourceId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            Log.d("MainViewModel", "Using preset avatar: $presetResourceId for user: $currentUserId")

            try {
                val presetUrl = "android.resource://com.example.billapp/$presetResourceId"
                currentUserId?.let { FirebaseRepository.updateUserImage(it, presetUrl) }
                _avatarUrl.value = presetUrl
                Log.d("MainViewModel", "Preset avatar used successfully: $presetUrl")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error using preset avatar: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAvatar() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            Log.d("MainViewModel", "Loading avatar for user: $currentUserId")

            try {
                val avatarUrl = currentUserId?.let { FirebaseRepository.getUserAvatarUrl(it) }
                _avatarUrl.value = avatarUrl
                Log.d("MainViewModel", "Avatar loaded successfully: $avatarUrl")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading avatar: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadGroupAvatar(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("MainViewModel", "Loading group avatar for group: $groupId")

            try {
                val groupAvatarUrl = FirebaseRepository.getGroupAvatarUrl(groupId)
                _groupAvatarUrl.value = groupAvatarUrl
                Log.d("MainViewModel", "Group avatar loaded successfully: $groupAvatarUrl")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading group avatar: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun usePresetGroupAvatar(presetResourceId: Int, groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("MainViewModel", "Using preset group avatar: $presetResourceId for group: $groupId")

            try {
                val presetUrl = "android.resource://com.example.billapp/$presetResourceId"
                FirebaseRepository.updateGroupImage(groupId, presetUrl)
                _groupAvatarUrl.value = presetUrl
                Log.d("MainViewModel", "Preset group avatar used successfully: $presetUrl")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error using preset group avatar: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Groups Function //
    fun loadUserGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val groups = FirebaseRepository.getUserGroups()
                Log.d("MainViewModel", "Loaded groups: ${groups.size}")
                _userGroups.value = groups
                _user.value = _user.value?.copy(groupsID = groups.map { it.id }.toMutableList())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading groups", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 取得個人交易紀錄
    fun loadUserTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = getCurrentUserID()
                val transactions = FirebaseRepository.getUserTransactions(userId)
                _userTransactions.value = transactions
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateGroup(groupId: String, updatedGroup: Group) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                FirebaseRepository.updateGroup(groupId, updatedGroup)
                loadUserGroups() // Refresh the list after updating
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                FirebaseRepository.deleteGroup(groupId)
                loadUserGroups() // Refresh the list after deletion
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTransaction(transactionId: String, transactionType: String, transactionAmount: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                FirebaseRepository.deletePersonalTransaction(transactionId, transactionType, transactionAmount)
                loadUserTransactions()
                // 你可以在這裡添加任何需要的額外操作，例如更新 UI 或顯示通知
            } catch (e: Exception) {
                // 處理異常，例如顯示錯誤訊息
                Log.e("deleteTransaction", "Failed to delete transaction", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun assignUserToGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                FirebaseRepository.assignUserToGroup(groupId, userId)
                loadUserGroups() // Refresh the list after assignment
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getGroup(groupId: String): StateFlow<Group?> {
        val groupFlow = MutableStateFlow<Group?>(null)
        viewModelScope.launch {
            try {
                val group = FirebaseRepository.getGroup(groupId)
                groupFlow.value = group
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
        return groupFlow.asStateFlow()
    }

    fun createGroup(groupName: String, imageUri: Uri?, context: Context) {
        viewModelScope.launch {
            _groupCreationStatus.value = GroupCreationStatus.LOADING
            try {
                val group = Group(name = groupName)
                val groupId = FirebaseRepository.createGroup(group) // groupId is now returned

                imageUri?.let { uri ->

                    if (uri.toString().startsWith("android.resource://")) {
                        // Default image
                        FirebaseRepository.updateGroupImage(groupId, uri.toString())
                    } else {
                        // Custom image
                        val imageUrl = uploadGroupAvatar(uri, groupId)
                        FirebaseRepository.updateGroupImage(groupId, imageUrl.toString())
                    }
                }

                _groupCreationStatus.value = GroupCreationStatus.SUCCESS
            } catch (e: Exception) {
                _groupCreationStatus.value = GroupCreationStatus.ERROR
            }
        }
    }



    fun resetGroupCreationStatus() {
        _groupCreationStatus.value = GroupCreationStatus.IDLE
    }
    /////


    ///////////////// 個人資料 ///////////////////////



    // 單一筆交易
    fun getTransaction(transactionId: String) {
        viewModelScope.launch {
            try {
                val transaction = FirebaseRepository.getTransaction(transactionId)
                _transaction.value = transaction
                setNote(transaction.note!!)
                setAmount(transaction.amount)
                setDate(transaction.date!!)
                setName(transaction.name)
                setTransactionType(transaction.type)
                setCategory(transaction.category)
            } catch (e: Exception) {
                // Handle the exception (e.g., log it or update a different state to indicate an error)
                _transaction.value = null // Optionally set the state to null or handle the error state as needed
            }
        }
    }

    // 新增一筆個人交易
    fun addPersonalTransaction() {
        viewModelScope.launch {
            try {
                val amountValue = _amount.value
                val categoryValue = _category.value

                if (categoryValue.isNotEmpty()) {
                    val category = stringToCategory(categoryValue)
                    FirebaseRepository.addPersonalTransaction(
                        PersonalTransaction(
                            userId = getCurrentUserID(),
                            type = _transactionType.value,
                            amount = amountValue,
                            category = category,
                            name = _name.value,
                            note = _note.value,
                            date = Timestamp.now(),
                            createdAt = Timestamp.now(),
                            updatedAt = Timestamp.now()
                        )
                    )
                    // Reset fields
                    _amount.value = 0.0
                    _category.value = TransactionCategory.FOOD.name // Reset to a default category
                    _name.value = ""
                    _note.value = ""
                } else {
                    Log.e("TransactionAdd", "Amount or category value is invalid")
                }
            } catch (e: Exception) {
                Log.e("TransactionAdd", "Error adding personal transaction: ${e.message}", e)
            }
        }
    }

    // Setters for fields
    fun setTransactionType(type: String) {
        _transactionType.value = type
    }

    fun setAmount(amount: Double) {
        _amount.value = amount
    }

    fun updateTransaction(transactionId: String, updatedTransaction: PersonalTransaction) {
        viewModelScope.launch {
            val userId = getCurrentUserID()
            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(userId)
                .collection(Constants.TRANSACTIONS)
                .document(transactionId)
                .set(updatedTransaction)
                .addOnSuccessListener {
                    _transaction.value = updatedTransaction

                    // 更新 _userTransactions
                    val currentTransactions = _userTransactions.value.toMutableList()
                    val index = currentTransactions.indexOfFirst { it.transactionId == transactionId }
                    if (index != -1) {
                        currentTransactions[index] = updatedTransaction
                        _userTransactions.value = currentTransactions
                    }

                }
                .addOnFailureListener { e ->
                    Log.e("updateTransaction", "Error updating transaction", e)
                }

        }
    }

    // Convert String to TransactionCategory
    fun stringToCategory(value: String): TransactionCategory {
        val category = TransactionCategory.values().find { it.name.equals(value, ignoreCase = true) }
        if (category == null) {
            Log.e("CategoryConversion", "Invalid category value: $value")
        }
        return category ?: TransactionCategory.OTHER
    }

    // Convert TransactionCategory to String
    fun categoryToString(category: TransactionCategory): String {
        return category.name
    }

    // Set category as String
    fun setCategory(value: String) {
        _category.value = value
    }

    // Set category using TransactionCategory
    fun setCategory(category: TransactionCategory) {
        _category.value = categoryToString(category)
    }

    fun setName(value: String) {
        _name.value = value
    }

    fun setNote(value: String) {
        _note.value = value
    }

    fun setDate(value: Timestamp) {
        _date.value = value
    }

    fun setUpdatetime(value: Timestamp){
        _updatetime.value = value
    }

    // 群組交易
    fun setShareMethod(method: String) {
        _shareMethod.value = method
    }
    // 設定群組交易名稱
    fun setGroupTransactionName(groupName: String){
        _groupName.value = groupName
    }

    fun getGroupMembers(groupId: String) {
        viewModelScope.launch {
            try {
                val members = FirebaseRepository.getGroupMembers(groupId)
                _groupMembers.value = members
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // New state flows for different share methods
    private val _userPercentages = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val _userAdjustments = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val _userExactAmounts = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val _userShares = MutableStateFlow<Map<String, Int>>(emptyMap())

    val userPercentages: StateFlow<Map<String, Float>> = _userPercentages.asStateFlow()
    val userAdjustments: StateFlow<Map<String, Float>> = _userAdjustments.asStateFlow()
    val userExactAmounts: StateFlow<Map<String, Float>> = _userExactAmounts.asStateFlow()
    val userShares: StateFlow<Map<String, Int>> = _userShares.asStateFlow()

    private val _groupName = MutableStateFlow("")
    val groupName: StateFlow<String> get() = _groupName

    fun addGroupTransaction(groupId: String) {
        viewModelScope.launch {
            try {
                val transaction = GroupTransaction(
                    id = "",
                    name = _groupName.value,
                    payer = _payers.value,
                    divider = _dividers.value,
                    shareMethod = _shareMethod.value,
                    type = _transactionType.value,
                    amount = _amount.value,
                    date = Timestamp.now(),
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                val deptRelations = when (_shareMethod.value) {
                    "均分" -> calculateEvenSplitRelations(transaction)
                    "比例" -> calculateProportionalRelations(transaction, _userPercentages.value)
                    "調整" -> calculateAdjustableRelations(transaction, _userAdjustments.value)
                    "金額" -> calculateExactAmountRelations(transaction, _userExactAmounts.value)
                    "份數" -> calculateSharesRelations(transaction, _userShares.value)
                    else -> emptyList() // Handle unexpected share method
                }

                FirebaseRepository.addGroupTransaction(groupId, transaction, deptRelations)

                // Reset fields
                _amount.value = 0.0
                _shareMethod.value = ""
                _dividers.value = emptyList()
                _payers.value = emptyList()
                _userPercentages.value = emptyMap()
                _userAdjustments.value = emptyMap()
                _userExactAmounts.value = emptyMap()
                _userShares.value = emptyMap()
            } catch (e: Exception) {
                Log.e("GroupTransactionAdd", "Error adding group transaction: ${e.message}", e)
            }
        }
    }

    fun updateGroupName(newName: String) {
        _groupName.value = newName
    }

    private fun calculateEvenSplitRelations(transaction: GroupTransaction): List<DeptRelation> {
        val deptRelations = mutableListOf<DeptRelation>()
        val amountPerDivider = transaction.amount / transaction.divider.size

        transaction.divider.forEach { dividerId ->
            transaction.payer.forEach { payerId ->
                if (dividerId != payerId) {
                    deptRelations.add(
                        DeptRelation(
                            id = UUID.randomUUID().toString(),
                            name = transaction.name,
                            groupTransactionId = transaction.id,
                            from = dividerId,
                            to = payerId,
                            amount = amountPerDivider / transaction.payer.size
                        )
                    )
                }
            }
        }
        return deptRelations
    }

    private fun calculateProportionalRelations(transaction: GroupTransaction, userPercentages: Map<String, Float>): List<DeptRelation> {
        val deptRelations = mutableListOf<DeptRelation>()
        val totalPercentage = userPercentages.values.sum()

        if (totalPercentage != 100f) return deptRelations // Ensure percentages sum to 100%

        transaction.payer.forEach { payerId ->
            userPercentages.forEach { (userId, percentage) ->
                if (userId != payerId) {
                    val amountOwed = transaction.amount * (percentage / 100) / transaction.payer.size
                    deptRelations.add(
                        DeptRelation(
                            id = UUID.randomUUID().toString(),
                            groupTransactionId = transaction.id,
                            from = userId,
                            to = payerId,
                            amount = amountOwed
                        )
                    )
                }
            }
        }
        return deptRelations
    }

    private fun calculateAdjustableRelations(transaction: GroupTransaction, userAdjustments: Map<String, Float>): List<DeptRelation> {
        val deptRelations = mutableListOf<DeptRelation>()
        val totalAdjustment = userAdjustments.values.sum()
        val remainingAmount = transaction.amount - totalAdjustment
        val evenSplitAmount = remainingAmount / transaction.divider.size

        transaction.payer.forEach { payerId ->
            transaction.divider.forEach { dividerId ->
                if (dividerId != payerId) {
                    val adjustment = userAdjustments[dividerId] ?: 0f
                    val amountOwed = (adjustment + evenSplitAmount) / transaction.payer.size
                    deptRelations.add(
                        DeptRelation(
                            id = UUID.randomUUID().toString(),
                            groupTransactionId = transaction.id,
                            from = dividerId,
                            to = payerId,
                            amount = amountOwed
                        )
                    )
                }
            }
        }
        return deptRelations
    }

    private fun calculateExactAmountRelations(transaction: GroupTransaction, userAmounts: Map<String, Float>): List<DeptRelation> {
        val deptRelations = mutableListOf<DeptRelation>()

        transaction.payer.forEach { payerId ->
            userAmounts.forEach { (userId, amount) ->
                if (userId != payerId) {
                    deptRelations.add(
                        DeptRelation(
                            id = UUID.randomUUID().toString(),
                            groupTransactionId = transaction.id,
                            from = userId,
                            to = payerId,
                            amount = amount.toDouble() / transaction.payer.size
                        )
                    )
                }
            }
        }
        return deptRelations
    }

    private fun calculateSharesRelations(transaction: GroupTransaction, userShares: Map<String, Int>): List<DeptRelation> {
        val deptRelations = mutableListOf<DeptRelation>()
        val totalShares = userShares.values.sum()

        if (totalShares == 0) return deptRelations // Avoid division by zero

        transaction.payer.forEach { payerId ->
            userShares.forEach { (userId, shares) ->
                if (userId != payerId) {
                    val amountOwed = transaction.amount * (shares.toDouble() / totalShares) / transaction.payer.size
                    deptRelations.add(
                        DeptRelation(
                            id = UUID.randomUUID().toString(),
                            groupTransactionId = transaction.id,
                            from = userId,
                            to = payerId,
                            amount = amountOwed
                        )
                    )
                }
            }
        }
        return deptRelations
    }

    // Functions to update state flows for different share methods
    fun updateUserPercentages(percentages: Map<String, Float>) {
        _userPercentages.value = percentages
    }

    fun updateUserAdjustments(adjustments: Map<String, Float>) {
        _userAdjustments.value = adjustments
    }

    fun updateUserExactAmounts(amounts: Map<String, Float>) {
        _userExactAmounts.value = amounts
    }

    fun updateUserShares(shares: Map<String, Int>) {
        _userShares.value = shares
    }

    fun updateShareMethod(method: String) {
        _shareMethod.value = method
    }

    fun getGroupDeptRelations(groupId: String) {
        viewModelScope.launch {
            try {
                val groupIdDeptRelations = FirebaseRepository.getGroupDeptRelations(groupId)
                _groupIdDeptRelations.value = groupIdDeptRelations
                _deptRelations.value = groupIdDeptRelations.values.flatten()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun calculateTotalDebt(userId: String): Double {
        return _deptRelations.value
            .filter { it.from == userId }
            .sumOf { it.amount }
    }

    fun getGroupIdDeptRelations(groupId: String): Map<String, List<DeptRelation>> {
        return _groupIdDeptRelations.value
    }

    fun loadGroupIdRelation(groupId: String){
        viewModelScope.launch {
            try {
                val deptRelations = FirebaseRepository.getGroupDeptRelations(groupId)
                _groupIdDeptRelations.value = deptRelations
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadGroupTransactions(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val transactions = FirebaseRepository.getGroupTransactions(groupId)
                _groupTransactions.value = transactions
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadGroupDeptRelations(groupId: String) {
        viewModelScope.launch {
            try {
                val deptRelationsMap = FirebaseRepository.getGroupDeptRelations(groupId)
                _groupIdDeptRelations.value = deptRelationsMap
            } catch (e: Exception) {
                Log.e("LoadGroupDeptRelations", "Error loading dept relations: ${e.message}", e)
            }
        }
    }

    fun updateGroupDeptRelations(transactionId: String, newDeptRelations: List<DeptRelation>) {
        viewModelScope.launch {
            try {
                val currentRelations = _groupIdDeptRelations.value.toMutableMap()

                // 更新指定的交易ID的 DeptRelations
                currentRelations[transactionId] = newDeptRelations

                // 更新 StateFlow 的值
                _groupIdDeptRelations.value = currentRelations
            } catch (e: Exception) {
                Log.e("UpdateGroupDeptRelations", "Error updating dept relations: ${e.message}", e)
            }
        }
    }

    fun deleteDeptRelation(groupId: String, deptRelationId: String) {
        viewModelScope.launch {
            FirebaseRepository.deleteDeptRelation(groupId, deptRelationId)
        }
    }

    fun toggleDivider(userId: String) {
        val currentDividers = dividers.value.toMutableList()
        if (currentDividers.contains(userId)) {
            currentDividers.remove(userId)
        } else {
            currentDividers.add(userId)
        }
        _dividers.value = currentDividers
    }

    fun togglePayer(userId: String) {
        val currentPayers = payers.value.toMutableList()
        if (currentPayers.contains(userId)) {
            currentPayers.remove(userId)
        } else {
            currentPayers.add(userId)
        }
        _payers.value = currentPayers
    }


}

enum class GroupCreationStatus {
    IDLE, LOADING, SUCCESS, ERROR
}