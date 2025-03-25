package com.example.filemanager.data

enum class FileType(val ext: String, val mimeType: String) {
    JPG("jpg", "image/*"),
    JPEG("jpeg", "image/*"),
    PNG("png", "image/*"),
    WEBP("webp", "image/*"),
    TIFF("tiff", "image/*"),
    TIF("tif", "image/*"),
    GIF("gif", "image/*"),
    MP4("mp4", "video/*"),
    AVI("avi", "video/*"),
    MP3("mp3", "audio/*"),
    PDF("pdf", "application/pdf"),
    TXT("txt", "text/plain")
}