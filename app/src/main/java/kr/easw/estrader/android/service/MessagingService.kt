package kr.easw.estrader.android.service

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainActivity
import kr.easw.estrader.android.fragment.realtor.DelegateWaitingFragment

class MessagingService : FirebaseMessagingService() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    companion object {
        private const val CHANNEL_NAME = "estrader"
        private const val CHANNEL_ID = "FOREGROUND_CHANNEL_ID"
    }

    //새로운 token 생성될 때마다 callback 호출
    override fun onNewToken(token: String) {
        Log.d("onNewToken", token)
    }

    //수신된 메시지 전달
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        initNotification()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        remoteMessage.notification?.let {
            Log.d("FirebaseMessagingService*************", "Message Notification Body: ${it.body}")
            sendNotification(remoteMessage.notification!!)
        }

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

    // 발생 이벤트를 알리는 NotificationManager, 알림을 생성하는 NotificationCompat.Builder 초기화
    private fun initNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(this)
        }
    }

    // Android O 부터 도입된 Notification Channel, 생성 시 중요도 수준 설정 가능
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    // 상태바에 Notification 등록
    private fun sendNotification(data: RemoteMessage.Notification) {
        val intent = Intent(this, DelegateWaitingFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            // 알림 제목 설정
            .setContentTitle(data.title)
            // 알림 내용 설정
            .setContentText(data.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }
}