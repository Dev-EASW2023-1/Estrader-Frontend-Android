package kr.easw.estrader.android.extensions

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf

inline fun <reified T: AppCompatActivity> Context.startActivity (
    vararg extras: Pair<String, Any?>,
    intentAction: Intent.() -> Unit = {}
) {
    startActivity(Intent(this, T::class.java).apply {
        putExtras(bundleOf(*extras))
        intentAction()
    })
}

inline fun <reified T: AppCompatActivity> Context.startActivity (
    data: Map<String, String>
) {
    startActivity(Intent(this, T::class.java).apply {
        putExtras(
            bundleOf(
                "itemImage" to data["itemImage"],
                "targetId" to data["userId"],
                "userId" to data["targetId"]
            )
        )
        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}