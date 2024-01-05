package com.example.snapsync
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.snapsync.ViewModels.AddScreenViewModel
import com.example.snapsync.ViewModels.DatabaseViewModel
import com.example.snapsync.ViewModels.BottomBarViewModel
import com.example.snapsync.ViewModels.ContactScreenViewModel
import com.example.snapsync.ViewModels.PhoneScreenViewModel
import com.example.snapsync.repository.Repository
import com.example.snapsync.room.ContactsDB
import com.example.snapsync.room.ContactsEntity
import com.example.snapsync.ui.theme.SnapSyncTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(),Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(),Color.Transparent.toArgb()
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            SnapSyncTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
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
fun MainScreen(
    viewModel: BottomBarViewModel = viewModel(),
    databaseViewModel: DatabaseViewModel,
    phoneScreenViewModel: PhoneScreenViewModel = viewModel(),
    addScreenViewModel: AddScreenViewModel = viewModel()
){
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
                Phone(phoneScreenViewModel)
            }
            composable(route = "Contacts"){
                Contacts(databaseViewModel,navController)
            }
            composable(route = "Add"){
                Add(addScreenViewModel, databaseViewModel)
            }
            composable(
                route = "EditContactScreen/{name}/{number}",
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("number") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val number = backStackEntry.arguments?.getString("number") ?: ""
                EditContactScreen(navController, name, number, databaseViewModel)
            }

        }
    }
}

@Composable
fun Phone(
    viewModel: PhoneScreenViewModel
){
    var ctx = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(70.dp),
        ) {
            Row (
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Row (
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(300.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = viewModel.phoneNo,
                        fontSize = 40.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(onClick = {viewModel.phoneNo = viewModel.phoneNo.dropLast(1)}) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 1..3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (j in 1..3) {
                            val digit = (i - 1) * 3 + j
                            Button(onClick = { viewModel.phoneNo += digit.toString() }) {
                                Text(text = digit.toString(), fontSize = 50.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("*", "0", "#").forEach { symbol ->
                        Button(onClick = { viewModel.phoneNo += symbol }) {
                            Text(text = symbol, fontSize = 50.sp)
                        }
                    }
                }
            }
        }

        Button(onClick = {
            val REQUEST_PHONE_CALL = 1
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${viewModel.phoneNo}"))
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ctx as Activity, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
            } else {
                ctx.startActivity(intent)
            }
        }) {
            Image(
                imageVector = Icons.Filled.Call,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun Contacts(
    databaseViewModel: DatabaseViewModel,
    navController: NavController
){
    val contactList by databaseViewModel.contactList.collectAsState(initial = emptyList())
    var sortedContactList = contactList.sortedBy { it.name }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items(sortedContactList){item ->
            ContactCard(item, ContactScreenViewModel(), databaseViewModel,navController)
        }
    }
}

@Composable
fun Add(
    viewModel: AddScreenViewModel,
    databaseViewModel: DatabaseViewModel
){
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
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
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
                    ),
                    maxLines = 1
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
fun ContactCard(
    contactsEntity: ContactsEntity,
    contactScreenViewModel: ContactScreenViewModel,
    databaseViewModel: DatabaseViewModel,
    navController: NavController
) {
    val ctx = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
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
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(260.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = contactsEntity.name,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                            tint = MaterialTheme.colorScheme.onBackground
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
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            if (contactScreenViewModel.expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 54.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = contactsEntity.number,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.size(14.dp))
                Divider(color = MaterialTheme.colorScheme.background)
                Column(
                    modifier = Modifier.padding(horizontal = 2.dp),
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
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "Delete Contact",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
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
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "Send A Text Message",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        IconButton(onClick = {
                            navController.navigate("EditContactScreen/${contactsEntity.name}/${contactsEntity.number}")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "Edit Contact",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun EditContactScreen(
    navController: NavController,
    name:String,
    number: String,
    databaseViewModel: DatabaseViewModel
){
    var ctx = LocalContext.current
    var EditedName by remember {
        mutableStateOf(name)
    }
    var EditedNumber by remember {
        mutableStateOf(number)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Edit Contact Details",
                    fontSize = 30.sp
                )
                OutlinedTextField(
                    value = EditedName,
                    onValueChange = {newText -> EditedName = newText},
                    shape = RoundedCornerShape(20.dp)
                )
                OutlinedTextField(
                    value = EditedNumber,
                    onValueChange = {newText -> EditedNumber = newText},
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth(0.9f),
            onClick = {
            databaseViewModel.deleteContact(ContactsEntity(number,name))
            databaseViewModel.addContact(ContactsEntity(EditedNumber,EditedName))
            Toast.makeText(ctx,"Updated Successfully",Toast.LENGTH_SHORT).show()
            navController.navigate("Contacts")
        },) {
            Text(
                text = "Update",
                fontSize = 25.sp,
            )
        }
    }
}
