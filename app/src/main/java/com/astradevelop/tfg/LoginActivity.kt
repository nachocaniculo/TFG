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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        FirebaseApp.initializeApp(this)

        val signBtn : TextView = findViewById(R.id.signTxt)
        signBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val errorTxt: TextView = findViewById(R.id.errorTxt2)
        val email: EditText = findViewById(R.id.emailET)
        val password: EditText = findViewById(R.id.passwordET)


        fun loginAuth(email: String, password: String) {
            val auth = FirebaseAuth.getInstance()

            if (email.isEmpty() || password.isEmpty()){
                errorTxt.visibility = View.VISIBLE
                errorTxt.text = "Error: Fill in all the fields"
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val intent = Intent(this, HomeActivity::class.java)
                            val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
                            val editor = sharedPref.edit()
                            editor.putString("userUID", user?.uid)
                            editor.apply()
                            startActivity(intent)
                        } else {
                            errorTxt.visibility = View.VISIBLE
                            errorTxt.text = "Error: ${task.exception?.message}"
                        }
                    }
            }
        }
        val loginBtn: Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            loginAuth(email.text.toString(), password.text.toString())
        }

    }
}