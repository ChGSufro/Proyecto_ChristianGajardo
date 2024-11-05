package proyecto.llenadobasura.app

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import proyecto.llenadobasura.api.Api

class UsuarioActivo(usuario: String, nombre: String, correo: String, basureros: Array<String>){

    private val api: Api = Api()

    private val Usuario: String = usuario
    private var Nombre: String = nombre
    private var Correo: String = correo
    private val Dispositivos: ArrayList<String> = ArrayList(basureros.toList())
    private val DispositivoActivo = Basurero("", 0, 0)

    init {
        try {
            if (Dispositivos.isNotEmpty()){
                DispositivoActivo.setNombre(Dispositivos[0])
            }
        }catch (e: NullPointerException){
            println(Dispositivos)
        }
    }

    fun getUsuario(): String{
        return Usuario
    }

    fun getNombre(): String{
        return Nombre
    }

    fun getCorreo(): String{
        return Correo
    }

    fun getDispositivos(): ArrayList<String>{
        return Dispositivos
    }

    fun getDispositivoActivo(): Basurero{
        cargarDatosDispositivoActivo()
        return DispositivoActivo
    }

    fun setNombre(nombre: String){
        Nombre = nombre
        subirDataUsuario()
    }

    fun setCorreo(correo: String){
        Correo = correo
        subirDataUsuario()
    }

    fun setDispositivoActivo(nombre: String){
        DispositivoActivo.setNombre(nombre)
    }

    private fun checkNombre(nombre: String): Boolean {
        if (nombre.isEmpty()) {
            return false
        }
        if (nombre.length > 30) {
            throw Exception("El nombre no puede tener más de 30 caracteres.")
        }
        return true
    }

    private fun checkCorreo(correo: String): Boolean {
        if (correo.isEmpty()) {
            return false
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            throw Exception("Correo inválido.")
        }
        return true
    }

    fun actualizarDatosUsuario(nombre: String, correo: String){
        if (nombre.isEmpty() && correo.isEmpty())
            throw Exception("No se ha ingresado ningún dato.")
        if (nombre != this.Nombre && checkNombre(nombre))
            setNombre(nombre)
        if (correo != this.Correo && checkCorreo(correo))
            setCorreo(correo)
    }

    fun subirDataUsuario(){
        val json = JsonObject(
            mapOf("usuario" to JsonPrimitive(this.Usuario),
                "nombre" to JsonPrimitive(this.Nombre),
                "correo" to JsonPrimitive(this.Correo),
                "dispositivos" to JsonArray(this.Dispositivos.map { JsonPrimitive(it) })))
        println(json["dispositivos"])
        return api.update_usuario(json)
    }

    fun cargarDatosDispositivoActivo(){
        val json = api.get_ultimo_dato_disp(this.DispositivoActivo.getNombre())
        DispositivoActivo.setCapacidad((json["capacidad"] as JsonPrimitive).int)
        DispositivoActivo.setLlenado((json["distancia"] as JsonPrimitive).int)
    }

    fun agregarDispositivo(nombre: String){
        Dispositivos.add(nombre)
        subirDataUsuario()
    }

    fun eliminarDispositivo(nombre: String){
        Dispositivos.remove(nombre)
        subirDataUsuario()
        if (Dispositivos.isEmpty()){
            DispositivoActivo.setNombre("")
            DispositivoActivo.setCapacidad(0)
            DispositivoActivo.setLlenado(0)
            return
        }
        if (nombre == DispositivoActivo.getNombre()){
            DispositivoActivo.setNombre(Dispositivos[0])
        }
    }


}

