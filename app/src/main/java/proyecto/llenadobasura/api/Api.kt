package proyecto.llenadobasura.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class Api {
    private val cliente = OkHttpClient()
    private val URL_IP: String = "http://52.0.17.176:8081"

    private fun formatJson_toRequestBody(json: JsonObject): RequestBody {
        val jsonString: String = json.toString()
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        return jsonString.toRequestBody(jsonMediaType)
    }

    private fun formatResponse_toJson(response: Response): JsonObject {
        val jsonString: String = response.body?.string() ?: ""
        return Json.parseToJsonElement(jsonString) as JsonObject
    }

    fun log_usuario(usuario: JsonObject) : JsonObject{
        val usr = formatJson_toRequestBody(usuario)
        val request = Request.Builder().url("$URL_IP/usuario/log").post(usr).build()
        val response = cliente.newCall(request).execute()
        return formatResponse_toJson(response)
    }

    fun add_usuario(usuario: JsonObject): JsonObject {
        val usr = formatJson_toRequestBody(usuario)
        val request = Request.Builder().url("$URL_IP/usuario/add").put(usr).build()
        val response = cliente.newCall(request).execute()
        return formatResponse_toJson(response)
    }

    fun update_usuario(usuario: JsonObject) {
        val usr = formatJson_toRequestBody(usuario)
        val request = Request.Builder().url("$URL_IP/usuario/put").put(usr).build()
        cliente.newCall(request).execute()
    }


    fun get_disp_disponibles(): JsonObject {
        val request = Request.Builder().url("$URL_IP/dispositivos/get").get().build()
        val response = cliente.newCall(request).execute()
        return formatResponse_toJson(response)
    }

    fun get_disp_info(id_disp: String): JsonObject {
        val request = Request.Builder().url("$URL_IP/dispositivo/get/$id_disp").get().build()
        val response = cliente.newCall(request).execute()
        return formatResponse_toJson(response)
    }

    fun get_ultimo_dato_disp(id_disp: String): JsonObject {
        val request = Request.Builder().url("$URL_IP/dato/get/$id_disp").get().build()
        val response = cliente.newCall(request).execute()
        return formatResponse_toJson(response)
    }

    fun update_dispositivo(disp: JsonObject) {
        val request = Request.Builder().url("$URL_IP/dispositivo/put").put(formatJson_toRequestBody(disp)).build()
        cliente.newCall(request).execute()
    }

}