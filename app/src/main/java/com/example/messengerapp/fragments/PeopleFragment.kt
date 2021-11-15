package com.example.messengerapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.MainActivity
import com.example.messengerapp.R
import com.example.messengerapp.model.ChatItem
import com.example.messengerapp.model.User
import com.example.messengerapp.recyclerview.RecyclerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_people.*


class PeopleFragment : Fragment() {
lateinit var recyclerView: RecyclerView
    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val textview_title = activity?.findViewById<TextView>(R.id.title_toolbar_textview)
        textview_title?.text = "People"
Log.d("alluser  s",MainActivity.allUsers.size.toString())
val view =inflater.inflate(R.layout.fragment_people, container, false)
        //recyclerView = activity?.findViewById(R.id.people_recyclerview)!!

        recyclerView= view.findViewById(R.id.people_recyclerview)
        initRecyclerView(MainActivity.allUsers)
//        addChatListener(::initRecyclerView)
        return view

    }


    private fun addChatListener(onListen: (ArrayList<ChatItem>) -> Unit): ListenerRegistration {
        return firebaseFirestore.collection("users")
            .addSnapshotListener { snapshot, exeption ->
                if (exeption != null) {
                    //then found error
                    return@addSnapshotListener
                }
                val chatItems = ArrayList<ChatItem>()

                snapshot!!.documents.forEach { document ->
                    if (document.exists()) {
                        chatItems.add(ChatItem(document.id, document.toObject(User::class.java)!!))
                    }
                }
                onListen(chatItems)
            }
    }

    private fun initRecyclerView(chatItems: ArrayList<ChatItem>) {

        val adapter = RecyclerAdapter(chatItems)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
    }
}