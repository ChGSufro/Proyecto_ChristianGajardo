// BienvenidaFragment.kt
package proyecto.llenadobasura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.llenadobasura.R

class Bienvenida : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sample_bienvenida, container, false)

        view.findViewById<Button>(R.id.BotonIniciar).setOnClickListener {
            findNavController().navigate(R.id.Bienvenida_to_InicioSesion)
        }

        view.findViewById<Button>(R.id.BotonRegistrar).setOnClickListener {
            findNavController().navigate(R.id.Bienvenida_to_RegistrarUsuario)
        }

        return view
    }
}