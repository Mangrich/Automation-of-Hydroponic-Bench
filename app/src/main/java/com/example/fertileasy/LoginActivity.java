package com.example.fertileasy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;
import java.util.Collections;
//Tela responsável pelo login do usuário
public class LoginActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener,Internet, InternetConnectivityListener {
    private static LoginActivity d;
    private boolean stop = false;
    private boolean isconnected = false;

    @Override//Método executado uma vez no início de cada atividade(tela)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//estende méthodo onCreate
        setContentView(R.layout.activity_login);//associa atividade ao respectivo layout
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//define orientação da tela como retrato
        InternetAvailabilityChecker.init(this);//começa a verificação do status da rede
        d=this;//Referencia estática da atividade LoginActivit
        ArmazenamentoLocal Al = new ArmazenamentoLocal(LoginActivity.this);//Cria um objeto com referencia a classe para armazenamento local
        ProgressBar pb = findViewById(R.id.progressBar);//Associa a barra de rolamento ao respectivo layout
        pb.setVisibility(View.GONE);//Define barra de rolamento como visível
        ResetVariables(Al);//Limpa a memória de todas as variáveis
        NavConfig.NavDrawerConfig(LoginActivity.this,this,this, "");//Configura menu de navegação
        NavConfig.SetItemacess(false,false,false,LoginActivity.this,false);

        InicializaListeners( Al);//Inicializa escutadores

    }

    @Override//Método executado quando a atividade é executada após a minimização do aplicativo
    public void onStart() {
        super.onStart();//estende méthodo onStart
        ArmazenamentoLocal Al = new ArmazenamentoLocal(LoginActivity.this);//Cria um objeto com referencia a classe para armazenamento local
        ProgressBar pb = findViewById(R.id.progressBar);//Associa a barra de rolamento ao respectivo layout
        pb.setVisibility(View.GONE);//Define barra de rolamento como visível
        ResetVariables(Al);//Limpa a memória de todas as variáveis
        NavConfig.NavDrawerConfig(LoginActivity.this,this,this,"");//Configura menu de navegação
        NavConfig.SetItemacess(false,false,false,LoginActivity.this,false);
        InicializaListeners(Al);
    }


