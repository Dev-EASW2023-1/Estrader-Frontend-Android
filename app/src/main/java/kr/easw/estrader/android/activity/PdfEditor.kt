package kr.easw.estrader.android.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import kr.easw.estrader.android.BuildConfig
import kr.easw.estrader.android.databinding.FragmentDelegateitemBinding
import kr.easw.estrader.android.databinding.FragmentPdfviewBinding
import java.io.File
import java.io.IOException

/**
 * Copyright [2023] [Nam Jae Gyeong]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

class PdfEditor : AppCompatActivity() {

    private lateinit var binding: FragmentPdfviewBinding
    private lateinit var binding2: FragmentDelegateitemBinding

    companion object {
        // TODO("좌표를 상수로 저장")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPdfviewBinding.inflate(layoutInflater)
        binding2 = FragmentDelegateitemBinding.inflate(layoutInflater)
        PDFBoxResourceLoader.init(applicationContext)
        editPdf()
    }

    private fun editPdf() {
        try {

            val inputStream = assets.open("기일입찰표.pdf")
            val document = PDDocument.load(inputStream)
            val page = document.getPage(0)
            val contentStream = PDPageContentStream(
                document, page, PDPageContentStream.AppendMode.APPEND, true
            )

            val fontStream = assets.open("nanumgothictext.ttf")
            val font: PDFont = PDType0Font.load(document, fontStream)
            val mediaBox: PDRectangle = page.mediaBox



            contentStream.beginText()
            println("폰트 인식 되냐? ${font.type}")
            contentStream.setFont(font, 20f)
            contentStream.setLeading(14.5f)
            contentStream.newLineAtOffset(220f, 600f)
            contentStream.showText("땅주인")

            contentStream.newLineAtOffset(195f, 5f)
            contentStream.setFont(font, 12f)
            contentStream.showText("010-1234-5678")

            contentStream.endText()
            contentStream.close()

            /**
             * val outputFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "입찰표.pdf")
             * 이거는 앱의  private한 공간 중, 다운로드 디렉토리에 저장
             * /storage/emulated/0/Android/data/your.package.name/files/Download/에 저장이 됨
             * 이렇게 하면 앱이 지워질때 pdf도 지워지지만 사용자가 접근할 수 없음 그래서 따로 저장
             */


            if (Build.VERSION.SDK_INT >= 29) {
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "입찰표"
                )
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "기일입찰표.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DOWNLOADS}/입찰표"
                    )
                }
                val contentResolver = applicationContext.contentResolver
                val uri =
                    contentResolver.insert(
                        MediaStore.Files.getContentUri("external"),
                        contentValues
                    )

                uri?.let {
                    contentResolver.openOutputStream(it)?.use { outputStream ->

                        document.save(outputStream)
                        document.close()
                    }
                }
                val openPdf = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                }
                if (openPdf.resolveActivity(packageManager) == null) {
                    Toast.makeText(
                        applicationContext,
                        "PDF 파일을 열 수 있는 앱이 설치되어 있지 않습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    pdfimage(document)
                } else {
                    startActivity(openPdf)
                    setContentView(binding2.root)
                }
            } else {
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "입찰표"
                )
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val outputFile = File(dir, "기일입찰표.pdf")
                val uri = FileProvider.getUriForFile(
                    this,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    outputFile
                )

                document.save(outputFile)
                document.close()
                if (outputFile.exists()) {
                    val pdfRenderer = PdfRenderer(
                        ParcelFileDescriptor.open(
                            outputFile, ParcelFileDescriptor.MODE_READ_ONLY
                        )
                    )
                    val currentPage = pdfRenderer.openPage(0)
                    val bitmap = Bitmap.createBitmap(
                        currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888
                    )
                    currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    setContentView(kr.easw.estrader.android.R.layout.fragment_pdfview)
                    binding.pdfview.setImageBitmap(bitmap)
                    setContentView(binding.root)

                    currentPage.close()
                    pdfRenderer.close()

                }
                val openPdf = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                if (openPdf.resolveActivity(packageManager) == null) {
                    Toast.makeText(
                        applicationContext,
                        "PDF 파일을 열 수 있는 앱이 설치되어 있지 않습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    pdfimage(document)
                } else {
                    startActivity(openPdf)
                    setContentView(binding2.root)
                }


            }


        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun pdfimage(document: PDDocument) {
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "기일입찰표.pdf"
        )
        document.save(outputFile.absoluteFile)
        document.close()

        if (outputFile.exists()) {
            val pdfRenderer = PdfRenderer(
                ParcelFileDescriptor.open(
                    outputFile, ParcelFileDescriptor.MODE_READ_ONLY
                )
            )
            val currentPage = pdfRenderer.openPage(0)
            val bitmap = Bitmap.createBitmap(
                currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888
            )
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            binding.pdfview.setImageBitmap(bitmap)
            setContentView(binding.root)

            currentPage.close()
            pdfRenderer.close()
        }
    }
}