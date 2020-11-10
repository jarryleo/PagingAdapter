package cn.leo.paging_adapter.net.interceptor

import android.text.TextUtils
import android.util.Log
import cn.leo.paging_adapter.net.utils.UrlUtil
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.util.*
import kotlin.jvm.Throws

/**
 * @author : leo
 * @date : 2019/4/18
 * 公共参数签名拦截器
 */
class SignInterceptor : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = rebuildRequest(chain.request())
        return chain.proceed(request)
    }

    @Throws(IOException::class)
    private fun rebuildRequest(request: Request): Request {
        return when (request.method) {
            "POST" -> rebuildPostRequest(request)
            "GET" -> rebuildGetRequest(request)
            else -> request
        }
    }

    @Throws(IOException::class)
    private fun rebuildGetRequest(request: Request): Request {
        val url = request.url.toString()
        val urlString = getSignUrl(url)
        val requestBuilder = request.newBuilder()
        return requestBuilder.url(urlString).build()
    }

    @Throws(IOException::class)
    private fun rebuildPostRequest(request: Request): Request {
        //对参数进行签名
        val signParams = TreeMap<String, String>()
        val originalRequestBody = request.body!!
        var newRequestBody: RequestBody? = null
        // 传统表单
        if (originalRequestBody is FormBody) {
            val builder = FormBody.Builder()
            val requestBody = request.body as FormBody?
            val fieldSize = requestBody?.size ?: 0
            for (i in 0 until fieldSize) {
                signParams[requestBody!!.name(i)] = requestBody.value(i)
            }
            val sign = sign(signParams, true)
            for ((key, value) in signParams) {
                builder.add(key, value)
            }
            builder.add(SIGN_KEY, sign)
            newRequestBody = builder.build()
        } else if (originalRequestBody is MultipartBody) {
            //文件提交
            val requestBody = request.body as MultipartBody?
            val multipartBodybuilder = MultipartBody.Builder()
            if (requestBody != null) {
                for (i in 0 until requestBody.size) {
                    val part = requestBody.part(i)
                    multipartBodybuilder.addPart(part)
                    val headers1 = part.headers
                    for (name in headers1!!.names()) {
                        Log.d(javaClass.simpleName, "name$name")
                    }
                    val mediaType = part.body.contentType()
                    if (mediaType == null) {
                        val normalParamKey: String
                        val normalParamValue: String
                        try {
                            normalParamValue = getParamContent(requestBody.part(i).body)
                            val headers = part.headers
                            if (!TextUtils.isEmpty(normalParamValue) && headers != null) {
                                for (name in headers.names()) {
                                    val headerContent = headers[name]
                                    if (!TextUtils.isEmpty(headerContent)) {
                                        val normalParamKeyContainer =
                                            headerContent!!.split("name=\"".toRegex())
                                                .dropLastWhile { it.isEmpty() }
                                                .toTypedArray()
                                        if (normalParamKeyContainer.size == 2) {
                                            normalParamKey =
                                                normalParamKeyContainer[1].split("\"".toRegex())
                                                    .dropLastWhile { it.isEmpty() }
                                                    .toTypedArray()[0]
                                            signParams[normalParamKey] = normalParamValue
                                            break
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
            val sign = sign(signParams, true)
            for (paramKey in signParams.keys) {
                multipartBodybuilder.addFormDataPart(paramKey, signParams[paramKey]!!)
            }
            multipartBodybuilder.addFormDataPart(SIGN_KEY, sign)
            newRequestBody = multipartBodybuilder.setType(MultipartBody.FORM).build()
        } else {
            return request
        }
        return request.newBuilder().method(request.method, newRequestBody).build()

    }

    /**
     * 获取常规post请求参数
     */
    @Throws(IOException::class)
    private fun getParamContent(body: RequestBody): String {
        val buffer = Buffer()
        body.writeTo(buffer)
        return buffer.readUtf8()
    }

    companion object {
        private const val SIGN_KEY = "Sign"

        @Throws(IOException::class)
        fun getSignUrl(url: String): String {
            val baseUrl = UrlUtil.getBaseUrl(url)
            val urlParamsMap = UrlUtil.getUrlParamsMap(url)
            val sign = sign(urlParamsMap, false)
            urlParamsMap[SIGN_KEY] = sign
            return UrlUtil.getUrlString(baseUrl, urlParamsMap)
        }

        @Throws(IOException::class)
        private fun sign(params: Map<String, String>, isPost: Boolean): String {
            //添加公共参数

            return UrlUtil.getUrlParamSortString(params)
        }
    }


}
