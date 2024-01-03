package com.example.snapsync.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDAO {
    @Insert
    suspend fun addContact(contactsEntity: ContactsEntity)

    @Query("SELECT * FROM contactsentity")
    fun getAllContacts(): Flow<List<ContactsEntity>>

    @Delete
    suspend fun deleteContact(contactsEntity: ContactsEntity)
}