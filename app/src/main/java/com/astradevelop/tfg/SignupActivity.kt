package com.astradevelop.tfg

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = FirebaseFirestore.getInstance()

        val loginBtn : TextView = findViewById(R.id.signTxt)
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val errorTxt: TextView = findViewById(R.id.errorTxt)
        val name: EditText = findViewById(R.id.nameET)
        val username: EditText = findViewById(R.id.userET)
        val email: EditText = findViewById(R.id.emailET)
        val password1: EditText = findViewById(R.id.passwordET)
        val password2: EditText = findViewById(R.id.passwordET2)

        fun registerAuth(email: String, password: String) {
            val auth = FirebaseAuth.getInstance()

            if (email.isEmpty() || password.isEmpty()){
                errorTxt.visibility = View.VISIBLE
                errorTxt.text = "Error: Fill in all the fields"
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val intent = Intent(this, HomeActivity::class.java)
                            val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
                            val editor = sharedPref.edit()
                            editor.putString("userUID", user?.uid)
                            editor.apply()

                            val usuario = hashMapOf(
                                "email" to email,
                                "name" to name.text,
                                "username" to username.text
                            )

                            val documentId = user?.uid

                            db.collection("players").document(documentId!!)
                                .set(usuario)
                                .addOnCompleteListener {
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    println("Error: $e")
                                }
                        } else {
                            errorTxt.visibility = View.VISIBLE
                            errorTxt.text = "Error: ${task.exception?.message}"
                        }
                    }
            }
        }

        val signUpBtn: Button = findViewById(R.id.signupBtn)
        signUpBtn.setOnClickListener {
            if (name.text.toString().isEmpty() || username.text.toString().isEmpty() || email.text.toString().isEmpty() || password1.text.toString().isEmpty() || password2.text.toString().isEmpty() ){
                errorTxt.visibility = View.VISIBLE
                errorTxt.text = "Error: Fill in all the fields"
            } else {
                if (password1.text.toString() == password2.text.toString()) {
                    registerAuth(email.text.toString(), password1.text.toString())
                } else {
                    errorTxt.visibility = View.VISIBLE
                    errorTxt.text = "Error: Passwords doesn't match"
                }
            }
        }
    }
}