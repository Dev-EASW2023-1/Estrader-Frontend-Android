package kr.easw.estrader.android.util

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.Volley

class VolleyUtil private constructor(context: Context) {
    private val queue by lazy { Volley.newRequestQueue(context) }

    companion object {
        private var instance: VolleyUtil? = null

        fun getInstance(context: Context): VolleyUtil {
            return instance ?: synchronized(this) {
                instance ?: VolleyUtil(context).also {
                    instance = it
                }
            }
        }
    }

    fun<T> request(req: Request<T>) {
        queue.add(req)
    }
}