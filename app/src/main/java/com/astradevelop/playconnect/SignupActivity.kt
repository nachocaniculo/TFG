package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException

class SignupActivity : AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
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

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("TU_CLIENT_ID_AQUI")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        //Button to go to the login page
        val loginBtn : TextView = findViewById(R.id.signTxt)
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Tv and ET that will be used
        val name: EditText = findViewById(R.id.nameET)
        val username: EditText = findViewById(R.id.userET)
        val email: EditText = findViewById(R.id.emailET)
        val password1: EditText = findViewById(R.id.passwordET)
        val password2: EditText = findViewById(R.id.passwordET2)

        //Function to change the password visibility in every password field
        fun togglePasswordVisibility(passwordField: EditText, visibilityIcon: ImageView, isVisible: Boolean): Boolean {
            visibilityIcon.setImageResource(if (isVisible) R.drawable.visible else R.drawable.invisible)
            passwordField.transformationMethod = if (isVisible)
                HideReturnsTransformationMethod.getInstance()
            else
                PasswordTransformationMethod.getInstance()
            passwordField.setSelection(passwordField.text.length)
            return !isVisible
        }

        //Both visibility buttons and states
        val visibility1: ImageView = findViewById(R.id.visibilitybtn)
        val visibility2: ImageView = findViewById(R.id.visibilitybtn2)
        var visible1 = false
        var visible2 = false

        //Visibility handlers
        visibility1.setOnClickListener {
            visible1 = togglePasswordVisibility(password1, visibility1, visible1)
        }
        visibility2.setOnClickListener {
            visible2 = togglePasswordVisibility(password2, visibility2, visible2)
        }

        //Instance from FirebaseDBConnection
        val dbConnection = FirebaseDBConnection()

        //Signup process
        val signUpBtn: Button = findViewById(R.id.signupBtn)
        signUpBtn.setOnClickListener {
            if (name.text.toString().isEmpty() || username.text.toString().isEmpty() || email.text.toString().isEmpty() || password1.text.toString().isEmpty() || password2.text.toString().isEmpty() ){
                Toast.makeText(
                    this,
                    "Error: Fill in all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (password1.text.toString() == password2.text.toString()) {
                    dbConnection.registerAuth(this, email.text.toString(), password1.text.toString(), name.text.toString(), username.text.toString())
                } else {
                    Toast.makeText(
                        this,
                        "Error: Passwords doesn't match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val googleSignIn: LinearLayout = findViewById(R.id.googleBtn)
        googleSignIn.setOnClickListener{
            signIn()
        }
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            val email = credential.id
            Log.d("GoogleSignIn", "ID Token: $idToken")
            Log.d("GoogleSignIn", "Email: $email")
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Error al autenticar: ${e.statusCode}")
        }
    }

    private fun signIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                signInLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
            }
            .addOnFailureListener { e ->
                Log.e("GoogleSignIn", "Error en Sign-In: ${e.localizedMessage}")
            }
    }
}