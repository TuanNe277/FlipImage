package com.example.flipimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class newImage : AppCompatActivity() {
    var selectedUri: String? = null
    var bitmap: Bitmap? = null
    var scaledBitMap:Bitmap?=null
    var newBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_image)

        pickImageFromGallery()

        val btnLuu: ImageButton = findViewById(R.id.luu)
//        //lat
        val latNgan: ImageButton = findViewById(R.id.latNgan)
        var xet = true
        latNgan.setOnClickListener(View.OnClickListener {
            val linearLayoutTrong: LinearLayout = findViewById(R.id.linearLayoutTrong)
            linearLayoutTrong.setScaleY(-1f)
        })

        //xoay
        val xoay: ImageButton = findViewById(R.id.xoay)
        var angle = 0
        val image_view: ImageView = findViewById(R.id.image_view)
        xoay.setOnClickListener(View.OnClickListener {

            val filename: String = selectedUri!!.substring(selectedUri!!.lastIndexOf("/") + 1)
            Log.d("Duong anh", selectedUri.toString())
            Log.d("Duong anh2", filename)

            angle += 90
            Log.d("Duong anh2", angle.toString())

            image_view.setRotation(angle.toFloat())

        })
//        btnLuu.setOnClickListener {
//            Log.d("a","Luu")
//            val matric: Matrix? = null
//            matric?.postScale()
//            matric?.postRotate(90F)
//            scaledBitMap = Bitmap.createScaledBitmap(bitmap!!,bitmap!!.width,bitmap!!.height,true)
//            newBitmap = Bitmap.createBitmap(scaledBitMap!!, 0, 0, scaledBitMap!!.width, scaledBitMap!!.height, matric, true)
//            bitmapToFile(newBitmap!!, Math.random().toString())
//        }

        //btn click
        val img_pick_btn: ImageButton = findViewById(R.id.img_pick_btn)
        img_pick_btn.setOnClickListener {
            //check runtime permission
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ),
                1
            )
            if (Environment.isExternalStorageManager()) {

            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri: Uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            pickImageFromGallery()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                    //permission denied
//                    var permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                    //show popup to request runtime permission
//                    requestPermissions(permissions, PERMISSION_CODE)
//
//                } else {
//                    //permission already grated
//                    pickImageFromGallery();
//                }
//
//            } else {
//                //system OS is <= Marshmallow
//                pickImageFromGallery();
//            }
        }
    }



    private fun pickImageFromGallery() {
        //Intent to pick image


        val gallaryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(gallaryIntent, 1)

    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1;

        //Permission code
        private val PERMISSION_CODE = 1001;

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Quyền bị từ chối!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && resultCode == IMAGE_PICK_CODE){
        val contentUri = data!!.data
        val image_view: ImageView = findViewById(R.id.image_view)
//        Toast.makeText(this, " ${image_view.equals("sq")}", Toast.LENGTH_SHORT).show()
        selectedUri = getRealPathFromURIAPI19(this, contentUri!!)
        //hủy xoay
        image_view.invalidate()
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
        var angle = 0
        image_view.setRotation(angle.toFloat())

        image_view.setImageURI(contentUri)

    }

    @SuppressLint("NewApi")
    private fun getRealPathFromURIAPI19(context: Context, uri: Uri): String? {
        Log.d("GetPath", uri.toString())
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )
                    cursor!!.moveToNext()
                    val fileName = cursor.getString(0)
                    val path = Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                    if (!TextUtils.isEmpty(path)) {
                        return path
                    }
                } finally {
                    cursor?.close()
                }
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads"),
                    java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + fileNameToSave + ".png"
            )
            Log.d("File",file.toString())
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
}