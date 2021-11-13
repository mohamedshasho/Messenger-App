package com.example.messengerapp

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View

import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.messengerapp.fragments.ChatFragment
import com.example.messengerapp.fragments.MoreFragment
import com.example.messengerapp.fragments.PeopleFragment
import com.example.messengerapp.glide.GlideApp
import com.example.messengerapp.model.User
import com.google.android.material.navigation.NavigationBarView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {


    private val mChatFragment = ChatFragment()
    private val mPeopleFragment = PeopleFragment()
    private val mMoreFragment = MoreFragment()

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar_main)
        supportActionBar?.title = ""// عند حذف التعليمة ياخذ تايتل الافتراضي وهو اسم تطبيق

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS,
                APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = Color.GRAY
        }

        bottomNavigationView_main.setOnItemSelectedListener(this)
        setFragment(mChatFragment)

        firestoreInstance.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user!!.profileImage.isNotEmpty()) {
                    GlideApp.with(this).load(storageInstance.getReference(user.profileImage))
                        .into(imageView_profile)
                }else{
                    imageView_profile.setImageResource(R.drawable.ic_account)
                }
            }

    }


    private fun setFragment(fragment: Fragment) {
        val fr = supportFragmentManager.beginTransaction()
        //coordinatorLayout or frameLayout but coordinatorLayout is more details
        fr.replace(R.id.coordinatorLayout_main_content, fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_chat_item -> {
                setFragment(mChatFragment)
                return true
            }
            R.id.navigation_people_item -> {
                setFragment(mPeopleFragment)
                return true
            }
            R.id.navigation_more_item -> {
                setFragment(mMoreFragment)
                return true
            }
            else -> return false
        }
    }
}

