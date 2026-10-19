package com.example.jetpackcompose.jetpackcompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ImageExample() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(

            painter = painterResource(R.drawable.girl_in_manga_v3),
            contentDescription = "Girl in Manga",
            modifier = Modifier
                .padding(16.dp)
                .size(size = 400.dp)
                .clip(RoundedCornerShape(size = 16.dp)),
            contentScale = ContentScale.FillBounds

        )
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IconExample() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Person Icon",
            modifier = Modifier.size(size = 50.dp),
            tint = Color.DarkGray
        )

    }
}