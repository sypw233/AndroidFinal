package ovo.sypw.wmx420.androidfinal.data.model

data class Banner(
    val id: String,
    val title: String,
    val linkUrl: String,
    val imageUrl: String,
    val order: Int = 0
){
    companion object{
        fun mock():List<Banner> = listOf(
            Banner(
                id = "1",
                title = "Title 1",
                linkUrl = "https://picsum.photos/800/600",
                imageUrl = "https://picsum.photos/800/600",
                order = 1
            ),
            Banner(
                id = "2",
                title = "Title 2",
                linkUrl = "https://picsum.photos/800/600",
                imageUrl = "https://picsum.photos/800/600",
                order = 2
            ),
            Banner(
                id = "3",
                title = "Title 3",
                linkUrl = "https://picsum.photos/800/600",
                imageUrl = "https://picsum.photos/800/600",
                order = 3
            ),
        )
    }
}
