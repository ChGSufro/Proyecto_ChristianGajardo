package proyecto.llenadobasura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import proyecto.llenadobasura.R
import proyecto.llenadobasura.app.MyViewModel
import proyecto.llenadobasura.app.UsuarioActivo

class UsuarioInfo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.sample_usuario_info, container, false)
        val USUARIO_ACTIVO: UsuarioActivo = ViewModelProvider(requireActivity())[MyViewModel::class.java].getUsuarioActivo()

        val menu: Spinner = view.findViewById(R.id.menu)
        val campoNombre: EditText = view.findViewById(R.id.nombre)
        val campoCorreo: EditText = view.findViewById(R.id.correo)
        val botonGuardar: Button = view.findViewById(R.id.guardar)

        campoNombre.setHint(USUARIO_ACTIVO.getNombre())
        campoCorreo.setHint(USUARIO_ACTIVO.getCorreo())

        //Configuración del menu
        val opciones = arrayOf("Perfil", "Mediciones", "Cerrar")
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menu.adapter = adapter


        //Menu
        menu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                when (selectedItem) {
                    "Perfil" -> {
                        //No hace nada
                    }
                    "Mediciones" -> {
                        findNavController().navigate(R.id.UsuarioInfo_to_Mediciones)
                    }

                    "Cerrar" -> {
                        findNavController().navigate(R.id.UsuarioInfo_to_Bienvenida)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }

        botonGuardar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    USUARIO_ACTIVO.actualizarUsuario(
                        campoNombre.text.toString(),
                        campoCorreo.text.toString()
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                        campoNombre.setHint(USUARIO_ACTIVO.getNombre())
                        campoCorreo.setHint(USUARIO_ACTIVO.getCorreo())
                        campoCorreo.setText("")
                        campoNombre.setText("")
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }
}