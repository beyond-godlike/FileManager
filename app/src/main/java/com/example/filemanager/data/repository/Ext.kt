package com.example.filemanager.data.repository

import android.annotation.SuppressLint
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Ext {
    companion object {
        fun longToLocalDate(timestamp: Long): LocalDate {
            val instant = Instant.fromEpochSeconds(timestamp)
            instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            return instant.toLocalDateTime(TimeZone.UTC).date
        }

        @SuppressLint("DefaultLocale")
        fun formatSize(sizeInBytes: Long): String {
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            var unitIndex = 0

            var size = sizeInBytes.toDouble()
            while (size > 1024) {
                size /= 1024
                unitIndex++
            }

            return String.format("%.2f %s", size, units[unitIndex])
        }
    }
}