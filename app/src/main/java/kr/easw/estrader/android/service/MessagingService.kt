package kr.easw.estrader.android.service

import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    //새로운 token 생성될 때마다 callback 호출
    override fun onNewToken(token: String) {
        Log.d("onNewToken", token)
    }

    //수신된 메시지 전달
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.let {
            // 수신한 메시지 전달할 Intent 생성
            val intent = Intent("MyNotification")

            // 데이터 전달
            intent.apply {
                putExtras(bundleOf(
                    "userId" to it["targetId"],
                    "targetId" to it["userId"],
                    "itemImage" to it["itemImage"],
                    "phase" to it["phase"]
                ))
            }

            // BroadcastReceiver 전송
            sendBroadcast(intent)
        }
    }
}