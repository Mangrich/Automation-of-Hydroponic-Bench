package com.example.fertileasy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
//Tela que recebe os valores mais recentes de concentração e temperatura
//Classe Bancada Activity estende classe AppCompatActivity e Implementa Listener OnNavigationItemSelectedListener, Internet, e InternetConnectivityListener
public class BancadaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Internet,InternetConnectivityListener  {
    private static BancadaActivity d;
    boolean stop;
    boolean responsenull = false;
    boolean mensagem = false;
    boolean isconnected = false;

//Sobrescreve método onCreate da classe android.app.Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// herda método onCreate e considera codigo abaixo
        setContentView(R.layout.activity_bancada);//Associa layout activity_bancada a Bancada Activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Determina orientação da tela:retrato
        d=this;//Atribui ao objeto Bancada Activity o contexto da classe atual
        ArrayList<String> dadosusuarioarray;//Define uma arraylist que vai receber os dados do usuário
        ArmazenamentoLocal Al = new ArmazenamentoLocal(BancadaActivity.this);//Inicializa Construtor da Classe Armazenamento Local com contexto BancadaActivity.this
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");//Obtém dados do usuário
        Inicializabotoes(Al);//Inicializa todos os listeners associados aos botões da tela
        ArrayList<Integer> calendario;//Define uma arraylist responsável por receber dados do calendario(dia,mes,ano,hora,minuto)
        calendario =  InicializaCalendar();//Recebe dados do calendario
       // Toast.makeText(BancadaActivity.this, codigo, Toast.LENGTH_LONG).show();
        NavConfig.NavDrawerConfig(BancadaActivity.this, this, this,dadosusuarioarray.get(0));// Configura barra de navegação
        NavConfig.SetItemacess(false,true,true, BancadaActivity.this,false);//Define quais itens da barra de navegação podem ser acessados(pressionados) pelo usuario
        stop =false;//valor booleano que interrompe função que executa continuamente(public void run()).Sempre começa em false
        TextView textView;//Declara uma TextView
        textView = findViewById(R.id.textView);//Associa TextView ao ID da textView declarado no arquivo layout activity_bancada
        ArrayList<String > nomearray;//Declara array que vai receber conjunto de nomes da memória local
        nomearray = Al.SharedGetArray("nome");//Recebe conjunto de nomes de memória local
        String textobancada = String.format(getResources().getString(R.string.Bancada) , nomearray.get(0));//String que exibe na tela acompanhamento + nome da bancada
        textView.setText(textobancada);//Exibe mensagem na tela
        int mes = calendario.get(1);//Obtém mes do arraylist calendario
        int dia_da_semana = calendario.get(0);//Obtém dia do arraylist calendario
        mes = mes+1;
        String mes_string = "";
        String dia_da_semana_string ="";
        switch(mes){

            case 1:
                mes_string = "Janeiro";
                break;
            case 2 :
                mes_string = "Fevereiro";
                break;
            case 3:
                mes_string = "Março";
                break;
            case 4:
                mes_string = "Abril";
                break;
            case 5:
                mes_string = "Maio";
                break;
            case 6:
                mes_string = "Junho";
                break;
            case 7:
                mes_string = "Julho";
                break;
            case 8:
                mes_string = "Agosto";
                break;
            case 9:
                mes_string = "Setembro";
                break;
            case 10:
                mes_string = "Outubro";
                break;
            case 11:
                mes_string = "Novembro";
                break;
            case 12:
                mes_string = "Dezembro";
                break;



        }
        switch(dia_da_semana){
            case 1:
                dia_da_semana_string = "Domingo";
                break;
            case 2 :
                dia_da_semana_string = "Segunda Feira";
                break;
            case 3:
                dia_da_semana_string = "Terça Feira";
                break;
            case 4:
                dia_da_semana_string = "Quarta Feira";
                break;
            case 5:
                dia_da_semana_string = "Quinta Feira";
                break;
            case 6:
                dia_da_semana_string = "Sexta";
                break;
            case 7:
                dia_da_semana_string = "Sábado";
                break;
        }
        // Cria objeto para classe ConvertData
        ConvertData cd = new ConvertData(BancadaActivity.this,Al);
        int ano = calendario.get(2);
        int dia = calendario.get(3);
        int hora = calendario.get(4);
        int minuto = calendario.get(5);
        //Usando métodos da classe ConvertData converte números inteiros em strings
        String ano_string = cd.IntegertoString(ano);
        String dia_string = cd.IntegertoString(dia);
        String minuto_string= String.format(Locale.getDefault(),"%02d",minuto);
        String hora_string = cd.IntegertoString(hora);
        // Determina como string vai ser exibida na tela
        String textodatabancada = String.format(getResources().getString(R.string.Bancadadata) , dia_da_semana_string, dia_string, mes_string, ano_string);
        String textohorabancada = String.format(getResources().getString(R.string.Bancadahora) , hora_string, minuto_string);
        //Armazena o texto da data da bancada e o texto da hora da bancada
        Al.SharedSetDataString("textodatabancada",textodatabancada);
        Al.SharedSetDataString("textohorabancada",textohorabancada);
        //Chama métdodo que exibe textos na tela
        setTexttela(textodatabancada,textohorabancada, "","", BancadaActivity.this);
        //Método que inicializa uma função que se repete(void loop)
        InicializaRunnable( Al);






    }
    //Método que é executado repetidamente a cada 4 segundos
    //
    void InicializaRunnable(ArmazenamentoLocal Al){
        //Inicialização do método usando handler e new Runnable()
        Handler handler= new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                if (!stop) {
                    //Obtém objeto da disponibilidade de internet
                    InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
                    //Adiciona listener para a verificação de internet
                    mInternetAvailabilityChecker.addInternetConnectivityListener(d);
                    //Se a internet estiver disponível, estiver conectado e a resposta não for nula
                    if ((Internet.isNetworkAvailable(BancadaActivity.this))&& !responsenull&& isconnected) {
                        Intent intent = getIntent();
                        mensagem = false;
                        //Habilita acesso as telas histórico e lista.
                        NavConfig.SetItemacess(false,true,true, BancadaActivity.this,false);
                        //Obtém posição da lista que foi selecionada na tela ListViewActivity
                        int position = intent.getIntExtra("position", -1);
                        //Define Url responsável pelo envio dos últimos dados de concentração e temperatura ao aplicativo
                        String urlAddress = "https://appfertileasy.herokuapp.com/androidacompanharbancada.php";
                        ArrayList<String> codigobancadaarray;//Define arrayList que irá receber códigos das bancadas
                        codigobancadaarray = Al.SharedGetArray("codigobancadaarray");//Recebe da memória códigos das bancadas
                        if (codigobancadaarray.size() != 0) {//Se já houver códigos de bancadas
                            mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(d);//Remove Listener de verificação de internet
                            //Envia dados para a classe Envio que serão direcionados para o arquivo php, androidacompanharbancada.php.
                            Envio s = new Envio(BancadaActivity.this, urlAddress, "", "", codigobancadaarray.get(position), "", "", "", "", "", "", "", "", "", "", BancadaActivity.this,1,position,"");
                            //Executa métodos dessa classe
                            s.execute();


                        }
                        //Se a resposta for nula
                        if (responsenull) {
                            //Imprime na tela mensagem abaixo
                            Toast.makeText(BancadaActivity.this, "Não há dados no histórico dessa bancada", Toast.LENGTH_SHORT).show();
                        }

                    }
                    //Se a internet não está disponível e não está conectado
                    if (!Internet.isNetworkAvailable(BancadaActivity.this)||!isconnected) {
                        if (!mensagem) {
                            mensagem = true;
                            //Desabilita o acesso as telas histórico e lista.
                            NavConfig.SetItemacess(false,false,false, BancadaActivity.this,false);
                            //Imprime na tela mensagem abaixo
                            Toast.makeText(BancadaActivity.this, "Não foi possível se conectar a internet, dados não puderam ser obtidos", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //Define intervalo entre execuções do método(4000 milisegundos= 4 segundos)
                    handler.postDelayed(this, 4000);
                }
            }
        });
    }
