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

    // 不要在這邊宣告firebase

    init {
        loadUserData()
        loadUserGroups()
        loadUserTransactions()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    val loggedInUser = document.toObject(User::class.java)
                    _user.value = loggedInUser
                }
                .addOnFailureListener { e ->
                    Log.e(
                        "loadUserData",
                        "Error while getting loggedIn user details",
                        e
                    )
                }
        }
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

    private fun updateUserProfile(updatedUser: User) {
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

    private fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_images/${UUID.randomUUID()}")

        imageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    onSuccess(downloadUri.toString())
                } else {
                    onFailure(task.exception ?: Exception("Unknown error"))
                }
            }
    }

    fun updateUserProfileWithImage(updatedUser: User, imageUri: Uri?) {
        viewModelScope.launch {
            if (imageUri != null) {
                uploadImage(
                    imageUri,
                    onSuccess = { imageUrl ->
                        val userWithImage = updatedUser.copy(image = imageUrl)
                        updateUserProfile(userWithImage)
                    },
                    onFailure = { e ->
                        Log.e("updateUserProfileWithImage", "Error uploading image", e)
                    }
                )
            } else {
                updateUserProfile(updatedUser)
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

    fun createGroup(name: String, imageUri: Uri?, context: Context) {
        viewModelScope.launch {
            _groupCreationStatus.value = GroupCreationStatus.LOADING
            try {
                val imageUrl = imageUri?.let { uploadImage(it, context) }
                val group = Group(name = name, image = (imageUrl ?: "").toString())
                FirebaseRepository.createGroup(group)
                loadUserGroups() // 在成功創建Group後更新userGroups
                _groupCreationStatus.value = GroupCreationStatus.SUCCESS
            } catch (e: Exception) {
                _groupCreationStatus.value = GroupCreationStatus.ERROR
                _error.value = e.message
            }
        }
    }

    private suspend fun uploadImage(imageUri: Uri, context: Context): String {
        return withContext(Dispatchers.IO) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("group_images/${UUID.randomUUID()}")

            val uploadTask = imageRef.putFile(imageUri).await()
            return@withContext imageRef.downloadUrl.await().toString()
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

    // 群組交易
    fun setShareMethod(method: String) {
        _shareMethod.value = method
    }

    fun toggleDivider(userId: String) {
        _dividers.value = if (_dividers.value.contains(userId)) {
            _dividers.value - userId
        } else {
            _dividers.value + userId
        }
    }

    fun togglePayer(userId: String) {
        _payers.value = if (_payers.value.contains(userId)) {
            _payers.value - userId
        } else {
            _payers.value + userId
        }
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

    fun addGroupTransaction(groupId: String) {
        viewModelScope.launch {
            try {
                FirebaseRepository.addGroupTransaction(
                    groupId,
                    GroupTransaction(
                        id = "",
                        payer = _payers.value,
                        divider = _dividers.value,
                        shareMethod = _shareMethod.value,
                        type = _transactionType.value,
                        amount = _amount.value,
                        date = Timestamp.now(),
                        createdAt = Timestamp.now(),
                        updatedAt = Timestamp.now()
                    )
                )
                // Reset fields
                _amount.value = 0.0
                _shareMethod.value = ""
                _dividers.value = emptyList()
                _payers.value = emptyList()
            } catch (e: Exception) {
                Log.e("GroupTransactionAdd", "Error adding group transaction: ${e.message}", e)
            }
        }
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
}

enum class GroupCreationStatus {
    IDLE, LOADING, SUCCESS, ERROR
}