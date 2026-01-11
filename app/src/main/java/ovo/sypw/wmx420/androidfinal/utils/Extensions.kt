package ovo.sypw.wmx420.androidfinal.utils

import com.google.firebase.auth.FirebaseUser
import ovo.sypw.wmx420.androidfinal.data.model.User
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 从 HTML 字符串中提取第一张图片的 URL
 * @return 图片链接，如果未找到则返回 null
 */
fun String.extractFirstImageUrl(): String? {
    val regex = """<img[^>]+src\s*=\s*['"]([^'"]+)['"][^>]*>""".toRegex(RegexOption.IGNORE_CASE)

    // find 函数只查找第一个匹配项，效率极高
    val matchResult = regex.find(this)

    // groupValues[1] 是第一个括号捕获的内容，即 URL
    return matchResult?.groupValues?.getOrNull(1)
}

fun String.formatDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.CHINA)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(this)
        outputFormat.format(date ?: return this)
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}
fun Long.formatCount(): String {
    return when {
        this >= 100000000 -> String.format("%.1f亿", this / 100000000.0)
        this >= 10000 -> String.format("%.1f万", this / 10000.0)
        this >= 1000 -> String.format("%.1fk", this / 1000.0)
        else -> this.toString()
    }
}
fun FirebaseUser.toUser(): User {
    return User(
        uid = uid,
        email = email ?: "",
        avatarUrl = photoUrl?.toString(),
        displayName = displayName
    )



}