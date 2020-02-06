#include <TinyGPS.h>
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
}
void loop()
{
  while(1)     // While there is data on the RX pin...
 
    int c = Serial1.read();    // load the data into a variable...
    float latitude=10;
    float longitude=10;
     
      if(1)      // if there is a new valid sentence...
      {
        gps.f_get_position(&latitude, &longitude);
        Serial2.print(cmd);
        Serial2.print(message);
        Serial2.print("#");
        Serial2.print(latitude,5);
        Serial2.print("#");
        Serial2.print(longitude,5);
        Serial2.print("\n");
        delay(500);
        message="";
      
      }
  
 }
 
