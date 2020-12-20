package com.company.craefe20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.account.WorkAccount.getClient
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credentials.getClient
import com.google.android.gms.auth.api.phone.SmsRetriever.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance();
        email_login_button.setOnClickListener {
            createAndLoginEmail()
        }
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        register_btn.setOnClickListener{
            moveRegisterPage(auth?.currentUser)
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    fun createAndLoginEmail() {
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                moveMainPage(auth?.currentUser)
                Toast.makeText(this, "아이디 생성 성공", Toast.LENGTH_LONG).show()
            } else if (task.exception?.message.isNullOrEmpty()) {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()

            } else {
                signinEmail()

            }
        }

    }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                moveMainPage(auth?.currentUser)
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"로그인실패", Toast.LENGTH_LONG).show()
            }

        }

    }
    fun moveMainPage(user :FirebaseUser?){
        if (user != null) {
            startActivity(Intent(this,MainActivity::class.java)
            )
            finish()
        }
    }
    fun moveRegisterPage(user :FirebaseUser?){
        if (user != null) {
            startActivity(Intent(this,RegisterActivity::class.java)
            )
            finish()
        }
    }
    fun googleLogin(){

        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }
    fun firebaseAuthWithGoolge(account: GoogleSignInAccount){
        var credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth?.signInWithCredential(credential)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            //오류 있을수도

            if (result != null) {
                if(result.isSuccess){
                    var account = result.signInAccount
                    firebaseAuthWithGoolge(account!!)

                }
            }

        }
    }


}