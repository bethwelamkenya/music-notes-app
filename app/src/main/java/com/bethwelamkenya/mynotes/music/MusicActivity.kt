package com.bethwelamkenya.mynotes.music

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import com.bethwelamkenya.mynotes.R
import com.bethwelamkenya.mynotes.music.models.Song
import com.bethwelamkenya.mynotes.music.services.MusicNotificationReceiver
import com.bethwelamkenya.mynotes.music.ui.theme.MyNotesTheme
import com.bethwelamkenya.mynotes.ui.components.CustomClickableImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class MusicActivity : ComponentActivity() {
    var mediaPlayer by mutableStateOf<MediaPlayer?>(null)
    var playingSong by mutableStateOf<Song?>(null)

    private lateinit var songs: List<Song>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyNotesTheme {
                songs = getMusicFiles(this)

                // Register the receiver to handle play/pause actions
                val filter = IntentFilter().apply {
                    addAction("PLAY")
                    addAction("PAUSE")
                }

                registerReceiver(
                    this,
                    playPauseReceiver,
                    filter,
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MusicPlayerScaffold(
                        context = this,
                        modifier = Modifier.padding(innerPadding),
                        songs = songs,
                        mediaPlayer = mediaPlayer,
                        playingSong = playingSong,
                        onMediaPlayerChange = {
                            mediaPlayer = null
                            mediaPlayer = it
                        },
                        onSongChange = {
                            mediaPlayer = null
                            playingSong = it
                        }
                    )
                }
            }
        }
        createNotificationChannel(context = this)
    }


    val playPauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                "PLAY" -> {
                    mediaPlayer?.start()
                    showMusicNotification(context, playingSong!!, mediaPlayer?.isPlaying == true)
                }

                "PAUSE" -> {
                    mediaPlayer?.pause()
                    showMusicNotification(context, playingSong!!, mediaPlayer?.isPlaying == true)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(playPauseReceiver)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScaffold(
    context: Context,
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayer?,
    playingSong: Song?,
    onMediaPlayerChange: (MediaPlayer) -> Unit,
    onSongChange: (Song) -> Unit,
    songs: List<Song>
) {
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
                        song = playingSong,
                        mediaPlayer = mediaPlayer,
                        onMediaPlayerChange = {
                            onMediaPlayerChange(
                                it
                            )
                        },
                        onNext = {
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
//                                mediaPlayer?.reset()
//                            onMediaPlayerChange(null)
//                            mediaPlayer = null
//                            playingSong = songs[(songs.indexOf(playingSong) + 1) % songs.size]
                            onSongChange(songs[(songs.indexOf(playingSong) + 1) % songs.size])
                        },
                        onPrevious = {
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
//                                mediaPlayer?.reset()
//                            mediaPlayer = null
//                            playingSong = songs[(songs.indexOf(playingSong) - 1 + songs.size) % songs.size]
                            onSongChange(songs[(songs.indexOf(playingSong) - 1 + songs.size) % songs.size])
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
//                                playingSong = songs[0]
                                onSongChange(songs[0])
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
                                mediaPlayer?.release()
//                                mediaPlayer?.reset()
//                                mediaPlayer = null
//                                playingSong = song
                                onSongChange(song)
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
    onMediaPlayerChange: (MediaPlayer) -> Unit,
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    width: Dp,
    state: SheetValue
) {
    var progress by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    // Register the receiver
    DisposableEffect(Unit) {
        val filter = IntentFilter().apply {
            addAction("PLAY")
            addAction("PAUSE")
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                when (intent?.action) {
                    "PLAY" -> {
                        mediaPlayer?.start()
                        showMusicNotification(context, song, true)
                    }

                    "PAUSE" -> {
                        mediaPlayer?.pause()
                        showMusicNotification(context, song, false)
                    }
                }
            }
        }
        context.registerReceiver(
            receiver, filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose {
            context.unregisterReceiver(
                receiver
            )
        }
    }

    LaunchedEffect(song.uri) {
        mediaPlayer?.release()
        onMediaPlayerChange(
            MediaPlayer().apply {
                setDataSource(context, android.net.Uri.parse(song.uri))
                prepare()
                start()
                setOnCompletionListener { onNext() }
            }
        )
    }

    LaunchedEffect(mediaPlayer?.isPlaying) {
        if (mediaPlayer?.isPlaying == true) {
            coroutineScope.launch {
                try {
                    while (mediaPlayer.isPlaying) {
                        mediaPlayer.let { player ->
                            if (player.isPlaying) {
                                progress = try {
                                    player.currentPosition / player.duration.toFloat()
                                } catch (e: IllegalStateException) {
                                    0f
                                }
                            }
                        }
                        delay(1000L) // Update progress every second
                    }
                } catch (_: Exception) {
                }
            }
        }
    }
    // Update the notification whenever the song or play state changes
    LaunchedEffect(song.uri, mediaPlayer?.isPlaying) {
        showMusicNotification(
            context = context,
            song = song,
            isPlaying = mediaPlayer?.isPlaying == true
        )
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

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = getLength(mediaPlayer?.currentPosition?.toLong() ?: 0),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.1F)
            )
            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    progress = newProgress
                    mediaPlayer?.let { player ->
                        try {
                            player.seekTo((newProgress * player.duration).toInt())
                        } catch (e: IllegalStateException) {
                            // Handle the exception if needed
                        }
                    }
                },
                modifier = Modifier
                    .weight(0.8F)
                    .height(15.dp)
            )
            Text(
                text = getLength(mediaPlayer?.duration?.toLong() ?: 0),
                textAlign = TextAlign.Center,
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
                image = if (mediaPlayer?.isPlaying == true) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
                text = "Play/Pause"
            ) {
                // Play/Pause logic
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer?.start()
                }
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

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Music Player"
        val descriptionText = "Channel for music player notifications"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel("music_player_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showMusicNotification(context: Context, song: Song, isPlaying: Boolean) {
    val playPauseIcon =
        if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
    val playPauseAction = if (isPlaying) "ACTION_PAUSE" else "ACTION_PLAY"

    val playPauseIntent = Intent(context, MusicNotificationReceiver::class.java).apply {
        action = playPauseAction
    }
    val playPausePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        playPauseIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val intent = Intent(context, MusicActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "music_player_channel")
        .setSmallIcon(R.drawable.baseline_music_note_24)
        .setContentTitle("Now Playing")
        .setContentText(song.title)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOnlyAlertOnce(true)
        .addAction(playPauseIcon, if (isPlaying) "Pause" else "Play", playPausePendingIntent)
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
        .build()

    NotificationManagerCompat.from(context).notify(1, notification)
}


