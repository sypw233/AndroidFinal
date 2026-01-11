package ovo.sypw.wmx420.androidfinal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BilibiliRankingResponse(
    val code: Int,
    val message: String,
    val data: BilibiliRankingData? = null
)

@Serializable
data class BilibiliRankingData(
    val note: String? = null,
    val list: List<BilibiliRankingItem> = emptyList()
)

@Serializable
data class BilibiliRankingItem(
    val aid: Long,
    val bvid: String,
    val pic: String,
    val title: String,
    val tname: String,
    val owner: BilibiliOwner,
    val stat: BilibiliStat,
)

@Serializable
data class BilibiliOwner(
    val mid: Long,
    val name: String,
    val face: String
)

@Serializable
data class BilibiliStat(
    val view: Long, // 播放量
    val danmaku: Long, // 弹幕量
    val reply: Long, // 评论量
    val favorite: Long, // 收藏量
    val coin: Long, // 投币量
    val share: Long, // 分享数
    @SerialName("now_rank")
    val nowRank: Long, // 当前排名
    @SerialName("his_rank")
    val hisRank: Long, // 历史排名
    val like: Long, // 点赞数
    val dislike: Long, // 点踩数
)

