package business.domain.main

import kotlinx.serialization.Serializable

@Serializable
data class DeviceData (
    var uuid: String = "",
    var username: String = "",
    var name: String = "",
    var version: String = "",
    var fcm: String = "",
    var deviceType: String = "",
    var lastInteractionTime: Long = 0,
    var versionCode: Int = 0
)