//Método que é chamado quando a tela se torna visiível para o usuário depois que o aplicativo foi minimizado.
    // sobrescreve método onStart da classe Activity
    @Override
    protected void onStart() {
        super.onStart();
        stop=false;
        ArmazenamentoLocal Al = new ArmazenamentoLocal(BancadaActivity.this);
        ArrayList<String> dadosusuarioarray;
        //Recebe dados do usuário armazenados localmente
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
        NavConfig.NavDrawerConfig(BancadaActivity.this, this, this,dadosusuarioarray.get(0));
        //Define quais itens da barra de navegação podem ser acessados(pressionados) pelo usuario
        NavConfig.SetItemacess(false,true,true, BancadaActivity.this,false);
        //Método que inicializa uma função que se repete(void loop)
        InicializaRunnable(Al);
    }
    //Método executado quando a tela não está mais visível, sobrescreve método onStop da classe Activity.
    @Override
    protected void onStop() {
        //Herda método onStop e adiciona algo além
        super.onStop();
        //Interrompe Método Inicializa Runnable
        stop=true;
    }
//Método que inicializa botões da tela
    void Inicializabotoes(ArmazenamentoLocal Al){
        //Define botão
        Button updatebutton;
        //Associa botão a ID do botão update definido no layout activity_bancada
        updatebutton = findViewById(R.id.update);
        //Cria listener para o botão. Se o botão for pressionado comandos abaixo serão executados
        updatebutton.setOnClickListener((View v) -> {
            InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(d);
            //Se a internet estiver disponível e estiver conectado
            if((Internet.isNetworkAvailable(BancadaActivity.this)&&isconnected)) {
                String urlAddress = "https://appfertileasy.herokuapp.com/androidacompanharbancada.php";
                ArrayList<String> codigobancadaarray;
                mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(d);
                codigobancadaarray = Al.SharedGetArray("codigobancadaarray");
                Intent intent = getIntent();
                int position = intent.getIntExtra("position", -1);
                if(codigobancadaarray.size()!= 0) {
                    //Toast.makeText(BancadaActivity.this, codigobancadaarray.get(position), Toast.LENGTH_LONG).show();
                    //Envia dados para a classe envio que enviará dados ao php androidacompanharbancada.php
                    Envio s = new Envio(BancadaActivity.this, urlAddress, "", "", codigobancadaarray.get(position), "", "", "", "", "", "", "", "", "", "", BancadaActivity.this,2,-1,"");
                    s.execute();
                }
                else{
                    //Se não houver códigos de bancada
                    Toast.makeText(BancadaActivity.this, "Não há bancadas cadastradas, tente novamente", Toast.LENGTH_SHORT).show();

                }
            }

            if(responsenull) {
                //Se não houver resposta imprimir mensagem abaixo
                Toast.makeText(BancadaActivity.this, "Não há dados no histórico dessa bancada", Toast.LENGTH_SHORT).show();
            }
            //Se não houver internet ou não estiver conectada
            if(!Internet.isNetworkAvailable(BancadaActivity.this)||!isconnected) {
                Toast.makeText(BancadaActivity.this, "Não foi possível se conectar a internet, dados não puderam ser obtidos", Toast.LENGTH_SHORT).show();
            }

        });



    }
    //Método usado para buscar o mês,dia,hora,minuto e dia da semana atual
    ArrayList<Integer> InicializaCalendar(){
        //Cria objeto para a classe Calendar
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        //usa método setTime da classe Calendar passando a data como parametro.
        cal.setTime(date);
        //Obtém dados do calendario em formato int
        int dia_da_semana = cal.get(Calendar.DAY_OF_WEEK);
        int mes = cal.get(Calendar.MONTH);
        int ano = cal.get(Calendar.YEAR);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);
        ArrayList<Integer> calendario = new ArrayList<>();
        //Adiciona esses dados em um ArrayList de inteiros
        calendario.add(dia_da_semana);
        calendario.add(mes);
        calendario.add(ano);
        calendario.add(dia);
        calendario.add(hora);
        calendario.add(minuto);
        return calendario;


    }
