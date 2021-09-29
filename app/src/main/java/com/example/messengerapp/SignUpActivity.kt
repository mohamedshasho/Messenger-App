package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.messengerapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), TextWatcher {
    //the best initial value
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

     val firestoreInstanse: FirebaseFirestore by lazy {
         FirebaseFirestore.getInstance()
    }
    private  val currentUserDocRef:DocumentReference
    get() = firestoreInstanse.document("users/${mAuth.currentUser?.uid.toString()}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editText_name_signUp.addTextChangedListener(this)
        editText_email_signUp.addTextChangedListener(this)
        editText_password_signUp.addTextChangedListener(this)

        btn_signUp.setOnClickListener {
            val name = editText_name_signUp.text.toString().trim()
            val email = editText_email_signUp.text.toString().trim()
            val password = editText_password_signUp.text.toString().trim()

            if (name.isEmpty()) {
                editText_name_signUp.error = "Name Required"
                editText_name_signUp.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                editText_email_signUp.error = "Email Required"
                editText_email_signUp.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editText_email_signUp.error = "Please Enter a vaild Email"
                editText_email_signUp.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                editText_password_signUp.error = "Password 6 char required"
                editText_password_signUp.requestFocus()
                return@setOnClickListener
            }
            createNewAccount(name = name, email = email, password) //لتحيد بدقة البرامترات

        }
    }

    private fun createNewAccount(name: String, email: String, password: String) {
//            val authResult= mAuth.createUserWithEmailAndPassword(email, password)
//            authResult.addOnCompleteListener {
//                Log.i("login",authResult.exception?.message.toString())
//            }
  progress_sign_up.visibility= View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progress_sign_up.visibility= View.GONE
                val newUser =User(name,profileImage = "")
                //تلقائي يصبح الاوبجكت ك json
                currentUserDocRef.set(newUser)
                val intentMailActivity = Intent(this, MainActivity::class.java)
                // use this when go to screen without back as login or signup
                intentMailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMailActivity)
//      finish() اذا حطيتها يتم الرجوع الى صفحة shin IN
            } else {
                progress_sign_up.visibility= View.GONE
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                Log.i("login", task.exception?.message.toString())
            }
        }


    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btn_signUp.isEnabled =
            editText_name_signUp.text.trim().isNotEmpty() && editText_email_signUp.text.trim()
                .isNotEmpty() && editText_password_signUp.text.trim().isNotEmpty()
    }

    override fun afterTextChanged(s: Editable?) {
    }
}