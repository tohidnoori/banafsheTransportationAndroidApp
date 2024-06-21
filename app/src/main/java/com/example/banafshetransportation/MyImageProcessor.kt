package com.example.banafshetransportation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
class MyImageProcessor private constructor() {

    companion object {
        val instance:MyImageProcessor by lazy {
            MyImageProcessor()
        }
    }

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(filePathColumn[0])
            val filePath = it.getString(columnIndex)
            it.close()
            return filePath
        }
        return null
    }

     fun resizeImage(context: Context,file: File): File {

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        // Calculate the inSampleSize to reduce the image size while maintaining aspect ratio
        options.inSampleSize = calculateInSampleSize(options, 1024, 1024) // Adjust the dimensions as needed
        // Decode the image with the calculated inSampleSize
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(file.path, options)

        val tempDir = File(context.externalCacheDir, "tempDir")
        if (!tempDir.exists()) {
            tempDir.mkdir()
        }
        val tempFile = File(tempDir, file.name+ "tempImage.jpg")

        try {
            // Compress the bitmap and save it to the file
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) // Adjust the quality as needed
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return tempFile
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}