package com.brandonhxrr.vault.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onCompletion

class EmployeesViewModel {
    val users: Flow<List<User>> = callbackFlow {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users_public_data")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = snapshot.children.map { data ->
                    val userId = data.key.toString()
                    val name = data.child("name").value.toString()
                    val email = data.child("email").value.toString()
                    val photoURL = data.child("profileImageUrl").value.toString()
                    val publicKey = data.child("public_key").value.toString()

                    User(
                        id = userId,
                        name = name,
                        email = email,
                        photoURL = photoURL,
                        publicKey = publicKey
                    )
                }
                this@callbackFlow.trySend(userList).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        usersRef.addValueEventListener(listener)

        awaitClose {
            usersRef.removeEventListener(listener)
        }
    }.onCompletion { cause ->
        cause?.let {
            Log.e("EmployeesViewModel", "Flow completed with an exception", it)
        }
    }
}
