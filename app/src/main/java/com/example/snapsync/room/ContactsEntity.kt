package com.example.snapsync.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactsEntity(
    @PrimaryKey
    var number: String,
    var name:String
)