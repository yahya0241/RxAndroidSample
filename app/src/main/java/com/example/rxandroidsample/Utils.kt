package com.example.rxandroidsample

import android.text.format.DateFormat
import java.util.*

class Utils {
    companion object{
        fun getDate(timestamp: Long): String {
            val calendar = Calendar.getInstance()
            val tz = TimeZone.getDefault()
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
            calendar.timeInMillis = timestamp
            return DateFormat.format("HH:mm:ss", calendar).toString()
        }
    }
}