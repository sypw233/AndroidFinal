package ovo.sypw.wmx420.androidfinal.utils

object UrlUtils {
    /**
     * 根据 URL Scheme 获取应用名称
     */
    fun getAppNameFromScheme(url: String): String {
        val scheme = url.substringBefore("://")
        return if (scheme.isNotEmpty() && scheme != url) "${scheme}应用" else "外部应用"
    }

    /**
     * Check if the URL is a standard HTTP/HTTPS URL
     */
    fun isHttpUrl(url: String?): Boolean {
        if (url.isNullOrEmpty()) return false
        return url.startsWith("http://") || url.startsWith("https://")
    }
}
