package com.example.snapsync.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ContactsEntity::class], version = 1, exportSchema = false)
abstract class ContactsDB : RoomDatabase(){
    abstract fun contactsDao(): ContactsDAO
    companion object{
        @Volatile
        var INSTANCE : ContactsDB?=null
        fun getInstance(context: Context):ContactsDB{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(context.applicationContext,ContactsDB::class.java,"contacts_db").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}