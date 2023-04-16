package kr.easw.estrader.android.util

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import kr.easw.estrader.android.util.VolleyUtil.Companion.execute
import org.bouncycastle.asn1.eac.CertificateBody.requestType
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * reflection 을 사용해 Java 객체와 JSON 간 변환 작업을 하는 라이브러리 Gson,
 * Volley Builder 패턴을 활용한 Http 통신 Template
 */
class RestRequestTemplate<request: Any?, response: Any>(
    context: Context,
    requestMethod: Int,
    requestUrl: String,
    requestParams: request?,
    responseParams: Class<response>?,
    listener: Response.Listener<response>?,
    requestHeaders: MutableMap<String, String>?
) {

    init {
        object: GsonRequest<response>(
            requestUrl,
            requestMethod,
            responseParams!!,
            requestHeaders,
            if(requestParams == null) null else JSONObject(Gson().toJson(requestParams)),
            listener!!,
            Response.ErrorListener {
                println("X3 | $it")
            }
        ) {

        }.execute(context)
    }

    class Builder<request: Any?, response: Any> {
        private var requestMethod = 0
        private var requestUrl: String? = null
        private var requestParams: request? = null
        private var responseParams: Class<response>? = null
        private var requestHeaders: MutableMap<String, String>? = null
        private var listener: Response.Listener<response>? = null

        fun setRequestMethod(requestMethod: Int): Builder<request, response> {
            this.requestMethod = requestMethod
            return this
        }

        fun setRequestUrl(requestUrl: String?): Builder<request, response> {
            this.requestUrl = requestUrl
            return this
        }

        fun setRequestParams(requestParams: request?): Builder<request, response> {
            this.requestParams = requestParams
            return this
        }

        fun setResponseParams(responseParams: Class<response>?): Builder<request, response> {
            this.responseParams = responseParams
            return this
        }

        fun setRequestHeaders(requestHeaders: MutableMap<String, String>?) : Builder<request, response> {
            this.requestHeaders = requestHeaders
            return this
        }

        fun setListener(listener: Response.Listener<response>?) : Builder<request, response> {
            this.listener = listener
            return this
        }

        fun build(context: Context): RestRequestTemplate<request, response> {
            check(requestType >= 0) { "RequestType is null" }
            checkNotNull(requestUrl) { "RequestUrl is null" }
            return RestRequestTemplate(context, requestMethod, requestUrl!!, requestParams, responseParams, listener, requestHeaders)
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
    jsonRequest: JSONObject?,
    private val listener: Response.Listener<T>,
    errorListener: Response.ErrorListener
) : JsonRequest<T>(requestMethod, url, jsonRequest?.toString(), listener, errorListener) {
    private val gson = GsonBuilder()
        .serializeNulls()
        .create()

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