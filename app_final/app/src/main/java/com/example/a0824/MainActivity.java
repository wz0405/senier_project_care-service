package com.example.a0824;

import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttToken;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    static String MQTTHOST = "soilder.cloudmqtt.com";
    static String USERNAME = "cseonlla";
    static String PASSWORD = "TC3m1odmVzBQ";

    private GoogleMap mMap;
    public String lat=" 36.764748";
    public String lon="127.282588";
    public String message="";
    public String tmp="";




    MqttAndroidClient client;
    TextView subText;
    TextView MainText;


    private Button btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subText =(TextView)findViewById(R.id.subText);
        MainText = (TextView)findViewById(R.id.MainText);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientId = MqttClient.generateClientId();
                client = new MqttAndroidClient(MainActivity.this, "tcp://soldier.cloudmqtt.com:17589", "cseonlla");

                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(USERNAME);
                options.setPassword(PASSWORD.toCharArray());


                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Toast.makeText(MainActivity.this, "연결되었습니다.", Toast.LENGTH_SHORT).show();

                            setSubscription();

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Toast.makeText(MainActivity.this, "X", Toast.LENGTH_SHORT).show();


                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                client.setCallback(new MqttCallback() {
                                       @Override
                                       public void connectionLost(Throwable throwable) {

                                       }

                                       @Override
                                       public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

                                           if(s.equals("lat")) {
                                               lat = mqttMessage.toString();

                                               double x = Double.parseDouble(lat);
                                               double y = Double.parseDouble(lon);



                                               LatLng POSITION = new LatLng(x , y);


                                               MarkerOptions markerOptions = new MarkerOptions();
                                               markerOptions.position(POSITION);




                                           }
                                           if(s.equals("lon")) {

                                               lon = mqttMessage.toString();

                                               double x = Double.parseDouble(lat);
                                               double y = Double.parseDouble(lon);



                                               LatLng POSITION = new LatLng(x , y);


                                               MarkerOptions markerOptions = new MarkerOptions();
                                               markerOptions.position(POSITION);
                                               markerOptions.title("위치");
                                               markerOptions.snippet("마지막으로 저장된 위치");

                                               mMap.addMarker(markerOptions);
                                               mMap.moveCamera(CameraUpdateFactory.newLatLng(POSITION));
                                               mMap.animateCamera(CameraUpdateFactory.zoomTo(16));


                                           }
                                           if(s.equals("message")){
                                               message = mqttMessage.toString();
                                               subText.setText(new String(mqttMessage.getPayload()));

                                           }
                                           if(s.equals("tmp")){
                                               tmp = mqttMessage.toString();
                                               MainText.setText(new String(mqttMessage.getPayload()));
                                           }

                                       }


                                       @Override
                                       public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                                       }
                                   }
                );

            }


        });

    }

    private void setSubscription(){
        try{
            client.subscribe("lat",0);
            client.subscribe("lon",0);
            client.subscribe("message",0);
            client.subscribe("tmp",0);





        }catch(MqttException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        double x = Double.parseDouble(lat);
        double y = Double.parseDouble(lon);



        LatLng POSITION = new LatLng(x , y);


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(POSITION);
        markerOptions.title("기점");
        markerOptions.snippet("초기로 설정된 위치");

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(POSITION));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }
}
