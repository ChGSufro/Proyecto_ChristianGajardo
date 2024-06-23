package proyecto.llenadobasura.app

import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import proyecto.llenadobasura.api.Api

class UsuarioActivo(usuario: String, nombre: String, correo: String, dispositivo: String){

    private val API: Api = Api()

    private val Usuario: String = usuario.replace("\"", "")
    private var Nombre: String = nombre.replace("\"", "")
    private var Correo: String = correo.replace("\"", "")
    private var Dispositivo = dispositivo.replace("\"", "")
    private val Basurero: Basurero = Basurero()

    fun getUsuario(): String{
        return this.Usuario
    }

    fun getNombre(): String{
        return this.Nombre
    }

    fun getCorreo(): String{
        return this.Correo
    }

    fun getDisp(): String{
        return this.Dispositivo
    }

    fun getCapacidadBasurero(): Int{
        obtenerInfoDisp()
        return this.Basurero.getCapacidad()
    }

    fun getAlturaBasura(): Int{
        obtenerInfoDisp()
        return this.Basurero.getAlturaBasura()
    }

    fun actualizarDispositivo(disp: String): Boolean{
        if (disp == this.Dispositivo){
            return false
        }
        val usr = JsonObject(mapOf("usuario" to JsonPrimitive(Usuario), "nombre" to JsonPrimitive(Nombre), "correo" to JsonPrimitive(Correo), "dispositivo" to JsonPrimitive (disp)))
        API.update_usuario(usr)
        this.Dispositivo = disp
        return true
    }

    fun actualizarUsuario(nombre: String, correo: String){
        if (nombre == this.Nombre || correo == this.Correo){
            throw Exception("El valor del nombre y correo no pueden ser iguales a los actuales")
        }
        if (nombre.replace(" ", "") == "" && correo.replace(" ", "") == ""){
            throw Exception("El valor del nombre y correo no pueden ser vacios")
        }
        if (nombre.length > 50 && correo.length > 50){
            throw Exception("El valor del nombre y correo no pueden ser mayores a 50 caracteres")
        }
        if (nombre != this.Nombre && nombre.replace(" ", "") != ""){
            this.Nombre = nombre
        }
        if (correo != this.Correo && correo.replace(" ", "") != ""){
            this.Correo = correo
        }
        val usr = JsonObject(mapOf("usuario" to JsonPrimitive(Usuario), "nombre" to JsonPrimitive(Nombre), "correo" to JsonPrimitive(Correo), "dispositivo" to JsonPrimitive (Dispositivo)))
        API.update_usuario(usr)
    }

    fun actualizarCapacidadBasurero(capacidad: Int){
        if (capacidad >= 1000){
            throw Exception("El valor de la capacidad no puede ser mayor a 1000 cm")
        }
        if (capacidad == this.Basurero.getCapacidad()){
            throw Exception("El valor de la capacidad no puede ser igual al actual")
        }
        val usr = JsonObject(mapOf("dispositivo" to JsonPrimitive(this.Dispositivo), "capacidad" to JsonPrimitive(capacidad)))
        API.update_dispositivo(usr)
        this.Basurero.setCapacidad(capacidad)
    }

    fun obtenerPorcentajeLlenado(): Int{
        obtenerInfoDisp()
        try {
            return this.Basurero.getPorcentajeLlenado()
        } catch (e: ArithmeticException){
            return 0
        }
    }

    fun obtenerNombresDispositivos(): List<String> {
        val dispositivos = API.get_disp_disponibles()["respuesta"] as JsonArray
        val nombresDispositivos = dispositivos.map { (it as JsonPrimitive).content }
        return nombresDispositivos
    }

    fun obtenerInfoDisp(){
        val info_disp = API.get_disp_info(Dispositivo)["respuesta"] as JsonObject
        val ultimo_dato = API.get_ultimo_dato_disp(Dispositivo)["respuesta"] as JsonObject
        val capacidad= (info_disp["capacidad"] as JsonPrimitive).int
        val distancia_basura = (ultimo_dato["distancia"] as JsonPrimitive).float.toInt()
        Basurero.setCapacidad(capacidad)
        Basurero.setAlturaBasura(capacidad - distancia_basura)
    }

}

