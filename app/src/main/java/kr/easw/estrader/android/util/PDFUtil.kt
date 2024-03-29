package kr.easw.estrader.android.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.tom_roush.pdfbox.pdmodel.PDDocument
import kr.easw.estrader.android.BuildConfig
import kr.easw.estrader.android.databinding.FragmentPdfImageviewBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object PDFUtil {
    // URI 생성을 위한 File 객체 생성(Android 29 미만)
    fun createSharedFile(fileName: String): File {
        // 공개 Directory
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "bidSheet"
        )
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }

    // URI 생성을 위한 File 객체 생성(Android 29 미만)
    fun createSharedFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "BidSheet_${timeStamp}.pdf"
        // 공개 Directory
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "bidSheet"
        )
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }

    // URI 생성(Android 29 미만)
    fun createUri(
        context: Context,
        outputFile: File
    ): Uri? {
        // 공유할 File 객체 생성 후, FileProvider.getUriForFile() 에 File 을 넘기고 URL 생성
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            outputFile
        )
    }

    // URI 생성(Android 29 이상)
    @RequiresApi(Build.VERSION_CODES.Q)
    fun createUri(
        context: Context,
        fileName: String
    ): Uri? {
        val uri: Uri?
        try {
            // 추가한 record 의 새로운 URL 리턴
            uri = context.contentResolver.insert(
                // External Storage 의 URI 획득
                MediaStore.Files.getContentUri("external"),
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DOWNLOADS}/bidSheet"
                    )
                }
            )
        } catch (e: IOException) {
            return null
        }
        return uri
    }

    // URI 생성(Android 29 이상)
    @RequiresApi(Build.VERSION_CODES.Q)
    fun contentUri(
        context: Context
    ): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "BidSheet_${timeStamp}.pdf"
        val uri: Uri?
        try {
            // 추가한 record 의 새로운 URL 리턴
            uri = context.contentResolver.insert(
                // External Storage 의 URI 획득
                MediaStore.Files.getContentUri("external"),
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DOWNLOADS}/test"
                    )
                }
            )
        } catch (e: IOException) {
            return null
        }
        return uri
    }

    // 해당 URI 에 대한 출력 스트림 얻고, document.save()으로 PDF 저장
    fun savePdf(
        context: Context,
        uri: Uri, document: PDDocument
    ) {
        try {
            context.contentResolver.openOutputStream(uri).use { outputStream ->
                document.save(outputStream)
                document.close()
            }
        } catch (e: IOException) {
            println("$e 오류가 발생했습니다.")
        }
    }

    // Content Uri 를 File Uri 변경 (DownloadManager 는 Content Uri 사용 불가능)
    fun getFileFromContentUri(
        context: Context,
        contentUri: Uri
    ): File? {
        var filePath: String? = null
        try {
            context.contentResolver.query(
                contentUri,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    filePath = cursor.getString(columnIndex)
                }
            }
        } catch (e: IOException) {
            println("$e 오류가 발생했습니다.")
            return null
        }
        return filePath?.let { File(it) }
    }


    // Android 10 이상은 getExternalStoragePublicDirectory 사용 불가능
    // contentResolver.query 에서 찾은 Content 의 Uri 로 FilePath 가져 오기
    @RequiresApi(Build.VERSION_CODES.Q)
    fun searchPdfFiles(
        context: Context,
        fileName: String
    ): File? {
        val externalUri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        var outputFile: File? = null

        runCatching {
            context.contentResolver.query(
                externalUri,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    val realPath = cursor.getString(columnIndex)
                    outputFile = File(realPath)
                }
            }
        }.onFailure { e ->
            when (e) {
                is IOException -> showToast(context, "오류가 발생하였습니다.")
                else -> showToast(context, "알 수 없는 오류가 발생하였습니다.")
            }
        }
        return outputFile
    }

    // 단순히 파일 찾고 ImageView 로 출력
    fun renderPdfToImageView(
        context: Context,
        outputFile: File,
        imageViewBinding: FragmentPdfImageviewBinding
    ) {
        runCatching {
            println("파일 확인 $outputFile")
            if (outputFile.exists()) {
                // PDF 파일을 렌더링 후 페이지 -> 비트맵 변환
                // 접근 가능한 공통된 저장소 위치에서 이미지를 가져올 경우 ParcelFileDescriptor 를 통해 접근
                PdfRenderer(
                    ParcelFileDescriptor.open(
                        outputFile, ParcelFileDescriptor.MODE_READ_ONLY
                    )
                ).use { pdfRenderer ->
                    rendering(context, pdfRenderer, imageViewBinding)
                }
            }
        }.onFailure { e ->
            when (e) {
                is IOException -> showToast(context, "오류가 발생하였습니다.")
                is OutOfMemoryError -> showToast(context, "오류가 발생하였습니다. 해당 오류는 보고됩니다.")
                else -> showToast(context, "알 수 없는 오류가 발생하였습니다.")
            }
        }
    }

    // BroadcastReceiver() 객체 재사용 위한 싱글톤 패턴 구현
    fun downloadReceiver(
        downloadId: Long,
        manager: DownloadManager
    ) : BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            )
            {
                // The intent has the download ID as an extra,
                // so I use that to again query the DownloadManager and check to see whether it completed successfully.
                val reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if (downloadId == reference) {
                    val query = DownloadManager.Query()
                    query.setFilterById(reference)
                    val status: Int?

                    manager.query(query).use {
                        status = if(it.moveToFirst()) {
                            val columnIndex = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            it.getInt(columnIndex)
                        } else {
                            null
                        }
                        status?.let { status ->
                            checkDownloadStatus(context, status)
                        }
                    }
                }
            }
        }
    }

    private fun rendering(
        context: Context,
        pdfRenderer: PdfRenderer,
        imageViewBinding: FragmentPdfImageviewBinding
    ) {
        try {
            pdfRenderer.openPage(0).use {
                val bitmap =
                    Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
                it.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                imageViewBinding.pdfview.setImageBitmap(bitmap)
            }
        } catch (e: IOException) {
            showToast(context, "오류가 발생하였습니다.")
        }
    }

    private fun checkDownloadStatus(
        context: Context,
        status: Int
    ) {
        when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> showToast(context,"다운로드가 성공했습니다.")
            DownloadManager.STATUS_PAUSED -> showToast(context,"다운로드가 중단되었습니다.")
            DownloadManager.STATUS_FAILED -> showToast(context,"다운로드가 실패했습니다.")
            else -> showToast(context,"오류가 발생했습니다.")
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}