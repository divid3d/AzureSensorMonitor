package com.example.azuresensormonitor

import android.os.SystemClock
import java.text.SimpleDateFormat
import java.util.*


val Long.getTimeString: String
    get() {
        val simpleDateFormat =
            SimpleDateFormat("dd-MM-yyy HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val refTime =
            System.currentTimeMillis() + ((this - SystemClock.elapsedRealtimeNanos()) / 1_000_000L)
        return simpleDateFormat.format(Date((refTime)))
    }