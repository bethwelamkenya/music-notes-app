package com.bethwelamkenya.mynotes.music

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bethwelamkenya.mynotes.R
import com.bethwelamkenya.mynotes.music.models.Song
import com.bethwelamkenya.mynotes.music.ui.theme.MyNotesTheme
import com.bethwelamkenya.mynotes.ui.components.CustomClickableImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class MusicActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyNotesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MusicPlayerScaffold(
                        context = this,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScaffold(context: Context, modifier: Modifier = Modifier) {
    val songs = getMusicFiles(context = context)
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            false,
            Density(1F),
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val sheetPeekHeight = screenHeight * 0.15f // 15% of the screen height
    val imageButtonWidth = screenWidth * 0.10f // 10% of the screen width

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var playingSong by remember { mutableStateOf<Song?>(null) }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = bottomSheetScaffoldState,
        sheetContent =
        {
            val myModifier =
                if (bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                    Modifier.fillMaxHeight()
                } else {
                    Modifier.height(sheetPeekHeight)
                }
            Column(
                modifier = myModifier
                    .fillMaxWidth()
            ) {
                if (playingSong != null) {
                    MusicPlayerScreen(
                        modifier = myModifier,
                        context = context,
                        song = playingSong!!,
                        mediaPlayer = mediaPlayer,
                        isPlaying = isPlaying,
                        onMediaPlayerChange = { mediaPlayer = it },
                        onIsPlayingChange = { isPlaying = it },
                        onNext = {
                            isPlaying = false
                            mediaPlayer?.stop()
//                            mediaPlayer?.release()
                            mediaPlayer?.reset()
                            playingSong = songs[(songs.indexOf(playingSong) + 1) % songs.size]
                        },
                        onPrevious = {
                            isPlaying = false
                            mediaPlayer?.stop()
//                            mediaPlayer?.release()
                            mediaPlayer?.reset()
                            playingSong =
                                songs[(songs.indexOf(playingSong) - 1 + songs.size) % songs.size]
                        },
                        width = imageButtonWidth,
                        state = bottomSheetScaffoldState.bottomSheetState.currentValue
                    )
                } else {
                    Column(
                        modifier = myModifier,
                        verticalArrangement = if (bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) Arrangement.Center else Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No song is playing",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CustomClickableImage(
                                modifier = Modifier.size(imageButtonWidth),
                                image = R.drawable.baseline_skip_previous_24,
                                text = "Previous Song"
                            ) {
                                // Previous song logic
                            }
                            CustomClickableImage(
                                modifier = Modifier.size(imageButtonWidth),
                                image = R.drawable.baseline_replay_10_24,
                                text = "Rewind"
                            ) {
                                // Rewind logic
                            }
                            CustomClickableImage(
                                modifier = Modifier.size(imageButtonWidth),
                                image = R.drawable.baseline_play_arrow_24,
                                text = "Play/Pause"
                            ) {
                                // Play/Pause logic
                                playingSong = songs[0]
                            }
                            CustomClickableImage(
                                modifier = Modifier.size(imageButtonWidth),
                                image = R.drawable.baseline_forward_10_24,
                                text = "Forward"
                            ) {
                                // Forward logic
                            }
                            CustomClickableImage(
                                modifier = Modifier.size(imageButtonWidth),
                                image = R.drawable.baseline_skip_next_24,
                                text = "Next"
                            ) {
                                // Next song logic
                            }
                        }
                    }
                }
            }
        },
        sheetPeekHeight = sheetPeekHeight,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (songs.isEmpty()) {
                Text(
                    text = "No songs found",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                return@Column
            }
            LazyColumn {
                itemsIndexed(items = songs) { index, song ->
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4F))
                            .clickable {
                                mediaPlayer?.stop()
//                                mediaPlayer?.release()
                                mediaPlayer?.reset()
                                isPlaying = false
                                playingSong = song
                                // Expand the bottom sheet when a song is clicked
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                }
                            }
                            .padding(5.dp)
                    ) {
                        Row {
                            Text(
                                text = (index + 1).toString() + " | ",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = song.title)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = getLength(song.duration), fontWeight = FontWeight.Bold)
                            Text(text = song.getFormattedDate())
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(
    modifier: Modifier = Modifier,
    context: Context,
    song: Song,
    mediaPlayer: MediaPlayer?,
    isPlaying: Boolean,
    onMediaPlayerChange: (MediaPlayer) -> Unit,
    onIsPlayingChange: (Boolean) -> Unit,
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    width: Dp,
    state: SheetValue
) {
    var progress by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(song.uri) {
        mediaPlayer?.release()
        onMediaPlayerChange(
            MediaPlayer().apply {
                setDataSource(context, android.net.Uri.parse(song.uri))
                prepare()
                setOnCompletionListener { onNext() }
                start()
            }
        )
        onIsPlayingChange(true)
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            coroutineScope.launch {
                while (mediaPlayer != null) {
                    suspend fun updatePosition() {
                        mediaPlayer.let { player ->
                            // TODO: fix this
                            if (player.isPlaying){
                                progress = player.currentPosition / player.duration.toFloat()
                            }
                        }
                        delay(1000L) // Update progress every second
                    }
                       updatePosition()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp),
        verticalArrangement = if (state == SheetValue.Expanded) Arrangement.Center else Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state == SheetValue.Expanded)
            Text(
                text = song.title,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            )
        else
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            )

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = getLength(mediaPlayer?.currentPosition?.toLong() ?: 0),
                modifier = Modifier.weight(0.1F)
            )
            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    progress = newProgress
                    mediaPlayer?.seekTo((newProgress * mediaPlayer.duration).toInt())
                },
                modifier = Modifier
                    .weight(0.8F)
                    .height(20.dp)
            )
            Text(
                text = getLength(mediaPlayer?.duration?.toLong() ?: 0),
                modifier = Modifier.weight(0.1F)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomClickableImage(
                modifier = Modifier.size(width),
                image = R.drawable.baseline_skip_previous_24,
                text = "Previous Song"
            ) {
                // Previous song logic
                progress = 0F
                onPrevious()
            }
            CustomClickableImage(
                modifier = Modifier.size(width),
                image = R.drawable.baseline_replay_10_24,
                text = "Rewind"
            ) {
                // Rewind logic
                mediaPlayer?.seekTo(mediaPlayer.currentPosition - 10000)
            }
            CustomClickableImage(
                modifier = Modifier.size(width),
                image = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
                text = "Play/Pause"
            ) {
                // Play/Pause logic
                if (isPlaying) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                }
//                isPlaying = !isPlaying
                onIsPlayingChange(!isPlaying)
            }
            CustomClickableImage(
                modifier = Modifier.size(width),
                image = R.drawable.baseline_forward_10_24,
                text = "Forward"
            ) {
                // Forward logic
                mediaPlayer?.seekTo(mediaPlayer.currentPosition + 10000)
            }
            CustomClickableImage(
                modifier = Modifier.size(width),
                image = R.drawable.baseline_skip_next_24,
                text = "Next"
            ) {
                // Next song logic
                progress = 0F
                onNext()
            }
        }
    }
}


