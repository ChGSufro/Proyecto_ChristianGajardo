// BienvenidaFragment.kt
package proyecto.llenadobasura.ui

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import proyecto.llenadobasura.R
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import proyecto.llenadobasura.app.MyViewModel
import proyecto.llenadobasura.app.SessionManager as SM
import proyecto.llenadobasura.app.UsuarioActivo

class InicioSesion : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        val view = inflater.inflate(R.layout.sample_inicio_sesion, container, false)
        val Model = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        val usuario: EditText = view.findViewById(R.id.Usuario)
        val contraseña: EditText = view.findViewById(R.id.Contrasena)
        val alerta: TextView = view.findViewById(R.id.Respuesta)

        view.findViewById<Button>(R.id.BotonContinuar).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val respuesta = SM().IniciarSesion(usuario.text.toString(), contraseña.text.toString())
                    Model.setUsuarioActivo(respuesta["usuario"].toString(), respuesta["nombre"].toString(), respuesta["correo"].toString(), respuesta["dispositivo"].toString())
                    findNavController().navigate(R.id.InicioSesion_to_log)

                } catch (error: Exception) {
                    withContext(Dispatchers.Main) {
                        alerta.text = error.message
                    }
                }
            }
        }

        view.findViewById<Button>(R.id.BotonRegresar).setOnClickListener {
            findNavController().popBackStack()
        }
        return view
    }

}