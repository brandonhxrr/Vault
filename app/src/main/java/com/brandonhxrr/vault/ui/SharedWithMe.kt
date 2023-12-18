package com.brandonhxrr.vault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brandonhxrr.vault.R

@Composable()
fun SharedWithMe(modifier: Modifier) {
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

        SharedFile(
            name = "File name",
            date = "Date",
            author = "Author"
        )

        SharedFile(
            name = "File name",
            date = "Date",
            author = "Author"
        )

        SharedFile(
            name = "File name",
            date = "Date",
            author = "Author"
        )

        SharedFile(
            name = "File name",
            date = "Date",
            author = "Author"
        )

        SharedFile(
            name = "File name",
            date = "Date",
            author = "Author"
        )
    }
}

@Composable
fun SharedFile(name: String, date: String, author: String) {
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
                modifier = Modifier.weight(0.2f).size(48.dp)
            )

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Text(
                        text = author,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.download),
                contentDescription = null,
                modifier = Modifier.weight(0.2f).size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharedWithMePreview() {
    SharedWithMe(modifier = Modifier)
}
