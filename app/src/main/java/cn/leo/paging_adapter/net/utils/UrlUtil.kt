package cn.leo.paging_adapter.net.utils

import java.util.*


/**
 * @author leo
 */
object UrlUtil {
    /**
     * 以map形式获取url的参数列表
     *
     * @param url 链接
     * @return 参数集合
     */
    fun getUrlParamsMap(url: String): MutableMap<String, String> {
        val params = TreeMap<String, String>()
        //切分基础url和参数
        val splitBp = url.split("\\?".toRegex(), 2).toTypedArray()
        //String baseUrl = splitBp[0]; //拿到基础地址
        if (splitBp.size > 1) {
            val paramStr = splitBp[1]
            //获取每个参数的键值对
            val singleParam =
                paramStr.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in singleParam) {
                //把键值对拆开获取键和值
                val param = s.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                //参数键
                val key = param[0]
                //参数值
                val value = param[1]
                params[key] = value
            }
        }
        return params
    }

    /**
     * 获得按字母排序后的参数字符串
     *
     * @param params 参数集合
     * @return 排序后的参数字符串
     */
    fun getUrlParamSortString(params: Map<String, String>): String {
        val urlSb = StringBuilder()
        //添加基础地址和参数分割符
        val paramSet = params.entries
        //遍历键值对
        for ((key, value) in paramSet) {
            urlSb.append(key)
                .append("=")
                .append(value)
                .append("&")
        }
        urlSb.deleteCharAt(urlSb.length - 1)
        return urlSb.toString()
    }

    /**
     * 拿到连接中除了参数的地址
     */
    fun getBaseUrl(url: String): String {
        //切分基础url和参数
        val splitBp = url.split("\\?".toRegex(), 2).toTypedArray()
        //拿到基础地址
        return splitBp[0]
    }

    /**
     * 把基础地址和参数组合成url地址
     */
    fun getUrlString(baseUrl: String, params: Map<String, String>): String {
        val urlSb = StringBuilder(baseUrl)
        //添加基础地址和参数分割符
        urlSb.append("?")
        val paramSet = params.entries
        //遍历键值对
        for ((key, value) in paramSet) {
            urlSb.append(key).append("=").append(value).append("&")
        }
        urlSb.deleteCharAt(urlSb.length - 1)
        return urlSb.toString()
    }

    /**
     * 往url插入map参数
     */
    fun addParamsToUrl(url: String, params: Map<String, String>): String {
        val baseUrl = getBaseUrl(url)
        val urlParamsMap = getUrlParamsMap(url)
        urlParamsMap.putAll(params)
        return getUrlString(baseUrl, urlParamsMap)
    }

    /**
     * 往url插入一个参数
     */
    fun addParamToUrl(url: String, key: String, value: String): String {
        val baseUrl = getBaseUrl(url)
        val urlParamsMap = getUrlParamsMap(url)
        urlParamsMap[key] = value
        return getUrlString(baseUrl, urlParamsMap)
    }
    //中文编码转化
    //URLEncoder.encode("张三","UTF-8");
    //URLDecoder.decode("%E6%B5%8B","UTF-8");
}
