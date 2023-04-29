package kr.easw.estrader.android.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.easw.estrader.android.activity.PDFActivity
import kr.easw.estrader.android.dialog.ErrorDialog
import kr.easw.estrader.android.dialog.RealtorMatchDialog
import kr.easw.estrader.android.dialog.SuccessDelegationDialog
import kr.easw.estrader.android.extensions.startActivity

class MessagingService : FirebaseMessagingService() {
    //새로운 token 생성될 때마다 callback 호출
    override fun onNewToken(token: String) {
        Log.d("onNewToken", token)
    }

    //수신된 메시지 전달
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        switchToActivity(remoteMessage.data)
    }

    private fun switchToActivity(data: Map<String, String>) {
        when(data["phase"]){
            "1" -> startActivity<RealtorMatchDialog>(data)
            "2" -> startActivity<SuccessDelegationDialog>(data)
            "3" -> startActivity<PDFActivity>(data)
            else -> startActivity<ErrorDialog>(data)
        }
    }
}