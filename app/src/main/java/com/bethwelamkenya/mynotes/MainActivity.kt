package com.bethwelamkenya.mynotes

import android.content.Context
import android.content.Intent
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bethwelamkenya.mynotes.music.MusicActivity
import com.bethwelamkenya.mynotes.notes.NotesActivity
import com.bethwelamkenya.mynotes.ui.components.CustomButton
import com.bethwelamkenya.mynotes.ui.theme.MyNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyNotesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(this, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(context: Context, modifier: Modifier = Modifier) {
    var shouldRequestPermission by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically)
    ) {
        CustomButton(onClick = {
            val intent = Intent(context, NotesActivity::class.java)
            context.startActivity(intent)
        }, text = "Notes")

        CustomButton(text = "Music") {
            shouldRequestPermission = true
        }
    }

    if (shouldRequestPermission) {
        RequestStoragePermission(
            onPermissionGranted = {
                val intent = Intent(context, MusicActivity::class.java)
                context.startActivity(intent)
            },
            onPermissionDenied = {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        )
        shouldRequestPermission = false
    }
}

@Composable
fun RequestStoragePermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val permissionGranted = remember { mutableStateOf(false) }

    // Create a permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            if (it.key == Manifest.permission.READ_MEDIA_AUDIO || it.key == Manifest.permission.READ_EXTERNAL_STORAGE) {
                permissionGranted.value = it.value
            }
        }
        if (permissionGranted.value) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_AUDIO))
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyNotesTheme {
        Greeting(MainActivity())
    }
}