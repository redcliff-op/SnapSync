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
    suspend fun getContactByNameAndNumber(name: String, number: String): ContactsEntity? {
        return repository.getContactByNameAndNumber(name, number)
    }
    fun updateContact(contactEntity: ContactsEntity) {
        viewModelScope.launch {
            repository.updateContactInRoom(contactEntity)
        }
    }
    fun deleteContact(contactsEntity: ContactsEntity){
        viewModelScope.launch {
            repository.deleteContactFromRoom(contactsEntity)
        }
    }
}