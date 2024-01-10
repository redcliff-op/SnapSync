package com.example.snapsync.repository

import com.example.snapsync.room.ContactsDB
import com.example.snapsync.room.ContactsEntity

class Repository (val contactsDB: ContactsDB) {
    suspend fun addContactToRoom(contactsEntity: ContactsEntity){
        contactsDB.contactsDao().addContact(contactsEntity)
    }
    fun getAllContactsFromRoom() = contactsDB.contactsDao().getAllContacts()
    suspend fun getContactByNameAndNumber(name: String, number: String): ContactsEntity? {
        return contactsDB.contactsDao().getContactByNameAndNumber(name, number)
    }
    suspend fun updateContactInRoom(contactsEntity: ContactsEntity) {
        contactsDB.contactsDao().updateContact(contactsEntity)
    }
    suspend fun deleteContactFromRoom(contactsEntity: ContactsEntity){
        contactsDB.contactsDao().deleteContact(contactsEntity)
    }
}