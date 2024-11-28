package com.bethwelamkenya.mynotes.music.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle play/pause actions here
        when (intent?.action) {
            "ACTION_PLAY" -> { // Handle play action
                context?.sendBroadcast(Intent("PLAY"))
            }

            "ACTION_PAUSE" -> { // Handle pause action
                context?.sendBroadcast(Intent("PAUSE"))
            }
        }
    }
}
