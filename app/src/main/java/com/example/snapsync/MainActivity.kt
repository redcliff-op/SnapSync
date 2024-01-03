package com.example.snapsync

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snapsync.ViewModels.AddScreenViewModel
import com.example.snapsync.ViewModels.DatabaseViewModel
import com.example.snapsync.ViewModels.BottomBarViewModel
import com.example.snapsync.repository.Repository
import com.example.snapsync.room.ContactsDB
import com.example.snapsync.room.ContactsEntity
import com.example.snapsync.ui.theme.SnapSyncTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnapSyncTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var context = LocalContext.current
                    var database = ContactsDB.getInstance(context)
                    var repository = Repository(database)
                    var dataBaseViewModel = DatabaseViewModel(repository)
                    MainScreen(databaseViewModel = dataBaseViewModel)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModel: BottomBarViewModel = viewModel(), databaseViewModel: DatabaseViewModel){
    val bottomBarList = viewModel.initialiseBottomBarList()
    val navController = rememberNavController()
    Scaffold (
       bottomBar = {
           NavigationBar{
                bottomBarList.forEachIndexed{index,item->
                    NavigationBarItem(
                        selected = viewModel.selected==index,
                        onClick = {
                            navController.navigate(item.scr)
                            viewModel.selected=index
                        },
                        icon = {
                            Icon(
                                imageVector =   if(viewModel.selected==index){
                                                    item.selected
                                                }else{
                                                    item.unselected
                                                },
                                contentDescription = null)
                        })
                }
           }
       }
    ){
        NavHost(navController = navController, startDestination = "Phone"){
            composable(route = "Phone"){
                Phone(viewModel = BottomBarViewModel())
            }
            composable(route = "Contacts"){
                Contacts(viewModel = BottomBarViewModel())
            }
            composable(route = "Add"){
                Add(viewModel = AddScreenViewModel(), databaseViewModel)
            }
        }
    }
}

@Composable
fun Phone(viewModel: BottomBarViewModel){

}

@Composable
fun Contacts(viewModel: BottomBarViewModel){

}

@Composable
fun Add(viewModel: AddScreenViewModel, databaseViewModel: DatabaseViewModel){
    val ctx = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(20.dp),
        ){
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ){
                Text(
                    text = "Add Contact Details",
                    fontSize = 30.sp
                )
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = {newName -> viewModel.name = newName},
                    label = { Text(text = "Contact Name")},
                    placeholder = { Text(text = "Please Enter the name of the Contact")},
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.number,
                    onValueChange = {newNumber -> viewModel.number = newNumber},
                    label = { Text(text = "Contact Number")},
                    placeholder = { Text(text = "Please Enter the Mobile Number")},
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Button(
            onClick = {
                databaseViewModel.addContact(ContactsEntity(viewModel.number, viewModel.name))
                Toast.makeText(ctx,"Added Successfully",Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text(
                text = "Add",
                fontSize = 25.sp,
            )
        }
    }
}