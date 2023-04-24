package kr.easw.estrader.android.activity

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import kr.easw.estrader.android.BuildConfig
import kr.easw.estrader.android.databinding.ActivityPdfBinding
import kr.easw.estrader.android.databinding.FragmentPdfviewBinding
import kr.easw.estrader.android.fragment.DelegateItemFragment
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

    private lateinit var imageViewBinding: FragmentPdfviewBinding
    private lateinit var activityBinding: ActivityPdfBinding

    companion object {
        // TODO("좌표를 상수로 저장")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageViewBinding = FragmentPdfviewBinding.inflate(layoutInflater)
        activityBinding = ActivityPdfBinding.inflate(layoutInflater)
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

            val onecm = 28.346f //실제 1cm가 약 28.346f

            /**
             * 입찰기일부터 물건번호까지
             */
            contentStream.beginText()
            contentStream.set(onecm*14f, onecm*23.4f, 9f, "2020", font, 0f)
            contentStream.set(onecm*1.2f, 0f, 9f, "04", font, 0f)
            contentStream.set(onecm*1.2f, 0f, 9f, "03", font, 0f)
            contentStream.set(-onecm*3f, -onecm*0.9f, 12f, "물건번호", font, 0f)
            contentStream.set(-onecm*8f, -2f, 12f, "2023", font, 0f)
            contentStream.set(onecm*3.5f, 0f, 12f, "123123", font, 0f)

            contentStream.endText()


            /**
             * 구매자 부분 인적사항
             */
            contentStream.beginText()
            contentStream.set(onecm*7.762f, 603f, 20f, "구매인", font, 0f)
            contentStream.set(onecm*6.879f, 2f, 12f, "010-1234-5678", font, 0f)
            contentStream.set(-onecm*7f , -onecm*0.95f, 12f, "960806-123456", font, 0f)
            contentStream.set(onecm*6.2f , 0f, 12f, "110111-110111", font, 0f)
            contentStream.set(-onecm*6.2f, -onecm*0.8f, 10f, "충청남도 홍성군 머시기", font, 0f)
            contentStream.endText()
            /**
             * 대리인 부분 인적사항
             */
            contentStream.beginText()
            contentStream.set(onecm*7.762f, onecm*18.8f, 20f, "대리인", font, 0f)
            contentStream.set(onecm*8f, 2f, 12f, "친척", font, 0f)
            contentStream.set(-onecm*8.121f , -onecm*0.88f, 12f, "960806-123456", font, 0f)
            contentStream.set(onecm*7.4f, 0f, 10f, "010-1234-4564", font, 0f)
            contentStream.set(-onecm*7.4f, -onecm*0.65f, 10f, "충청북도 청주 머시기", font, 0f)
            contentStream.endText()
            /**
             * 입찰금액 및 보증금액
             */
            contentStream.beginText()
            contentStream.set(onecm*3.8f, onecm*15.1f, 20f, "1", font, 0f)
            contentStream.set(onecm*0.5f, 0f, 20f, "2", font, 0f)
            contentStream.set(onecm*0.55f, 0f, 20f, "3", font, 0f)//십억
            contentStream.set(onecm*0.5f, 0f, 20f, "4", font, 0f)
            contentStream.set(onecm*0.5f, 0f, 20f, "5", font, 0f)
            contentStream.set(onecm*0.5f, 0f, 20f, "6", font, 0f)//백만
            contentStream.set(onecm*0.5f, 0f, 20f, "7", font, 0f)
            contentStream.set(onecm*0.5f, 0f, 20f, "8", font, 0f)
            contentStream.set(onecm*0.55f, 0f, 20f, "9", font, 0f)//천
            contentStream.set(onecm*0.5f, 0f, 20f, "0", font, 0f)
            contentStream.set(onecm*0.5f, 0f, 20f, "1", font, 0f)
            contentStream.set(onecm*0.55f, 0f, 20f, "2", font, 0f)
            /**
             * 밑에는 보증금액 -> 입찰에 참여할 때는 통상 경매 물건의 최저매각가격의 10분의 1에 해당하는 금액을 매수신청의 보증을 제공(「민사집행법」 제113조, 「민사집행규칙」 제63조제1항 및 제71조)
             */
            contentStream.set(onecm*2.2f, 0f, 20f, "7", font, 0f)
            contentStream.set(onecm*0.45f, 0f, 20f, "1", font, 0f)
            contentStream.set(onecm*0.5f, 0f, 20f, "2", font, 0f)//십억
            contentStream.set(onecm*0.45f, 0f, 20f, "3", font, 0f)
            contentStream.set(onecm*0.45f, 0f, 20f, "4", font, 0f)
            contentStream.set(onecm*0.45f, 0f, 20f, "5", font, 0f)//백만
            contentStream.set(onecm*0.5f, 0f, 20f, "6", font, 0f)
            contentStream.set(onecm*0.45f, 0f, 20f, "7", font, 0f)
            contentStream.set(onecm*0.5f, 0f, 20f, "8", font, 0f)//천
            contentStream.set(onecm*0.55f, 0f, 20f, "9", font, 0f)
            contentStream.set(onecm*0.49f, 0f, 20f, "0", font, 0f)

            contentStream.endText()


            contentStream.close()

            if (Build.VERSION.SDK_INT >= 29) {
                aboveQPDFViewer(document)
            } else {
                belowQPDFViewer(document)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun PDPageContentStream.set (x: Float, y: Float, fontSize: Float, text: String, font: PDFont, leading: Float){
        newLineAtOffset(x,y)
        setFont(font, fontSize)
        showText(text)
        setLeading(leading)
    }

    // 파일 저장 + URL 따와서 PDF 뷰어로 열기
    @TargetApi(Build.VERSION_CODES.Q)
    private fun aboveQPDFViewer(document: PDDocument){
        // 추가한 record 의 새로운 URL 리턴
        val uri = contentResolver.insert(
            // External Storage 의 URI 획득
            MediaStore.Files.getContentUri("external"),
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "기일입찰표.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/입찰표")
            }
        )

        // 파일 URL 로 데이터 저장
        uri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                document.save(outputStream)
                document.close()
            }
        }

        // ACTION_VIEW 를 이용해 PDF 뷰어 호출
        // Intent 에 setData 를 통해 uri 설정 후, Intent.setFlags() 를 통해 권한 부여
        val openPdf = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (openPdf.resolveActivity(packageManager) == null) {
            showToast()
            pdfImageView()

        } else {
            startActivity(openPdf)
            setContentView(activityBinding.root)

            supportFragmentManager
                .beginTransaction()
                .replace(activityBinding.containerView.id, DelegateItemFragment())
                .commit()
        }
    }

    // 파일 저장 + URL 따와서 PDF 뷰어로 열기
    private fun belowQPDFViewer(document: PDDocument){
        // 공개 Directory
        val outputFile = File(
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "입찰표"), "기일입찰표.pdf"
        )

        // 공유할 File 객체 생성 후, FileProvider.getUriForFile() 에 File 을 넘기고 URL 생성
        val uri = FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.provider",
            outputFile
        )

        document.save(outputFile)
        document.close()

        val openPdf = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (openPdf.resolveActivity(packageManager) == null) {
            showToast()
            pdfImageView()
        } else {
            startActivity(openPdf)
            setContentView(activityBinding.root)

            supportFragmentManager
                .beginTransaction()
                .replace(activityBinding.containerView.id, DelegateItemFragment())
                .commit()
        }
    }

    // 단순히 파일 찾고 ImageView 로 출력
    private fun pdfImageView() {
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "기일입찰표.pdf"
        )

        if (outputFile.exists()) {
            PdfRenderer(
                ParcelFileDescriptor.open(
                    outputFile, ParcelFileDescriptor.MODE_READ_ONLY)
            ).use {
                it.openPage(0).apply {
                    this.use {
                        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
                        this.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                        imageViewBinding.pdfview.setImageBitmap(bitmap)
                        setContentView(imageViewBinding.root)
                    }
                }
            }
        }
    }

    private fun showToast() {
        Toast.makeText(this, "PDF 파일을 열 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    }
}