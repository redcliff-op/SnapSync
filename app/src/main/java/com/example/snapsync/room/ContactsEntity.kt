package com.example.snapsync.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactsEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var number: String,
    var name:String
)