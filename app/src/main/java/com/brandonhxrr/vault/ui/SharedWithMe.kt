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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brandonhxrr.vault.R
import com.brandonhxrr.vault.data.SharedFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private fun getSharedFilesFromDatabase(onSuccess: (List<SharedFile>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser // Replace with actual user ID

    val filesReference = database.reference
        .child("users_private_data")
        .child(currentUser!!.uid)
        .child("shared")

    val sharedFiles = mutableListOf<SharedFile>()

    filesReference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (userSnapshot in snapshot.children) {
                val filesNode = userSnapshot.child("files")
                for (fileSnapshot in filesNode.children) {
                    val id = fileSnapshot.key ?: ""
                    val name = fileSnapshot.child("name").getValue(String::class.java) ?: ""
                    val date = fileSnapshot.child("date").getValue(String::class.java) ?: ""
                    val author = fileSnapshot.child("author").getValue(String::class.java) ?: ""
                    val fileURL =
                        fileSnapshot.child("fileURL").getValue(String::class.java) ?: ""
                    val type = fileSnapshot.child("type").getValue(String::class.java) ?: ""

                    val sharedFile = SharedFile(id, name, date, author, fileURL, type)
                    sharedFiles.add(sharedFile)
                }
            }
            onSuccess(sharedFiles)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
}

@Composable
fun SharedWithMe(modifier: Modifier) {
    var sharedFiles by remember { mutableStateOf<List<SharedFile>>(emptyList()) }

    LaunchedEffect(Unit) {
        getSharedFilesFromDatabase {
            sharedFiles = it
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Shared with me",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyColumn {
            items(sharedFiles) { file ->
                SharedFile(file)
            }
        }
    }
}

@Composable
fun SharedFile(sharedFile: SharedFile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.file),
                contentDescription = null,
                modifier = Modifier
                    .weight(0.2f)
                    .size(48.dp)
            )

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = sharedFile.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = sharedFile.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Text(
                        text = sharedFile.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.download),
                contentDescription = null,
                modifier = Modifier
                    .weight(0.2f)
                    .size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharedWithMePreview() {
    SharedWithMe(modifier = Modifier)
}
