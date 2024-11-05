package proyecto.llenadobasura.main

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.json.JSONException
import proyecto.llenadobasura.api.Api
import proyecto.llenadobasura.app.SessionManager
import proyecto.llenadobasura.app.Universidad
import proyecto.llenadobasura.app.UsuarioActivo
import kotlin.reflect.typeOf


fun main(){
    try {
        val api = Api()
        val sm = SessionManager()
        println( api.get_disp_info())
        println(api.get_ultimo_dato_disp("Dispositivo:001"))
        println(sm.RegistrarUsuario("212901075", "Jorge", "christiang9982@gmail.com", "1234", "1234"))
        println(Universidad().getBasureros()[0] == "Dispositivo:001")


    }catch (e: ClassCastException){
        println("Usuario y/o contraseña incorrectos")

    } catch (e: Exception){
        println(e.stackTraceToString())
    }
}