fun showMusicNotification1(context: Context, song: Song, isPlaying: Boolean) {
    val playPauseIcon =
        if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
    val playPauseAction = if (isPlaying) "ACTION_PAUSE" else "ACTION_PLAY"

    val playPauseIntent = Intent(context, MusicNotificationReceiver::class.java).apply {
        action = playPauseAction
    }
    val playPausePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        playPauseIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val intent = Intent(context, MusicActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent =
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val notification = NotificationCompat.Builder(context, "music_player_channel")
        .setSmallIcon(R.drawable.baseline_music_note_24)
        .setContentTitle("Now Playing")
        .setContentText(song.title)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOnlyAlertOnce(true)
        .addAction(playPauseIcon, playPauseAction, pendingIntent)
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
        .build()

    NotificationManagerCompat.from(context).notify(1, notification)
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
    } else if (min > 0) {
        String.format(Locale.US, "%d:%02d", min, sec) // Show minutes if greater than 0
    } else {
        String.format(Locale.US, ":%02d", sec) // Show only seconds with leading colon
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MyNotesTheme {
//        MusicPlayerScaffold(
//            context = MusicActivity(),
//            mediaPlayer = mediaPlayer,
//            playingSong = playingSong,
//            onMediaPlayerChange = {
//                mediaPlayer = it
//            },
//            onSongChange = {
//                playingSong = it
//            },
//            songs = songs
//        )
    }
}