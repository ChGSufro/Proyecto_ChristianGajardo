from flask import Flask, request, jsonify, render_template
from pymongo import MongoClient
import json  # Asegúrate de importar json

from email.message import EmailMessage
import ssl
import smtplib

import time

client = MongoClient("mongodb://localhost:27017/")

Ch_G_db = client["proyecto"]

# {"dispositivo", "capacidad", distancia, fecha, hora}
datos = Ch_G_db["datos"]

# {"usuario": Sring, "nombre": Sring, "contraseña": Sring, "correo": Sring, "dispositivos": list[String]}
usuario = Ch_G_db["usuario"]

#usuario.drop()
#datos.drop()

def extraer_fecha():
    return time.strftime("%d/%m/%y")

def extraer_hora():
    return time.strftime("%H:%M:%S")

######################
#  MANEJO DE DATOS   #
######################

#funcion encargada de insertar nuevos datos de un dispositivo
def agregar_datos(dato):
    dato["fecha"] = extraer_fecha()
    dato["hora"] = extraer_hora()
    datos.insert_one(dato)

def extraer_datos_dispositivo(disp):
    return list(datos.find({"dispositivo": disp}, {"_id": 0}))

#funcion que me retorna los ultimos datos asociados a un dispositivo
def extraer_ultimo_dato_dispositivo(disp):
    return extraer_datos_dispositivo(disp)[-1]

#funcion encargada de retornar los nombres de los dispositivos existentes
def extraer_dispositivos():
    dispositivos = list(datos.distinct("dispositivo"))
    return dispositivos

#MANEJO DE USUARIOS
def usuario_existe(usr):# Función para verificar si un usuario existe
    if usuario.find_one({"usuario": usr}) != None:
        return True
    return False

def extraer_usuario(usr):# Función para extraer el correo
    return usuario.find_one({"usuario": usr["usuario"], "contraseña": usr["contraseña"]}, 
                            {"_id": 0, "contraseña": 0})

def agregar_usuario(usr):# Función para agregar un usuario
    if not usuario_existe(usr["usuario"]):
        usuario.insert_one(usr)
        return "Usuario agregado"
    return "Usuario ya existe"

def modificar_usuario(usr):# Función para modificar un usuario
    if usuario_existe(usr["usuario"]):
        usuario.update_one({"usuario": usr["usuario"]}, 
        {"$set": {"nombre": usr["nombre"], "correo": usr["correo"], "dispositivos": usr["dispositivos"]}})


#Metodos correos
def extraer_correos_vinculados(nombre_dispositivo):
    correos = list(usuario.find({"dispositivos": nombre_dispositivo}, {"_id": 0, "correo": 1}))
    return [correo["correo"] for correo in correos]


def cambio_de_volumen(dispositivo):
    ultimos_datos = extraer_datos_dispositivo(dispositivo)[-2:]  # Tomar solo los últimos datos
    distancia1 = ultimos_datos[0]["distancia"]
    distancia2 = ultimos_datos[1]["distancia"]
    if distancia1 - distancia2 > 5:
        return True
    return False

def extraer_porcentaje_llenado(capacidad_max, distancia):
    altura_llena = capacidad_max - distancia

    if altura_llena < 0:
        return 0
    
    porcentaje = (altura_llena/capacidad_max) * 100    
    return int(porcentaje)

def porcentaje_mayor_80(dispositivo):
    ultimos_datos = extraer_ultimo_dato_dispositivo(dispositivo)
    distancia = ultimos_datos["distancia"]
    capacidad = ultimos_datos["capacidad"]
    porcentaje = extraer_porcentaje_llenado(capacidad, distancia)
    if porcentaje >= 80:
        return True
    return False

def enviar_correo(correo, nombre_basurero):
    with open("credenciales.json", "r") as file:
        credenciales = json.load(file)

    emisor = credenciales["emisor"]
    contraseña_app = credenciales["contraseña"]
    destinatario = correo

    asunto = "Basurero lleno"
    mensaje = render_template("correo.html", nombre_basurero=nombre_basurero)

    msg = EmailMessage()
    msg['Subject'] = asunto
    msg['From'] = emisor
    msg['To'] = destinatario
    msg.set_content(mensaje, subtype='html')

    contexto = ssl.create_default_context()

    with smtplib.SMTP_SSL("smtp.gmail.com", 465, context = contexto) as server:
        server.login(emisor, contraseña_app)
        server.send_message(msg)
        server.quit()# Cerrar la conexión con el servidor

def basurero_esta_lleno(nombre_dispositivo):
    if cambio_de_volumen(nombre_dispositivo) and porcentaje_mayor_80(nombre_dispositivo):
        for correo in extraer_correos_vinculados(nombre_dispositivo):
            try:
                enviar_correo(correo, nombre_dispositivo)
                print("Correo enviado a: ", correo)
            except:
                print("Error al enviar correo a: ", correo)



## Flask ##
def create_app():
    app = Flask(__name__, static_folder='static')
    return app

app = create_app()

@app.route("/status")
def status():
    return {
        "estado": "1",
        "texto": "OK" 
}

@app.route("/BaseDatos", methods=['GET'])
def BaseDatos():
    return {"datos": list(datos.find({}, {"_id": 0})), "usuarios": list(usuario.find({}, {"_id": 0})) }

@app.route("/estado/<disp>", methods=['GET'])
def mostrar_server(disp):
    try:
        datos_encontrados = extraer_ultimo_dato_dispositivo(disp)
    except:
        return render_template("index.html", porcentaje_llenado=json.dumps(0))

    capacidad = datos_encontrados["capacidad"]
    distancia = datos_encontrados["distancia"]
    porcentaje_llenado = json.dumps(extraer_porcentaje_llenado(capacidad, distancia))

    return render_template("index.html", porcentaje_llenado=porcentaje_llenado)

#Metodos CRUD (sensor)
@app.route("/dato/add", methods=['PUT'])
def post_datos():
    datos_recibidos = request.json
    agregar_datos(datos_recibidos)
    try:
        basurero_esta_lleno(datos_recibidos["dispositivo"])
    except:
        print("Error al enviar correos")
    return "OK", 200



#Metodo CRUD (app)

#Metodos de usuario
@app.route("/usuario/log", methods=['POST'])
def post_usuarios_log():
    usuario_recibido = request.json
    return jsonify({"respuesta": extraer_usuario(usuario_recibido)})

@app.route("/usuario/add", methods=['PUT'])
def put_usuarios():
    dato_recibido = request.json
    print(dato_recibido)
    dato_recibido["dispositivos"] = []
    return jsonify({"respuesta": agregar_usuario(dato_recibido)})

@app.route("/usuario/put", methods=['PUT'])
def put_usuario():
    dato_recibido = request.json
    modificar_usuario(dato_recibido)
    return "actualizado",  200

#Metodos de dispositivos
@app.route("/dispositivos/get", methods=['GET'])
def get_dispositivos():
    return jsonify({"respuesta": extraer_dispositivos()})

#Metodos de datos
@app.route("/dato/get/<disp>", methods=['GET'])
def get_datos(disp):
    return jsonify({"respuesta": extraer_ultimo_dato_dispositivo(disp)})


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8081, debug=True)


