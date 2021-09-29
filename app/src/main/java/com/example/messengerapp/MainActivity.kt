package com.example.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.messengerapp.fragments.ChatFragment
import com.example.messengerapp.fragments.MoreFragment
import com.example.messengerapp.fragments.PeopleFragment
import com.google.android.material.navigation.NavigationBarView

import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , NavigationBarView.OnItemSelectedListener {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val mChatFragment =ChatFragment()
    private val mPeopleFragment =PeopleFragment()
    private val mMoreFragment = MoreFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView_main.setOnItemSelectedListener(this)
        setFragment(mChatFragment)


    }



    private fun setFragment(fragment: Fragment) {
        val fr = supportFragmentManager.beginTransaction()
        //coordinatorLayout or frameLayout but coordinatorLayout is more details
        fr.replace(R.id.coordinatorLayout_main_content,fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navigation_chat_item ->{
                setFragment(mChatFragment)
                return true
            }
            R.id.navigation_people_item ->{
                setFragment(mPeopleFragment)
                return true
            }
            R.id.navigation_more_item ->{
                setFragment(mMoreFragment)
                return true
            }
            else -> return false
        }
    }
}

