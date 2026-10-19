package com.example.jetpackcompose.jetpackcompose

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//@Preview(showBackground = true, showSystemUi = true , backgroundColor = black.toLong())
@Composable
fun TextExample() {
    Text(
        text = "Mission Android 2026",
        color = Blue,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        letterSpacing = 1.sp,
        textAlign = Center,
    )
}


//@Preview(showBackground = true, showSystemUi = true , backgroundColor = black.toLong())
@Composable
fun TextFieldExample() {

    var name by remember { mutableStateOf("") }
    TextField(
        value = name,
        onValueChange = { name = it },
        //label = { Text(text = "Enter Your Name") }, //Type Karney pe label upar aayega
        placeholder = { Text(text = "Enter Your Name") }, //Type Karney pe label chala jayega
        leadingIcon = {
            Text(
                text = "*",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        },
        trailingIcon = {
            Text(
                text = "!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        singleLine = true,
        shape = CircleShape,
        colors = colors(
            unfocusedTextColor = Red,
            focusedTextColor = Green,
            unfocusedContainerColor = Gray,
            focusedContainerColor = Magenta,
            unfocusedIndicatorColor = Black,
            focusedIndicatorColor = Blue,
            cursorColor = Magenta,
            unfocusedPlaceholderColor = Yellow,
            focusedPlaceholderColor = DarkGray,
            unfocusedLeadingIconColor = Black,
            focusedLeadingIconColor = Blue,
            unfocusedTrailingIconColor = Black,
            focusedTrailingIconColor = Blue,
            unfocusedLabelColor = Black,
            focusedLabelColor = Blue,
        )
    )
}

@Preview (showBackground = true, showSystemUi = true)
@Composable
fun OutlinedTextFieldExample() {

    var name by remember { mutableStateOf("") }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text(text = "Enter Your Name" ) },
        leadingIcon = {
            Text(
                text = "*",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        },
        trailingIcon = {
            Text(
                text = "!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = Red,
            focusedTextColor = Green,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Gray,
            focusedBorderColor = Blue,
            unfocusedBorderColor = Red,
            cursorColor = Magenta,
            unfocusedPlaceholderColor = Yellow,
            focusedPlaceholderColor = DarkGray,
            unfocusedLeadingIconColor = Black,
            focusedLeadingIconColor = Blue,
            unfocusedTrailingIconColor = Black,
            focusedTrailingIconColor = Blue,
            unfocusedLabelColor = Black,
            focusedLabelColor = Blue,
        )
    )
}