package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.PdfEditor
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_ID
import kr.easw.estrader.android.model.dto.FcmRequest
import kr.easw.estrader.android.model.dto.LookUpItemRequest
import kr.easw.estrader.android.util.PreferenceUtil

// 만약에 phase가 1인 fcm을 받았을 경우
class RealtorMatchDialog : AppCompatActivity() {
    private lateinit var alertBtn: Button
    private lateinit var item: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_realtormatch)

        alertBtn = findViewById(R.id.confirm_button)

        item =  intent.getStringExtra("itemImage")!!

        showItem(item)

        // PdfEditor 이동
        alertBtn.setOnClickListener {
            alertClick()
        }
    }

    private fun alertClick() {
        AlertDialog.Builder(this)
            .setMessage("입찰표 PDF를 출력합니다.")
            .setPositiveButton("확인") { _, _ ->
                val dialog = Dialog(this)
                dialog.setContentView(ProgressBar(this))
                dialog.show()

                ApiDefinition.REALTOR_SEND_FCM
                    .setRequestParams(
                        FcmRequest(
                            PreferenceUtil(this).init().start().getString(PREFERENCE_REALTOR_ID)!!,
                            intent.getStringExtra("targetId")!!,
                            intent.getStringExtra("itemImage")!!,
                            "2",
                            "안녕",
                            "난 야옹이야."
                        )
                    )
                    .setListener {
                        showToast(it.message)
                        dialog.dismiss()

                        startActivity(
                            Intent(this, PdfEditor::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                        )
                        finish()
                    }
                    .build(this)
            }
            .create()
            .show()
    }

    private fun showItem(item: String) {
        val dialog = Dialog(this)
        dialog.setContentView(ProgressBar(this))
        dialog.show()

        ApiDefinition.REALTOR_SHOW_ITEM
            .setRequestParams(
                LookUpItemRequest(
                    item
                )
            )
            .setListener {
                println(it.picture)
                println(it.information)
                println(it.period)
                println(it.location)
                println(it.reserveprice)
                println(it.auctionperiod)

                dialog.dismiss()
            }
            .build(this)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}