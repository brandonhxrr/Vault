package com.brandonhxrr.vault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brandonhxrr.vault.R
import com.brandonhxrr.vault.data.EmployeesViewModel
import com.brandonhxrr.vault.data.User
import com.brandonhxrr.vault.data.loadPrivateKeyFromFile
import com.brandonhxrr.vault.data.performECDHKeyExchange
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.util.Base64

@Composable()
fun Employees(modifier: Modifier) {

    val viewModel = remember { EmployeesViewModel() }
    val users by viewModel.users.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Empleados",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyColumn {
            items(users) { user ->
                Employee(user = user)
            }
        }


    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Employee(user: User) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var publicKey by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GlideImage(
                model = user.photoURL, contentDescription = "Profile Picture", modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.product_sans_regular))
                )

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.product_sans_regular))
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    },
                    title = {
                        Text(text = "¿Desea generar la clave compartida?")
                    },
                    text = {
                        Text(text = "Esto generará la clave compartida con ${user.name}.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val privateKey = loadPrivateKeyFromFile(context)
                                val decodedPublicKey = Base64.getDecoder().decode(publicKey)
                                val decodedPrivateKey = Base64.getDecoder().decode(privateKey)
                                val sharedKey = performECDHKeyExchange(decodedPrivateKey, decodedPublicKey)

                                val currentUser = Firebase.auth.currentUser
                                uploadSharedKeyToFirebase(currentUser?.uid!!, sharedKey, user.id)

                                showDialog = false
                            }
                        ) {
                            Text(text = "Aceptar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text(text = "Cancelar")
                        }
                    }
                )
            }
        }
    }
}

private fun uploadSharedKeyToFirebase(
    userId: String,
    sharedKey: ByteArray,
    otherPartyId: String
): Boolean {
    var success = false
    val userReference =
        FirebaseDatabase.getInstance().getReference("users_private_data").child(userId)
    userReference.child("shared").child(otherPartyId).child("shared_key")
        .setValue(Base64.getEncoder().encodeToString(sharedKey))
        .addOnSuccessListener {
            success = true
        }
        .addOnFailureListener { exception ->
            println("Ocurrió un error al subir la clave compartida a Firebase: ${exception.message}")
            success = false
        }
    return success
}


@Preview(showBackground = true)
@Composable
fun EmployeesPreview() {
    Employees(modifier = Modifier)
}
