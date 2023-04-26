package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.databinding.FragmentSuccessfuldelegationBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.ContractRequest

/**
 * 대리위임 신청 완료 Dialog
 * 확인 누르면 "전 화면으로 돌아갑니다." 팝업 후, MainListActivity 이동
 */
class SuccessDelegationDialog : AppCompatActivity() {
    private lateinit var binding: FragmentSuccessfuldelegationBinding
    private lateinit var alertBtn: Button
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSuccessfuldelegationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.bingbong)
        mediaPlayer.start()

        alertBtn = findViewById(R.id.confirm_button)

        contractComplete()

        alertBtn.setOnClickListener {
            alertClick()
        }
    }

    private fun alertClick() {
        AlertDialog.Builder(this)
            .setMessage("전 화면으로 돌아갑니다.")
            .setPositiveButton("확인") { _, _ ->
                startActivity<MainListActivity>{
                    flags = FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP
                }
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
                    intent.getStringExtra("itemImage")!!,
                    "3",
                    "제목",
                    "내용"
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