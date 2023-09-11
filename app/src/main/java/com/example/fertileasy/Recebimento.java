package com.example.fertileasy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.lang.ref.WeakReference;
import java.util.ArrayList;


//Classe responsável por tratar e armazenar os dados recebidos para serem usados em outras telas
public class Recebimento extends AsyncTask<Context, Void, Context>  {
    private String urlAddress;
    private int times,position;
    private WeakReference<Context> contextRef;
    private WeakReference<Activity> weakActivity;

    Recebimento(String urlAddress, Context c, Activity act, int times,int position) {
        this.urlAddress = urlAddress;
        this.contextRef = new WeakReference<>(c);
        this.weakActivity = new WeakReference<>(act);
        this.times = times;
        this.position = position;
    }

    @Override
    protected Context doInBackground(Context... params) {

        Context c = contextRef.get();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(c);
        //Chama classe GetDatabaseInfo
        GetDatabaseInfo GDI = new GetDatabaseInfo();
        String resposta = Al.SharedGetDataString("resposta");
        GDI.getInfo(resposta, c, urlAddress);
        return null;
    }

    @Override
    protected void onPostExecute(Context params) {
        Context c = contextRef.get();
        Activity act = weakActivity.get();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(c);
        ArrayList<String> dadosusuarioarray;
        ArrayList<String> bancadadadosarray;
        Al.SharedSetDataInt("times", times - 1);
        int times = Al.SharedGetDataInt("times");
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
        bancadadadosarray = Al.SharedGetArray("bancadadadosarray");
        String emailTxt =dadosusuarioarray.get(0);
        String urlAddress = bancadadadosarray.get(0);
        String tipodebancada = bancadadadosarray.get(1);
        String cultura = bancadadadosarray.get(2);
        String concentracao = bancadadadosarray.get(3);
        String iniciododia = bancadadadosarray.get(4);
        String fimdodia = bancadadadosarray.get(5);
        String tempoligado = bancadadadosarray.get(6);
        String tempodesligado = bancadadadosarray.get(7);
        String ciclosnoturnos = bancadadadosarray.get(8);
        String datadecultivo = bancadadadosarray.get(9);
        String nomedabancada = bancadadadosarray.get(10);
        boolean login = Al.SharedGetBool("Login");
        boolean listView = Al.SharedGetBool("ListView");
        boolean historico = Al.SharedGetBool("HistoricoActivity");
        boolean bancada = Al.SharedGetBool("bancada");
        boolean configActivity = Al.SharedGetBool("ConfigActivity");
        boolean mainactivity= Al.SharedGetBool("WebSocketActivity");

        if (login) {
            if (times == -2) {
                //Método de LoginActivity que realiza o Login
                LoginActivity.TentativadeConexao();
                Al.SharedSetBool("Login", false);

            }
            if (times == 0) {
                urlAddress = "https://appfertileasy.herokuapp.com/androidbancadas.php";
                ProgressBar pb = act.findViewById(R.id.progressBar);//Associa a barra de rolamento ao respectivo layout
                //Faz aparecer barra de rolamento no centro da tela
                pb.setVisibility(View.VISIBLE);
                Al.SharedSetBool("Login", false);
                if(dadosusuarioarray.size()>1) {
                    String codigousuario = dadosusuarioarray.get(1);
                    Envio s = new Envio(c, urlAddress, emailTxt, codigousuario, "", tipodebancada, cultura, concentracao, iniciododia, fimdodia, tempoligado, tempodesligado, ciclosnoturnos, datadecultivo, nomedabancada, act, times, -1, "");
                    s.execute();
                }
                else{
                    //Se o usuário não estiver cadastrado remove barra de rolamento
                    pb.setVisibility(View.GONE);
                    Toast.makeText(c, "Usuário não cadastrado, tente novamente", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (configActivity) {
            if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidbancadas.php")) {
                Al.SharedSetBool("ConfigActivity", false);
                //Cria referencia para classe intent
                Intent intent = act.getIntent();
                String codigo = intent.getStringExtra("codigo");
                //Troca tela de ConfigActivity para ListActivity
                intent = new Intent(c, ListActivity.class);
                intent.putExtra("codigo", codigo);
                act.startActivity(intent);
            }
            if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidexportarhistorico.php")) {

                Intent intent = act.getIntent();
                ArrayList<String> temperaturaarray;
                Al = new ArmazenamentoLocal(c);
                temperaturaarray = Al.SharedGetArray("temperatura");
                int icount =Al.SharedGetDataInt("icount");
                if (icount != -1) {
                    if (!temperaturaarray.get(0).equals("")) {
                        Al.SharedSetBool("ConfigActivity", false);
                        String codigo = intent.getStringExtra("codigo");
                        //Troca tela de ConfigActivity para HistoricoActivity
                        intent = new Intent(c, HistoricoActivity.class);
                        //Envia codigo e posição para a tela HistoricoActivity
                        intent.putExtra("position", position);
                        intent.putExtra("codigo", codigo);
                        act.startActivity(intent);
                    }
                }
                else{
                    Toast.makeText(c, "Não há dados no histórico dessa bancada", Toast.LENGTH_SHORT).show();
                }


            }
        }
        if (bancada) {

            if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidexportarhistorico.php")) {
                Intent intent = act.getIntent();
                ArrayList<String> temperaturaarray;
                Al = new ArmazenamentoLocal(c);
                temperaturaarray = Al.SharedGetArray("temperatura");
                int icount =Al.SharedGetDataInt("icount");
                //Se houver dados de data,hora concentração e temperatura
                if (icount != -1) {
                    if (!temperaturaarray.get(0).equals("")) {
                        String codigo = intent.getStringExtra("codigo");
                        //Troca tela de BancadaActivity para HistoricoActivity
                        intent = new Intent(c, HistoricoActivity.class);
                        Al.SharedSetBool("bancada", false);
                        intent.putExtra("position", position);
                        intent.putExtra("codigo", codigo);
                        act.startActivity(intent);
                    }
                }
                else{
                    Toast.makeText(c, "Não há dados no histórico dessa bancada", Toast.LENGTH_SHORT).show();
                }

            }
            if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidbancadas.php")) {
                Intent intent = act.getIntent();
                Al.SharedSetBool("bancada", false);
                String codigo = intent.getStringExtra("codigo");
                //Troca tela de BancadaActivity para ListActivity
                intent = new Intent(c, ListActivity.class);
                intent.putExtra("codigo", codigo);
                act.startActivity(intent);


            }
            if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidacompanharbancada.php")) {
                Al.SharedSetBool("bancada", false);
                String resposta = Al.SharedGetDataString("resposta");
                String Concentracao = Al.SharedGetDataString("concentracao");
                String Temperatura = Al.SharedGetDataString("temperatura");
                String textodatabancada = Al.SharedGetDataString("textodatabancada");
                String textohorabancada = Al.SharedGetDataString("textohorabancada");
                BancadaActivity.setTexttela(textodatabancada, textohorabancada, Concentracao, Temperatura, act);
                if (resposta.equals("[]")) {
                    Toast.makeText(c, "Não há dados no histórico dessa bancada", Toast.LENGTH_SHORT).show();


                }
            }
        }
            if (historico) {
                if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidbancadas.php")) {
                    Intent intent = act.getIntent();
                    String codigo = intent.getStringExtra("codigo");
                    //Troca tela de HistoricoActivity para ListActivity
                    intent = new Intent(act, ListActivity.class);
                    Al.SharedSetBool("HistoricoActivity", false);
                    intent.putExtra("codigo", codigo);
                    act.startActivity(intent);


                }
            }
            if (listView) {
                Al = new ArmazenamentoLocal(c);
                ArrayList<String> nomearray;
                nomearray = Al.SharedGetArray("nome");
                if (nomearray.size() != 0) {
                    if (!cultura.equals("")) {
                        Al.SharedSetBool("ListView", false);
                        Intent intent = act.getIntent();
                        String codigo = intent.getStringExtra("codigo");
                        String email = intent.getStringExtra("email");
                        ProgressBar pb = act.findViewById(R.id.progressBar);
                        pb.setVisibility(View.GONE);
                        //Troca tela de ListActivity para ConfigActivity
                        intent = new Intent(act, ConfigActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("codigo", codigo);
                        intent.putExtra("email", email);
                        act.startActivity(intent);
                    }



                    //Context c = contextRef.get();
                    //if(urlAddress.equals("https://appfertileasy.herokuapp.com/androidlogin.php")) {
                    //      pb.setVisibility(View.GONE);
                    //    Intent i = new Intent(c, SplashScreen.class);
                    //  i.putExtra("password", senha);
                    //c.startActivity(i);
                    //cancel(true);
                    //Activity newact = myact.get();
                    //newact.finish();
                    //}
                }
            }
        if (mainactivity)
            if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidbancadas.php")) {
                Al.SharedSetBool("WebSocketActivity", false);
                Intent intent = act.getIntent();
                String codigo = intent.getStringExtra("codigo");
                //Troca tela de WebSocketActivity para ListActivity
                intent = new Intent(c, ListActivity.class);
                intent.putExtra("codigo", codigo);
                act.startActivity(intent);
            }

    }

    @Override
    protected void onPreExecute() {
        Activity act = weakActivity.get();
        Context c = contextRef.get();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(c);
        //Se atividade que chamou a classe Envio for LoginActivity
        if (act instanceof LoginActivity) {
            Al.SharedSetBool("Login", true);
        }
        //Se atividade que chamou a classe Envio for ListActivity
        if (act instanceof ListActivity) {
            Al.SharedSetBool("ListView", true);
        }
        //Se atividade que chamou a classe Envio for HistoricoActivity
        if (act instanceof HistoricoActivity) {
            Al.SharedSetBool("HistoricoActivity", true);
        }
        //Se atividade que chamou a classe Envio for BancadaActivity
        if (act instanceof BancadaActivity) {
            Al.SharedSetBool("bancada", true);
        }
        //Se atividade que chamou a classe Envio for ConfigActivity
        if (act instanceof ConfigActivity) {
            Al.SharedSetBool("ConfigActivity", true);

        }


    }
}


