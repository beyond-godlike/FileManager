package com.example.filemanager.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Uri.open(context: Context, mimeType: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(this@open, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(intent)
}