package com.example.kotlininstagramfirebasechat.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ansh on 13/10/18.
 */
object DateUtils {

    private val fullFormattedTime = SimpleDateFormat("d MMM, h:mm a", Locale.US)
    private val onlyDate = SimpleDateFormat("d MMM", Locale.US)
    private val onlyTime = SimpleDateFormat("h:mm a", Locale.US)

    fun getFormattedTime(timeInMilis: Long): String {
        val date = Date(timeInMilis)

        return when {
            isToday(date) -> onlyTime.format(date)
            isYesterday(date) -> "Yesterday"
            else -> onlyDate.format(date)
        }

    }

    fun getFormattedTimeChatLog(timeInMilis: Long): String {
        val date = Date(timeInMilis)

        return when {
            isToday(date) -> onlyTime.format(date)
            else -> fullFormattedTime.format(date)
        }

    }

    private fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    private fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }

}