package kr.easw.estrader.android.activity.user

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.broadcast.FragmentChangeReceiver
import kr.easw.estrader.android.extensions.replaceFragment
import kr.easw.estrader.android.fragment.delegation.user.AwaitingFragment

/**
 * 사용자 전용 대리위임 신청 중 Dialog
 * 앱이 다른 activity 전환 or 백그라운드에 있을 때 BroadcastReceiver 등록 해제
 */
class AwaitingActivity : AppCompatActivity() {
    private var broadcastReceiver : BroadcastReceiver? = null
    private var isReceiverRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_awaiting)

        supportFragmentManager.replaceFragment<AwaitingFragment>(
            R.id.container_view,
            null
        )

        startRegisterReceiver()
    }

    private fun startRegisterReceiver() {
        if(!isReceiverRegistered) {
            broadcastReceiver = FragmentChangeReceiver()

            // BroadcastReceiver 등록
            val filter = IntentFilter().apply {
                addAction("MyNotification")
            }
            registerReceiver(broadcastReceiver, filter)
            isReceiverRegistered = true
        }
    }

    private fun finishRegisterReceiver() {
        if (isReceiverRegistered) {
            unregisterReceiver(broadcastReceiver)
            broadcastReceiver = null
            isReceiverRegistered = false
        }
    }

    override fun onResume() {
        super.onResume()
        startRegisterReceiver()
    }

    override fun onStop() {
        super.onStop()
        finishRegisterReceiver()
    }
}


