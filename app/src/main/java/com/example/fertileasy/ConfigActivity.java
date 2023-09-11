package com.example.fertileasy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.widget.TimePicker;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class ConfigActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener,NavigationView.OnNavigationItemSelectedListener,Internet, InternetConnectivityListener {
    boolean stop = false;
    boolean isconnected = false;
    public static  TextView MyQrcode;
    boolean saved=false;
    String sconcentracao, sciclosnoturnos, scultura, stipodebancada,sdatadecultivostr;
    String siniciododia, sfimdodia,stempoligado,stempodesligado,sqrcode;

    //Sobrescreve método onCreate da classe android.app.Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// herda método onCreate e considera codigo abaixo
        setContentView(R.layout.activity_config);//Associa layout activity_config a Bancada Activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Determina orientação da tela:retrato
        ArrayList<String> dadosusuarioarray;
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ConfigActivity.this);
        //Obtém dados do usuário
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
        //Coloca valor nulo ao qrcodetext
        Al.SharedSetDataString("qrcodetext","");
        //Define barra de navegação para essa tela
        NavConfig.NavDrawerConfig(ConfigActivity.this,this,this, dadosusuarioarray.get(0));
        //Habilita ou desabilita acessos a telas
        NavConfig.SetItemacess(true,true,true,ConfigActivity.this,false);
        ArrayList<Object> obj;
        //Associa informações da bancada(Tempo Ligado,Tempo Desligado,Cultura,Concentracao...) ao layout activity_config
        //Salva todos esses dados em um arraylist do tipo Object
        obj = InicializaWidgets();
        Intent intent = getIntent();
        ArrayList<String> nomearray;
        //Obtém conjunto de nomes das bancadas
        nomearray= Al.SharedGetArray("nome");
        int position = intent.getIntExtra("position", -1);
        //Configura acessos a barra de navegação e coloca informações na tela.
        InitConfig( position,obj, Al, nomearray);
        Inicializabool();
        //Inicializa botões
        InicializaListeners(obj, position);
        //Recebe paraâmetros Ano,Mês,Dia do mês,hora e minuto
        final Calendar cal = Calendar.getInstance();
        cal.get(Calendar.YEAR);
        cal.get(Calendar.MONTH);
        cal.get(Calendar.DAY_OF_MONTH);
        cal.get(Calendar.HOUR);
        cal.get(Calendar.MINUTE);


    }
    //Desabilita botão de voltar do celular
    @Override
    public void onBackPressed() {

    }
