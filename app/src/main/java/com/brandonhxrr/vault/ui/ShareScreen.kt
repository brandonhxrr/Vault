package com.brandonhxrr.vault.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.brandonhxrr.vault.data.EmployeesViewModel
import com.brandonhxrr.vault.data.User
import com.brandonhxrr.vault.data.encryptFileAesGcm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.spec.SecretKeySpec

@Composable
fun Share(modifier: Modifier) {
    val context = LocalContext.current
    var selectedUser by remember { mutableStateOf<User?>(null) }
    val usersViewModel = remember { EmployeesViewModel() }
    val users by usersViewModel.users.collectAsState(initial = emptyList())
    var selectedFile: File? by remember { mutableStateOf(null) }
    var selectedFileUri by remember { mutableStateOf("") }

    var isMenuExpanded by remember { mutableStateOf(false) }

    val selectFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedFile = uri?.let { documentUri ->
                val documentFile = DocumentFile.fromSingleUri(context, documentUri)
                File(documentFile?.name ?: "archivo_desconocido")
            }
            selectedFileUri = uri.toString()
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable {
                    isMenuExpanded = !isMenuExpanded
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = selectedUser?.email ?: "Seleccionar usuario")
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "Toggle Dropdown"
                )
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                // Cerrar el menú cuando el componente se dispose
                isMenuExpanded = false
            }
        }

        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ),
            expanded = isMenuExpanded,
            onDismissRequest = {
                isMenuExpanded = false
            }
        ) {
            users.forEach { user ->
                DropdownMenuItem(
                    onClick = {
                        selectedUser = user
                        isMenuExpanded = false
                    },
                    text = {
                        Text(text = user.email)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = selectedFile?.name ?: "")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                selectFileLauncher.launch("*/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Outlined.CloudUpload, contentDescription = "Select File")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Seleccionar archivo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedUser != null && selectedFile != null) {
                    // Obtener la shared_key de Firebase
                    val userId = selectedUser?.id
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val databaseReference: DatabaseReference =
                            FirebaseDatabase.getInstance()
                                .getReference("users_private_data/${currentUser.uid}/shared/$userId/shared_key")

                        databaseReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val sharedKey = snapshot.getValue(String::class.java)

                                if (sharedKey != null) {
                                    val decodedSharedKey = Base64.getDecoder().decode(sharedKey)
                                    val aesKey = SecretKeySpec(decodedSharedKey, "AES")

                                    val contentResolver = context.contentResolver
                                    val inputStream =
                                        contentResolver.openInputStream(selectedFileUri.toUri())

                                    val tempFile =
                                        File.createTempFile("temp", selectedFile!!.extension)
                                    tempFile.outputStream().use { output ->
                                        inputStream?.copyTo(output)
                                    }

                                    val encryptedFile =
                                        encryptFileAesGcm(context, aesKey, tempFile)

                                    val storage = FirebaseStorage.getInstance()

                                    val storageRef: StorageReference = storage.reference

                                    val fileId = UUID.randomUUID().toString()

                                    val fileRef: StorageReference =
                                        storageRef.child("files/$fileId")

                                    val uploadTask = fileRef.putFile(Uri.fromFile(encryptedFile))

                                    val dateFormat =
                                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val currentDate = dateFormat.format(Date())

                                    uploadTask.continueWithTask { task ->
                                        if (!task.isSuccessful) {
                                            task.exception?.let {
                                                throw it
                                            }
                                        }
                                        fileRef.downloadUrl
                                    }.addOnCompleteListener { downloadUrlTask ->
                                        if (downloadUrlTask.isSuccessful) {
                                            val downloadUri = downloadUrlTask.result

                                            Log.d("ShareScreen", "downloadUri: $downloadUri")

                                            val fileReference = FirebaseDatabase.getInstance()
                                                .getReference("users_private_data")
                                                .child(currentUser.uid).child("shared")
                                                .child(userId!!).child("files").child(fileId)

                                            val fileData = hashMapOf(
                                                "path" to downloadUri.toString(),
                                                "name" to selectedFile?.name,
                                                "type" to selectedFile?.extension,
                                                "date" to currentDate.toString()
                                            )

                                            fileReference.setValue(fileData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Archivo compartido exitosamente con ${selectedUser?.email}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }.addOnFailureListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Error al agregar la ruta del archivo en la base de datos",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error al subir el archivo",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Primero debes generar la llave compartida",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    context,
                                    "Error al obtener la llave compartida",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Selecciona un usuario y un archivo antes de compartir",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Compartir")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShareScreenPreview() {
    Share(modifier = Modifier)
}
