package com.astradevelop.tfg

import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseDBConnection {

    //Function to login with email and password
    fun loginAuth(context: Context, errorTxt: TextView, email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        if (email.isEmpty() || password.isEmpty()) {
            errorTxt.visibility = View.VISIBLE
            errorTxt.text = "Error: Fill in all the fields"
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val intent = Intent(context, HomeActivity::class.java)
                        val sharedPref = context.getSharedPreferences(
                            "playconnectlogintoken",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPref.edit()
                        editor.putString("userUID", user?.uid)
                        editor.apply()
                        context.startActivity(intent)
                    } else {
                        errorTxt.visibility = View.VISIBLE
                        errorTxt.text = "Error: ${task.exception?.message}"
                    }
                }
        }
    }

    //Function to register with email and password
    fun registerAuth(context:Context, errorTxt: TextView, email: String, password: String, name: String, username: String) {
        val auth = FirebaseAuth.getInstance()

        
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || username.isEmpty()) {
            errorTxt.visibility = View.VISIBLE
            errorTxt.text = "Error: Fill in all the fields"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorTxt.visibility = View.VISIBLE
            errorTxt.text = "Error: Invalid email format"
            return
        }

        if (password.length < 6) {
            errorTxt.visibility = View.VISIBLE
            errorTxt.text = "Error: Password must be at least 6 characters"
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    user?.let {
                        val intent = Intent(context, ProfilePictureActivity::class.java)

                        val sharedPref = context.getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("userUID", it.uid)
                        editor.apply()

                        val usuario = hashMapOf(
                            "email" to email,
                            "name" to name,
                            "username" to username,
                            "picture" to "1"
                        )

                        val documentId = it.uid
                        val db = FirebaseFirestore.getInstance()
                        db.collection("players").document(documentId)
                            .set(usuario)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    context.startActivity(intent)
                                } else {
                                    errorTxt.visibility = View.VISIBLE
                                    errorTxt.text = "Error saving user data: ${dbTask.exception?.message}"
                                }
                            }
                            .addOnFailureListener { e ->
                                errorTxt.visibility = View.VISIBLE
                                errorTxt.text = "Error saving user data: $e"
                            }
                    }

                } else {
                    errorTxt.visibility = View.VISIBLE
                    errorTxt.text = "Error: ${task.exception?.message}"
                }
            }
            .addOnFailureListener { e ->
                errorTxt.visibility = View.VISIBLE
                errorTxt.text = "Error: $e"
            }
    }

    fun updateDocumentPicture(
        context: Context,
        documentId: String,
        value: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("players").document(documentId)

        val updates = hashMapOf<String, Any>(
            "picture" to value
        )

        documentRef.update(updates)
            .addOnSuccessListener {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
    }

}