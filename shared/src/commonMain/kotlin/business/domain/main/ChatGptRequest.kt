package business.domain.main

import kotlinx.serialization.Serializable

@Serializable
data class ChatGptRequest(
    var message: String = "",
    var data: String = "64b639d2838642001bbcb14e"
)