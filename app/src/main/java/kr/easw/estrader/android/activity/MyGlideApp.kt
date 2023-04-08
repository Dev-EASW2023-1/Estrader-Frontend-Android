package kr.easw.estrader.android.activity

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Glide Glide timeout set
 *
 * connectTimeOut: 설정된 연결 시간 제한 내에 서버 연결할 수 없는 경우, 해당 요청을 실패한 것으로 계산
 * ReadTimeOut: 서버로부터 응답 시간이 읽기 시간보다 크면, 해당 요청을 실패한 것으로 계산
 */

@GlideModule
class MyGlideApp : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
        val factory = OkHttpUrlLoader.Factory(client)
        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }
}