//Método que é chamado quando a tela se torna visiível para o usuário depois que o aplicativo foi minimizado.
    // sobrescreve método onStart da classe Activity
    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<String> dadosusuarioarray;
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ConfigActivity.this);
        Al.SharedSetDataString("qrcodetext","");
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
        NavConfig.NavDrawerConfig(ConfigActivity.this,this,this,dadosusuarioarray.get(0));
        NavConfig.SetItemacess(true,true,true,ConfigActivity.this,false);
        ArrayList<Object> obj;
        obj = InicializaWidgets();
        Intent intent = getIntent();
        ArrayList<String> nomearray;
        nomearray= Al.SharedGetArray("nome");
        int position = intent.getIntExtra("position", -1);
        //Configura acessos a barra de navegação e coloca informações na tela.
        InitConfig( position,obj, Al, nomearray);
        Inicializabool();
        //Inicializa Botões da tela(Atualizar, calendario,tempo ligado,tempo desligado, data de cultivo)
        InicializaListeners(obj, position);
        //Obtém objeto da classe calendário
        final Calendar cal = Calendar.getInstance();
        //Recebe paraâmetros Ano,Mês,Dia do mês,hora e minuto
        cal.get(Calendar.YEAR);
        cal.get(Calendar.MONTH);
        cal.get(Calendar.DAY_OF_MONTH);
        cal.get(Calendar.HOUR);
        cal.get(Calendar.MINUTE);
    }

    ArrayList<Object> InicializaWidgets() {
        ArrayList<String> bancadadadosarray;
        ArrayList<Object> textList = new ArrayList<>();
        //Associa informações do layout da tela através do ID com a parte lógica(escrita/leitura de dados)
        TextView tempodesligadoview = findViewById(R.id.tempodesligadoview);
        TextView tempoligadoview = findViewById(R.id.tempoligadoview);
        TextView fimdodiaview = findViewById(R.id.fimdodiaview);
        TextView iniciododiaview = findViewById(R.id.iniciododiaview);
        TextView datadecultivo = findViewById(R.id.datadecultivo);
        EditText Mytipodebancada = findViewById(R.id.tipodebancada);
        EditText Mycultura = findViewById(R.id.cultura);
        EditText Myconcentracao = findViewById(R.id.concentração);
        EditText Myciclosnoturnos = findViewById(R.id.ciclosnoturnos);
        MyQrcode = findViewById(R.id.qrcodetext);
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ConfigActivity.this);
        //Recebe da memória local dados da bancada selecionada
        bancadadadosarray=Al.SharedGetArray("bancadadadosarray");
        textList.add(datadecultivo);
        textList.add(iniciododiaview);
        textList.add(fimdodiaview);
        textList.add(tempoligadoview);
        textList.add(tempodesligadoview);
        textList.add(Myconcentracao);//editList.get(0)
        textList.add(Myciclosnoturnos);//editList.get(1)
        textList.add(Mycultura);//
        textList.add(Mytipodebancada);
        //Se não houver 12 dados nesse arraylist, adiciona valores nulos no Arraylist
        if(bancadadadosarray.size()!=12) {
            textList.add("");
            textList.add("");
            textList.add("");
            textList.add("");
            textList.add("");
            textList.add("");
            textList.add("");
            textList.add("");
            textList.add("");
            //Se for diferente de zero adiciona apenas o qrcode
            if(bancadadadosarray.size()!=0) {
                textList.add(bancadadadosarray.get(bancadadadosarray.size()-1));
            }
            else{
                textList.add("");
            }
        }
        //Se o tamanho da bancada for = 12 e dados não tiverem sido salvos
        if(bancadadadosarray.size()==12&&!saved) {
            String Qrcodeconfig = bancadadadosarray.get(11);
           // Toast.makeText(ConfigActivity.this, Qrcodeconfig, Toast.LENGTH_SHORT).show();
            String Tipodebancada = bancadadadosarray.get(1);
            String Cultura = bancadadadosarray.get(2);
            String Concentracao = bancadadadosarray.get(3);
            String Ciclosnoturnos = bancadadadosarray.get(8);
            //Converte formato de tempo de HH:mm:ss para HH:mm e formato yyyy-MM-dd para dd/MM/yyyy
            String Tempoligado = DateFormat.getFormatedDateTime(bancadadadosarray.get(6), "HH:mm:ss", "HH:mm");
            String Tempodesligado = DateFormat.getFormatedDateTime(bancadadadosarray.get(7), "HH:mm:ss", "HH:mm");
            String Iniciododia = DateFormat.getFormatedDateTime(bancadadadosarray.get(4), "HH:mm:ss", "HH:mm");
            String Fimdodia = DateFormat.getFormatedDateTime(bancadadadosarray.get(5), "HH:mm:ss", "HH:mm");
            String Datadecultivo = DateFormat.getFormatedDateTime(bancadadadosarray.get(9), "yyyy-MM-dd", "dd/MM/yyyy");
            textList.add(Tipodebancada);
            textList.add(Cultura);
            textList.add(Concentracao);
            textList.add(Ciclosnoturnos);
            textList.add(Tempoligado);
            textList.add(Tempodesligado);
            textList.add(Iniciododia);
            textList.add(Fimdodia);
            textList.add(Datadecultivo);
            textList.add(Qrcodeconfig);
        }
        //Se dados já tiverem sido salvos
        if(saved) {
            textList.add(stipodebancada);
            textList.add(scultura);
            textList.add(sconcentracao);
            textList.add(sciclosnoturnos);
            textList.add(stempoligado);
            textList.add(stempodesligado);
            textList.add(siniciododia);
            textList.add(sfimdodia);
            textList.add(sdatadecultivostr);
            textList.add(sqrcode);

        }



        return textList;
    }
    //Inicializa ArrayList booleano com 4 valores false, salva esse array na memória local
    void Inicializabool() {
        ArrayList<Boolean> boollist = new ArrayList<>();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ConfigActivity.this);
        boollist.add(false);
        boollist.add(false);
        boollist.add(false);
        boollist.add(false);
        Al.SharedSetDataInt("sizebooldata", boollist.size());
        Al.SharedSetArrayBool(boollist);


    }
    //Método que exibe dados na tela
    void SetScreenData(String s, ArrayList<Object> obj) {
        //Recupera todos os objetos do Arraylist do tipo objeto
        Object o1 = obj.get(0);
        Object o2 = obj.get(1);
        Object o3 = obj.get(2);
        Object o4 = obj.get(3);
        Object o5 = obj.get(4);
        Object o6 = obj.get(5);
        Object o7 = obj.get(6);
        Object o8 = obj.get(7);
        Object o9 = obj.get(8);
        Object o10 = obj.get(9);
        Object o11 = obj.get(10);
        Object o12 = obj.get(11);
        Object o13 = obj.get(12);
        Object o14 = obj.get(13);
        Object o15 = obj.get(14);
        Object o16 = obj.get(15);
        Object o17 = obj.get(16);
        Object o18 = obj.get(17);
        Object o19 = obj.get(18);
        //Recupera formato inicial do dado
        TextView datadecultivo = (TextView) o1;
        TextView iniciododiaview = (TextView) o2;
        TextView fimdodiaview = (TextView) o3;
        TextView tempoligadoview = (TextView) o4;
        TextView tempodesligadoview = (TextView) o5;
        EditText Myconcentracao = (EditText) o6;
        EditText Myciclosnoturnos = (EditText) o7;
        EditText Mycultura = (EditText) o8;
        EditText Mytipodebancada = (EditText) o9;
        String Tipodebancada = (String) o10;
        String Cultura = (String) o11;
        String Concentracao = (String) o12;
        String Ciclosnoturnos = (String) o13;
        String Tempoligado = (String) o14;
        String Tempodesligado = (String) o15;
        String Iniciododia = (String) o16;
        String Fimdodia = (String) o17;
        String Datadecultivo = (String) o18;
        String Qrcodeconfig = (String) o19;

        //Exibe dados na tela
        Mytipodebancada.setText(Tipodebancada);
        Mycultura.setText(Cultura);
        datadecultivo.setText(Datadecultivo);
        Myconcentracao.setText(Concentracao);
        iniciododiaview.setText(Iniciododia);
        fimdodiaview.setText(Fimdodia);
        tempoligadoview.setText(Tempoligado);
        tempodesligadoview.setText(Tempodesligado);
        Myciclosnoturnos.setText(Ciclosnoturnos);
        EditText Config = findViewById(R.id.bancada);
        Config.setText(s);
        MyQrcode.setText(Qrcodeconfig);

        //Config.setText(bancada);

    }
    //Método que inicializa todos os botões
    public void InicializaListeners(ArrayList<Object> obj, int position) {
        Button Atualizar = findViewById(R.id.atualizar);
        //Atualizar.setEnabled(false);
        ArrayList<String>  bancadadadosarray;
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ConfigActivity.this);
        bancadadadosarray=Al.SharedGetArray("bancadadadosarray");
        ImageView Escolher_data = findViewById(R.id.image_delete);
        //Associa imagens ao ID do layout activity_config
        ImageView time_on = findViewById(R.id.time_on);
        ImageView time_off = findViewById(R.id.time_off);
        ImageView dayend = findViewById(R.id.day_end);
        ImageView day_begin = findViewById(R.id.day_begin);
        ImageView Qrcode = findViewById(R.id.qrcode);
        //Se a bancada selecionada já existir não haverá botão de Qrcode
        if(position !=-1){
            Qrcode.setVisibility(View.GONE);
            //Qrcode.setEnabled(false);
            //Qrcode.getBackground().setAlpha(0);
        }

        Object o1 = obj.get(0);
        Object o2 = obj.get(1);
        Object o3 = obj.get(2);
        Object o4 = obj.get(3);
        Object o5 = obj.get(4);
        Object o6 = obj.get(5);
        Object o7 = obj.get(6);
        Object o8 = obj.get(7);
        Object o9 = obj.get(8);
        TextView datadecultivo = (TextView) o1;
        TextView iniciododiaview = (TextView) o2;
        TextView fimdodiaview = (TextView) o3;
        TextView tempoligadoview = (TextView) o4;
        TextView tempodesligadoview = (TextView) o5;
        EditText Myconcentracao = (EditText) o6;
        EditText Myciclosnoturnos = (EditText) o7;
        EditText Mycultura = (EditText) o8;
        EditText Mytipodebancada = (EditText) o9;
        //Define listeners e o que acontece após pressionar cada botão
        Escolher_data.setOnClickListener(this::MostrarData);
        //Botão correspondente ao tempo ligado
        time_on.setOnClickListener((View v) -> {

            ArrayList<Boolean> boollist = new ArrayList<>();
            boollist = Al.SharedGetArrayBool(boollist);
            boollist.set(2, true);
            Al.SharedSetArrayBool(boollist);
            MostrarHora2format( v);


        });
        //Botão correspondenten ao tempo desligado
        time_off.setOnClickListener((View v) -> {

            ArrayList<Boolean> boollist = new ArrayList<>();
            boollist = Al.SharedGetArrayBool(boollist);
            boollist.set(3, true);
            Al.SharedSetArrayBool(boollist);
            MostrarHora2format( v);

        });
        //Botão correspondente ao inicio do dia
        day_begin.setOnClickListener((View v) -> {

            ArrayList<Boolean> boollist = new ArrayList<>();
            boollist = Al.SharedGetArrayBool(boollist);
            boollist.set(0, true);
            Al.SharedSetArrayBool(boollist);
            MostrarHora(v);

        });
        //Botão correspondente ao final do dia
        dayend.setOnClickListener((View v) -> {
            ArrayList<Boolean> boollist = new ArrayList<>();
            boollist = Al.SharedGetArrayBool(boollist);
            boollist.set(1, true);
            Al.SharedSetArrayBool(boollist);
            MostrarHora(v);

        });
        //Botão correspondente ao QRcode
        Qrcode.setOnClickListener((View v) -> {
            Intent intent = new Intent(ConfigActivity.this, ScanCodeActivity.class);
            startActivity(intent);



        });
        //Botão Atualizar
        Atualizar.setOnClickListener((View v) -> {
            InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(ConfigActivity.this);
            //Se internet estiver conectada e disponível
            if (Internet.isNetworkAvailable(ConfigActivity.this)&&isconnected) {
                //Remove verificador de disponibilidade de internet
                mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(ConfigActivity.this);
                //Converte dados digitados na tela em String
                String Tipodebancada = Mytipodebancada.getText().toString();
                String Cultura = Mycultura.getText().toString();
                String Datadecultivo = datadecultivo.getText().toString();
                Datadecultivo =DateFormat.getFormatedDateTime(Datadecultivo, "dd/MM/yyyy", "yyyy-MM-dd");
                String Concentracao = Myconcentracao.getText().toString();
                String Iniciododia = iniciododiaview.getText().toString();
                String Fimdodia = fimdodiaview.getText().toString();
                String Tempoligado = tempoligadoview.getText().toString();
                String Tempodesligado = tempodesligadoview.getText().toString();
                String Ciclosnoturnos = Myciclosnoturnos.getText().toString();
                EditText Config = findViewById(R.id.bancada);
                ArrayList<String> codigobancadaarray;
                ArrayList<String> nomearray;
                codigobancadaarray = Al.SharedGetArray("codigobancadaarray");
                String nomedabancada = Config.getText().toString();

                if (position != -1) {
                    String qrcodevalue = bancadadadosarray.get(11);
                    nomearray= Al.SharedGetArray("nome");
                    nomearray.set(position,nomedabancada);
                    String urlAddress = "https://appfertileasy.herokuapp.com/androidatualizarbancada.php";
                    //Faz Post do código da bancada e de todas as informações, retorna confirmação da atualização dos dados
                    Envio s = new Envio(ConfigActivity.this, urlAddress, "", "",  codigobancadaarray.get(position), Tipodebancada, Cultura, Concentracao, Iniciododia, Fimdodia, Tempoligado, Tempodesligado, Ciclosnoturnos, Datadecultivo, nomedabancada, ConfigActivity.this,1, position,qrcodevalue);
                    s.execute();
                }
                else
                    {
                        String qrcodevalue = bancadadadosarray.get(bancadadadosarray.size()-1);
                        String codigo = Al.SharedGetDataString("codigo");
                    String urlAddress = "https://appfertileasy.herokuapp.com/androidnovabancada.php";
                        Envio s = new Envio(ConfigActivity.this, urlAddress, "", codigo,  "", Tipodebancada, Cultura, Concentracao, Iniciododia, Fimdodia, Tempoligado, Tempodesligado, Ciclosnoturnos, Datadecultivo, nomedabancada, ConfigActivity.this,1, position,qrcodevalue);
                        s.execute();

                }
            } if(!Internet.isNetworkAvailable(ConfigActivity.this)||!isconnected) {
                Toast.makeText(ConfigActivity.this, "Não foi possível se conectar a internet, tente novamente", Toast.LENGTH_SHORT).show();

            }

        });
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String Tipodebancada = Mytipodebancada.getText().toString();
                String Cultura = Mycultura.getText().toString();
                String Datadecultivo = datadecultivo.getText().toString();
                String Concentracao = Myconcentracao.getText().toString();
                String Iniciododia = iniciododiaview.getText().toString();
                String Fimdodia = fimdodiaview.getText().toString();
                String Tempoligado = tempoligadoview.getText().toString();
                String Tempodesligado = tempodesligadoview.getText().toString();
                String Ciclosnoturnos = Myciclosnoturnos.getText().toString();
                String QrCode = MyQrcode.getText().toString();
                sconcentracao=Concentracao;
                sciclosnoturnos=Ciclosnoturnos;
                stipodebancada=Tipodebancada;
                scultura=Cultura;
                sdatadecultivostr=Datadecultivo;
                siniciododia=Iniciododia;
                sfimdodia=Fimdodia;
                stempoligado=Tempoligado;
                stempodesligado=Tempodesligado;
                sqrcode=QrCode;
                saved = true;
                InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
                mInternetAvailabilityChecker.addInternetConnectivityListener(ConfigActivity.this);
                if (Internet.isNetworkAvailable(ConfigActivity.this)&&isconnected) {
                    mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(ConfigActivity.this);
                    NavConfig.SetItemacess(true,true,true,ConfigActivity.this,false);
                    //Se houver dados nos campos(Tipo de Bancada,Cultura, Data de Cultivo,Concentração, Inicio do Dia, Fim do Dia, Tempo Ligado, Tempo Desligado, Ciclos Noturnos, QRCode)
                    if (!Tipodebancada.equals("") && !Cultura.equals("") && !Datadecultivo.equals("") && !Concentracao.equals("") && !Iniciododia.equals("") && !Fimdodia.equals("") && !Tempoligado.equals("") && !Tempodesligado.equals("") && !Ciclosnoturnos.equals("")&& !QrCode.equals("")) {
                        //Habilita botão atualizar
                        Atualizar.setEnabled(true);
                        NavConfig.SetItemacess(true,true,true,ConfigActivity.this,true);
                        Button shape =  findViewById(R.id.atualizar);
                        Drawable bgShape = shape.getBackground();
                        //Muda cor do botão para verde
                        int color = ResourcesCompat.getColor(getResources(), R.color.colorPrimary,null);
                        bgShape.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    } else {
                        //Desabilita botão atualizar
                        Atualizar.setEnabled(false);
                        Button shape = findViewById(R.id.atualizar);
                        shape.getBackground();
                        Drawable bgShape;
                        //Muda cor do botão para verde transparente
                        int color = ResourcesCompat.getColor(getResources(), R.color.colorTransp,null);
                        bgShape = shape.getBackground();
                        bgShape.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    }
                }
                if (!Internet.isNetworkAvailable(ConfigActivity.this)||!isconnected) {
                    DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
                    drawerlayout.closeDrawer(Gravity.START);
                    NavConfig.SetItemacess(true,false,false,ConfigActivity.this,false);
                }

                if (!stop) {
                    handler.postDelayed(this, 500);
                }
            }
        });

    }

    void AtualizarData(int Ano, int Mes, int Dia) {
        ArrayList<Object> obj;
        obj = InicializaWidgets();
        Object o1 = obj.get(0);
        TextView datadecultivo = (TextView) o1;
        //Atualiza data na tela depois de pressionar botão para selecionar a data
        datadecultivo.setText(new StringBuilder().append(Ano).append("-").append(Mes + 1).append("-").append(Dia).append(" "));
    }





    void AtualizarFimdodia(int Hora, int Minuto) {
        ArrayList<Object> obj;
        obj = InicializaWidgets();
        Object o3 = obj.get(2);
        TextView fimdodiaview = (TextView) o3;

        String curTime = String.format(Locale.getDefault(), "%02d", Minuto);
        //Atualiza Fim do Dia na tela depois de pressionar botão para selecionar fim do dia
        fimdodiaview.setText(new StringBuilder().append(Hora).append(":").append(curTime));
    }

    void AtualizarTempoLigado(int Hora, int Minuto) {
        ArrayList<Object> obj;
        obj = InicializaWidgets();
        Object o4 = obj.get(3);
        TextView tempoligadoview = (TextView) o4;
        String curTime = String.format(Locale.getDefault(), "%02d", Minuto);
        //Atualiza Tempo Ligado na tela depois de pressionar botão para selecionar tempo ligado
        tempoligadoview.setText(new StringBuilder().append(Hora).append(":").append(curTime));
    }

    void AtualizarTempoDesligado(int Hora, int Minuto) {
        ArrayList<Object> obj;
        obj = InicializaWidgets();
        Object o5 = obj.get(4);
        TextView tempodesligadoview = (TextView) o5;
        String curTime = String.format(Locale.getDefault(), "%02d", Minuto);
        //Atualiza Tempo Desligado na tela depois de pressionar botão para selecionar tempo desligado
        tempodesligadoview.setText(new StringBuilder().append(Hora).append(":").append(curTime));
    }

    public void MensagemData() {
        //Toast.makeText(c, new- StringBuilder().append("Data: ").append(iniciododia.getText()),  Toast.LENGTH_SHORT).show();
    }

    public void AtualizarInicioDoDia(int Hora, int Minuto) {
        String curTime = String.format(Locale.getDefault(), "%02d", Minuto);
        ArrayList<Object> obj;
        obj = InicializaWidgets();
        Object o2 = obj.get(1);
        TextView iniciododiaview = (TextView) o2;
        //Atualiza Inicio do Dia na tela depois de pressionar botão para selecionar inicio do dia
        iniciododiaview.setText(new StringBuilder().append(Hora).append(":").append(curTime));
    }

    public void MensagemHora() {
        // Toast.makeText(this, new StringBuilder().append("Hora: ").append(iniciododia.getText()),  Toast.LENGTH_SHORT).show();
    }

    public void MostrarHora(View v) {
        DialogFragment ClasseHora = new TimePickerFragment();
        //Mostra caixa com relógio para seleção de hora e minuto
        ClasseHora.show(getFragmentManager(), "timePicker");
    }
    public void MostrarHora2format(View v) {
        final Calendar cal = Calendar.getInstance();

        cal.get(Calendar.HOUR);
        cal.get(Calendar.MINUTE);
        //Mostra caixa para seleção de hora e minuto
        TimePickerDialog timePickerDialog = new TimePickerDialog(ConfigActivity.this,R.style.CustomTimePickerDialog,ConfigActivity.this,cal.get(Calendar.HOUR),cal.get(Calendar.MINUTE),true);
        timePickerDialog.show();
    }

    public void MostrarData(View v) {
        DialogFragment ClasseData = new DataPickerFragment();
        //Mostra caixa para seleção da data do calendário
        ClasseData.show(getFragmentManager(), "datepicker");
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        AtualizarData(year, month, day);


        MensagemData();
    }
    //Método para selecionar a atualização de algum atributo dependendo do botão que foi pressionado
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ConfigActivity.this);
        ArrayList<Boolean> boollist = new ArrayList<>();
        boollist = Al.SharedGetArrayBool(boollist);
        boolean iniciodia = boollist.get(0);
        boolean fimdodia = boollist.get(1);
        boolean timeon = boollist.get(2);
        boolean timeoff = boollist.get(3);


        if (iniciodia) {
            AtualizarInicioDoDia(hourOfDay, minute);
            boollist.set(0, false);
            Al.SharedSetArrayBool(boollist);

        }
        if (fimdodia) {
            AtualizarFimdodia(hourOfDay, minute);
            boollist.set(1, false);
            Al.SharedSetArrayBool(boollist);

        }
        if (timeon) {
            AtualizarTempoLigado(hourOfDay, minute);
            boollist.set(2, false);
            Al.SharedSetArrayBool(boollist);

        }
        if (timeoff) {
            AtualizarTempoDesligado(hourOfDay, minute);
            boollist.set(3, false);
            Al.SharedSetArrayBool(boollist);

        }

        MensagemHora();

    }
