package com.example.snapsync.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ContactScreenViewModel :ViewModel(){
    var expanded by mutableStateOf(false)
}