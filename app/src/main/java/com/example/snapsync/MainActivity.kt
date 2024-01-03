package com.example.snapsync
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snapsync.ViewModels.AddScreenViewModel
import com.example.snapsync.ViewModels.DatabaseViewModel
import com.example.snapsync.ViewModels.BottomBarViewModel
import com.example.snapsync.ViewModels.ContactScreenViewModel
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
                        },
                        label = {Text(text = item.label)}
                    )
                }
           }
       }
    ){
        NavHost(navController = navController, startDestination = "Phone"){
            composable(route = "Phone"){
                Phone(viewModel = BottomBarViewModel())
            }
            composable(route = "Contacts"){
                Contacts(viewModel = ContactScreenViewModel(), databaseViewModel)
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
fun Contacts(viewModel: ContactScreenViewModel, databaseViewModel: DatabaseViewModel){
    val contactList by databaseViewModel.contactList.collectAsState(initial = emptyList())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items(contactList){item ->
            ContactCard(item, contactScreenViewModel = ContactScreenViewModel(), databaseViewModel)
        }
    }
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
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
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

@Composable
fun ContactCard(contactsEntity: ContactsEntity, contactScreenViewModel: ContactScreenViewModel,databaseViewModel: DatabaseViewModel) {
    val ctx = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 7.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 13.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = contactsEntity.name,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }
                Row {
                    IconButton(
                        onClick = {
                            val REQUEST_PHONE_CALL = 1
                            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${contactsEntity.number}"))
                            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ctx as Activity, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
                            } else {
                                ctx.startActivity(intent)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = {
                            contactScreenViewModel.expanded = !contactScreenViewModel.expanded
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
            if (contactScreenViewModel.expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 61.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = contactsEntity.number,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp),
                ) {
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        IconButton(onClick = {
                            databaseViewModel.deleteContact(contactsEntity)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Delete Contact",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${contactsEntity.number}"))
                            ctx.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.MailOutline,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Send A Text Message",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Edit Contact",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }

                }
            }
        }
    }
}
