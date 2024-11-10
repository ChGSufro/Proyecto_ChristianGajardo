#include <WiFi.h>
#include <HTTPClient.h>

#define LED 2
#define PIN 23
#define interruptor 34

// Configura tu red WiFi
const char* ssid = "S21 FE 5G";
const char* password = "12345678";
const char* host = "http://34.176.235.209:8081/dato/add";

const int capacidad = 100; //cm
const char* dispositivo = "Dispositivo:002"; //cm


void setup() {
  Serial.begin(115200);
  pinMode(LED, OUTPUT); // Configurar el pin del LED incorporado como salida
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
      delay(1000);
      Serial.println("Conectando a WiFi...");
  }
  Serial.println("Conectado a WiFi");
}

void loop() {
  long distancia = get_distance();
  Serial.print("Distancia: ");
  Serial.print(distancia);
  Serial.println(" cm");

  String json = "{";
  json += "\"capacidad\": " + String(capacidad) + ",";
  json += "\"distancia\": " + String(distancia) + ",";
  json += "\"dispositivo\": \"" + String(dispositivo) + "\"";
  json += "}";

  if (WiFi.status() == WL_CONNECTED){
    sendToFlask(json);
    digitalWrite(LED, HIGH); // Encender el LED
    delay(1000); // Esperar un segundo
    digitalWrite(LED, LOW); // Apagar el LED
  }
  delay(10000); // Esperar un segundo

}

long get_distance() {
  pinMode(PIN, OUTPUT);        // Configura el pin como salida
  digitalWrite(PIN, LOW);      // Envia una señal baja
  delayMicroseconds(2);      // Espera 2 microsegundos
  digitalWrite(PIN, HIGH);     // Envia una señal alta
  delayMicroseconds(10);     // Espera 10 microsegundos
  digitalWrite(PIN, LOW);      // Vuelve a señal baja

  pinMode(23, INPUT);         // Cambia el pin a modo entrada
  long distance = pulseIn(PIN, HIGH); // Lee el tiempo de duración del pulso alto
  distance = distance / 58;  // Convierte el tiempo en distancia en cm
  return distance;
}


void sendToFlask(String json){

    HTTPClient http;
    http.begin(host);
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.PUT(json);

    if (httpResponseCode > 0)
    {
        String response = http.getString();
        Serial.println(httpResponseCode);
        Serial.println(response);
    }
    else
    {
        Serial.print("Error en la petición PUT: ");
        Serial.println(httpResponseCode);
    }

    http.end();
        
}
