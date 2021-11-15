package com.example.messengerapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import com.example.messengerapp.glide.GlideApp
import com.example.messengerapp.model.*
import com.example.messengerapp.recyclerview.MessageItemAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var mCurrentChatChannelId: String
    private val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val fireStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val chatChannelCollectionRef = fireStoreInstance.collection("chatChannels")


    //values
    private var mRecipientID = ""
    private var mCurrentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private val currentImageRef: StorageReference
        get() = firebaseStorage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = Color.GRAY
        }

        imageview_back_chat.setOnClickListener {
            finish()
        }
        fab_send_image.setOnClickListener {
            val myIntentImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }

            startActivityRequest.launch(Intent.createChooser(myIntentImage, "Select Image"))
        }
        val username = intent.getStringExtra("username")
        val profileImage = intent.getStringExtra("profile_image")
        mRecipientID = intent.getStringExtra("other_uid")!!

        createChatChannel { channelID ->
            mCurrentChatChannelId = channelID
            getMessages(channelID)

            imageview_send.setOnClickListener {
                val text = edittext_message.text.toString()
                if (text.isNotEmpty()) {
                    val messageSend = TextMessage(
                        text,
                        mCurrentUserId,
                        mRecipientID,
                        Calendar.getInstance().time
                    )
                    sendMessage(channelID, messageSend)
                    edittext_message.setText("")
                } else {
                    Toast.makeText(this, "Empty Faild", Toast.LENGTH_SHORT).show()
                }
            }
        }


        textview_profile_chat.text = username

        if (profileImage != null && profileImage.isNotEmpty()) {
            GlideApp.with(this).load(firebaseStorage.getReference(profileImage))
                .into(imageview_profile_chat)
        } else {
            imageview_profile_chat.setImageResource(R.drawable.ic_account)
        }


    }

    private fun sendMessage(channelID: String, message: Message) {
        //interface Message because different type message
        chatChannelCollectionRef.document(channelID).collection("messages")
            .add(message)
    }

    private fun createChatChannel(onComplete: (channelID: String) -> Unit) {

        fireStoreInstance.collection("users").document(mCurrentUserId)
            .collection("sharedChat").document(mRecipientID).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    onComplete(doc["channelID"] as String)
                    return@addOnSuccessListener
                }

                val newChatChannel = fireStoreInstance.collection("users").document()

                fireStoreInstance.collection("users")
                    .document(mRecipientID)
                    .collection("sharedChat")
                    .document(mCurrentUserId)
                    .set(mapOf("channelID" to newChatChannel.id))
                fireStoreInstance.collection("users")
                    .document(mCurrentUserId)
                    .collection("sharedChat")
                    .document(mRecipientID)
                    .set(mapOf("channelID" to newChatChannel.id))
                onComplete(newChatChannel.id)
            }
    }

    private fun getMessages(channelID: String) {
        val query = chatChannelCollectionRef.document(channelID).collection("messages")
            .orderBy("date", Query.Direction.DESCENDING)
        query.addSnapshotListener { snapshot, exption ->

            if (snapshot!!.documents.isNotEmpty()) {

                val itemMessages = ArrayList<MessageDoc>()
                snapshot.documents.forEach { doc ->
                    if (doc["type"] == MessageType.TEXT) {
                        val textMessage = doc.toObject(TextMessage::class.java)
                        val item = MessageDoc(doc.id, textMessage = textMessage!!)
                        itemMessages.add(item)
                    } else if (doc["type"] == MessageType.IMAGE) {
                        val imageMessage = doc.toObject(ImageMessage::class.java)
                        val item = MessageDoc(doc.id, imageMessage = imageMessage!!)
                        itemMessages.add(item)
                    }
                }
                val messageAdapter = MessageItemAdapter(itemMessages,this)
                chat_recyclerView.apply {
                    //layoutManager =LinearLayoutManager(this@ChatActivity)
                    //because is added in layoutManager xml
                    adapter = messageAdapter
                }
            }

        }
    }

    private val startActivityRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == RESULT_OK && it.data != null && it.data?.data != null) {
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
                val outputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 25, outputStream)
                val selectedImageBytes = outputStream.toByteArray()

                uploadImage(selectedImageBytes) { path ->

                    val imageMessage =
                        ImageMessage(
                            path,
                            mCurrentUserId,
                            mRecipientID,
                            Calendar.getInstance().time
                        )
//                    chatChannelCollectionRef.document(mCurrentChatChannelId).collection("messages")
//                        .add(imageMessage)
                    sendMessage(mCurrentChatChannelId, imageMessage)
                }

            }
        }

    private fun uploadImage(selectedImageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {
        //putBytes للتحكم بحجم الصورة
        val ref = currentImageRef.child(
            "${mCurrentUserId}/images/${
                UUID.nameUUIDFromBytes(selectedImageBytes)
            }"
        )
        ref.putBytes(selectedImageBytes).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(ref.path) //higher order function
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}