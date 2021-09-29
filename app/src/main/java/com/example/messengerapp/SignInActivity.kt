package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : AppCompatActivity(), TextWatcher {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        editText_email_signIn.addTextChangedListener(this)
        editText_password_signIn.addTextChangedListener(this)

        btn_signIn.setOnClickListener {
            val email = editText_email_signIn.text.toString()
            val password = editText_password_signIn.text.toString()

            if (email.isEmpty()) {
                editText_email_signIn.error = "Email Required"
                editText_email_signIn.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editText_email_signIn.error = "Please Enter a vaild Email"
                editText_email_signIn.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                editText_password_signIn.error = "Password 6 char required"
                editText_password_signIn.requestFocus()
                return@setOnClickListener
            }
            signIn(email, password)
        }

        btn_create_account.setOnClickListener() {
            val createAccountIntent = Intent(this, SignUpActivity::class.java)
            startActivity(createAccountIntent)
        }


    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser?.uid!=null){
            val intentMailActivity = Intent(this, MainActivity::class.java)
            startActivity(intentMailActivity)
            finish()
        }
    }
    private fun signIn(email: String, password: String) {
        progress_sign_in.visibility = View.VISIBLE


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progress_sign_in.visibility = View.INVISIBLE
                val intentMailActivity = Intent(this, MainActivity::class.java)
                // use this when go to screen without back as login or signup
                intentMailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMailActivity)
            } else {
                progress_sign_in.visibility = View.INVISIBLE
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btn_signIn.isEnabled =
            editText_email_signIn.text.trim().isNotEmpty() && editText_password_signIn.text.trim()
                .toString().isNotEmpty()
    }

    override fun afterTextChanged(s: Editable?) {
    }
}