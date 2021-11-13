package com.example.messengerapp.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.ChatActivity
import com.example.messengerapp.R
import com.example.messengerapp.glide.GlideApp
import com.example.messengerapp.model.ChatItem
import com.example.messengerapp.model.User
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


class RecyclerAdapter(private val chatItem: ArrayList<ChatItem>) :
    RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val user: User = chatItem[position].user
        holder.username.text = user.name
        if(user.profileImage.isNotEmpty()){
            GlideApp.with(holder.itemView.context)
                .load(FirebaseStorage.getInstance().getReference(user.profileImage))
                .placeholder(R.drawable.ic_account)
                .into(holder.circleImageProfile)
        }else{
            holder.circleImageProfile.setImageResource(R.drawable.ic_account)
        }

        holder.lastMessage.text = "last message..."
        holder.itemView.setOnClickListener {
            val intent =Intent(it.context, ChatActivity::class.java)
            intent.putExtra("username",user.name)
            intent.putExtra("profile_image",user.profileImage)
            intent.putExtra("other_uid",chatItem[position].uid)
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return chatItem.size
    }


    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView.findViewById(R.id.item_name_textview) as TextView
        val time = itemView.findViewById(R.id.item_time_textview) as TextView
        val lastMessage = itemView.findViewById(R.id.item_last_message_textview) as TextView
        val circleImageProfile =
            itemView.findViewById(R.id.item_cirecle_imageview) as CircleImageView
    }
}

