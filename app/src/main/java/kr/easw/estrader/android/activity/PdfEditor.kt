package kr.easw.estrader.android.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.appcompat.app.AppCompatActivity
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import kr.easw.estrader.android.databinding.FragmentPdfBinding
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
 */

class PdfEditor : AppCompatActivity() {
    private lateinit var binding: FragmentPdfBinding
    private lateinit var bindingToPDF: FragmentPdfviewBinding

    companion object{
        // TODO("좌표를 상수로 저장")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PDFBoxResourceLoader.init(applicationContext)

        editPDF()
    }

//            val name: String = binding.nameInput.text.toString()
//            val num: String = binding.numInput.text.toString()
//
//            val textBitmap: Bitmap = textAsBitmap(name, 50f)
//            val numBitmap: Bitmap = textAsBitmap(num, 50f)
//
//            val pngFile = File(this.externalCacheDir, "image.png")
//            val pngNum = File(this.externalCacheDir, "num.png")
//            val fileOutputStream = FileOutputStream(pngFile)
//            val fileOutputStream2 = FileOutputStream(pngNum)
//            textBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
//            numBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream2)
//            fileOutputStream.flush()
//            fileOutputStream.close()
//
//            val inputStream = assets.open("입찰.pdf")
//            val document = PDDocument.load(inputStream)
//            val imageStream: InputStream = FileInputStream(pngFile)
//            val image: PDImageXObject = LosslessFactory.createFromImage(document, BitmapFactory.decodeStream(imageStream))
//            val numStream: InputStream = FileInputStream(pngNum)
//            val numimg: PDImageXObject = LosslessFactory.createFromImage(document, BitmapFactory.decodeStream(numStream))
//
//            // 페이지 1의 내용 수정
//            val page1 = document.getPage(0)
//            val contentStream1 = PDPageContentStream(document, page1, PDPageContentStream.AppendMode.APPEND, true)
//            contentStream1.drawImage(image, 220f, 490f,100f,30f)
//            contentStream1.close()
//
//            // 페이지 2의 내용 수정
//            val page2 = document.getPage(0)
//            val contentStream2 = PDPageContentStream(document, page2, PDPageContentStream.AppendMode.APPEND, true)
//            contentStream2.drawImage(numimg, 50f, 500f)
//            contentStream2.close()
//
//            // 파일 저장
//            document.save("/sdcard/Download/example3.pdf")
//            document.close()
//            val pdfFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "example3.pdf")
//
//            if (pdfFile.exists()) {
//                val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
//                val currentPage = pdfRenderer.openPage(0) // 첫 번째 페이지
//                val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
//                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//                setContentView(kr.easw.estrader.android.R.layout.fragment_pdfview)
//                binding2 = FragmentPdfviewBinding.inflate(layoutInflater)
//                binding2.pdfview.setImageBitmap(bitmap) //pdfview -> 이미지뷰고 set머시기 이미지뷰만됨
//                setContentView(binding2.root)
//
//                currentPage.close()
//                pdfRenderer.close()
//
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // 권한이 없는 경우 권한 요청 대화 상자를 표시
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION)
//        }

    @SuppressLint("SdCardPath")
    private fun editPDF() {
        try {
            val inputStream = assets.open("입찰.pdf")
            val document = PDDocument.load(inputStream)
            val page = document.getPage(0)
            val contentStream = PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)

            val fontStream = assets.open("nanumgothictext.ttf")
            val font: PDFont = PDType0Font.load(document, fontStream)

            val fontSize = 25f
            val leading = 1.5f * fontSize
            val mediaBox: PDRectangle = page.mediaBox
            val margin = 72f
            val width: Float = mediaBox.width - 2 * margin
            val startX: Float = mediaBox.lowerLeftX + margin
            val startY: Float = mediaBox.upperRightY - margin

            println("X3 | ${mediaBox.upperRightX}")
            println("X3 | ${mediaBox.upperRightY}")

            contentStream.beginText() // 하나만

            contentStream.setFont(font, 20f)
            contentStream.setLeading(14.5f)
            contentStream.newLineAtOffset(220f, 613f) // 좌표 설정 y값은 커질수록 올라감
            contentStream.showText("김덕배") // 텍스트 설정

            contentStream.newLineAtOffset(195f, 5f) // 좌표 설정 y값은 커질수록 올라감
            contentStream.setFont(font, 12f)
            contentStream.showText("010-1234-5678")

            contentStream.endText() // 하나만

            contentStream.close()

            document.save("/sdcard/Download/Hello World12121412333.pdf")
            document.close()

            val pdfFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Hello World12121412333.pdf")

            if (pdfFile.exists()) {
                val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
                val currentPage = pdfRenderer.openPage(0) // 첫 번째 페이지
                val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                setContentView(kr.easw.estrader.android.R.layout.fragment_pdfview)
                bindingToPDF = FragmentPdfviewBinding.inflate(layoutInflater)
                bindingToPDF.pdfview.setImageBitmap(bitmap) //pdfview -> 이미지뷰고 set머시기 이미지뷰만됨
                setContentView(bindingToPDF.root)

                currentPage.close()
                pdfRenderer.close()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun textAsBitmap(text: String, textSize: Float): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL

        val baseline = -paint.ascent() // ascent() is negative
        val width = (paint.measureText(text) + 0.5f).toInt() // round
        val height = (baseline + paint.descent() + 0.5f).toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(text, 0f, baseline, paint)

        return bitmap
    }
}