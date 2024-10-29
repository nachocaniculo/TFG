package com.astradevelop.tfg

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
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
        name.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        username.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        email.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        password1.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        password2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        fun togglePasswordVisibility(passwordField: EditText, visibilityIcon: ImageView, isVisible: Boolean): Boolean {
            visibilityIcon.setImageResource(if (isVisible) R.drawable.invisible else R.drawable.visible)
            passwordField.inputType = if (isVisible)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            else
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            passwordField.setSelection(passwordField.text.length)
            return !isVisible
        }

        val visibility1: ImageView = findViewById(R.id.visibilitybtn)
        val visibility2: ImageView = findViewById(R.id.visibilitybtn2)

        var visible1 = false
        var visible2 = false

        visibility1.setOnClickListener {
            visible1 = togglePasswordVisibility(password1, visibility1, visible1)
        }

        visibility2.setOnClickListener {
            visible2 = togglePasswordVisibility(password2, visibility2, visible2)
        }


        val dbConnection = FirebaseDBConnection()

        val signUpBtn: Button = findViewById(R.id.signupBtn)
        signUpBtn.setOnClickListener {
            if (name.text.toString().isEmpty() || username.text.toString().isEmpty() || email.text.toString().isEmpty() || password1.text.toString().isEmpty() || password2.text.toString().isEmpty() ){
                errorTxt.visibility = View.VISIBLE
                errorTxt.text = "Error: Fill in all the fields"
            } else {
                if (password1.text.toString() == password2.text.toString()) {
                    dbConnection.registerAuth(this, errorTxt, email.text.toString(), password1.text.toString(), name.text.toString(), username.text.toString())
                } else {
                    errorTxt.visibility = View.VISIBLE
                    errorTxt.text = "Error: Passwords doesn't match"
                }
            }
        }
    }
}