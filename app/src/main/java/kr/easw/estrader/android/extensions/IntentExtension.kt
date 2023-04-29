package kr.easw.estrader.android.extensions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import kr.easw.estrader.android.R

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

inline fun <reified T: AppCompatActivity> Context.startActivity (
    bundle: Bundle
) {
    startActivity(Intent(this, T::class.java).apply {
        putExtras(
            bundle
        )
        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}

inline fun <reified T : Fragment> FragmentManager.replaceFragment(
    containerViewId: Int,
    enter: Int,
    exit: Int
) {
    val fragment = T::class.java.newInstance()

    this.beginTransaction()
        .setCustomAnimations(enter, exit)
        .replace(containerViewId, fragment)
        .commit()
}

inline fun <reified T : Fragment> FragmentManager.replaceFragment(
    containerViewId: Int,
    bundle: Bundle? = null
) {
    val fragment = T::class.java.newInstance()

    if (bundle != null) {
        fragment.arguments = bundle
    }

    this.beginTransaction()
        .replace(containerViewId, fragment)
        .commit()
}