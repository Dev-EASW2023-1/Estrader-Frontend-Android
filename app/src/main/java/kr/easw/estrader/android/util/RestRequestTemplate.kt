package kr.easw.estrader.android.util

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kr.easw.estrader.android.util.VolleyUtil.Companion.execute
import org.bouncycastle.asn1.eac.CertificateBody.requestType
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.nio.charset.Charset

class RestRequestTemplate(
    context: Context,
    requestMethod: Int,
    requestUrl: String,
    requestParams: Any,
    listener: Response.Listener<JsonObject>?,
    requestHeaders: MutableMap<String, String>?
) {

    init {
        object: GsonRequest<JsonObject>(
            requestUrl,
            requestMethod,
            object : TypeToken<JsonObject>() {}.type,
            requestHeaders,
            listener!!,
            Response.ErrorListener {
                println("X3 | $it")
            }
        ) {

        }.execute(context)
    }

    class Builder {
        private var requestMethod = 0
        private var requestUrl: String? = null
        private var requestParams: Any? = null
        private var requestHeaders: MutableMap<String, String>? = null
        private var listener: Response.Listener<JsonObject>? = null

        fun setRequestMethod(requestMethod: Int): Builder {
            this.requestMethod = requestMethod
            return this
        }

        fun setRequestUrl(requestUrl: String?): Builder {
            this.requestUrl = requestUrl
            return this
        }

        fun setRequestParams(requestParams: Any?): Builder {
            this.requestParams = requestParams
            return this
        }

        fun setRequestHeaders(requestHeaders: MutableMap<String, String>?) : Builder {
            this.requestHeaders = requestHeaders
            return this
        }

        fun setListener(listener: Response.Listener<JsonObject>?) : Builder {
            this.listener = listener
            return this
        }

        fun build(context: Context): RestRequestTemplate {
            check(requestType >= 0) { "RequestType is null" }
            checkNotNull(requestUrl) { "RequestUrl is null" }
            return RestRequestTemplate(context, requestMethod, requestUrl!!, requestParams!!, listener, requestHeaders)
        }
    }
}

open class GsonRequest<T>(
    url: String,
    requestMethod: Int,
    private val type: Type,
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
            println("X3 | $json")
            Response.success(
                gson.fromJson(json, type),
                HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Response.error(ParseError(e))
        }
    }
}