@Composable
fun MediaPlayerControl(context: Context, songUri: String) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(songUri) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, android.net.Uri.parse(songUri))
            prepare()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(onClick = {
            if (isPlaying) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
            isPlaying = !isPlaying
        }) {
            Text(text = if (isPlaying) "Pause" else "Play")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            mediaPlayer?.seekTo(0)
            mediaPlayer?.pause()
            isPlaying = false
        }) {
            Text(text = "Stop")
        }
    }
}


fun getMusicFiles(context: Context): List<Song> {
    val musicFiles = mutableListOf<Song>()
    val contentResolver = context.contentResolver
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATA,
    )
    val cursor = contentResolver.query(uri, projection, null, null, null)

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dateColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
        val uriColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

        while (it.moveToNext()) {
            val id = it.getInt(idColumn)
            val title = it.getString(titleColumn)
            val duration = it.getLong(durationColumn)
            val dateAdded = it.getLong(dateColumn) * 1000L // Convert to milliseconds
            val songUri = it.getString(uriColumn)

            val song = Song(
                id = id,
                title = title,
                duration = duration,
                date = Date(dateAdded),
                uri = songUri
            )
            musicFiles.add(song)
        }
    }
    return musicFiles
}

fun getLength(duration: Long): String {
    val hrs = (duration / 3600000).toInt()
    val min = ((duration % 3600000) / 60000).toInt()
    val sec = (((duration % 3600000) % 60000) / 1000).toInt()

    return if (hrs > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hrs, min, sec)
    } else {
        String.format(Locale.US, "%02d:%02d", min, sec).trimStart { it == '0' }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MyNotesTheme {
        MusicPlayerScaffold(context = MusicActivity())
    }
}