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
import kr.easw.estrader.android.fragment.delegation.RealtorMatchFragment
import kr.easw.estrader.android.fragment.delegation.SuccessDelegationFragment

class FragmentChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = bundleOf(
            "itemImage" to intent.getStringExtra("itemImage"),
            "targetId" to intent.getStringExtra("userId"),
            "userId" to intent.getStringExtra("targetId")
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