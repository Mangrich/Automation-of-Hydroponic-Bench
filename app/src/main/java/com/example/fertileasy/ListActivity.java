package com.example.fertileasy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;

//Atividade que lista todas as bancadas ao usuário e permite a criação de novas bancadas
public class ListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Internet, InternetConnectivityListener {

    private ArrayList<com.example.fertileasy.ExampleItem> mExampleList;
    private com.example.fertileasy.ExampleAdapter mAdapter;
    int numerobancadas;
    private static ListActivity d;
    private boolean isconnected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//estende méthodo onCreate
        setContentView(R.layout.activity_list);//Associa ID do layout activity_list a lógica da atividade
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Configura orientação como retrato
        ArrayList<String> dadosusuarioarray;
        d=this;
        ProgressBar pb = findViewById(R.id.progressBar);//Cria uma barra de rolamento
        pb.setVisibility(View.GONE);//Desabilita barra de rolamento
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ListActivity.this);
        dadosusuarioarray=Al.SharedGetArray("dadosusuarioarray");//Recebe dados do usuário
        //Configura barra de navegação
        NavConfig.NavDrawerConfig(ListActivity.this,this,this,dadosusuarioarray.get(0));
        //Configura acesso a outras telas pela barra de navegação
        NavConfig.SetItemacess(false,false,false, ListActivity.this,false);

        buildRecyclerView(Al);
        //Configura listeners
        setButtons(Al);
        //Mostra bancadas na tela
        setBancadas(Al);
        int counter = mAdapter.getItemCount();//Obtém número de bancadas
        Al.SharedSetDataInt("counter",counter);//Salva número de bancadas

    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressBar pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ListActivity.this);
        ArrayList<String> dadosusuarioarray;
        dadosusuarioarray=Al.SharedGetArray("dadosusuarioarray");
        NavConfig.NavDrawerConfig(ListActivity.this,this,this, dadosusuarioarray.get(0));
        NavConfig.SetItemacess(false,false,false, ListActivity.this,false);
        buildRecyclerView(Al);
        setButtons(Al);
        setBancadas(Al);
        int counter = mAdapter.getItemCount();
        Al.SharedSetDataInt("counter",counter);
    }
    //Método que é chamado quando a tela se torna visiível para o usuário depois que o aplicativo foi minimizado.
    // sobrescreve método onStart da classe Activity
    @Override
    public void onBackPressed() {
            //finish();
        //Botão voltar faz o aplicativo minimizar
        Intent minimize = new Intent(Intent.ACTION_MAIN);
        minimize.addCategory(Intent.CATEGORY_HOME);
        minimize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(minimize);
    }
    //Método que é executado quando botão é pressionado. Tela muda para ConfigActivity
    public void insertItem(ArmazenamentoLocal Al) {
        ArrayList<String> nullarray = new ArrayList<>();
        Al.SharedSetArray(nullarray,"bancadadadosarray");
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);
        //Se não for pressionada nenhuma bancada da lista
        if(position==-1){
            String codigo = intent.getStringExtra("codigo");
            intent = new Intent(ListActivity.this, ConfigActivity.class);
            intent.putExtra("codigo",codigo);
            intent.putExtra("position",position);
            startActivity(intent);

        }

    }
    //Adiciona lista de bancadas
    public void setBancadas(ArmazenamentoLocal Al) {
        int count = 0;
        ArrayList<String> nomearray;
        //Recebe array com nomes de todas as bancadas
        nomearray = Al.SharedGetArray("nome");
        //Se não for um array vazio
        if(nomearray!=null) {
            //Percorre nomearray adicionando bancadas na lista
            for (int i = 0; i < nomearray.size(); i++) {
                mExampleList.add(count, new com.example.fertileasy.ExampleItem(R.drawable.fertileasy, nomearray.get(i), "Clique na bancada para monitorar"));

                count = count + 1;
            }
            //Reflete o número de bancadas na tela
            mAdapter.notifyItemInserted(count);
        }


    }

    public void changeItem(int position,ArmazenamentoLocal Al) {
        ProgressBar pb = findViewById(R.id.progressBar);
        //Faz aparecer barra de rolamento
        pb.setVisibility(View.VISIBLE);
        //Verifica disponibilidade de internet
        InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);
        //Se a internet está disponível e está conectada
        if (Internet.isNetworkAvailable(ListActivity.this)&&isconnected) {
            ArrayList<String> codigobancadaarray;
            mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
            codigobancadaarray = Al.SharedGetArray("codigobancadaarray");
            mAdapter.notifyItemChanged(position);
            String urlAddress = "https://appfertileasy.herokuapp.com/androiddadosbancada.php";
          // Faz post do codigo da bancada, buscando receber os dados da bancada
            Envio s = new Envio(ListActivity.this, urlAddress, "","", codigobancadaarray.get(position), "", "", "", "", "", "", "", "", "", "", ListActivity.this,1,position,"");
            s.execute();

        }



         if(!Internet.isNetworkAvailable(ListActivity.this)||!isconnected) {
            pb.setVisibility(View.GONE);
            Toast.makeText(ListActivity.this, "Não foi possível se conectar a internet, tente novamente", Toast.LENGTH_SHORT).show();

        }
    }



   //Método que descreve a lista de bancadas e o que acontece quando algum item é pressionado
    public void buildRecyclerView(ArmazenamentoLocal Al) {
        mExampleList = new ArrayList<>();
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new com.example.fertileasy.ExampleAdapter(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new com.example.fertileasy.ExampleAdapter.OnItemClickListener() {

            //Se alguma bancada for pressionada executa onItemClick
            @Override
            public void onItemClick(int position, View v) {
                changeItem(position,Al);
            }
            //Se o botão de deletar for pressionado executa onDeleteClick
            @Override
            public void onDeleteClick(int position, View v) {
                InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
                mInternetAvailabilityChecker.addInternetConnectivityListener(d);

                if (Internet.isNetworkAvailable(d)&&isconnected) {
                    mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(d);
                    //Executa método para remoção de bancada da lista e do banco de dados
                    Alert(position,Al);
                } if(!Internet.isNetworkAvailable(d)||!isconnected) {
                    Toast.makeText(ListActivity.this, "Não foi possível se conectar a internet, tente novamente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void setButtons(ArmazenamentoLocal Al) {
        Button buttonInsert = findViewById(R.id.button_insert);
        buttonInsert.setOnClickListener((View v) -> insertItem(Al));

    }
    //Método que gera caixa de texto pedindo a confirmação da remoção da bancada
    void Alert(int position,ArmazenamentoLocal Al) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ListActivity.this);

        // Exibe Título da caixa de texto
        alertDialogBuilder.setTitle("Aviso");

        // Exibe mensagem da caixa de texto
        alertDialogBuilder.setMessage("Você tem certeza que quer remover a bancada");
        alertDialogBuilder.setCancelable(false);
        //Se for pressionado o botão Não
        alertDialogBuilder.setPositiveButton("Não", (DialogInterface dialog, int id) -> {

            //Cancela caixa de texto
            dialog.cancel();

        });
        //Se for pressionado o botão Sim
        alertDialogBuilder.setNegativeButton("Sim", (DialogInterface dialog, int id) -> {
            ArrayList<String> codigobancadaarray;
            ArrayList<String> nomearray;
            //Obtém dados de código bancada e nomes
            codigobancadaarray = Al.SharedGetArray("codigobancadaarray");
            nomearray = Al.SharedGetArray("nome");
            if (position < codigobancadaarray.size()) {
                String urlAddress = "https://appfertileasy.herokuapp.com/androiddeletarbancada.php";
                Envio s = new Envio(ListActivity.this, urlAddress, "", "", codigobancadaarray.get(position), "", "", "", "", "", "", "", "", "","", ListActivity.this,1,position,"");
                s.execute();
                //Remove código da bancada e nome
                codigobancadaarray.remove(position);
                nomearray.remove(position);
                //Atualiza lista de codigos
                Al.SharedSetArray(codigobancadaarray, "codigobancadaarray");
                //Deleta bancada da lista
                mExampleList.remove(position);
                //Confirma remoção
                mAdapter.notifyItemRemoved(position);
                Al.SharedSetDataInt("numerobancadas", numerobancadas);
                //ArrayList<String> nullarray = new ArrayList<>();
               // Al.SharedSetArray(nullarray,"bancadadadosarray");

            } else {
                mExampleList.remove(position);
                mAdapter.notifyItemRemoved(position);

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        setProgressBarIndeterminateVisibility(true);



        switch (id) {
           //Se o item escolhido for Quit
            case R.id.first:

                //android.os.Process.killProcess(android.os.Process.myPid());
                Intent intent = new Intent(ListActivity.this, FinishActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();



                break;
            //Se o item escolhido for Login
            case R.id.logout:
                intent = new Intent(ListActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();


                break;
            //Se o item escolhido for Credenciais Wi-fi
            case R.id.configesp32:
                intent = new Intent(ListActivity.this, WebSocketActivity.class);
                startActivity(intent);
                finish();


                break;
        }
        //Fecha barra de navegação
        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        drawerlayout.closeDrawer(Gravity.START);
        return true;
    }


    //Método que verifica se internet está disponível
    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        //Se a internet estiver disponível isconnected=true
        isconnected=isConnected;

    }
}




