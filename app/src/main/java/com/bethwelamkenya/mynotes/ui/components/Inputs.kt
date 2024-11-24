package com.bethwelamkenya.mynotes.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bethwelamkenya.mynotes.R
import com.bethwelamkenya.mynotes.music.models.Song

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
    }
}

@Composable
fun Other(modifier: Modifier = Modifier, songs: List<Song>, playingSong: Song) {
    Column {
        Row {
            Text(text = (songs.indexOf(playingSong) + 1).toString())
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = playingSong.title)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomClickableImage(
                image = R.drawable.baseline_skip_previous_24,
                text = "Previous Song"
            ) {

            }
            CustomClickableImage(image = R.drawable.baseline_replay_10_24, text = "Rewind") {

            }
            CustomClickableImage(image = R.drawable.baseline_play_arrow_24, text = "Play/Pause") {

            }
            CustomClickableImage(image = R.drawable.baseline_forward_10_24, text = "Forward") {

            }
            CustomClickableImage(image = R.drawable.baseline_skip_next_24, text = "Next") {

            }
        }
    }
}

@Composable
fun CustomButtonWithImage(
    modifier: Modifier = Modifier,
    text: String = "Button",
    image: Int,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Image(painter = painterResource(image), contentDescription = text)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text)
    }
}

@Composable
fun CustomClickableImage(
    modifier: Modifier = Modifier,
    text: String = "Button",
    image: Int,
    onClick: () -> Unit,
) {
    Image(
        painter = painterResource(image),
        contentDescription = text,

        modifier = modifier
            .padding(5.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4F))
            .clickable { onClick() }
            .padding(5.dp)
    )
}

@Preview(name = "Inputs")
@Composable
private fun PreviewInputs() {
    CustomButton()
}