//Barra de navegação que descreve o que acontece ao selecionar cada item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent = getIntent();
        intent.getIntExtra("position", -1);
        int position;
        InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(ConfigActivity.this);
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ConfigActivity.this);
        if (!Internet.isNetworkAvailable(ConfigActivity.this)||!isconnected) {
            NavConfig.SetItemacess(true,false,false,ConfigActivity.this,false);

        }



        switch (id) {
            //Se item quit for pressionado
            case R.id.first:
                stop=true;
                intent = new Intent(ConfigActivity.this, FinishActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();


                break;
                //Se item login for pressionado
            case R.id.logout:
                stop=true;
                intent = new Intent(ConfigActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();


                break;
                //Se item lista de bancadas for pressionado
            case R.id.lista:
                String urlAddress = "https://appfertileasy.herokuapp.com/androidbancadas.php";
                String codigo =  intent.getStringExtra("codigo");
                position = intent.getIntExtra("position", -1);
                mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(ConfigActivity.this);
                Envio s = new Envio(ConfigActivity.this, urlAddress, "", codigo, "", "", "", "", "", "", "", "", "", "", "", ConfigActivity.this, 1, position,"");
                s.execute();
                stop=true;
                break;

            //Se item valores recentes for pressionado
            case R.id.valores:
                stop=true;
                codigo =  intent.getStringExtra("codigo");
                position =  intent.getIntExtra("position",-1);
                intent = new Intent(ConfigActivity.this, BancadaActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("codigo",codigo);
                startActivity(intent);
                finish();


                break;
            //Se item histórico for pressionado
            case R.id.historico:
                    stop = true;
                    urlAddress = "https://appfertileasy.herokuapp.com/androidexportarhistorico.php";
                    ArrayList<String> codigobancadaarray;
                    mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(ConfigActivity.this);
                    codigobancadaarray = Al.SharedGetArray("codigobancadaarray");
                    intent = getIntent();
                    position = intent.getIntExtra("position", -1);
                    //Toast.makeText(ConfigActivity.this, codigobancadaarray.get(position), Toast.LENGTH_SHORT).show();
                    if(position!=-1) {
                        s = new Envio(ConfigActivity.this, urlAddress, "", "", codigobancadaarray.get(position), "", "", "", "", "", "", "", "", "", "", ConfigActivity.this, 1, position, "");
                        s.execute();
                    }

                break;
                    //Se item credenciais Wi-fi for pressionado
            case R.id.configesp32:
                codigo =  intent.getStringExtra("codigo");
                intent = new Intent(ConfigActivity.this, WebSocketActivity.class);
                intent.putExtra("codigo",codigo);
                startActivity(intent);
                finish();


                break;
        }
        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        drawerlayout.closeDrawer(Gravity.START);
        return true;
    }

    void InitConfig(int position,ArrayList<Object>obj,ArmazenamentoLocal Al,ArrayList<String> nomearray){
        // Se a bancada for selecionada na lista de bancadas
        if (position!=-1) {
            Al.SharedSetDataString("nomelistabancada","");
            SetScreenData(nomearray.get(position), obj);
            NavConfig.SetItemacess(true,true,true,ConfigActivity.this,false);
        }
        //Se a bancada for nova (recém criada)
        else {
            NavConfig.SetItemacess(false,false,true,ConfigActivity.this,false);
        }

    }

    //Método que verifica se internet está disponível
    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        //Se a internet estiver disponível isconnected=true
        isconnected=isConnected;

    }
}






