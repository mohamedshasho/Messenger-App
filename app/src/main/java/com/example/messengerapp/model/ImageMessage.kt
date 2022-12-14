package com.example.messengerapp.model

import java.util.*

class ImageMessage(
    val imagePath: String,
    override val senderId: String,
    override val recipientId: String,
    override val date: Date,
    override val type: String = MessageType.IMAGE
) : Message {
    constructor() : this("", "", "", Date())
}