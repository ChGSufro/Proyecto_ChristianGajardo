package proyecto.llenadobasura.app

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {

    private lateinit var USUARIO_ACTIVO: UsuarioActivo

    fun setUsuarioActivo(usuario: String, nombre: String, correo: String, dispositivo: String){
        USUARIO_ACTIVO = UsuarioActivo(usuario, nombre, correo, dispositivo)
    }

    fun getUsuarioActivo(): UsuarioActivo{
        return USUARIO_ACTIVO
    }
}