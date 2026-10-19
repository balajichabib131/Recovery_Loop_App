package com.example.jetpackcompose.jetpackcompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RowExample() {

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Apple", fontSize = 30.sp)
        Text(text = "Banna", fontSize = 30.sp)
        Text(text = "Grapes", fontSize = 30.sp)
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ColumnExample() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(text = "Book 1", fontSize = 30.sp)
        Text(text = "Book 2", fontSize = 30.sp)
        Text(text = "Book 3", fontSize = 30.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ColumnExample2() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Login", fontSize = 30.sp)

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(text = "Login ID") }
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(text = "Password") }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BoxExample() {

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Note 1",
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Text(
            text = "Note 2",
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        Text(
            text = "Note 3",
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

}