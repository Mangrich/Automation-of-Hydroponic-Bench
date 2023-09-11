package com.example.fertileasy;


import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * 1.BASICALLY PACKS DATA WE WANNA SEND
 */
 class DataPackager {

    private String email,novocodusuario,codigobancada,novotipobancada,novacultura,novadatacultivo,novaconcentracaoreferencia,novoiniciodia,novofimdia,novotempoligado,novotempodesligado,novociclosnoturnos,urlAddress,nomedabancada,qrcode;
    private WeakReference<Context> contextRef;
     DataPackager(String urlAddress, String email, String novocodusuario, String codigobancada, String novotipobancada, String novacultura, String novadatacultivo, String novaconcentracaoreferencia, String novoiniciodia, String novofimdia, String novotempoligado, String novotempodesligado, String novociclosnoturnos, Context c,String nomedabancada,String qrcode) {

        this.urlAddress = urlAddress;
        //INPUT EDITTEXTS

        //GET TEXTS FROM EDITEXTS
        this.email=email;
        this.novocodusuario = novocodusuario;
        this.codigobancada=codigobancada;
        this.novotipobancada =novotipobancada;
        this.novacultura = novacultura;
        this.novadatacultivo=novadatacultivo;
        this.novaconcentracaoreferencia =novaconcentracaoreferencia;
        this.novoiniciodia = novoiniciodia;
        this.novofimdia=novofimdia;
        this.novotempoligado=novotempoligado;
        this.novotempodesligado= novotempodesligado;
        this.nomedabancada = nomedabancada;
        this.novociclosnoturnos = novociclosnoturnos;
        this.contextRef = new WeakReference<>(c);
        this.qrcode =qrcode;

    }

    /*
   SECTION 2
   1.PACK THEM INTO A JSON OBJECT
   1. READ ALL THIS DATA AND ENCODE IT INTO A FROMAT THAT CAN BE SENT VIA NETWORK
    */
     String packData()
    {
        Context c = contextRef.get();
        ArmazenamentoLocal Al = new ArmazenamentoLocal(c);
        //Adiciona novo objeto JSON
        JSONObject jo=new JSONObject();
        StringBuilder packedData=new StringBuilder();

        ConvertData cd = new ConvertData(c,Al);
        int codusuario = cd.StringtoInteger(novocodusuario);



        try
        {
            //Adiciona ao JSONObject parametros que serão enviados ao arquivo php correspondente
            if(urlAddress.equals("https://appfertileasy.herokuapp.com/androidlogin.php")) {
                jo.put("email", email);
            }
            if(urlAddress.equals("https://appfertileasy.herokuapp.com/androidbancadas.php")) {
                jo.put("codigousuario",novocodusuario);
            }
            if(urlAddress.equals("https://appfertileasy.herokuapp.com/androidnovabancada.php")) {
                jo.put("novacultura",novacultura);
                jo.put("novadatacultivo",novadatacultivo);
                jo.put("novotipobancada",novotipobancada);
                jo.put("novoiniciodia",novoiniciodia);
                jo.put("novofimdia",novofimdia);
                jo.put("novotempoligado",novotempoligado);
                jo.put("novotempodesligado",novotempodesligado);
                jo.put("novociclosnoturnos",novociclosnoturnos);
                jo.put("novaconcentracaoreferencia",novaconcentracaoreferencia);
                jo.put("novocodusuario",codusuario);
                jo.put("novonome",nomedabancada);
                jo.put("novocodqr",qrcode);

            }
            if(urlAddress.equals("https://appfertileasy.herokuapp.com/androiddadosbancada.php")) {
                jo.put("codigobancada",codigobancada);


            }

            if(urlAddress.equals("https://appfertileasy.herokuapp.com/androiddeletarbancada.php")) {
               jo.put("codigobancada",codigobancada);



           }
            if(urlAddress.equals("https://appfertileasy.herokuapp.com/androidacompanharbancada.php")) {
                jo.put("codigobancada",codigobancada);



            }
            if(urlAddress.equals("https://appfertileasy.herokuapp.com/androidexportarhistorico.php")) {
                jo.put("codigobancada",codigobancada);



            }
            if(urlAddress.equals("https://appfertileasy.herokuapp.com/androidatualizarbancada.php")) {
                jo.put("codigobancada",codigobancada);
                jo.put("atualizarcultura",novacultura);
                jo.put("atualizardatacultivo",novadatacultivo);
                jo.put("atualizartipobancada",novotipobancada);
                jo.put("atualizariniciodia",novoiniciodia);
                jo.put("atualizarfimdia",novofimdia);
                jo.put("atualizartempoligado",novotempoligado);
                jo.put("atualizartempodesligado",novotempodesligado);
                jo.put("atualizarciclosnoturnos",novociclosnoturnos);
                jo.put("atualizarconcentracaoreferencia",novaconcentracaoreferencia);
                jo.put("atualizarnome",nomedabancada);



            }


            boolean firstValue=true;

            Iterator it=jo.keys();
            //Organiza parametros dentro do objeto JSON em formato adequado para envio

            do {
                String key=it.next().toString();
                String value=jo.get(key).toString();

                if(firstValue)
                {
                    firstValue=false;
                }else
                {
                    packedData.append("&");
                }

                packedData.append(URLEncoder.encode(key,"UTF-8"));
                packedData.append("=");
                packedData.append(URLEncoder.encode(value,"UTF-8"));

            }while (it.hasNext());

            return packedData.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}