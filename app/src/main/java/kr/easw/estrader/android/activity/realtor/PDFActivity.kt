package kr.easw.estrader.android.activity.realtor

import android.annotation.TargetApi
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.commit
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import kotlinx.coroutines.*
import kr.easw.estrader.android.BuildConfig
import kr.easw.estrader.android.databinding.ActivityPdfBinding
import kr.easw.estrader.android.databinding.FragmentPdfImageviewBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.fragment.realtor.RealtorLookUpFragment
import kr.easw.estrader.android.model.dto.ContractInfoRequest
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

class PDFActivity : AppCompatActivity() {
    private lateinit var imageViewBinding: FragmentPdfImageviewBinding
    private lateinit var activityBinding: ActivityPdfBinding
    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        //실제 1cm가 약 28.346f
        const val ONE_CM = 28.346f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageViewBinding = FragmentPdfImageviewBinding.inflate(layoutInflater)
        activityBinding = ActivityPdfBinding.inflate(layoutInflater)

        PDFBoxResourceLoader.init(this)
        editPDF()
    }

    private fun editPDF() {
        try {
            val dialog = Dialog(this)
            dialog.setContentView(ProgressBar(this))
            dialog.show()

            ApiDefinition.GET_CONTRACT_INFO
                .setRequestParams(
                    ContractInfoRequest(
                        intent.getStringExtra("targetId")!!,
                        intent.getStringExtra("userId")!!,
                        intent.getStringExtra("itemImage")!!
                    )
                )
                .setListener {
                    scope.launch {
                        // background thread 사용 (파일 I/O)
                        withContext(Dispatchers.IO) {
                            val document = PDDocument.load(assets.open("기일입찰표.pdf"))
                            val font: PDFont =
                                PDType0Font.load(document, assets.open("nanumgothictext.ttf"))
                            val page = document.getPage(0)

                            PDPageContentStream(
                                document, page, PDPageContentStream.AppendMode.APPEND, true
                            ).use { text ->
                                /**
                                 * 입찰 기일 ~ 물건 번호
                                 */
                                text.beginText()

                                text.set(ONE_CM * 14f, ONE_CM * 23.4f, 9f, "2020", font, 0f)
                                text.set(ONE_CM * 1.2f, 0f, 9f, "04", font, 0f)
                                text.set(ONE_CM * 1.2f, 0f, 9f, "03", font, 0f)
                                text.set(-ONE_CM * 3f, -ONE_CM * 0.9f, 12f, "물건번호", font, 0f)
                                text.set(-ONE_CM * 8f, -2f, 12f, "2023", font, 0f)
                                text.set(ONE_CM * 3.5f, 0f, 12f, "123123", font, 0f)

                                text.endText()

                                /**
                                 * 구매자 부분 인적 사항
                                 */
                                text.beginText()

                                text.set(ONE_CM * 7.762f, 603f, 20f, "구매인", font, 0f)
                                text.set(ONE_CM * 6.879f, 2f, 12f, "010-1234-5678", font, 0f)
                                text.set(-ONE_CM * 7f, -ONE_CM * 0.95f, 12f, "960806-123456", font, 0f)
                                text.set(ONE_CM * 6.2f, 0f, 12f, "110111-110111", font, 0f)
                                text.set(-ONE_CM * 6.2f, -ONE_CM * 0.8f, 10f, "충청남도 홍성군 머시기", font, 0f)

                                text.endText()

                                /**
                                 * 대리인 부분 인적 사항
                                 */
                                text.beginText()

                                text.set(ONE_CM * 7.762f, ONE_CM * 18.8f, 20f, "대리인", font, 0f)
                                text.set(ONE_CM * 8f, 2f, 12f, "친척", font, 0f)
                                text.set(-ONE_CM * 8.121f, -ONE_CM * 0.88f, 12f, "960806-123456", font, 0f)
                                text.set(ONE_CM * 7.4f, 0f, 10f, "010-1234-4564", font, 0f)
                                text.set(-ONE_CM * 7.4f, -ONE_CM * 0.65f, 10f, "충청북도 청주 머시기", font, 0f)

                                text.endText()

                                /**
                                 * 입찰 금액 및 보증 금액
                                 */
                                text.beginText()

                                text.set(ONE_CM * 3.8f, ONE_CM * 15.1f, 20f, "1", font, 0f)
                                text.set(ONE_CM * 0.5f, 0f, 20f, "2", font, 0f)
                                text.set(ONE_CM * 0.55f, 0f, 20f, "3", font, 0f) //십억
                                text.set(ONE_CM * 0.5f, 0f, 20f, "4", font, 0f)
                                text.set(ONE_CM * 0.5f, 0f, 20f, "5", font, 0f)
                                text.set(ONE_CM * 0.5f, 0f, 20f, "6", font, 0f) //백만
                                text.set(ONE_CM * 0.5f, 0f, 20f, "7", font, 0f)
                                text.set(ONE_CM * 0.5f, 0f, 20f, "8", font, 0f)
                                text.set(ONE_CM * 0.55f, 0f, 20f, "9", font, 0f) //천
                                text.set(ONE_CM * 0.5f, 0f, 20f, "0", font, 0f)
                                text.set(ONE_CM * 0.5f, 0f, 20f, "1", font, 0f)
                                text.set(ONE_CM * 0.55f, 0f, 20f, "2", font, 0f)

                                /**
                                 * 보증 금액
                                 * => 입찰에 참여할 때는 통상 경매 물건의 최저매각가격의 10분의 1에 해당하는 금액을 매수신청의 보증을 제공(「민사집행법」 제113조, 「민사집행규칙」 제63조제1항 및 제71조)
                                 */
                                text.set(ONE_CM * 2.2f, 0f, 20f, "7", font, 0f)
                                text.set(ONE_CM * 0.45f, 0f, 20f, "1", font, 0f)
                                text.set(ONE_CM * 0.5f, 0f, 20f, "2", font, 0f) //십억
                                text.set(ONE_CM * 0.45f, 0f, 20f, "3", font, 0f)
                                text.set(ONE_CM * 0.45f, 0f, 20f, "4", font, 0f)
                                text.set(ONE_CM * 0.45f, 0f, 20f, "5", font, 0f) //백만
                                text.set(ONE_CM * 0.5f, 0f, 20f, "6", font, 0f)
                                text.set(ONE_CM * 0.45f, 0f, 20f, "7", font, 0f)
                                text.set(ONE_CM * 0.5f, 0f, 20f, "8", font, 0f)//천
                                text.set(ONE_CM * 0.55f, 0f, 20f, "9", font, 0f)
                                text.set(ONE_CM * 0.49f, 0f, 20f, "0", font, 0f)

                                text.endText()
                            }

                            if (Build.VERSION.SDK_INT >= 29) {
                                aboveQPDFViewer(document)
                            } else {
                                belowQPDFViewer(document)
                            }
                        }
                    }

                    dialog.dismiss()

                }.build(this)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun PDPageContentStream.set(
        x: Float,
        y: Float,
        fontSize: Float,
        text: String,
        font: PDFont,
        leading: Float
    ) {
        newLineAtOffset(x, y)
        setFont(font, fontSize)
        showText(text)
        setLeading(leading)
    }

    // 파일 저장 + URL 따와서 PDF 뷰어로 열기
    @TargetApi(Build.VERSION_CODES.Q)
    private suspend fun aboveQPDFViewer(document: PDDocument) {
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

        // Main thread 사용 (UI 작업)
        withContext(Dispatchers.Main) {
            if (openPdf.resolveActivity(packageManager) == null) {
                showToast()
                pdfImageView()

            } else {
                startActivity(openPdf)
                setContentView(activityBinding.root)
                supportFragmentManager.commit {
                    replace(activityBinding.containerView.id,
                        RealtorLookUpFragment.setArguments(
                            intent.getStringExtra("targetId")!!,
                            intent.getStringExtra("userId")!!,
                            intent.getStringExtra("itemImage")!!
                        )
                    )
                }
            }
        }
    }

    // 파일 저장 + URL 따와서 PDF 뷰어로 열기
    private suspend fun belowQPDFViewer(document: PDDocument) {
        // 공개 Directory
        val outputFile = File(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "입찰표"
            ), "기일입찰표.pdf"
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

        // Main thread 사용 (UI 작업)
        withContext(Dispatchers.Main) {
            if (openPdf.resolveActivity(packageManager) == null) {
                showToast()
                pdfImageView()
            } else {
                startActivity(openPdf)
                setContentView(activityBinding.root)
                supportFragmentManager.commit {
                    replace(activityBinding.containerView.id,
                        RealtorLookUpFragment.setArguments(
                            intent.getStringExtra("targetId")!!,
                            intent.getStringExtra("userId")!!,
                            intent.getStringExtra("itemImage")!!
                        )
                    )
                }
            }
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
                    outputFile, ParcelFileDescriptor.MODE_READ_ONLY
                )
            ).use { pdfRenderer ->
                pdfRenderer.openPage(0).use {
                    val bitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
                    it.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    imageViewBinding.pdfview.setImageBitmap(bitmap)
                    setContentView(imageViewBinding.root)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // 코루틴 작업 취소
    }

    private fun showToast() {
        Toast.makeText(this, "PDF 파일을 열 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    }
}