package com.astradevelop.tfg

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod

class LoginActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Start the DB
        FirebaseApp.initializeApp(this)

        //Button to go to signup activity
        val signBtn : TextView = findViewById(R.id.signTxt)
        signBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        //TV and ET that will be used
        val errorTxt: TextView = findViewById(R.id.errorTxt2)
        val email: EditText = findViewById(R.id.emailET)
        val password: EditText = findViewById(R.id.passwordET)

        //Visibility for the password ET
        val visibility: ImageView = findViewById(R.id.visibilitybtn)
        var visible = false

        //Password visibility handler
        visibility.setOnClickListener {
            visible = !visible
            visibility.setImageResource(if (visible) R.drawable.visible else R.drawable.invisible)
            password.transformationMethod = if (visible) HideReturnsTransformationMethod.getInstance()
            else PasswordTransformationMethod.getInstance()
        }

        //Instance from FirebaseDBConnection
        val dbConnection = FirebaseDBConnection()

        //Login process
        val loginBtn: Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            dbConnection.loginAuth(this, errorTxt, email.text.toString(), password.text.toString())
        }

    }
}