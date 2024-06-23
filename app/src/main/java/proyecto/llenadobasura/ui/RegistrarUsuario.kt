package proyecto.llenadobasura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.llenadobasura.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import proyecto.llenadobasura.app.SessionManager as SM

class RegistrarUsuario : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.sample_registrar_usuario, container, false)

        val usuario: EditText = view.findViewById(R.id.Usuario)
        val nombre: EditText = view.findViewById(R.id.Nombre)
        val contraseña: EditText = view.findViewById(R.id.Contraseña)
        val confcontraseña: EditText = view.findViewById(R.id.ConfirmarContraseña)
        val correo: EditText = view.findViewById(R.id.Correo)

        val alerta = view?.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.Respuesta)

        view.findViewById<Button>(R.id.BotonRegistrar).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                try {
                    val response = SM().RegistrarUsuario(
                        usuario.text.toString(),
                        nombre.text.toString(),
                        contraseña.text.toString(),
                        confcontraseña.text.toString(),
                        correo.text.toString())

                    withContext(Dispatchers.Main){
                        alerta?.text = response
                        if (response == "Usuario agregado."){
                            findNavController().popBackStack()
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (error: Exception) {
                    withContext(Dispatchers.Main){
                        alerta?.text = error.message
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