//Método que define campos de texto para email e senha
    ArrayList<Object> InicializaWidgets() {
        ArrayList<Object> obj = new ArrayList<>();//Cria um array com tamanho indefinido
        AutoCompleteTextView mEmailView = findViewById(R.id.email);//Associa o campo de texto ao respectivo layout
        EditText mPasswordView = findViewById(R.id.password);//Associa o campo de texto ao respectivo layout
        obj.add(mEmailView);//Adiciona ao array do tipo objeto
        obj.add(mPasswordView);//Adiciona ao array do tipo objeto
        return obj;
    }

    void InicializaListeners(ArmazenamentoLocal Al) {
        ArrayList<Object> widget;
        widget = InicializaWidgets();
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        Object w1 = widget.get(0);//Obtém do array campo de texto email
        Object w2 = widget.get(1);//Obtém do array campo de texto senha
        ConvertData cd = new ConvertData(LoginActivity.this, Al);//Cria objeto com referência para classe de Conversão de dados
        AutoCompleteTextView mEmailView = (AutoCompleteTextView) w1;//Recupera formato original do email
        InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();//Obtém instância da classe InternetAvailabilityChecker
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);//Adiciona escutador de status da rede
        EditText mPasswordView = (EditText) w2;//Recupera formato original da senha
        ArrayList<String> dadosusuarioarray = new ArrayList<>();
        mEmailSignInButton.setOnClickListener((View v) -> {//Escutador para clicks do botão  mEmailSignInButton
            // se o botão  mEmailSignInButton for clicado

            ProgressBar pb = findViewById(R.id.progressBar);//Associa a barra de rolamento ao respectivo layout
            pb.setVisibility(View.VISIBLE);//Define barra de rolamento como visível
            if (Internet.isNetworkAvailable(LoginActivity.this)&&isconnected) {//se a internet estiver disponível e estiver conectada
                mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);//remove o escutador
                final String email = cd.editTexttoString(mEmailView);//Converte formato de campo de texto em string
                dadosusuarioarray.add(email);
                Al.SharedSetArray(dadosusuarioarray,"dadosusuarioarray");//Armazena email
                dadosusuarioarray.clear();
                String urlAddress = "https://appfertileasy.herokuapp.com/androidlogin.php";//urlAddress do arquivo php necessário para realizar o login
                Envio s = new Envio(LoginActivity.this, urlAddress, email, "", "", "", "", "", "", "", "", "", "", "", "", LoginActivity.this,2,-1,"");//Cria objeto com referencia a classe envio
                //Classe envio :classe que envia dados do android ao php
                s.execute();//Executa métodos AsyncTask da classe Envio
            }
            if (!Internet.isNetworkAvailable(LoginActivity.this)||!isconnected) {//se a internet não estiver disponível ou não estiver conectada
                pb.setVisibility(View.GONE);//Remove barra de rolamento
                Toast.makeText(LoginActivity.this, "Não foi possível se conectar a internet, tente novamente", Toast.LENGTH_SHORT).show();//Imprime na tela respectiva mensagem

            }
            });

        mPasswordView.setOnEditorActionListener((v, actionId, event) -> {// Escutador para clicks do botão Ok do teclado
            boolean handled = false;//Quando o escutador for executado handled deve ser true

            if (actionId == EditorInfo.IME_ACTION_DONE) {// Se o botão Ok do teclado do celular for pressionado
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                final String email = cd.editTexttoString(mEmailView);
                dadosusuarioarray.add(email);
                Al.SharedSetArray(dadosusuarioarray,"dadosusuarioarray");//Armazena email
                dadosusuarioarray.clear();
                if (Internet.isNetworkAvailable(LoginActivity.this)&&isconnected) {
                    ProgressBar pb = findViewById(R.id.progressBar);
                    //Cria barra de rolamento
                    pb.setVisibility(View.VISIBLE);
                    String urlAddress = "https://appfertileasy.herokuapp.com/androidlogin.php";
                    mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
                    //Faz POST do email, tem como resposta senha e código do usuário.
                    Envio s = new Envio(LoginActivity.this, urlAddress, email, "", "", "", "", "", "", "", "", "", "", "", "", LoginActivity.this,2,-1,"");
                    s.execute();
                } if (!Internet.isNetworkAvailable(LoginActivity.this)||!isconnected) {
                    Toast.makeText(LoginActivity.this, "Não foi possível se conectar a internet, tente novamente", Toast.LENGTH_SHORT).show();

                }


                handled = true;
            }
            return handled;
        });
        //Cria multithread para a habilitação do botão de autenticação
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String password =mPasswordView.getText().toString();
                String email =mEmailView.getText().toString();
                if (!email.equals("") && !password.equals("")) {// se email e senha não forem nulos
                    mEmailSignInButton.setEnabled(true);//Botão de autenticação está habilitado
                    Button shape =  findViewById(R.id.email_sign_in_button);
                    Drawable bgShape = shape.getBackground();//Define um plano de fundo para o botão
                    int color = ResourcesCompat.getColor(getResources(), R.color.colorPrimary,null);//Resgata cor definida no arquivo colors.xml
                    bgShape.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);//Define cor do botão
                }  if (email.equals("") || password.equals("")) {// se email e senha  forem nulos
                    //mPasswordView.setImeOptions(EditorInfo.IME_ACTION_NONE);
                    mEmailSignInButton.setEnabled(false);//Botão de autenticação está desabilitado
                    Button shape = findViewById(R.id.email_sign_in_button);
                    Drawable bgShape = shape.getBackground();
                    int color = ResourcesCompat.getColor(getResources(), R.color.colorTransp,null);
                    bgShape.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }
                if (!stop) {// valor booleano que define se a multithread deve continuar sendo executada a cada 500 ms
                    handler.postDelayed(this, 500);
                }
            }
        });




    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    //Método que define o que acontece ao abrir o menu de navegação
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();


        switch (id) {

            case R.id.first://Se item Quit for pressionado

                //android.os.Process.killProcess(android.os.Process.myPid());
                Intent intent = new Intent(LoginActivity.this, FinishActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();


                break;
        }
        //Fecha barra de navegação
        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        drawerlayout.closeDrawer(Gravity.START);
        return true;
    }

    void ResetVariables(ArmazenamentoLocal Al) {
        //Limpa todas os dados armazenados do programa
        Al.SharedClear();
    }

    static void TentativadeConexao() {
        ArrayList<Object> obj;
        ArrayList<String> dadosusuarioarray;
        ProgressBar pb = d.findViewById(R.id.progressBar);
        obj = d.InicializaWidgets();
        Object o2 = obj.get(1);
        EditText mPasswordView = (EditText) o2;
        ArmazenamentoLocal Al = new ArmazenamentoLocal(d);
        ConvertData cd = new ConvertData(d, Al);
        //Converte dado digitado em formato String
        final String password = cd.editTexttoString(mPasswordView);
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
        String codigo = "-1";
        String senha = "";
        codigo = dadosusuarioarray.get(1);
        senha = dadosusuarioarray.get(2);



        ArrayList<String> codigobancadaarray;
        codigobancadaarray = Al.SharedGetArray("codigobancadaarray");
         //Se a senha digitada for igual a senha retornada pelo POST
        if (password.equals(senha)) {
            d.stop(true);
            //Se ambos não forem nulos
            if (!password.equals("") || !senha.equals("")) {
                   //remove barra de rolamento
                    pb.setVisibility(View.GONE);
                    //Troca tela de login para tela de Lista de bancadas
                    Intent intent = new Intent(d, ListActivity.class);
                    intent.putExtra("codigo", codigo);
                    //Muda tela
                    d.startActivity(intent);
                    //Encerra método
                    d.finish();
            }
        }
        //Se senha digitada não for igual a senha retornada
        if (!password.equals(senha)) {
            d.stop(false);
            //remove barra de rolamento
            pb.setVisibility(View.GONE);
            //Imprime na tela
            Toast.makeText(d, "Senha incorreta, tente novamente", Toast.LENGTH_LONG).show();

        }



    }
    void stop(boolean status){
        stop=status;
    }

    //Método que verifica se internet está disponível
    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        //Se a internet estiver disponível isconnected=true
        isconnected = isConnected;

    }
}
