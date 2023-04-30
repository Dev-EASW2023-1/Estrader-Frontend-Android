package kr.easw.estrader.android.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.realtor.PDFActivity
import kr.easw.estrader.android.extensions.replaceFragment
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.activity.ErrorActivity
import kr.easw.estrader.android.fragment.delegation.realtor.RealtorMatchFragment
import kr.easw.estrader.android.fragment.delegation.user.SuccessDelegationFragment

/**
 * FCM 메시지 수신 시 이벤트 발생 BroadcastReceiver
 * FCM 메시지 Phase 값에 맞게 Fragment or Activity 화면 전환
 */
class FragmentChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = bundleOf(
            "itemImage" to intent.getStringExtra("itemImage"),
            "targetId" to intent.getStringExtra("targetId"),
            "userId" to intent.getStringExtra("userId")
        )

        val fragmentManager = (context as AppCompatActivity).supportFragmentManager

        when (intent.getStringExtra("phase")) {
            "1" -> fragmentManager.replaceFragment<RealtorMatchFragment>(
                R.id.container_view,
                bundle
            )
            "2" -> fragmentManager.replaceFragment<SuccessDelegationFragment>(
                R.id.container_view,
                bundle
            )
            "3" -> context.startActivity<PDFActivity>(
                bundle
            )
            else -> context.startActivity<ErrorActivity>(
                bundle
            )
        }
    }
}