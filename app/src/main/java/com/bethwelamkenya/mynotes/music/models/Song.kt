package com.bethwelamkenya.mynotes.music.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Song(
    val id: Int,
    val title: String,
    val duration: Long,
    val date: Date,
    val uri: String = ""
) {
    constructor(title: String, duration: Long, uri: String) : this(id = 0, title, duration, date = Date(), uri = uri)

    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return formatter.format(
            date
        )
    }
}
