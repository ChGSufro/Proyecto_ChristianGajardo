package proyecto.llenadobasura.app

class Basurero(nombre: String, capacidad: Int, llenado: Int) {

    private var Nombre: String = nombre
    private var Capacidad: Int = capacidad
    private var Llenado: Int = llenado


    fun getNombre(): String{
        return this.Nombre
    }

    fun getCapacidad(): Int{
        return this.Capacidad
    }

    fun getLlenado(): Int{
        return this.Llenado
    }

    fun setNombre(nombre: String){
        this.Nombre = nombre
    }

    fun setCapacidad(capacidad: Int){
        this.Capacidad = capacidad
    }

    fun setLlenado(distancia: Int){
        val llenado = this.Capacidad - distancia
        if (llenado < 0){
            this.Llenado = 0
            return
        }
        this.Llenado = llenado
    }


    fun porcentajeLlenado(): Int{
        return (this.Llenado * 100) / this.Capacidad
    }


}