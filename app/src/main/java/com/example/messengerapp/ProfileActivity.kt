package com.example.messengerapp

import android.R.attr
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.android.synthetic.main.activity_profile.*
import android.R.attr.bitmap

import android.os.Build
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.view.View
import android.view.WindowInsetsController
import com.example.messengerapp.glide.GlideApp
import com.example.messengerapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUserStorageRef: StorageReference
        get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")
    private lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(profile_toolbar)
        supportActionBar?.title = "Me"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            window.statusBarColor = Color.GRAY
        }


        getUserInfo { user ->
            if(user.profileImage.isNotEmpty()){
                GlideApp.with(this).load(storageInstance.getReference(user.profileImage))
                    .placeholder(R.drawable.ic_account)//default image
                    .into(imageView_profile_image)
            }
            username_profile.text=user.name
            username = user.name
        }

        val previewRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                Log.i("Dataaaa", it.data.toString())
                Log.i("Dataaaa", it.data?.data.toString())
                if (it.resultCode == RESULT_OK && it.data != null && it.data?.data != null) {
                progress_profile.visibility = View.VISIBLE
                    //data?.data is selected  uri image
                    imageView_profile_image.setImageURI(it.data?.data)
                    //تقليص الصورة ثم تحويلها الى بايتس لرفعها
                    val selectedImagePath = it.data?.data
                    var bitmap: Bitmap? = null
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            selectedImagePath
                        )
                    } else {
                        val source: ImageDecoder.Source =
                            ImageDecoder.createSource(contentResolver, selectedImagePath!!)
                        bitmap = ImageDecoder.decodeBitmap(source)
                    }
                    val outputStorage = ByteArrayOutputStream()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 25, outputStorage)
                    val selectedImageBytes = outputStorage.toByteArray()

                    //todo lamda expetion search

                    uploadProfileImage(selectedImageBytes) { path ->
                        val userFieldMap = mutableMapOf<String, Any>()
                        Log.i("tagpath", path)
//                        userFieldMap["name"]=username
                        userFieldMap["profileImage"] = path
                        currentUserDocRef.update(userFieldMap)
                    }
                }
            }

        imageView_profile_image.setOnClickListener {
            val myIntentImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            previewRequest.launch(Intent.createChooser(myIntentImage, "Select Image"))
//            startActivityForResult(Intent.createChooser(myIntentImage,"Select Image"),RC_SELECT_IMAGE)
        }


    }

    private fun uploadProfileImage(
        selectedImageBytes: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) {
        //UUID build name from bytes or random ...
        val ref =
            currentUserStorageRef.child("profilePictures/${UUID.nameUUIDFromBytes(selectedImageBytes)}")
        ref.putBytes(selectedImageBytes).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(ref.path)
                progress_profile.visibility = View.GONE
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }

    private fun getUserInfo(onComplete: (User) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            // نضع كونستركتر constructor() افتراضي في كلاس يوزر منشان عملية التحويل يطلبه فايربيس
            onComplete(it.toObject(User::class.java)!!)
        }
    }
}