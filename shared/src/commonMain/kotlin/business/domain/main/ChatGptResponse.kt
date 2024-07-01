package business.domain.main

import kotlinx.serialization.Serializable

@Serializable
data class ChatGptResponse(
    var message: String = "",
    var data: String = ""
)