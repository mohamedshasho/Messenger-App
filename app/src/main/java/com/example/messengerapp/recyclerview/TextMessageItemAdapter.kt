package com.example.messengerapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.R
import android.text.format.DateFormat
import android.util.Log
import com.example.messengerapp.model.TextMessageDoc
import com.google.firebase.auth.FirebaseAuth

class TextMessageItemAdapter(private var itemMessages: ArrayList<TextMessageDoc>) :
    RecyclerView.Adapter<TextMessageItemAdapter.ViewHolderItem>() {
    private var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    override fun getItemViewType(position: Int): Int {
        //في تطبيقنا فقط رسائل نصية حاليا
        if (itemMessages[position].textMessage.senderId == currentUserId) {
            return 0
        } else return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val view: View
// here choose type message if text sender or recipient or sender image or ......
        //في تطبيقنا فقط رسائل نصية حاليا
        if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.sender_item_text_message, parent, false)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recipient_item_text_message, parent, false)
        }
        return ViewHolderItem(view)
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        Log.i("tagid", itemMessages[position].id)
        holder.text_view_time.text =
            DateFormat.format("hh:mm a", itemMessages[position].textMessage.date).toString()
        holder.text_view_message.text = itemMessages[position].textMessage.text
    }

    override fun getItemCount(): Int = itemMessages.size

    class ViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text_view_message = itemView.findViewById<TextView>(R.id.text_view_message)
        var text_view_time = itemView.findViewById<TextView>(R.id.text_view_time)
    }

}