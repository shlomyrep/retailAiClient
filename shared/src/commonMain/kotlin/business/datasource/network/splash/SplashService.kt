package business.datasource.network.splash

import business.datasource.network.common.MainGenericResponse
import business.domain.main.SalesMans

interface SplashService {
    companion object {
        const val REGISTER = "register"
        const val LOGIN = "auth/login"
    }

    suspend fun login(email: String, password: String): MainGenericResponse<SalesMans?>

    suspend fun register(name: String, email: String, password: String): MainGenericResponse<String?>

}