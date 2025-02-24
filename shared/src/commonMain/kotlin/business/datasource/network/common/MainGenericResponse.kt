package business.datasource.network.common


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MainGenericResponse<T>(
    @SerialName("result") var result: T?,
    @SerialName("token") var token: String?,
    @SerialName("success") var status: Boolean?,
    @SerialName("alert") var alert: JAlertResponse? = JAlertResponse(),
)