package ovo.sypw.wmx420.androidfinal.data.model

data object NewsViewType {
    const val VIEW_TYPE_ONLY_TEXT = 0
    const val VIEW_TYPE_HAS_IMAGE = 1
}

data class NewsListData(
    val banners: List<Banner>,
    val news: List<News>,
    val hasMore: Boolean
)
data class News(
    val id: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val author: String,
    val publishTime: String,
    val url: String,
    val viewType: Int = if (imageUrl == null) NewsViewType.VIEW_TYPE_ONLY_TEXT else NewsViewType.VIEW_TYPE_HAS_IMAGE
) {
    companion object {
        fun mock(page: Int = 1, pageSize: Int = 20): List<News> {
            val startIndex = (page - 1) * pageSize
            return (startIndex until startIndex + pageSize).map { index ->
                News(
                    id = "news_$index",
                    title = "Title $index",
                    content = "Content $index",
                    imageUrl = "https://picsum.photos/800/600",
                    author = "位置养",
                    publishTime = "2026-1-1 11:33",
                    url = "https://picsum.photos/800/600",
                )
            }
        }
    }
}
