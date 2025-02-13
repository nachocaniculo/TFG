package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

class LoginActivity : AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
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

        //Start the DB
        FirebaseApp.initializeApp(this)

        //Button to go to signup activity
        val signBtn : TextView = findViewById(R.id.signTxt)
        signBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        //TV and ET that will be used
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
            dbConnection.loginAuth(this, email.text.toString(), password.text.toString())
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