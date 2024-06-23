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

class Mediciones : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mediciones, container, false)
        val USUARIO_ACTIVO: UsuarioActivo = ViewModelProvider(requireActivity())[MyViewModel::class.java].getUsuarioActivo()

        val menu: Spinner = view.findViewById(R.id.menu)
        val grafico_basurero: BarChart = view.findViewById(R.id.grafico_basurero)
        val etiqueta_altura_basurero: TextView = view.findViewById(R.id.etiqueta_altura_basura)
        val listaDispisitivos: Spinner = view.findViewById(R.id.dispositivos)
        val campoCapacidad: EditText = view.findViewById(R.id.campo_capacidad_basurero)
        val botonGuardar: Button = view.findViewById(R.id.boton_guardar)

        // Configurar el menú
        val opciones = arrayOf("Mediciones", "Perfil", "Cerrar")
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menu.adapter = adapter


        // Configura el gráfico
        setGrafico(grafico_basurero, R.color.dark_green)
        grafico_basurero.axisRight.isEnabled = false
        grafico_basurero.xAxis.setDrawGridLines(false)
        grafico_basurero.axisLeft.setDrawGridLines(false)
        grafico_basurero.axisRight.setDrawGridLines(false)
        grafico_basurero.description.isEnabled = false
        grafico_basurero.data.setDrawValues(false)
        grafico_basurero.legend.yOffset = 0f
        grafico_basurero.setTouchEnabled(false)

        grafico_basurero.invalidate() // Refresca el gráfico

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
                    "Mediciones" -> {
                        // No hacer nada
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

        //LIsta de dispositivos
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val opciones = USUARIO_ACTIVO.obtenerNombresDispositivos()
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    listaDispisitivos.adapter = adapter

                    listaDispisitivos.setSelection(adapter.getPosition(USUARIO_ACTIVO.getDisp()))
                }

            }catch (e: Exception){
                withContext(Dispatchers.Main) {
                    listaDispisitivos.adapter = null
                }
            }
        }

        //Cargar datos
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    val porcentaje = USUARIO_ACTIVO.obtenerPorcentajeLlenado()
                    val altura = USUARIO_ACTIVO.getAlturaBasura()
                    val capacidad = USUARIO_ACTIVO.getCapacidadBasurero()

                    withContext(Dispatchers.Main) {
                        updateBarChart(porcentaje, grafico_basurero)
                        etiqueta_altura_basurero.text = "Altura de basura: $altura cm"
                        if(capacidad.toString() != campoCapacidad.text.toString()){
                            campoCapacidad.setHint(capacidad.toString())
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        etiqueta_altura_basurero.text = "Error de conexión"
                    }
                }

                delay(5000)
            }
        }


        listaDispisitivos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val disp = parent.getItemAtPosition(position).toString()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        USUARIO_ACTIVO.actualizarDispositivo(disp)
                        val porcentaje = USUARIO_ACTIVO.obtenerPorcentajeLlenado()
                        val altura = USUARIO_ACTIVO.getAlturaBasura()
                        val capacidad = USUARIO_ACTIVO.getCapacidadBasurero()

                        withContext(Dispatchers.Main) {
                            updateBarChart(porcentaje, grafico_basurero)
                            etiqueta_altura_basurero.text = "Altura de basura: $altura cm"
                            campoCapacidad.setHint(capacidad.toString())
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            etiqueta_altura_basurero.text = "Error de conexión."
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }


        botonGuardar.setOnClickListener {
            val capacidad = campoCapacidad.text.toString().toInt()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    USUARIO_ACTIVO.actualizarCapacidadBasurero(capacidad)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Capacidad actualizada", Toast.LENGTH_SHORT).show()
                        campoCapacidad.setHint(capacidad.toString())
                        campoCapacidad.setText("")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        return view
    }

    private fun setGrafico(barChart: BarChart, color: Int) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 0f)) // Inicialmente el valor es 0
        val barDataSet = BarDataSet(entries, "Porcentaje basura")
        barDataSet.color = resources.getColor(color)
        val data = BarData(barDataSet)
        barChart.data = data

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.axisMaximum = 100f
    }

    private fun updateBarChart(value: Int, barChart: BarChart) {
        if (value > 80){
            setGrafico(barChart, R.color.dark_red)
        } else {
            setGrafico(barChart, R.color.dark_green)
        }
        barChart.data.getDataSetByIndex(0).getEntryForIndex(0).y = value.toFloat()
        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }


}