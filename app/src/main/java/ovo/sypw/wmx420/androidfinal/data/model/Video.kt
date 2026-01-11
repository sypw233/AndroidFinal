package ovo.sypw.wmx420.androidfinal.data.model

data class Video(
    val id: String,
    val name: String,
    val coverUrl: String,
    val intro: String,
    val videoDetailList: List<VideoDetail> = emptyList()
) {
    companion object {
        fun mock(): List<Video> = listOf(
            Video(
                id = "1001",
                name = "航拍中国：紫禁城的四季轮转",
                coverUrl = "https://picsum.photos/seed/v1/800/450",
                intro = "采用 8K 超清画质，带你领略故宫从春日繁花到冬日皑皑白雪的极致美感。这不仅是一场视觉盛宴，更是一次穿越时空的文化对话。",
                videoDetailList = listOf(
                    VideoDetail("1001", "第一集：春风拂过红墙", "https://www.w3schools.com/html/mov_bbb.mp4"),
                    VideoDetail("1001", "第二集：夏雨润泽琉璃", "https://www.w3schools.com/html/movie.mp4")
                )
            ),
            Video(
                id = "1002",
                name = "Android 糕手进阶之路：Compose 动画实战",
                coverUrl = "https://picsum.photos/seed/v2/800/450",
                intro = "深度解析 Jetpack Compose 动画系统。从基础的 animate*AsState 到复杂的 Transition，手把手教你打造丝滑的交互体验。",
                videoDetailList = listOf(
                    VideoDetail("1002", "1. 布局动画入门", "https://www.w3schools.com/html/mov_bbb.mp4")
                )
            ),
            Video(
                id = "1003",
                name = "极客厨房：如何制作一杯完美的咖啡？",
                coverUrl = "https://picsum.photos/seed/v3/800/450",
                intro = "从豆子的产地选择到研磨刻度，再到水温与压力的精准控制。这是一场关于风味萃取的科学实验。",
                videoDetailList = listOf(
                    VideoDetail("1003", "准备篇：豆种的选择", "url_placeholder"),
                    VideoDetail("1003", "实践篇：拉花的艺术", "url_placeholder"),
                    VideoDetail("1003", "进阶篇：意式浓缩的秘密", "url_placeholder")
                )
            ),
            Video(
                id = "1004",
                name = "赛博朋克 2077：夜之城生存指南",
                coverUrl = "https://picsum.photos/seed/v4/800/450",
                intro = "在光怪陆离的未来都市，生存是唯一的法则。这里有最全的装备获取攻略与隐藏任务解析。别让这座城市把你生吞活剥了。",
                videoDetailList = emptyList() // 测试空列表状态
            ),
            Video(
                id = "1005",
                name = "一个超级无敌爆炸长标题的测试视频——为了看看我们的 UI 到底会不会在多行显示时出现挤压或被切断的情况",
                coverUrl = "https://picsum.photos/seed/v5/800/450",
                intro = "短介绍。",
                videoDetailList = emptyList()
            )
        )
    }
}

data class VideoDetail(
    val videoId: String,
    val videoName: String,
    val videoUrl: String
)
