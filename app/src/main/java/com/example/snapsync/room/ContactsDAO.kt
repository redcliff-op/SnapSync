package com.example.snapsync.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDAO {
    @Insert
    suspend fun addContact(contactsEntity: ContactsEntity)

    @Query("SELECT * FROM contactsentity")
    fun getAllContacts(): Flow<List<ContactsEntity>>

    @Query("SELECT * FROM contactsentity WHERE name = :name AND number = :number LIMIT 1")
    suspend fun getContactByNameAndNumber(name: String, number: String): ContactsEntity?

    @Update
    suspend fun updateContact(contactsEntity: ContactsEntity)

    @Delete
    suspend fun deleteContact(contactsEntity: ContactsEntity)
}