package ovo.sypw.wmx420.androidfinal.data.model

sealed interface ChartData {
    val title: String

    data class LineChartData(
        override val title: String,
        val labels: List<String>,
        val values: List<Float>
    ):ChartData {
        companion object {
            // 参考示例代码：Java工程师经验与工资对应情况
            fun mock() = LineChartData(
                title = "Java工程师经验与工资对应情况",
                labels = listOf("1年", "2年", "3年", "4年", "5年", "6年"),
                values = listOf(8000f, 12000f, 15000f, 20000f, 28000f, 35000f)
            )
        }
    }

    data class BarChartData(
        override val title: String,
        val labels: List<String>,
        val values: List<Float>,
        val values2: List<Float>? = null
    ):ChartData {
        companion object {
            // 参考示例代码：Java/PHP工程师经验与工资对应情况
            fun mock() = BarChartData(
                title = "Java/PHP工程师经验与工资对应情况",
                labels = listOf("1年", "2年", "3年", "4年", "5年", "6年"),
                values = listOf(8000f, 12000f, 15000f, 20000f, 28000f, 35000f),
            )
        }
    }

    data class PieChartData(
        override val title: String,
        val items: List<PieChartItem>
    ):ChartData {
        companion object {
            // 参考示例代码 PieViewModel：Android工程师薪资占比情况
            fun mock() = PieChartData(
                title = "Android工程师薪资占比情况",
                items = listOf(
                    PieChartItem("月薪8k-15k", 50f, 0xFF888888.toInt()),   // 灰色
                    PieChartItem("月薪15-30k", 25f, 0xFFE040FB.toInt()),   // 品红
                    PieChartItem("月薪30-100k", 15f, 0xFF4CAF50.toInt()),  // 绿色
                    PieChartItem("月薪100k+", 10f, 0xFF2196F3.toInt())     // 蓝色
                )
            )
        }
    }

    data class PieChartItem(
        val label: String,
        val value: Float,
        val color: Int
    )


}