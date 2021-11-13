package com.example.messengerapp.model

data class User(val name: String, val profileImage: String) {
    // نضع  constructor() افتراضي في كلاس يوزر منشان عملية التحويل يطلبه فايربيس
    constructor() : this("", "")
}