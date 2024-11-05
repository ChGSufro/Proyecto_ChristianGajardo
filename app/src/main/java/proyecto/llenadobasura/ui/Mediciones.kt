package proyecto.llenadobasura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
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
import proyecto.llenadobasura.R
import proyecto.llenadobasura.app.Universidad
import proyecto.llenadobasura.app.UsuarioActivo

class Mediciones : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mediciones, container, false)
        val USUARIO_ACTIVO: UsuarioActivo = ViewModelProvider(requireActivity())[Universidad::class.java].getUsuarioActivo()

        val menu: Spinner = view.findViewById(R.id.menu)
        val grafico_basurero: BarChart = view.findViewById(R.id.grafico_basurero)
        val etiqueta_basurero: TextView = view.findViewById(R.id.etiqueta_basurero)
        val listaDispisitivos: Spinner = view.findViewById(R.id.dispositivos)
        val etiqueta_altura_basurero: TextView = view.findViewById(R.id.etiqueta_altura_basura)
        val etiquetq_capacidad_basurero: TextView = view.findViewById(R.id.etiqueta_capacidad_basurero)


        // Configurar el menú
        val opciones = arrayOf("Mediciones", "Dispositivos", "Perfil", "Cerrar")
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
                    "Dispositivos" -> {
                        findNavController().navigate(R.id.Mediciones_to_dispositivos)
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

        // Configurar la lista de dispositivos
        val opciones2 = USUARIO_ACTIVO.getDispositivos()
        val adapter2: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones2)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listaDispisitivos.adapter = adapter2

        listaDispisitivos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                USUARIO_ACTIVO.setDispositivoActivo(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }

        //Cargar datos
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    val basurero = USUARIO_ACTIVO.getDispositivoActivo()
                    val capacidad = basurero.getCapacidad()
                    val llenado = basurero.getLlenado()
                    val porcentaje = basurero.porcentajeLlenado()

                    withContext(Dispatchers.Main) {
                        etiqueta_basurero.text = "Basurero: ${basurero.getNombre()}"
                        etiqueta_altura_basurero.text = "Altura de basura (cm): $llenado"
                        etiquetq_capacidad_basurero.text = "Capacidad (cm): $capacidad"
                        updateBarChart(porcentaje, grafico_basurero)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        etiqueta_basurero.text = "No se encontraron dispositivos vinculados."
                        etiqueta_altura_basurero.text = "Altura de basura (cm): --"
                        etiquetq_capacidad_basurero.text = "Capacidad (cm): --"
                    }
                }

                delay(3000)
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
        if (value >= 80){
            setGrafico(barChart, R.color.dark_red)
        } else {
            setGrafico(barChart, R.color.dark_green)
        }
        barChart.data.getDataSetByIndex(0).getEntryForIndex(0).y = value.toFloat()
        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }

}