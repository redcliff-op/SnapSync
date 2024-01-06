package com.example.snapsync.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapsync.room.ContactsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchScreenViewModel : ViewModel() {

    var searchQuery by mutableStateOf("")

    private val filteredContacts = MutableStateFlow<List<ContactsEntity>>(emptyList())
    val filteredContactsState: StateFlow<List<ContactsEntity>> get() = filteredContacts
    fun filterContacts(contactList: List<ContactsEntity>, searchQuery: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val filteredList = contactList.filter {
                it.name.contains(searchQuery, true) || it.number.contains(searchQuery)
            }
            filteredContacts.emit(filteredList)
        }
    }
}