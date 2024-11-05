package proyecto.llenadobasura.app

import proyecto.llenadobasura.api.Api
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class SessionManager(){

    private val api = Api()
    private val LETRAS_MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val LETRAS_MINUSCULAS = "abcdefghijklmnopqrstuvwxyz"
    private val NUMEROS = "0123456789"
    private val CARACTERES_BLOQUEADOS = " "

    private fun checkUsuario(usuario: String): Boolean {
        if (usuario.contains(CARACTERES_BLOQUEADOS)) {
            throw Exception("El usuario no puede contener espacios.")
        }
        if (usuario.length > 20) {
            throw Exception("El usuario no puede tener más de 20 caracteres.")
        }
        if (usuario.isEmpty()) {
            return false
        }
        return true
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

    private fun checkContraseña(contraseña: String): Boolean {
        if (contraseña.isEmpty()) {
            return false
        }
        if (contraseña.contains(CARACTERES_BLOQUEADOS)) {
            throw Exception("La contraseña no puede contener espacios.")
        }
        return true
    }

    private fun checkContraseñas(contraseña: String, confcontraseña: String): Boolean {
        if (!checkContraseña(contraseña))  {
            throw Exception("Rellene todos los campos.")
        }
        if (contraseña != confcontraseña) {
            throw Exception("Las contraseñas no coinciden.")
        }
        confcontraseña.replace(" ", "")
        return true
    }

    private fun checkCorreo(correo: String): Boolean {
        if (correo.isEmpty()) {
            return false
        }
        if (correo.contains(CARACTERES_BLOQUEADOS)) {
            throw Exception("Correo inválido.")
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            throw Exception("Correo inválido.")
        }
        return true
    }

    fun IniciarSesion(usuario: String, contraseña: String): JsonObject {
        if (!checkUsuario(usuario)){
            throw Exception("Rellene todos los campos.")
        }
        val jsonUsuario = JsonObject(mapOf("usuario" to JsonPrimitive(usuario), "contraseña" to JsonPrimitive(contraseña)))
        val respuesta = api.log_usuario(jsonUsuario)
        return respuesta
    }

    fun RegistrarUsuario(usuario: String, nombre: String, correo: String, contraseña: String, confcontraseña: String): String {
        if (!checkUsuario(usuario) || !checkNombre(nombre) || !checkContraseñas(contraseña, confcontraseña) || !checkCorreo(correo)) {
            throw Exception("Rellene todos los campos.")
        }
        val jsonUsuarioNuevo = JsonObject(mapOf(
            "usuario" to JsonPrimitive(usuario),
            "nombre" to JsonPrimitive(nombre),
            "contraseña" to JsonPrimitive(contraseña),
            "correo" to JsonPrimitive(correo)))
        return api.add_usuario(jsonUsuarioNuevo)
    }
}
