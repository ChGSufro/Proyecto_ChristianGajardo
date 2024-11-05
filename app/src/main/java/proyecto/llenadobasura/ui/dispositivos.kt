package proyecto.llenadobasura.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import proyecto.llenadobasura.R
import proyecto.llenadobasura.app.Universidad


class dispositivos() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.dispositivos, container, false)
        val UNIVERSIDAD = ViewModelProvider(requireActivity())[Universidad::class.java]
        val USUARIO_ACTIVO = UNIVERSIDAD.getUsuarioActivo()

        val menu: Spinner = view.findViewById(R.id.menu)
        val listaEmparejados = view.findViewById<ListView>(R.id.listaDispEmparejados)
        val actualizar = view.findViewById<View>(R.id.botonEmparejar)


        // Aquí se crea el menú desplegable
        val opciones_menu = arrayOf("Dispositivos", "Mediciones", "Perfil", "Cerrar")
        val adapter_menu = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones_menu)
        adapter_menu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menu.adapter = adapter_menu

        menu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                when (selectedItem) {
                    "Dispositivos" -> {
                        // No hacer nada
                    }
                    "Mediciones" -> {
                        findNavController().navigate(R.id.dispositivos_to_Mediciones)
                    }
                    "Perfil" -> {
                        findNavController().navigate(R.id.Mediciones_to_UsuarioInfo)
                    }

                    "Cerrar" -> {
                        findNavController().navigate(R.id.Mediciones_to_Bienvenida)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Aquí se obtienen los dispositivos vinculados y se muestran en la lista
        fun cargarDispositivos() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dispositivos = UNIVERSIDAD.getBasureros()
                    val dispositivos_activos = USUARIO_ACTIVO.getDispositivos()

                    val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.lista_con_checkbox, R.id.device_name, dispositivos) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent)
                            val checkBox = view.findViewById<CheckBox>(R.id.device_checkbox)
                            checkBox.isChecked = dispositivos_activos.contains(dispositivos[position])

                            checkBox.setOnCheckedChangeListener { _, isChecked ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (isChecked) {
                                        USUARIO_ACTIVO.agregarDispositivo(dispositivos[position])
                                    } else {
                                        USUARIO_ACTIVO.eliminarDispositivo(dispositivos[position])
                                    }
                                }
                            }
                            return view
                        }
                    }

                    withContext(Dispatchers.Main) {
                        listaEmparejados.adapter = adapter
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al cargar los dispositivos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        cargarDispositivos()


        // Aquí se actualiza la lista de dispositivos emparejados
        actualizar.setOnClickListener {
            cargarDispositivos()
        }


        return view
    }
}