package proyecto.llenadobasura.main

import kotlinx.serialization.json.JsonObject
import proyecto.llenadobasura.api.Api
import proyecto.llenadobasura.app.UsuarioActivo
import kotlin.reflect.typeOf


fun main(){
    val api: Api = Api()
    println(api.get_disp_info("212901075"))
    println(api.get_disp_disponibles()["respuesta"])
    println(api.get_ultimo_dato_disp("212901075"))
    val UsuarioActivo = UsuarioActivo("usuario", "nombre", "correo", "212901075")
    println(UsuarioActivo.obtenerPorcentajeLlenado())
    println(UsuarioActivo.getCapacidadBasurero())
    println(UsuarioActivo.getAlturaBasura())
    println(UsuarioActivo.obtenerNombresDispositivos())
}
