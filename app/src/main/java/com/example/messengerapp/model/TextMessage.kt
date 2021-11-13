package com.example.messengerapp.model

import java.util.*

data class TextMessage(
    val text: String,
    override val senderId: String,
    override val recipientId: String,
    override val date: Date,
    override val type: String = MessageType.TEXT
) : Message {
    constructor() : this("", "", "", Date())
}