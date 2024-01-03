package com.example.snapsync.ViewModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.snapsync.BottomBarList

class BottomBarViewModel :ViewModel(){
    fun initialiseBottomBarList():List<BottomBarList>{
        var output = listOf<BottomBarList>(
            BottomBarList("Phone", Icons.Filled.Phone,Icons.Outlined.Phone,"Phone"),
            BottomBarList("Contacts",Icons.Filled.AccountCircle,Icons.Outlined.AccountCircle,"Contacts"),
            BottomBarList("Add",Icons.Filled.AddCircle,Icons.Outlined.AddCircle,"Add")
        )
        return output
    }
    var selected by mutableStateOf(0)
}