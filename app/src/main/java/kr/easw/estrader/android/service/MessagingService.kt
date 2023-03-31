package kr.easw.estrader.android.service

import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.easw.estrader.android.activity.MainActivity


class MessagingService : FirebaseMessagingService() {
    private var msg: String? = null
    private  var title: String? = null

    override fun onMessageReceived(message: RemoteMessage) {

        title = message.notification?.title
        msg = message.notification?.body

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val contentIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                MainActivity::class.java
            ), 0
        )

        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this).setSmallIcon(R.drawable.sym_def_app_icon)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(longArrayOf(1, 1000))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, mBuilder.build())
        mBuilder.setContentIntent(contentIntent)

    }

    override fun onNewToken(token: String) {

    }
}
