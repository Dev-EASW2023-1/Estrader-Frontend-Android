package kr.easw.estrader.android.util

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kr.easw.estrader.android.util.VolleyUtil.Companion.execute
import org.bouncycastle.asn1.eac.CertificateBody.requestType
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * reflection 을 사용해 Java 객체와 JSON 간 변환 작업을 하는 라이브러리 Gson,
 * Volley Builder 패턴을 활용한 Http 통신 Template
 *
 * 서버로부터 응답 구현, 서버에 요청 미구현
 */
class RestRequestTemplate<T: Any>(
    context: Context,
    requestMethod: Int,
    requestUrl: String,
    requestParams: Class<T>?,
    listener: Response.Listener<T>?,
    requestHeaders: MutableMap<String, String>?
) {

    init {
        object: GsonRequest<T>(
            requestUrl,
            requestMethod,
            requestParams!!,
            requestHeaders,
            listener!!,
            Response.ErrorListener {
                println("X3 | $it")
            }
        ) {

        }.execute(context)
    }

    class Builder<T : Any> {
        private var requestMethod = 0
        private var requestUrl: String? = null
        private var requestParams: Class<T>? = null
        private var requestHeaders: MutableMap<String, String>? = null
        private var listener: Response.Listener<T>? = null

        fun setRequestMethod(requestMethod: Int): Builder<T> {
            this.requestMethod = requestMethod
            return this
        }

        fun setRequestUrl(requestUrl: String?): Builder<T> {
            this.requestUrl = requestUrl
            return this
        }

        fun setRequestParams(requestParams: Class<T>?): Builder<T> {
            this.requestParams = requestParams
            return this
        }

        fun setRequestHeaders(requestHeaders: MutableMap<String, String>?) : Builder<T> {
            this.requestHeaders = requestHeaders
            return this
        }

        fun setListener(listener: Response.Listener<T>?) : Builder<T> {
            this.listener = listener
            return this
        }

        fun build(context: Context): RestRequestTemplate<T> {
            check(requestType >= 0) { "RequestType is null" }
            checkNotNull(requestUrl) { "RequestUrl is null" }
            return RestRequestTemplate(context, requestMethod, requestUrl!!, requestParams, listener, requestHeaders)
        }
    }
}

/**
 * Make a GET request and return a parsed object from JSON.
 *
 * @param url URL of the request to make
 * @param clazz Relevant class object, for Gson's reflection
 * @param headers Map of request headers
 */
open class GsonRequest<T>(
    url: String,
    requestMethod: Int,
    private val clazz: Class<T>,
    private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<T>,
    errorListener: Response.ErrorListener
) : Request<T>(requestMethod, url, errorListener) {
    private val gson = Gson()

    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun deliverResponse(response: T) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            val json = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
            Response.success(
                gson.fromJson(json, clazz),
                HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Response.error(ParseError(e))
        }
    }
}