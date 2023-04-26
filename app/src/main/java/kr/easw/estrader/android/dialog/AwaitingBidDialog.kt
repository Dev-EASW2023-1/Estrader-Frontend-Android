package kr.easw.estrader.android.dialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R

/**
 * 대리위임 신청 대기 Dialog
 *
 * 지금은 5초 뒤, SuccessDelegationDialog 이동
 * 추후 대리인 앱에서 대리위임 수락 후, SuccessDelegationDialog 이동 수정 예정
 */
class AwaitingBidDialog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_awaitingbid)
    }
}


