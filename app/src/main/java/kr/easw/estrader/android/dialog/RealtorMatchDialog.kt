package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.PdfEditor
import kr.easw.estrader.android.databinding.ActivityLoginBinding
import kr.easw.estrader.android.databinding.FragmentRealtormatchBinding
import kr.easw.estrader.android.definitions.*
import kr.easw.estrader.android.model.dto.FcmRequest
import kr.easw.estrader.android.model.dto.LookUpItemRequest
import kr.easw.estrader.android.util.PreferenceUtil
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class RealtorMatchDialog : AppCompatActivity() {
    private lateinit var binding: FragmentRealtormatchBinding
    private val alertBtn: Button by lazy {
        binding.confirmButton
    }
    private val auctionHouse : TextView by lazy {
        binding.auctionHouse
    }
    private val caseNumber : TextView by lazy {
        binding.caseNumber
    }
    private val reservePrice : TextView by lazy {
        binding.reservePrice
    }
    private val auctionPeriod: TextView by lazy {
        binding.auctionPeriod
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRealtormatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alertBtn
        auctionHouse
        caseNumber
        reservePrice
        auctionPeriod

        showItem()

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

                        if(it.isSuccess){
                            PreferenceUtil(this).init().start().setString(PREFERENCE_ID, intent.getStringExtra("targetId")!!)
                            PreferenceUtil(this).init().start().setString(PREFERENCE_PICTURE_URL,intent.getStringExtra("itemImage")!!)
                        }

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

    private fun showItem() {
        val dialog = Dialog(this)
        dialog.setContentView(ProgressBar(this))
        dialog.show()

        ApiDefinition.REALTOR_SHOW_ITEM
            .setRequestParams(
                LookUpItemRequest(
                    URLEncoder.encode(intent.getStringExtra("itemImage")!!, StandardCharsets.UTF_8.toString())!!
                )
            )
            .setListener {
                auctionHouse.text = it.court
                caseNumber.text = it.caseNumber
                reservePrice.text = it.minimumBidPrice
                auctionPeriod.text = it.biddingPeriod.replace("\n", "")
                dialog.dismiss()
            }
            .build(this)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}