package com.example.messengerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.messengerapp.MainActivity
import com.example.messengerapp.ProfileActivity
import com.example.messengerapp.R
import com.example.messengerapp.model.ChatItem
import com.example.messengerapp.model.User
import com.example.messengerapp.recyclerview.RecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment() {
lateinit var chatRecyclerView:RecyclerView
    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
val view =inflater.inflate(R.layout.fragment_chat, container, false)
        val textview_title = activity?.findViewById<TextView>(R.id.title_toolbar_textview)
        textview_title?.text = "Chats"
        val circleImageViewProfile = activity?.findViewById(R.id.imageView_profile) as ImageView
        circleImageViewProfile.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
        addChatListener(::initRecyclerView)
chatRecyclerView =view.findViewById(R.id.chat_fragment_recyclerview)
        return view
    }

    private fun addChatListener(onListen: (ArrayList<ChatItem>) -> Unit): ListenerRegistration {
        return firebaseFirestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("sharedChat")
            .addSnapshotListener { snapshot, exeption ->
            if (exeption != null) {
                //then found error
                return@addSnapshotListener
            }


            val chatItems = ArrayList<ChatItem>()

            snapshot!!.documents.forEach { document ->
                if(document.exists()){
                    chatItems.add(MainActivity.getUser(document.id)!!)
                }
            }
            onListen(chatItems)
        }
    }
//    private fun getCurrentUser(uid: String, onComplete:(User)-> Unit){
//    firebaseFirestore.collection("users").document(uid).get().addOnSuccessListener {
//        Log.d("userss",it.toObject(User::class.java)!!.toString())
//        onComplete(it.toObject(User::class.java)!!)
//    }
//    }
    private fun initRecyclerView(chatItems: ArrayList<ChatItem>) {

        val adapter = RecyclerAdapter(chatItems)
    chat_fragment_recyclerview.layoutManager = LinearLayoutManager(activity, VERTICAL, false)
    chat_fragment_recyclerview.adapter = adapter
    }

}