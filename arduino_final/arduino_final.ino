#include <TinyGPS.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#define ONE_WIRE_BUS A0
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
TinyGPS gps;
String cmd = "AT+DATA=1f98a0:#";
String message = "";
void getgps(TinyGPS &gps);
char val;
int flag = 1;
void setup()
{
  Serial.begin(9600); //pc
  Serial1.begin(9600); //gps
  Serial2.begin(115200); //로라
  Serial3.begin(9600); //블루투스
  sensors.begin();
}
void loop()
{
  while(Serial1.available())     // While there is data on the RX pin...
  {
    sensors.requestTemperatures();
    float temperature = sensors.getTempCByIndex(0);
    while(Serial3.available()){
    char myChar = (char)Serial3.read();
    message+=myChar;
    delay(5);
    } 
    int c = Serial1.read();    // load the data into a variable...
    float latitude;
    float longitude;
    
    
     
      if(gps.encode(c))      // if there is a new valid sentence...
      {
        gps.f_get_position(&latitude, &longitude);
        Serial2.print(cmd);
        Serial2.print(message);
        Serial2.print("#");
        Serial2.print(latitude,5);
        Serial2.print("#");
        Serial2.print(longitude,5);
        Serial2.print("#");
        Serial2.print(temperature);
        Serial2.print("\n");
        delay(1000);
        if(Serial3.available()){
          message="";
        }
      
      }
  
 }
 
}
