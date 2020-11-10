package cn.leo.paging_adapter.net.utils

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.text.TextUtils

/**
 * NetWork Utils
 *
 * **Attentions**
 *  * You should shape_add **android.permission.ACCESS_NETWORK_STATE** in manifest,
 *  to get network status.
 *
 *
 * @author [Trinea](http://www.trinea.cn) 2014-11-03
 */


const val NETWORK_TYPE_WIFI = "wifi"
const val NETWORK_TYPE_3G = "eg"
const val NETWORK_TYPE_2G = "2g"
const val NETWORK_TYPE_WAP = "wap"
const val NETWORK_TYPE_UNKNOWN = "unknown"
const val NETWORK_TYPE_DISCONNECT = "disconnect"

/**
 * Get network type
 * 获取手机网络类型
 *
 * @return
 */
val Context.networkType: Int
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo?.type ?: -1
    }

/**
 * Get network type name
 * 获取手机网络连接类型名称
 *
 * @return
 */
val Context.networkTypeName: String
    get() {
        val manager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val networkInfo = manager?.activeNetworkInfo
        var type = NETWORK_TYPE_DISCONNECT
        if (networkInfo?.isConnected == true) {
            val typeName = networkInfo.typeName
            type = if ("WIFI".equals(typeName, ignoreCase = true)) {
                NETWORK_TYPE_WIFI
            } else if ("MOBILE".equals(typeName, ignoreCase = true)) {
                val proxyHost = android.net.Proxy.getDefaultHost()
                if (TextUtils.isEmpty(proxyHost))
                    if (isFastMobileNetwork) NETWORK_TYPE_3G else NETWORK_TYPE_2G
                else
                    NETWORK_TYPE_WAP
            } else {
                NETWORK_TYPE_UNKNOWN
            }
        }
        return type
    }

val Context.isWifiNetWork: Boolean
    get() = networkType == ConnectivityManager.TYPE_WIFI


/**
 * Whether is fast mobile network
 * 判断是否是高速数据网络
 *
 * @return
 */
val Context.isFastMobileNetwork: Boolean
    get() {
        val telephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        return when (telephonyManager?.networkType) {
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_LTE -> true
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_IDEN,
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
            else -> false
        }
    }

/**
 * Check network
 * 检查网络是否连接
 *
 * @return
 */
fun Context.checkNetwork(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val networkInfo = connectivityManager?.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}
