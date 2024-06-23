package proyecto.llenadobasura.app

class Basurero {

    private var capacidad: Int = 0
    private var altura_basura: Int = 0

    fun setCapacidad(capacidad: Int){
        this.capacidad = capacidad
    }

    fun setAlturaBasura(altura_basura: Int){
        if (altura_basura < 0){
            this.altura_basura = 0
            return
        }
        this.altura_basura = altura_basura
    }

    fun getCapacidad(): Int{
        return this.capacidad
    }

    fun getAlturaBasura(): Int{
        return this.altura_basura
    }

    fun getPorcentajeLlenado(): Int{
        val porcentaje: Int = (this.altura_basura * 100 / this.capacidad)
        if (porcentaje > 100){
            return 100
        }
        if (porcentaje < 0){
            return 0
        }
        return porcentaje

    }

}