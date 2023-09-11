package com.example.fertileasy;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

class HttpProtocol {
    private HttpURLConnection con;

    HttpProtocol(HttpURLConnection con){
        this.con = con;
    }

    void HttpConnection(String urlAddress){
        //Abre conexão http
         con=Connector.connect(urlAddress);
    }
    HttpURLConnection getConnection(){
        return con;
    }
    String DataReadWrite(String urlAddress, String email, String codigousuario, String codigobancada, String novotipobancada, String novacultura, String novadatacultivo, String novaconcentracao, String novoiniciodia, String novofimdia, String novotempoligado, String novotempodesligado, String novociclosnoturnos, Context c, String nomedabancada,String qrcode){
        StringBuffer response;
        try {
            //Obtém objeto de saída de dados
            OutputStream os = con.getOutputStream();
            //Se a versão do sistema operacional for maior que a versão KitKat
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //Gera objeto Bufferedwriter-> classe usada para escrita de arquivos de texto
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                //Retorna objeto JSON com todos os atributos a serem enviados
                bw.write(new DataPackager(urlAddress, email, codigousuario, codigobancada, novotipobancada, novacultura, novadatacultivo, novaconcentracao, novoiniciodia, novofimdia, novotempoligado, novotempodesligado, novociclosnoturnos,c,nomedabancada,qrcode).packData());
                //Garante que dados são enviados no momento em que flush é chamado
                bw.flush();
                //Encerra escrita
                bw.close();
            }
            //Realiza mesmo processo acima mas com outro CharsetName
            else{
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(new DataPackager(urlAddress, email, codigousuario, codigobancada, novotipobancada, novacultura, novadatacultivo, novaconcentracao, novoiniciodia, novofimdia, novotempoligado, novotempodesligado, novociclosnoturnos,c,nomedabancada,qrcode).packData());
                bw.flush();
                bw.close();
            }
            //Encerra saída de dados
            os.close();
            //Obtém código de resposta caso mensagem seja enviada com sucesso
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //Lê resposta
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                 response = new StringBuffer();

                String line;

                //Lê a resposta linha por linha
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                //Encerra entrada de dados
                br.close();
                //Retorna resposta no formato String
                return response.toString();

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
