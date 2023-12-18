package com.brandonhxrr.vault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brandonhxrr.vault.data.User
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable()
fun Employees(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Employees",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Employee(
            user = User(
                name = "Brandon Herrera",
                email = "brandondh769@gmail.com",
                photoURL = "https://avatars.githubusercontent.com/u/44008476?v=4", publicKey = ""
            )
        )


    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Employee(user: User) {
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
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                // Correo electrónico del usuario
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = { /* Acción al hacer clic en el botón */ },
                modifier = Modifier
                    .weight(0.2f)
                    .size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.Lock,
                    contentDescription = "Lock Icon",
                    tint = Color.Gray
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EmployeesPreview() {
    Employees(modifier = Modifier)
}
