package com.example.billapp.firebase

import com.example.billapp.models.User
import com.example.billapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signIn(email: String, password: String): User = suspendCoroutine { continuation ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firestore.collection(Constants.USERS)
                        .document(firebaseAuth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            val user = document.toObject(User::class.java)!!
                            continuation.resume(user)
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Sign in failed"))
                }
            }
    }
    suspend fun signUp(name: String, email: String, password: String): User = suspendCoroutine { continuation ->
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    val user = User(firebaseUser!!.uid, name, email)
                    firestore.collection(Constants.USERS)
                        .document(user.id)
                        .set(user)
                        .addOnSuccessListener {
                            continuation.resume(user)
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Sign up failed"))
                }
            }
    }
}