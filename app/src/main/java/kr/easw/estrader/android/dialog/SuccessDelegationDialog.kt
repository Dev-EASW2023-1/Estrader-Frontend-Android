package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.model.dto.ContractRequest

/**
 * 대리위임 신청 완료 Dialog
 * 확인 누르면 "전 화면으로 돌아갑니다." 팝업 후, MainListActivity 이동
 */
class SuccessDelegationDialog : AppCompatActivity() {
    private lateinit var alertBtn: Button
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_successfuldelegation)

        mediaPlayer = MediaPlayer.create(this, R.raw.bingbong)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        mediaPlayer.start()
        vibrator.vibrate(1000)

        alertBtn = findViewById(R.id.confirm_button)

        contractComplete()

        // "전 화면으로 돌아갑니다." 팝업 확인 후, MainListActivity 이동
        alertBtn.setOnClickListener {
            alertClick()
        }
    }

    private fun alertClick() {
        AlertDialog.Builder(this)
            .setMessage("전 화면으로 돌아갑니다.")
            .setPositiveButton("확인") { _, _ ->
                startActivity(
                    Intent(this, MainListActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                )
                finish()
            }
            .create()
            .show()
    }

    private fun contractComplete(){
        val dialog = Dialog(this)
        dialog.setContentView(ProgressBar(this))
        dialog.show()

        ApiDefinition.CONTRACT_COMPLETE
            .setRequestParams(
                ContractRequest(
                    intent.getStringExtra("userId")!!,
                    intent.getStringExtra("targetId")!!,
                    intent.getStringExtra("itemImage")!!
                )
            )
            .setListener {
                showToast(it.message)
                dialog.dismiss()
            }
            .build(this)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}