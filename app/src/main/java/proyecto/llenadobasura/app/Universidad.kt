package proyecto.llenadobasura.app

import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray

import proyecto.llenadobasura.api.Api

class Universidad : ViewModel() {

    private val sm = SessionManager()
    private val api = Api()
    private lateinit var basureros : Array<String>
    private lateinit var usuarioActivo: UsuarioActivo

    fun logUsuario(usuario: String, contraseña: String){
        try {
            val respuesta = sm.IniciarSesion(usuario, contraseña)
            usuarioActivo = UsuarioActivo(
                (respuesta["usuario"] as JsonPrimitive).content,
                (respuesta["nombre"] as JsonPrimitive).content,
                (respuesta["correo"] as JsonPrimitive).content,
                (respuesta["dispositivos"] as JsonArray).map { (it as JsonPrimitive).content }.toTypedArray())
        } catch (e: ClassCastException){
            throw Exception("Usuario y/o contraseña incorrectos")
        }catch (e: Exception){
            throw e
        }
    }

    fun regUsuario(usuario: String, nombre: String, correo: String, contraseña: String, confcontraseña: String){
        try {
            val respuesta = sm.RegistrarUsuario(usuario, nombre, correo, contraseña, confcontraseña)
            if (respuesta == "Usuario ya existe")
                throw Exception("Usuario ya existe")
            usuarioActivo = UsuarioActivo(usuario, nombre, correo, arrayOf())
        }catch (e: Exception){
            throw e
        }
    }

    fun getUsuarioActivo(): UsuarioActivo{
        return usuarioActivo
    }

    fun cargarBasureros(){
        try {
            this.basureros = api.get_disp_info()
        }catch (e: Exception){
            throw e
        }
    }

    fun getBasureros(): Array<String>{
        cargarBasureros()
        return this.basureros
    }
}