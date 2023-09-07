package kr.easw.estrader.android.activity.realtor

import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.*
import kr.easw.estrader.android.databinding.ActivityPdfBinding
import kr.easw.estrader.android.databinding.FragmentPdfImageviewBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PDF_URL
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_TOKEN
import kr.easw.estrader.android.extensions.replaceFragment
import kr.easw.estrader.android.fragment.realtor.RealtorLookUpFragment
import kr.easw.estrader.android.model.dto.ContractInfoRequest
import kr.easw.estrader.android.util.PDFUtil.contentUri
import kr.easw.estrader.android.util.PDFUtil.createSharedFile
import kr.easw.estrader.android.util.PDFUtil.createUri
import kr.easw.estrader.android.util.PDFUtil.downloadReceiver
import kr.easw.estrader.android.util.PDFUtil.getFileFromContentUri
import kr.easw.estrader.android.util.PDFUtil.renderPdfToImageView
import kr.easw.estrader.android.util.PreferenceUtil
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
    private var downloadReceiver : BroadcastReceiver? = null
    private var downloadCompleteFilter : IntentFilter? = null
    private var downloadManager : DownloadManager? = null
    private var file : File? = null
    private var uri : Uri? = null
    private var isReceiverRegistered = false
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
                            if (Build.VERSION.SDK_INT >= 29) {
                                uri = contentUri(this@PDFActivity)
                                uri?.let {
                                    file = getFileFromContentUri(this@PDFActivity, it)
                                    urlDownloading(file)
                                    pdfViewer(uri, file)
                                }
                            } else {
                                file = createSharedFile()
                                file?.let {
                                    uri = createUri(this@PDFActivity, it)
                                    urlDownloading(file)
                                    pdfViewer(uri, file)
                                }
                            }
                        }
                    }
                    dialog.dismiss()

                }
                .setRequestHeaders(mutableMapOf("Authorization" to "Bearer " + PreferenceUtil(this).init().start().getString(PREFERENCE_REALTOR_TOKEN)!!))
                .build(this)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // 파일 저장 + URL 따와서 PDF 뷰어로 열기
    private suspend fun pdfViewer(uri: Uri?, file: File?) {
        uri?.let {
            val openPdf = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(it, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Main thread 사용 (UI 작업)
            withContext(Dispatchers.Main) {
                try {
                    if (openPdf.resolveActivity(packageManager) == null) {
                        showToast("PDF 파일을 열 수 있는 앱이 설치되어 있지 않습니다.")
                        renderPdfToImageView(this@PDFActivity, file!!, imageViewBinding)
                        setContentView(imageViewBinding.root)
                    } else {
                        startActivity(openPdf)
                        setContentView(activityBinding.root)
                        supportFragmentManager.replaceFragment<RealtorLookUpFragment>(
                            activityBinding.containerView.id,
                            bundleOf(
                                "userId" to intent.getStringExtra("targetId")!!,
                                "targetId" to intent.getStringExtra("userId")!!,
                                "itemImage" to intent.getStringExtra("itemImage")!!
                            )
                        )
                    }
                } catch (e: NullPointerException) {
                    showToast("오류가 발생하였습니다.")
                }
            }
        }
    }

    private fun urlDownloading(file: File?) {
        file?.let {
            // DownloadManager 초기화
            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager?.let { manager ->
                val request = DownloadManager.Request(Uri.parse(PDF_URL))
//                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setRequiresCharging(false)
                    .setDestinationUri(Uri.fromFile(it))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                val downloadId = manager.enqueue(request)

                if(!isReceiverRegistered) {
                    // Download 완료를 알려줄 BroadcastReceiver 생성
                    downloadReceiver = downloadReceiver(downloadId, manager)
                    downloadCompleteFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    registerReceiver(downloadReceiver, downloadCompleteFilter)
                    isReceiverRegistered = true
                }
            }
        }
    }

    // 다른 Activity 호출 시 BroadcastReceiver 등록 취소
    override fun onPause() {
        super.onPause()
        if(isReceiverRegistered) {
            unregisterReceiver(downloadReceiver)
            downloadReceiver = null
            isReceiverRegistered = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // 코루틴 작업 취소
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}