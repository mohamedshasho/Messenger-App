package com.example.messengerapp.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.R
import android.text.format.DateFormat
import android.util.Log
import android.widget.ImageView
import com.example.messengerapp.glide.GlideApp
import com.example.messengerapp.model.MessageDoc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MessageItemAdapter(private var messages: ArrayList<MessageDoc>, val context: Context) :
    RecyclerView.Adapter<MessageItemAdapter.ViewHolderItem>() {

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    override fun getItemViewType(position: Int): Int {
        //في تطبيقنا فقط رسائل نصيةوصورة حاليا
        if (messages[position].imageMessage == null && messages[position].textMessage != null) {
            if (messages[position].textMessage?.senderId == currentUserId) {
                return 0
            } else
                return 1
        } else {
            //hire image message
            if (messages[position].imageMessage?.senderId == currentUserId) {
                return 2
            } else
                return 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val view: View
// here choose type message if text sender or recipient or sender image or ......
        //في تطبيقنا فقط رسائل نصية حاليا
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sender_item_text_message, parent, false)
                return ViewHolderItem(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recipient_item_text_message, parent, false)
                return ViewHolderItem(view)
            }
            2 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sender_item_image_message, parent, false)
                return ViewHolderItem(view)

            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recipient_item_image_message, parent, false)
                return ViewHolderItem(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        if (messages[position].textMessage != null) {
            holder.text_view_time.text =
                DateFormat.format("hh:mm a", messages[position].textMessage?.date).toString()
            holder.text_view_message.text = messages[position].textMessage?.text
        } else {
            holder.textview_image_time.text =
                DateFormat.format("hh:mm a", messages[position].imageMessage?.date).toString()
            GlideApp.with(context)
                .load(storageInstance.getReference(messages[position].imageMessage!!.imagePath))
                .placeholder(R.drawable.ic_image)
                .into(holder.imageview_message_image)
        }

    }

    override fun getItemCount(): Int = messages.size

    class ViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text_view_message = itemView.findViewById<TextView>(R.id.text_view_message)
        var text_view_time = itemView.findViewById<TextView>(R.id.text_view_time)
        var textview_image_time = itemView.findViewById<TextView>(R.id.textview_image_time)
        var imageview_message_image = itemView.findViewById<ImageView>(R.id.imageview_message_image)
    }

}