package kr.easw.estrader.android.util

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley


class VolleyUtil private constructor(context: Context) {
    private val queue by lazy { Volley.newRequestQueue(context) }

    companion object {
        @Volatile
        private var instance: VolleyUtil? = null

        private fun getInstance(context: Context): VolleyUtil {
            return instance ?: synchronized(this) {
                instance ?: VolleyUtil(context).also {
                    instance = it
                }
            }
        }

        fun<T> Request<T>.execute(context: Context) {
            retryPolicy = DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            getInstance(context).request(this)
        }
    }

    private fun<T> request(req: Request<T>) {
        queue.add(req)
    }
}