//Método que sobrescreve  o método da classe onNavigationItemSelected
    //Responsável por definir o que acontece quando algum dos itens da barra de navegação é pressionado
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //Obtém id do menu de navegação
        int id = menuItem.getItemId();
        Intent intent = getIntent();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(BancadaActivity.this);
        ArrayList<String> codigobancadaarray;
        //Obtém lista de todos os códigos de bancada.
        codigobancadaarray = Al.SharedGetArray("codigobancadaarray");
        switch (id) {

            case R.id.first:
                //Interrompe InicializaRunnable
                stop=true;
                //Aplicativo executa classe FinishActivity onde o programa é fechado.
                intent = new Intent(BancadaActivity.this, FinishActivity.class);
                //Flags usadas no encerramento do programa
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //Muda de tela
                startActivity(intent);
                //Termina troca de tela
                finish();
                break;

            case R.id.logout:
                //Interrompe InicializaRunnable
                stop=true;
                //Aplicativo executa classe LoginActivity.class, exibe tela de Login.
                intent = new Intent(BancadaActivity.this, LoginActivity.class);
                //Muda de tela
                startActivity(intent);
                //Termina troca de tela
                finish();


                break;
            case R.id.lista:
                InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
                mInternetAvailabilityChecker.addInternetConnectivityListener(d);
                if (Internet.isNetworkAvailable(BancadaActivity.this)&&isconnected) {
                    //Interrompe InicializaRunnable
                    stop = true;
                    String urlAddress = "https://appfertileasy.herokuapp.com/androidbancadas.php";
                    String codigo = intent.getStringExtra("codigo");
                    mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(d);

                    int position = intent.getIntExtra("position", -1);
                    Envio s = new Envio(BancadaActivity.this, urlAddress, "", codigo, "", "", "", "", "", "", "", "", "", "", "", BancadaActivity.this, 1, position,"");
                    s.execute();
                }
                if (!Internet.isNetworkAvailable(BancadaActivity.this)||!isconnected) {
                    Toast.makeText(BancadaActivity.this, "Não foi possível se conectar a internet, dados não puderam ser obtidos", Toast.LENGTH_SHORT).show();
                    NavConfig.SetItemacess(true,false,false, BancadaActivity.this,false);

                }

                break;


            case R.id.historico:
                mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
                mInternetAvailabilityChecker.addInternetConnectivityListener(d);
                if ((Internet.isNetworkAvailable(BancadaActivity.this))) {
                    //Interrompe InicializaRunnable
                    stop=true;
                    String urlAddress ="https://appfertileasy.herokuapp.com/androidexportarhistorico.php";
                    intent = getIntent();
                    mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(d);
                    int position = intent.getIntExtra("position", -1);
                    if(position!=-1) {
                        Envio s = new Envio(BancadaActivity.this, urlAddress, "", "", codigobancadaarray.get(position), "", "", "", "", "", "", "", "", "", "", BancadaActivity.this, 1, position, "");
                        s.execute();
                    }
                }





                if (!Internet.isNetworkAvailable(BancadaActivity.this)||!isconnected) {
                    Toast.makeText(BancadaActivity.this, "Não foi possível se conectar a internet, dados não puderam ser obtidos", Toast.LENGTH_SHORT).show();
                    NavConfig.SetItemacess(true,false,false, BancadaActivity.this,false);
                }
                break;

        }
        //Associa barra de navegação a um ID no layout activity_bancada
        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        //Posiciona barra de navegação na borda esquerda da tela
        drawerlayout.closeDrawer(Gravity.START);
        return true;
    }
    //Método que exibe dados na tela
    static void setTexttela(String s , String t,String C,String T, Activity act){
        android.widget.TextView Data;
        android.widget.TextView Hora;
        android.widget.TextView Concentracao;
        android.widget.TextView Temperatura;
        //Associa Data com ID do textView3 no layout activity_bancada
        Data = act.findViewById(R.id.textView3);
        //Associa Data com ID do textView4 no layout activity_bancada
        Hora = act.findViewById(R.id.textView4);
        //Associa Data com ID da concentracao no layout activity_bancada
        Concentracao =act.findViewById(R.id.concentracao);
        //Associa Data com ID da temperatura no layout activity_bancada
        Temperatura = act.findViewById(R.id.temperatura);
        //Exibe concentracao na tela
        Concentracao.setText(C);
        //Exibe temperatura na tela
        Temperatura.setText(T);
        //Exibe Data na tela
        Data.setText(s);
        //Exibe Hora na tela
        Hora.setText(t);


    }
    //Método que verifica se internet está disponível
    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        //Se s internet estiver disponível isconnected=true
        isconnected=isConnected;
    }
}
