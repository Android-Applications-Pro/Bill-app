package com.example.billapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billapp.models.User

@Composable
fun UserAvatar(user: User) {
    val avatarResource = if (user.id == "2") {
        R.drawable.avatar_placeholder_2
    } else {
        R.drawable.avatar_placeholder
    }

    Image(
        painter = painterResource(id = avatarResource),
        contentDescription = user.name,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
@Preview(showBackground = true)
@Composable
fun UserAvatarPreview() {
    UserAvatar(user = User(id = "1", name = "John Doe"))
}