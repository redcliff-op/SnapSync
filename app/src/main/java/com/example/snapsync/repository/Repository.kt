package com.example.snapsync.repository

import com.example.snapsync.room.ContactsDB
import com.example.snapsync.room.ContactsEntity

class Repository (val contactsDB: ContactsDB) {
    suspend fun addContactToRoom(contactsEntity: ContactsEntity){
        contactsDB.contactsDao().addContact(contactsEntity)
    }
    fun getAllContactsFromRoom() = contactsDB.contactsDao().getAllContacts()
    suspend fun deleteContactFromRoom(contactsEntity: ContactsEntity){
        contactsDB.contactsDao().deleteContact(contactsEntity)
    }
}