package com.bethwelamkenya.mynotes.notes.models

import java.util.Date

data class Note(
    val id: Int,
    val title: String,
    val note: String,
    val date: Date,
) {
    constructor(title: String, note: String) : this(id = 0, title, note, date = Date())
}
