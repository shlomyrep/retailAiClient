package business.domain.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    var idleModeType: String = "",
    var idleThresholdInSeconds: Long = 0L,
    var screenSaver: ScreenSaver = ScreenSaver(),
    var generalSku: String = "122000358",
    var customerIdRegex: String = "^(|[45]\\d{7})$",
    var skuRegex: String = "^\\d{9}$",
    )

@Serializable
data class ScreenSaver(
    var type: String = "VIDEO",
    var images: List<String> = listOf(),
    @SerialName("video_url")
    var videoUrl: String = ""
)