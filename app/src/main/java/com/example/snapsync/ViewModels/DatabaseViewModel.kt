package com.example.snapsync.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapsync.repository.Repository
import com.example.snapsync.room.ContactsEntity
import kotlinx.coroutines.launch

class DatabaseViewModel(val repository: Repository): ViewModel() {
    fun addContact(contactsEntity: ContactsEntity){
        viewModelScope.launch {
            repository.addContactToRoom(contactsEntity)
        }
    }
    var contactList = repository.getAllContactsFromRoom()
    fun deleteContact(contactsEntity: ContactsEntity){
        viewModelScope.launch {
            repository.deleteContactFromRoom(contactsEntity)
        }
    }
}