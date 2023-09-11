package com.example.fertileasy;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

// Classe responsável pelo tratamento e armazenamento de dados vindos do POST
class GetDatabaseInfo {

    GetDatabaseInfo(){

    }

    void getInfo(String resposta, Context md,String urlAddress) {
        ArmazenamentoLocal Al;
        Al = new ArmazenamentoLocal(md);
        int i = -1;

        //if (!resposta.equals("[]")){
            try {
                if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidlogin.php")) {
                    //Declara um JSOArray contendo a resposta do POST
                    JSONArray arr = new JSONArray(resposta);
                    ArrayList<String> dadosusuarioarray;
                    //Obtém JSON Object a partir do JSONArray
                    JSONObject jObj = arr.getJSONObject(0);
                    //Obtém string do codigo e senha usando como valor referencia(key value)
                    //"codigo" e "senha"
                    String codigo = jObj.getString("codigo");
                    String senha = jObj.getString("senha");
                    dadosusuarioarray = Al.SharedGetArray("dadosusuarioarray");
                    //se o JSON object for não nulo adiciona codigo no arrayList dadosusuario
                    if(!jObj.isNull("codigo")){
                        dadosusuarioarray.add(codigo);
                    }
                    //se o JSON object for não nulo adiciona senha no arrayList dadosusuario
                    if(!jObj.isNull("senha")){
                        dadosusuarioarray.add(senha);
                    }
                    //Salva arrayList dadosusuarioarray para ser usado em qualquer lugar do aplicativo
                    Al.SharedSetArray(dadosusuarioarray, "dadosusuarioarray");
                    //Al.SharedSetDataString("codigo", codigo);
                    //Al.SharedSetDataString("senha", senha);
                }
                if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidbancadas.php")) {
                    //Declara um JSOArray contendo a resposta do POST
                    JSONArray arr = new JSONArray(resposta);
                    ArrayList<String> codigobancadaarray = new ArrayList<>();
                    ArrayList<String> nomearray = new ArrayList<>();
                    //Percorre o JSONObject salvando todos os valores com key "codigo" e "nome"
                    do {
                        i++;
                        Al = new ArmazenamentoLocal(md);
                        JSONObject jObj = arr.getJSONObject(i);
                        String codigobancada = jObj.getString("codigo");
                        String nome = jObj.getString("nome");
                        codigobancadaarray.add(codigobancada);
                        Al.SharedSetArray(codigobancadaarray, "codigobancadaarray");
                        nomearray.add(nome);
                        Al.SharedSetArray(nomearray, "nome");

                        //Al.SharedSetDataString("codigobancada", codigobancada);

                    } while (arr.getJSONObject(i) != null);


                }

                if (urlAddress.equals("https://appfertileasy.herokuapp.com/androiddadosbancada.php")) {
                    //Declara um JSOArray contendo a resposta do POST
                    JSONArray arr = new JSONArray(resposta);
                    ArrayList<String> bancadadadosarray;
                    bancadadadosarray = Al.SharedGetArray("bancadadadosarray");
                    //Obtém dados de configuração da bancada
                    JSONObject jObj = arr.getJSONObject(0);
                    String concentracao = jObj.getString("concentracaoreferencia");


                    jObj = arr.getJSONObject(0);
                    String cultura = jObj.getString("cultura");


                    jObj = arr.getJSONObject(0);
                    String datacultivo = jObj.getString("datacultivo");


                    jObj = arr.getJSONObject(0);
                    String tipobancada = jObj.getString("tipobancada");


                    jObj = arr.getJSONObject(0);
                    String iniciodia = jObj.getString("iniciodia");


                    jObj = arr.getJSONObject(0);
                    String fimdia = jObj.getString("fimdia");


                    jObj = arr.getJSONObject(0);
                    String tempoligado = jObj.getString("tempoligado");


                    jObj = arr.getJSONObject(0);
                    String tempodesligado = jObj.getString("tempodesligado");


                    jObj = arr.getJSONObject(0);
                    String ciclosnoturnos = jObj.getString("ciclosnoturnos");


                    jObj = arr.getJSONObject(0);
                    String nomedabancada = jObj.getString("nome");


                    jObj = arr.getJSONObject(0);
                    String qrcode = jObj.getString("codqr");

                    //Atualiza e salva dados da bancada
                    bancadadadosarray.set(1, tipobancada);
                    bancadadadosarray.set(2, cultura);
                    bancadadadosarray.set(3, concentracao);
                    bancadadadosarray.set(4, iniciodia);
                    bancadadadosarray.set(5, fimdia);
                    bancadadadosarray.set(6, tempoligado);
                    bancadadadosarray.set(7, tempodesligado);
                    bancadadadosarray.set(8, ciclosnoturnos);
                    bancadadadosarray.set(9, datacultivo);
                    bancadadadosarray.set(10, nomedabancada);
                    bancadadadosarray.set(11, qrcode);
                    Al.SharedSetArray(bancadadadosarray, "bancadadadosarray");
                }
                if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidacompanharbancada.php")) {
                    //Declara um JSOArray contendo a resposta do POST
                    JSONArray arr = new JSONArray(resposta);
                    JSONObject jObj = arr.getJSONObject(0);
                    String concentracao = jObj.getString("concentracao");
                    String temperatura = jObj.getString("temperatura");
                    Al.SharedSetDataString("concentracao", concentracao);
                    Al.SharedSetDataString("temperatura", temperatura);
                }
                if (urlAddress.equals("https://appfertileasy.herokuapp.com/androidexportarhistorico.php")) {
                    //Declara um JSOArray contendo a resposta do POST
                    JSONArray arr = new JSONArray(resposta);
                    ArrayList<String> horaarray = new ArrayList<>();
                    ArrayList<String> dataarray = new ArrayList<>();
                    ArrayList<String> concentracaoarray = new ArrayList<>();
                    ArrayList<String> temperaturaarray = new ArrayList<>();
                    //Percorre JSONObject enquanto ele não for vazio
                    for (i = 0; arr.getJSONObject(i) != null; i++) {
                        JSONObject jObj = arr.getJSONObject(i);
                        //Obtém hora,data,concentracao e temperatura se não forem nulos
                        if (!jObj.getString("hora").equals("") || !jObj.getString("data").equals("") || !jObj.getString("temperatura").equals("") || !jObj.getString("concentracao").equals("")) {
                            String hora = jObj.getString("hora");
                            String data = jObj.getString("data");
                            String concentracao = jObj.getString("concentracao");
                            String temperatura = jObj.getString("temperatura");
                            //Se a concentração não for infinita, salva dados na memória local
                            if(!concentracao.equals("Infinity")) {
                                horaarray.add(hora);
                                dataarray.add(data);
                                concentracaoarray.add(concentracao);
                                temperaturaarray.add(temperatura);
                                Al.SharedSetArray(horaarray, "hora");
                                Al.SharedSetArray(dataarray, "data");
                                Al.SharedSetArray(concentracaoarray, "concentracao");
                                Al.SharedSetArray(temperaturaarray, "temperatura");
                                Al.SharedSetDataInt("icount", i);
                            }

                        }


                    }


                }
            } catch (
                    Exception e) {
                System.out.println("String arguments must contain valid digits, unable to create box.");

            }
    //}
    }


}
