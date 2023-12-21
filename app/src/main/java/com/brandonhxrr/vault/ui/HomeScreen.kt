package com.brandonhxrr.vault.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brandonhxrr.vault.R
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun Home(navController: NavController
) {

    val auth = FirebaseAuth.getInstance()
    var generatedKeys by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.splash_logo),
                                contentDescription = "App logo",
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                color = colorScheme.onBackground
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if(generatedKeys) {
                            navController.navigate(Screens.UserScreen.name)
                        }
                    }) {
                        if (auth.currentUser?.photoUrl != null) {
                            GlideImage(
                                model = auth.currentUser?.photoUrl,
                                contentDescription = "User profile picture",
                                modifier = Modifier.clip(
                                    CircleShape
                                )
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "User profile picture"
                            )
                        }

                    }
                }
            )
        },
        content = {
            var selectedItem by remember { mutableIntStateOf(0) }
            val items = listOf(R.string.menu_home, R.string.menu_users, R.string.menu_share)
            val icons = listOf(R.drawable.home, R.drawable.users, R.drawable.share)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )  {
                when (selectedItem) {
                    0 -> {
                        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
                        val userReference = firebaseDatabase.getReference("users_public_data").child(auth.currentUser?.uid.toString())

                        userReference.child("public_key").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val publicKey = snapshot.value
                                Log.d("HomeScreen", "Public key: $publicKey")
                                if (publicKey != null) {
                                    generatedKeys = true
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("HomeScreen", "Error getting user data")
                            }
                        })
                        if (generatedKeys) {
                            SharedWithMe(modifier = Modifier.weight(0.9f))
                        } else {
                            NoKeys(modifier = Modifier.weight(0.9f))
                        }
                    }
                    1 -> Employees(modifier = Modifier.weight(0.9f))
                    2 -> Share(modifier = Modifier.weight(0.9f))
                }

                NavigationBar(modifier = Modifier.weight(0.11f)) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            icon = {
                                Icon(
                                    painter = painterResource(id = icons[index]),
                                    contentDescription = stringResource(id = item)
                                )
                            },
                            label = { Text(stringResource(id = item)) })
                    }
                }
            }
        }
    )
}


