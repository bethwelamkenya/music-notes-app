package com.bethwelamkenya.mynotes.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bethwelamkenya.mynotes.notes.models.Note
import com.bethwelamkenya.mynotes.notes.ui.theme.MyNotesTheme

class NotesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyNotesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting3(
                        name = "Notes",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    val notes: ArrayList<Note> = arrayListOf(
        Note("title", "note\nnote"),
        Note("title", "note"),
        Note("title", "note"),
        Note("title", "note"),
        Note("title", "note\nnote"),
    )
    Column(modifier = modifier) {
        Text(
            text = "Hello $name!"
        )
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(200.dp)) {
            itemsIndexed(items = notes) { index, note ->
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4F))
                        .padding(5.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = (index + 1).toString(), fontWeight = FontWeight.Bold)
                        Text(text = note.title, fontWeight = FontWeight.Bold)
                    }
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                            .width(intrinsicSize = IntrinsicSize.Max)
                            .background(color = MaterialTheme.colorScheme.onBackground)
                    )
                    Text(text = note.note)
                    Spacer(
                        modifier = Modifier
                            .height(1.dp)
                            .background(color = MaterialTheme.colorScheme.onBackground)
                    )
                    Text(text = note.date.toString(), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    MyNotesTheme {
        Greeting3("Android")
    }
}