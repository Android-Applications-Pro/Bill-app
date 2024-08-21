package com.example.billapp.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billapp.firebase.FirebaseRepository
import com.example.billapp.models.Group
import com.example.billapp.models.User
import com.example.billapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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


    init {
        loadUserData()
        loadUserGroups()
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

    // Groups Function //
    fun loadUserGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val groups = FirebaseRepository.getUserGroups()
                _userGroups.value = groups
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

    /////

}