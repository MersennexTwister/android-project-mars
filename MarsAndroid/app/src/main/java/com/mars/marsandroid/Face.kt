package com.mars.marsandroid

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream


class Face(_success: Int, _process_id: Int, _name: String, _btm: String) {

    var success = _success
    var process_id = _process_id
    var name = _name
    var btm = _btm

    public fun getBitmap(): Bitmap {
        return FaceBuilder.getBitmapFromString(btm);
    }
}

class FaceList(_list: ArrayDeque<Face>) {
    var t_process = 0
    var list = _list

    fun add(_btm: Bitmap): Int {
        list.addLast(Face(0, t_process, "", FaceBuilder.getStringFromBitmap(_btm)))
        val toRet = t_process
        t_process = (t_process + 1) % FaceBuilder.MAX_PROCESS
        return toRet
    }
}

object FaceBuilder {
    var MAX_SIZE = 10
    var MAX_PROCESS = 100000

    public fun getStringFromBitmap(bitmapPicture: Bitmap): String {
        val COMPRESSION_QUALITY = 100
        val encodedImage: String
        val byteArrayBitmapStream = ByteArrayOutputStream()
        bitmapPicture.compress(
            Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
            byteArrayBitmapStream
        )
        val b = byteArrayBitmapStream.toByteArray()
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
        return encodedImage
    }

    public fun getBitmapFromString(stringPicture: String): Bitmap {
        val decodedString =
            Base64.decode(stringPicture, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}