package com.example.fertileasy;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.net.wifi.WifiConfiguration;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.util.ArrayList;


//Tela de envio dos dados da rede local ao ESP32 usando websockets
public class WebSocketActivity extends AppCompatActivity {

    private TextView output;
    private boolean stop = false;


    private final class EchoWebSocketListener extends WebSocketListener {
        //Abre conexão Websocket
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            ArmazenamentoLocal Al = new ArmazenamentoLocal(WebSocketActivity.this);
            if (text.equals("No")) {
                Al.SharedSetDataString("textmain", "ESP 32 is not Connected to Internet");
            } else {
                Al.SharedSetDataString("textmain", "ESP 32 is Connected to Internet");
            }


        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {

        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//estende méthodo onCreate
        setContentView(R.layout.activity_socket);//Associa ID do layout activity_socket a lógica da atividade
        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
        output = findViewById(R.id.output);
        InicializaButtons();//Inicializa botões da tela
        configure_ESP32();//Habilita Conexão Wi-Fi
        DetectaCamposVazios();//Método que desabilita botão de Conectar se campos forem nulos e habilita
        //se campos estiverem não nulos
    }

    void InicializaButtons() {
        Button enablebutton = findViewById(R.id.enable);
        Button backbutton = findViewById(R.id.back);
        enablebutton.setOnClickListener((View v) -> {
            final EditText Senha = findViewById(R.id.editText);
            final EditText Rede = findViewById(R.id.editText2);
            //Converte dados digitados no formato String
            final String Senha_ESP32 = Senha.getText().toString();
            final String Rede_ESP32 = Rede.getText().toString();
            //Método que inicia comunicação for websockets
            start(Rede_ESP32, Senha_ESP32);

        });
        backbutton.setOnClickListener((View v) -> {
            Intent intent = getIntent();
            String urlAddress = "https://appfertileasy.herokuapp.com/androidbancadas.php";
            String codigo = intent.getStringExtra("codigo");
            stop=true;
            int position = intent.getIntExtra("position", -1);
            Envio s = new Envio(WebSocketActivity.this, urlAddress, "", codigo, "", "", "", "", "", "", "", "", "", "", "", WebSocketActivity.this, 1, position, "");
            s.execute();

        });


    }

    private void start(String Rede_ESP32, String Senha_ESP32) {
        //Cria um cliente
        OkHttpClient client = new OkHttpClient();
        ArrayList<String> bancadadadosarray;
        ArmazenamentoLocal Al = new ArmazenamentoLocal(WebSocketActivity.this);
        bancadadadosarray = Al.SharedGetArray("bancadadadosarray");
        Toast.makeText(WebSocketActivity.this, bancadadadosarray.get(bancadadadosarray.size() - 1), Toast.LENGTH_SHORT).show();
        //Define requisição para o IP do ESP32
        Request request = new Request.Builder().url("ws://192.168.4.1").build();
        //Cria listener
        EchoWebSocketListener listener = new EchoWebSocketListener();
        //Cria objeto websocket para envio dos dados
        WebSocket ws = client.newWebSocket(request, listener);
        //Envia dados de rede,senha e Qrcode
        ws.send(Rede_ESP32);
        ws.send(Senha_ESP32);
        ws.send(bancadadadosarray.get(bancadadadosarray.size() - 1));
        //ws.close(1000, "");
        //Encerra serviço
        client.dispatcher().executorService().shutdown();
    }


    private void configure_ESP32() {

        try {


            //String ssid = "MyESP32AP";
            //String senha = "testpassword";
            WifiConfiguration wc = new WifiConfiguration();
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
            //wc.SSID = "\"" + ssid + "\"";
            //wc.preSharedKey = "\"" + senha + "\"";
            //wc.wepKeys[0] = "\"" + ssid + "\"";
            wc.wepTxKeyIndex = 0;
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiManager.addNetwork(wc);
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {


            } else {
                Toast.makeText(getApplicationContext(), "NotConnected to ESP32 net", Toast.LENGTH_LONG).show();


                //       break;
                //     }

                //   }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void DetectaCamposVazios() {
        EditText RedeT = findViewById(R.id.editText2);
        EditText SenhaT = findViewById(R.id.editText);
        Button enablebutton = findViewById(R.id.enable);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String Rede = RedeT.getText().toString();
                String Senha = SenhaT.getText().toString();
                if (!Rede.equals("") && !Senha.equals("")) {// se email e senha não forem nulos
                    enablebutton.setEnabled(true);//Botão de autenticação está habilitado
                    //mPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    Drawable bgShape = enablebutton.getBackground();//Define um plano de fundo para o botão
                    int color = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);//Resgata cor definida no arquivo colors.xml
                    bgShape.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);//Define cor do botão
                }
                if (Rede.equals("") || Senha.equals("")) {// se email e senha  forem nulos
                    //mPasswordView.setImeOptions(EditorInfo.IME_ACTION_NONE);
                    enablebutton.setEnabled(false);//Botão de autenticação está desabilitado
                    Button  enablebutton = findViewById(R.id.enable);
                    Drawable bgShape = enablebutton.getBackground();
                    int color = ResourcesCompat.getColor(getResources(), R.color.colorTransp, null);
                    bgShape.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }
                if (!stop) {// valor booleano que define se a multithread deve continuar sendo executada a cada 500 ms
                    handler.postDelayed(this, 500);
                }
            }
        });
    }
}

