package com.example.messengerapp.recyclerview

import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.R
import com.example.messengerapp.model.TextMessageDoc

class RecipientTextMessageItem(private var itemMessages:ArrayList<TextMessageDoc>) : RecyclerView.Adapter<RecipientTextMessageItem.RecipientHolderItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipientHolderItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipient_item_text_message,parent,false)
        return  RecipientHolderItem(view)
    }
    override fun onBindViewHolder(holder: RecipientHolderItem, position: Int) {
        Log.i("tagid",itemMessages[position].id)
        holder.text_view_time.text =  DateFormat.format("hh:mm a",itemMessages[position].textMessage.date).toString()
        holder.text_view_message.text =  itemMessages[position].textMessage.text
    }
    override fun getItemCount(): Int = itemMessages.size

    class RecipientHolderItem(itemView: View): RecyclerView.ViewHolder(itemView){
        var text_view_message = itemView.findViewById<TextView>(R.id.text_view_message)
        var text_view_time = itemView.findViewById<TextView>(R.id.text_view_time)
    }
}