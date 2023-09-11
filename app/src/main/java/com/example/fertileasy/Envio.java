package com.example.fertileasy;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
// Toda classe que realiza requisições http deve estender AsyncTask.
//Método doinbackground roda paralelamente a thread principal.
//Depois do construtor( Envio(...) métodos  são executados na seguinte ordem-> 1.onPreExecute 2.DoinBackground 3.OnPostExecute

//Classe responsável pelo envio dos parametros a alguma URL pelo método POST
public class Envio extends AsyncTask<Void, Void, String> {
    private String urlAddress;
    private WeakReference<Context> contextRef;
    private int times,position;
    private WeakReference<Activity> weakActivity;
    private String email, codigousuario, codigobancada, tipodebancada, cultura, datadecultivo, concentracao, iniciododia, fimdodia, tempoligado, tempodesligado, ciclosnoturnos,nomedabancada,qrcode;

    Envio(Context c, String urlAddress, final String emailTxt, final String codigousuario, final String codigobancada, String tipodebancada, String cultura, String concentracao, String iniciododia, String fimdodia, String tempoligado, String tempodesligado, String ciclosnoturnos, String datadecultivo, String nomedabancada,Activity act,int times,int position,String qrcode) {
        this.urlAddress = urlAddress;
        this.email = emailTxt;
        this.codigousuario = codigousuario;
        this.codigobancada = codigobancada;
        this.tipodebancada = tipodebancada;
        this.cultura = cultura;
        this.concentracao = concentracao;
        this.iniciododia = iniciododia;
        this.fimdodia = fimdodia;
        this.tempoligado = tempoligado;
        this.tempodesligado = tempodesligado;
        this.ciclosnoturnos = ciclosnoturnos;
        this.datadecultivo = datadecultivo;
        this.nomedabancada = nomedabancada;
        this.contextRef = new WeakReference<>(c);
        this.weakActivity = new WeakReference<>(act);
        this.times = times;
        this.position = position;
        this.qrcode=qrcode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Activity act = weakActivity.get();
        Context c = contextRef.get();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(c);
        ArrayList<String> bancadadadosarray = new ArrayList<>();
        //Adiciona e salva dados em um arraylist de bancada de dados
        bancadadadosarray.add(urlAddress);
        bancadadadosarray.add(tipodebancada);
        bancadadadosarray.add(cultura);
        bancadadadosarray.add(concentracao);
        bancadadadosarray.add(iniciododia);
        bancadadadosarray.add(fimdodia);
        bancadadadosarray.add(tempoligado);
        bancadadadosarray.add(tempodesligado);
        bancadadadosarray.add(ciclosnoturnos);
        bancadadadosarray.add(datadecultivo);
        bancadadadosarray.add(nomedabancada);
        bancadadadosarray.add(qrcode);
        Al.SharedSetArray(bancadadadosarray,"bancadadadosarray");
        Al.SharedSetDataInt("times",times-1);
        Al.SharedSetBool("Login",false);
        Al.SharedSetBool("ListView",false);
        Al.SharedSetBool("HistoricoActivity",false);
        Al.SharedSetBool("bancada",false);
        Al.SharedSetBool("ConfigActivity",false);
        Al.SharedSetBool("WebSocketActivity",false);
        //Se a tela de onde é chamado a classe Envio for LoginActivity
        if (act instanceof LoginActivity) {
            Al.SharedSetBool( "Login",true);

        }
        //Se a tela de onde é chamado a classe Envio for ListActivity
        if (act instanceof ListActivity) {
            Al.SharedSetBool( "ListView",true);

        }
        //Se a tela de onde é chamado a classe Envio for HistoricoActivity
        if (act instanceof HistoricoActivity) {
            Al.SharedSetBool( "HistoricoActivity",true);

        }
        //Se a tela de onde é chamado a classe Envio for BancadaActivity
        if (act instanceof BancadaActivity) {
            Al.SharedSetBool( "bancada",true);

        }
        //Se a tela de onde é chamado a classe Envio for ConfigActivity
        if (act instanceof ConfigActivity) {
            Al.SharedSetBool( "ConfigActivity",true);

        }
        //Se a tela de onde é chamado a classe Envio for WebSocketActivity
        if (act instanceof WebSocketActivity) {
            Al.SharedSetBool( "WebSocketActivity",true);

        }
    }
    //Método que executa o método send em paralelo a thread principal
    @Override
    protected String doInBackground(Void... params) {

            return this.send();

    }
    //Retorno do método doinBackground deve ser parâmetro do método onPostExecute
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        //Reponse= resposta da requisição http POST
        Context c = contextRef.get();
        Activity act = weakActivity.get();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(c);
        //Valor booleano que indica de qual atividade(tela) é chamada a classe Envio
        boolean login=Al.SharedGetBool("Login");
        boolean listView=Al.SharedGetBool("ListView");
        boolean historico=Al.SharedGetBool("HistoricoActivity");
        boolean bancada=Al.SharedGetBool("bancada");
        boolean configActivity=Al.SharedGetBool("ConfigActivity");
        boolean mainactivity=Al.SharedGetBool("WebSocketActivity");
        int times = Al.SharedGetDataInt("times");
        //Se a resposta da requisição http não for nula, se o envio for chamado da tela configActivity
        //ou da tela bancada e se a resposta não conter [{ imprime resposta do POST na tela
        if (response != null) {
            if(configActivity || bancada) {
                if(!response.contains("[{")) {
                    //Imprime resposta na tela
                    Toast.makeText(c, response, Toast.LENGTH_LONG).show();
                }
            }
            //Salva resposta na memória local
            Al.SharedSetDataString("resposta", response);
            //Se a atividade for login(LoginActivity)
            if (login) {
                if(!urlAddress.equals("https://appfertileasy.herokuapp.com/androiddeletarbancada.php")) {
                    Recebimento s = new Recebimento(urlAddress, c, act, times, position);
                    s.execute();
                }

            }
            //Se a atividade for a tela de configuração(ConfigActivity)
            if(configActivity){
                //Al.SharedSetBool("ConfigActivity",false);
                Recebimento s = new Recebimento(urlAddress, c,act,times,position);
                s.execute();

            }
            //Se a atividade for a tela de lista de bancadas(ListViewActivity)
            if(listView){
                //Al.SharedSetBool("Listview",false);
                Recebimento s = new Recebimento(urlAddress, c,act,times,position);
                s.execute();

            }
            //Se a atividade for a tela de bancadas(BancadaActivity)
            if(bancada){
                //Al.SharedSetBool("bancada",false);
                Recebimento s = new Recebimento(urlAddress, c,act,times,position);
                s.execute();

            }
            //Se a atividade for a tela de historico(HistoricoActivity)
            if(historico){
               // Al.SharedSetBool("HistoricoActivity",false);
                Recebimento s = new Recebimento(urlAddress, c,act,times,position);
                s.execute();

            }
            //Se a atividade for a tela de WebSocket(WebSocketActivity)
            if(mainactivity){
                //Al.SharedSetBool("WebSocketActivity",false);
                Recebimento s = new Recebimento(urlAddress, c,act,times,position);
                s.execute();

            }




        } else {

            Toast.makeText(c, "Unsuccessful " + "", Toast.LENGTH_LONG).show();
        }
    }







    /*
    SEND DATA OVER THE NETWORK
    RECEIVE AND RETURN A RESPONSE
     */

    private String send() {
        //CONNECT
        Context c = contextRef.get();
        //Cria objeto para a classe HttpProtocol
        HttpProtocol HP = new HttpProtocol(null);
        //Executa método da classe HttpProtocol passando o URL
        HP.HttpConnection(urlAddress);
        HttpURLConnection con = HP.getConnection();
        if (con == null) {
            return null;
        }
       else{
           //Retorna resposta do POST
            return HP.DataReadWrite(urlAddress,email,codigousuario, codigobancada, tipodebancada, cultura, datadecultivo, concentracao, iniciododia, fimdodia, tempoligado, tempodesligado, ciclosnoturnos,c,nomedabancada,qrcode);
        }
    }
}
