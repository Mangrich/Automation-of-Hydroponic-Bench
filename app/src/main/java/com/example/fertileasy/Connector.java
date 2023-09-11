package com.example.fertileasy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

 class Connector {

    /*
    1.SHALL HELP US ESTABLISH A CONNECTION TO THE NETWORK
    1. WE ARE MAKING A POST REQUEST
    */
     static HttpURLConnection connect(String urlAddress) {

        try
        {
            //Cria novo objeto URL com o parâmetro urlAddress
            URL url=new URL(urlAddress);
            //Abre nova requisição http
            HttpURLConnection con= (HttpURLConnection) url.openConnection();

            //SET PROPERTIES
            //Define método POST dentro dessa requisição HTTP
            con.setRequestMethod("POST");
            //Estabelece Tempo para conexão
            con.setConnectTimeout(20000);
            //Estabelece Tempo para leitura
            con.setReadTimeout(20000);
            //Flag que indica o uso da requisição Http como entrada
            con.setDoInput(true);
            //Flag que indica o uso da requisição Http como saída
            con.setDoOutput(true);

            //retorna objeto
            return con;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}