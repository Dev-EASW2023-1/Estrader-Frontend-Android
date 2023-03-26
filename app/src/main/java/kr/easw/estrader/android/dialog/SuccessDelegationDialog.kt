package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.databinding.FragmentSuccessfuldelegationBinding

/**
 * 대리위임 신청 완료 Dialog
 * 확인 누르면 "전 화면으로 돌아갑니다." 팝업 후, MainListActivity 이동
 */
class SuccessDelegationDialog : AppCompatActivity() {
    private lateinit var binding: FragmentSuccessfuldelegationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSuccessfuldelegationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // "전 화면으로 돌아갑니다." 팝업 확인 후, MainListActivity 이동
        binding.confirmButton.setOnClickListener {
            alertClick()
        }
    }

    private fun alertClick() {
        AlertDialog.Builder(this, R.style.AppTheme_AlertDialogTheme).setMessage("전 화면으로 돌아갑니다.")
            .setPositiveButton("확인") { _, _ ->
                startActivity(Intent(this, MainListActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
                finish()
            }.create().show()
    }
}