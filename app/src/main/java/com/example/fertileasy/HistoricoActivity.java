package com.example.fertileasy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.itextpdf.text.Image;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.Locale;
//Atividade que mostra um gráfico com as 20 últimas medições de concentração e possibilita exportar esses dados em um documento pdf
public class HistoricoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InternetConnectivityListener {
    private static final int STORAGE_CODE = 1000;
    boolean mensagem = false;
    boolean stop=false;
    private static HistoricoActivity d;
    boolean isconnected=false;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //Método executado uma vez no início de cada atividade(tela)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//estende méthodo onCreate
        setContentView(R.layout.activity_historico);//associa atividade ao respectivo layout
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//define orientação da tela como paisagem
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels / dm.density;
        d=this;
        //Define um objeto da classe Graphview. Primeiro passo na criação de um gráfico
        GraphView graph = findViewById(R.id.graph);
        //Define botão exportar
        Button Exportar =findViewById(R.id.exportar);
        //Define formatação estática do gráfico
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        ArmazenamentoLocal Al = new ArmazenamentoLocal(HistoricoActivity.this);
        ArrayList<String> dadosusuarioarray;
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
        ArrayList<String> horaarray;
        horaarray = Al.SharedGetArray("hora");
        //Configura barra de navegação e define acesso a itens
        NavConfig.NavDrawerConfig(HistoricoActivity.this, this, this,dadosusuarioarray.get(0));
        NavConfig.SetItemacess(true,false,true, HistoricoActivity.this,false);
        //Define número fixo de valores no eixo x(20 valores)
        graph.getGridLabelRenderer().setNumHorizontalLabels(horaarray.size());
        // se largura da tela for maior que 600
        if (screenWidth>600) {
           // Toast.makeText(HistoricoActivity.this,"maior que 600" ,  Toast.LENGTH_SHORT).show();
            //Configura tamanho da letra do gráfico
            graph.getGridLabelRenderer().setTextSize(20f);
        }
        else{
            //Toast.makeText(HistoricoActivity.this,"menor que 600" ,  Toast.LENGTH_SHORT).show();
            //Configura tamanho da letra do gráfico
            graph.getGridLabelRenderer().setTextSize(8f);

        }
        graph.getGridLabelRenderer().reloadStyles();
           //Cria array de Strings a partir dos dados de hora
            String[] myStringArray = new String[horaarray.size()];
            //Percorre dados do ArrayList de horas atualizando cada índice com a hora no formato HH:mm:ss
            for (int i = horaarray.size() - 1; i >=0; i--) {
                horaarray.set(i, DateFormat.getFormatedDateTime(horaarray.get(i), "HH:mm:ss", "HH:mm"));


            }
            int i = -1;
            //Percorre dados do ArrayList de horas adicionando esses dados no Array String(myStringArray)
            for (int j =  horaarray.size() - 1; j >=0; j--) {
                i++;
                myStringArray[i] = horaarray.get(j);
            }
            //Define os valores do eixo x
            staticLabelsFormatter.setHorizontalLabels(myStringArray);
            //Atualiza o gráfico
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        ArrayList<String> temperaturaarray;
        temperaturaarray = Al.SharedGetArray("temperatura");
        //Cria objeto do tipo LineGraphSeries,objeto contém dados de temperatura
        LineGraphSeries<DataPoint> series=new LineGraphSeries<>(generateData(temperaturaarray,Al));
        //Adiciona esse objeto no gráfico
        graph.addSeries(series);
        ArrayList<String> concentracaoarray;
        concentracaoarray = Al.SharedGetArray("concentracao");



        //Cria objeto do tipo LineGraphSeries,objeto contém dados de concentração
        LineGraphSeries<DataPoint> series2=new LineGraphSeries<>(generateData( concentracaoarray,Al));
        //Adiciona esse objeto no gráfico
        graph.addSeries(series2);
        //Habilita legendas
        graph.getLegendRenderer().setVisible(true);

