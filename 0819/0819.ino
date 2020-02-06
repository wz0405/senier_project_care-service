#include <TinyGPS.h>
TinyGPS gps;
const int gpsEnablePin = 5;
int second = 0, gps_second = 0;
int g_count = 0;
float latitude = -1.00, longitude = -1.00;
String cmd = "AT+DATA=1f98a0:#";
void setup() {
  Serial.begin(115200);  // Serial  : computer - arduino
  Serial1.begin(9600);   // Serail1 : Arduino - GPS Module
  Serial2.begin(115200);  // Serial2 : Arduino - LoRa
}

void loop() {
 while(Serial1.available())     // While there is data on the RX pin...
        {
            //latitude = -1.00, longitude = -1.00;
           int c = Serial1.read();    // load the data into a variable...
           float latitude;
           float longitude;
           if(gps.encode(c))      // if there is a new valid sentence...
           {
                getgps(gps);         // then grab the data.
                 if(latitude != -1.00 && longitude != -1.00){
                    break;
                 }
               
                Serial2.print(cmd);
                Serial2.print(latitude,8);
                Serial2.print("#");
                Serial2.print(longitude,8);
                Serial2.print("\n");
                Serial.print(cmd);
                Serial.print(latitude,8);
                Serial.print("#");
                Serial.print(longitude,8);
                Serial.print("\n");
            }   
        }
         
         
         delay(1000);
  
}            
void getgps(TinyGPS &gps)
{
  gps.f_get_position(&latitude, &longitude);
}

void plm_start(){
    String cmd = "AT+SHOW\n";
    for(int i = 0 ; i <cmd.length() ; i++){
        Serial2.write(cmd.charAt(i));
    }
}
