/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.toxsl.restfulClient.api

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CreateMultipartListMap(val context: Context) {
    fun prepareFilePart(fileHashMap: HashMap<String, Uri>): MutableList<MultipartBody.Part>? {

        val list: MutableList<MultipartBody.Part>? = ArrayList()
        for (mutableEntry in fileHashMap) {
            mutableEntry.value
            // use the FileUtils to get the actual file by uri

            val file = File(getPathFromUri(mutableEntry.value))
            // create RequestBody instance from file
            val requestFile = RequestBody.create(
                    context.contentResolver.getType(mutableEntry.value)!!.toMediaTypeOrNull(),
                    file
            )
            // MultipartBody.Part is used to send also the actual file name
            list?.add(
                    MultipartBody.Part.createFormData(
                            mutableEntry.key, file.getName(), requestFile
                    )
            )
        }
        return list

    }


    @SuppressLint("Recycle")
    fun getPathFromUri(uri: Uri): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        return cursor.getString(columnIndex)
    }


}