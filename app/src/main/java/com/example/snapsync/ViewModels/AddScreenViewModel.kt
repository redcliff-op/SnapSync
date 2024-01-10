package com.example.snapsync.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.snapsync.room.ContactsEntity

class AddScreenViewModel : ViewModel() {
    var name by mutableStateOf("")
    var number by mutableStateOf("")
}