        //Alinha legendas no canto superior direito
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        //Cria pontos selecionáveis no gráfico(cria objeto da classe PointsGraphSeries)
        //para concentracao e temperatura
        PointsGraphSeries<DataPoint> datapoint = new PointsGraphSeries<>(generateData(temperaturaarray,Al));
        PointsGraphSeries<DataPoint> datapoint2 = new PointsGraphSeries<>(generateData(concentracaoarray,Al));
        //Adiciona ambos ao gráfico
        graph.addSeries(datapoint);
        graph.addSeries(datapoint2);
        //Adiciona títulos a legenda
        series.setTitle("Temperatura");
        series2.setTitle("Concentração");
        datapoint.setTitle("Referencias de Temperatura");
        if (screenWidth>600) {
            datapoint.setSize(10f);
            datapoint2.setSize(10f);
            graph.getLegendRenderer().setTextSize(10f);
        }
        else{
            datapoint.setSize(5f);
            datapoint2.setSize(5f);
            graph.getLegendRenderer().setTextSize(5f);


        }
        datapoint2.setTitle("Referencias de Concentração");
        graph.setTitle("Medições de Temperatura e Concentração");
        //Cria espaço entre valores do eixo e gráfico
        graph.getGridLabelRenderer().setLabelsSpace(18);
        //Define cores para cada objeto de gráfico
        series.setColor(Color.RED);
        series2.setColor(Color.BLUE);
        datapoint.setColor(Color.RED);
        datapoint2.setColor(Color.BLUE);
        //int g = Color.green(color);
        //int b = Color.blue(color);
        //int a = Color.alpha(color);
        //Define o que ocorre ao pressionar um ponto qualquer do gráfico(associado a temperatura)
        datapoint.setOnDataPointTapListener((series12, dataPoint) -> {
            ArrayList<String> resultado;
            String degrees = getResources().getString(R.string.degreescelsius);
            String msg = "Temperatura: "+ dataPoint.getY()+degrees;
            resultado=selecionadado(dataPoint.getX());
            String msg1 = "Hora: "+ resultado.get(0);
            String msg2 = "Data: "+ resultado.get(1);
            Toast.makeText(HistoricoActivity.this,msg + "\n" + msg1+ "\n" + msg2 ,  Toast.LENGTH_SHORT).show();

        });
        //Define o que ocorre ao pressionar um ponto qualquer do gráfico(associado a concentracao)
        datapoint2.setOnDataPointTapListener((series1, dataPoint) -> {
            String ohm = getResources().getString(R.string.ohm);
            String msg = "Concentração: "+ dataPoint.getY()+"k"+ohm;
            ArrayList<String> resultado;
            resultado=selecionadado(dataPoint.getX());
            String msg1 = "Hora: "+ resultado.get(0);
            String msg2 = "Data: "+ resultado.get(1);
            Toast.makeText(HistoricoActivity.this,msg + "\n" + msg1+ "\n" + msg2 ,  Toast.LENGTH_SHORT).show();

        });
        //Define o que ocorre ao pressionar o botão exportar(exporta o histórico em um documento PDF)
        Exportar.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                //Verifica permissões do Android para escrever um arquivo pdf
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                    String[ ] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, STORAGE_CODE);
                }
                else{
                    savePDF(Al);
                }

            }
            else{
                savePDF(Al);
            }

        });
        Handler handler= new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!stop) {
                    InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
                    mInternetAvailabilityChecker.addInternetConnectivityListener(d);
                    //Se a internet estiver conectada e disponível
                    if (Internet.isNetworkAvailable(HistoricoActivity.this)&&isconnected) {
                        mensagem = false;
                        mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(d);
                        //Configura acesso a itens
                        NavConfig.SetItemacess(true,false,true, HistoricoActivity.this,false);
                    }


                    if (!Internet.isNetworkAvailable(HistoricoActivity.this)||!isconnected) {
                        if (!mensagem) {
                            mensagem = true;
                            //Configura acesso a itens
                            NavConfig.SetItemacess(true,false,false, HistoricoActivity.this,false);
                            //Toast.makeText(HistoricoActivity.this, "Não foi possível se conectar a internet, dados não puderam ser obtidos", Toast.LENGTH_SHORT).show();
                        }
                    }
                    handler.postDelayed(this, 4000);
                }
            }
        });






    }
    //Método executado quando a atividade é executada após a minimização do aplicativo
    @Override
    protected void onStart() {
        super.onStart();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(HistoricoActivity.this);
        ArrayList<String> dadosusuarioarray;
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
        NavConfig.NavDrawerConfig(HistoricoActivity.this, this, this,dadosusuarioarray.get(0));
        NavConfig.SetItemacess(false,true,true, HistoricoActivity.this,false);
    }
    //Método responsável pela criação do arquivo pdf
    private void savePDF(ArmazenamentoLocal Al){
        ArrayList<String> dataarray;
        dataarray = Al.SharedGetArray("data");
        ArrayList<String> horaarray;
        horaarray = Al.SharedGetArray("hora");
        ArrayList<String> nomearray;
        nomearray = Al.SharedGetArray("nome");
        ArrayList<String> temperaturaarray;
        temperaturaarray = Al.SharedGetArray("temperatura");
        ArrayList<String> concentracaoarray;
        concentracaoarray = Al.SharedGetArray("concentracao");
        ArrayList<String> dadosusuarioarray;
        dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
       // String email = Al.SharedGetDataString("email");
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);
        //Cria novo documento
         Document doc = new Document();
         //Define nome do arquivo
         String  mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
         //Define onde arquivo será salvo
         String mFilepath =  Environment.getExternalStorageDirectory() + "/" + mFileName +  ".pdf";
         try{
             //Cria arquivo de saída de dados
             PdfWriter.getInstance(doc,new FileOutputStream(mFilepath));
             //Abre documento
             doc.open();
             //Obtém imagem da logo da Fertileasy
             InputStream ims = getAssets().open("fertileasy.png");
             Bitmap bmp = BitmapFactory.decodeStream(ims);
             ByteArrayOutputStream stream = new ByteArrayOutputStream();
             //Comprime imagem
             bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
             Image image = Image.getInstance(stream.toByteArray());
             //Alinha imagem a direita do documento
             image.setAlignment(Element.ALIGN_RIGHT);
             //Adiciona imagem ao documento
             doc.add(image);
             //Define fonte do novo parágrafo
             Font f=new Font(Font.FontFamily.UNDEFINED,25.0f, Font.BOLD, BaseColor.BLACK);
             Paragraph para=new Paragraph("Dados de Temperatura e Concentração",f);

             //Alinha parágrafo no centro
             para.setAlignment(Element.ALIGN_CENTER);
             //Adiciona parágrafo no documento
             doc.add(para);
             //Pula duas linhas
             doc.add( Chunk.NEWLINE );
             doc.add( Chunk.NEWLINE );
             //Adiciona parágrafos
             Paragraph para1=new Paragraph(("Usuario: "+dadosusuarioarray.get(0)));
             para1.setAlignment(para.ALIGN_LEFT);
             doc.add(para1);
             Paragraph para2=new Paragraph(("BancadaActivity: "+nomearray.get(position)));
             para2.setAlignment(para.ALIGN_LEFT);
             doc.add(para2);
             String Datadecultivo = DateFormat.getFormatedDateTime(dataarray.get(dataarray.size()-1), "yyyy-MM-dd", "dd/MM/yyyy");
             Paragraph para3=new Paragraph(("Data: "+Datadecultivo));
             doc.add(para3);
            //Adiciona autor do documento
             doc.addAuthor("Fertileasy");
             //Adiciona uma tabela
             PdfPTable table = new PdfPTable(3);
             //Define parâmetros da tabela
             table.setWidthPercentage(105);
             table.setSpacingAfter(11f);
             table.setSpacingBefore(11f);
             table.setHorizontalAlignment(para2.ALIGN_LEFT);
             //Define 3 colunas
             float[] colWidth ={2f,2f,2f};
             table.setWidths(colWidth);
             PdfPCell c1 = new PdfPCell(new Paragraph("Hora"));
             PdfPCell c2 = new PdfPCell(new Paragraph("Temperatura"));
             PdfPCell c3 = new PdfPCell(new Paragraph("Concentração"));
             //Adiciona a tabela 3 novas células
             table.addCell(c1);
             table.addCell(c2);
             table.addCell(c3);
             for (int i=horaarray.size()-1;i>=0;i--) {

                 table.addCell(new PdfPCell(new Paragraph(horaarray.get(i))));
                 table.addCell(new PdfPCell(new Paragraph(temperaturaarray.get(i))));
                 table.addCell(new PdfPCell(new Paragraph(concentracaoarray.get(i))));
                 if (i != 0) {
                     if (!dataarray.get(i - 1).equals(dataarray.get(i))) {
                         doc.add(table);
                         Datadecultivo = DateFormat.getFormatedDateTime(dataarray.get(i - 1), "yyyy-MM-dd", "dd/MM/yyyy");
                         para3 = new Paragraph(("Data: " +  Datadecultivo));
                         doc.add(para3);
                         table = new PdfPTable(3);
                         table.setWidthPercentage(105);
                         table.setSpacingAfter(11f);
                         table.setSpacingBefore(11f);
                         table.setHorizontalAlignment(para2.ALIGN_LEFT);
                         table.setWidths(colWidth);

                     }
                 }

             }
             //Adiciona tabela ao documento
             doc.add(table);
             //Fecha documento
             doc.close();
             Toast.makeText(HistoricoActivity.this,"Files saved to"+ mFilepath ,  Toast.LENGTH_SHORT).show();
         }
         catch(Exception e){
             Toast.makeText(HistoricoActivity.this,e.getMessage() ,  Toast.LENGTH_SHORT).show();


         }
    }
   //Método que requisita permissões para a escrita de dados
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){

            case STORAGE_CODE:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(HistoricoActivity.this,"Permission Granted" ,  Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(HistoricoActivity.this,"Permission Denied" ,  Toast.LENGTH_SHORT).show();

                }

            }

        }
    }
  //Método que converte dados no formato arraylist em dados no formato DataPoint.
    private DataPoint[] generateData(ArrayList<String> array,ArmazenamentoLocal Al) {
        ConvertData cd = new ConvertData(HistoricoActivity.this, Al);
        DataPoint[] values = new DataPoint[array.size()];
        int count = -1;

                for (int j = array.size()-1; j >= 0 ; j--) {
                    count++;
                    double x = count;
                    double y = cd.ParseDouble(array.get(j));
                    DataPoint v = new DataPoint(x, y);
                    values[count] = v;
                }
        return values;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent = getIntent();
        intent.getStringExtra("codigo");
        int position =intent.getIntExtra("position",-1);




        switch (id) {
            //Se o item escolhido for Quit
            case R.id.first:

                intent = new Intent(HistoricoActivity.this, FinishActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();


                break;
            //Se o item escolhido for Login
            case R.id.logout:
                intent = new Intent(HistoricoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();


                break;
            //Se o item escolhido for Lista
            case R.id.lista:
                InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
                mInternetAvailabilityChecker.addInternetConnectivityListener(this);
                mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
                String urlAddress = "https://appfertileasy.herokuapp.com/androidbancadas.php";
                String codigo = intent.getStringExtra("codigo");
                Envio s = new Envio(HistoricoActivity.this, urlAddress, "", codigo, "", "", "", "", "", "", "", "", "", "", "", HistoricoActivity.this, 1, position,"");
                s.execute();


                break;


            //Se o item escolhido for Valores Recentes
            case R.id.valores:
                codigo =  intent.getStringExtra("codigo");
                intent = new Intent(HistoricoActivity.this, BancadaActivity.class);
                intent.putExtra("codigo",codigo);
                intent.putExtra("position",position);
                startActivity(intent);
                finish();


                break;
        }

        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        drawerlayout.closeDrawer(Gravity.START);
        return true;
    }
    //Método que converte formatos de data yyyy-MM-dd em dd/MM/yyyy
    ArrayList<String> selecionadado(double dadox){
        ArmazenamentoLocal Al = new ArmazenamentoLocal(HistoricoActivity.this);
        ArrayList<String> resultadoarray= new ArrayList<>();
        ArrayList<String> horaarray;
        ArrayList<String> dataarray;
        String resultado;
        String data;
        int i = (int) dadox;
        horaarray=Al.SharedGetArray("hora");
        dataarray=Al.SharedGetArray("data");
        switch(i) {
            case 0:
                resultado = horaarray.get(horaarray.size()-1);
                data = dataarray.get(dataarray.size()-1);
                resultadoarray.add(resultado);
                resultadoarray.add(data);
                break;
            case 1:
                resultado = horaarray.get(horaarray.size()-2);
                data = dataarray.get(dataarray.size()-2);
                String Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 2:
                resultado = horaarray.get(horaarray.size()-3);
                data = dataarray.get(dataarray.size()-3);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 3:
                resultado = horaarray.get(horaarray.size()-4);
                data = dataarray.get(dataarray.size()-4);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 4:
                resultado = horaarray.get(horaarray.size()-5);
                data = dataarray.get(dataarray.size()-5);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 5:
                resultado = horaarray.get(horaarray.size()-6);
                data = dataarray.get(dataarray.size()-6);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 6:
                resultado = horaarray.get(horaarray.size()-7);
                data = dataarray.get(dataarray.size()-7);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 7:
                resultado = horaarray.get(horaarray.size()-8);
                data = dataarray.get(dataarray.size()-8);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 8:
                resultado = horaarray.get(horaarray.size()-9);
                data = dataarray.get(dataarray.size()-9);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 9:
                resultado = horaarray.get(horaarray.size()-10);
                data = dataarray.get(dataarray.size()-10);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 10:
                resultado = horaarray.get(horaarray.size()-11);
                data = dataarray.get(dataarray.size()-11);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 11:
                resultado = horaarray.get(horaarray.size()-12);
                data = dataarray.get(dataarray.size()-12);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 12:
                resultado = horaarray.get(horaarray.size()-13);
                data = dataarray.get(dataarray.size()-13);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 13:
                resultado = horaarray.get(horaarray.size()-14);
                data = dataarray.get(dataarray.size()-14);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 14:
                resultado = horaarray.get(horaarray.size()-15);
                data = dataarray.get(dataarray.size()-15);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 15:
                resultado = horaarray.get(horaarray.size()-16);
                data = dataarray.get(dataarray.size()-16);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 16:
                resultado = horaarray.get(horaarray.size()-17);
                data = dataarray.get(dataarray.size()-17);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 17:
                resultado = horaarray.get(horaarray.size()-18);
                data = dataarray.get(dataarray.size()-18);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 18:
                resultado = horaarray.get(horaarray.size()-19);
                data = dataarray.get(dataarray.size()-19);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;
            case 19:
                resultado = horaarray.get(horaarray.size()-20);
                data = dataarray.get(dataarray.size()-20);
                Data = DateFormat.getFormatedDateTime(data, "yyyy-MM-dd", "dd/MM/yyyy");
                resultadoarray.add(resultado);
                resultadoarray.add(Data);
                break;


        }
        return resultadoarray;
    }
    //Método que verifica se internet está disponível
    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        //Se a internet estiver disponível isconnected=true
        isconnected=isConnected;
    }
}


