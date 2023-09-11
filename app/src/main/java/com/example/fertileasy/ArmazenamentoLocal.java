package com.example.fertileasy;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
//class ArmazenamentoLocal:Classe que implementa o armazenamento local de arquivos usando a classe SharedPreferences
//SharedSetDataString: Armazena Strings
//SharedGetDataString: Retorna Strings Armazenadas.
//SharedSetDataInt: Armazena inteiros
//SharedGetDataInt: Retorna inteiro armazenado
//SharedSetBool: Armazena booleanos
//SharedGetBool: Retorna booleanos
//SharedSetArrray: Armazena ArrayLists de Strings.
//SharedGetArrray: Retorna ArrayLists de Strings.
//SharedSetArrrayBool: Armazena ArrayLists de valores booleanos.
//SharedGetArrrayBool: Retorna ArrayLists de valores booleanos.
//SharedClear: Limpa a memória do SharedPreferences.Ou seja limpa toda a memória local do aplicativo
class ArmazenamentoLocal {
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    ArmazenamentoLocal( Context c){
        this.pref = c.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
    }

     void SharedSetDataString(String var, String value){
         this.edit = pref.edit();
         edit.putString(var, value);
         edit.apply();
    }

    void SharedSetDataInt(String var, int value){
        this.edit = pref.edit();
        edit.putInt(var, value);
        edit.apply();
    }

    int SharedGetDataInt(String var){
        return pref.getInt(var, -1);
    }

    String SharedGetDataString(String var){
        return pref.getString(var, "");


    }
    //void SharedSetDataBool(String var){
      //  this.edit = pref.edit();
        //edit.putBoolean(var, true);
        //edit.apply();


    //}
     void SharedSetArray( ArrayList<String> list,String r){

         this.edit = pref.edit();
         Gson gson = new Gson();
         String json = gson.toJson(list);
         edit.putString(r, json);
         edit.commit();


     }
    void SharedClear(){
        this.edit = pref.edit();
        edit.clear().apply();
       // edit.clear().apply();


    }

    void SharedSetArrayBool(ArrayList<Boolean> list){
        this.edit = pref.edit();
        for (int i = 0;  i<list.size(); i++) {
            boolean booleandata = list.get(i);
            edit.putBoolean("booldata" + "_" + i,booleandata);
            edit.apply();
        }




    }
    void SharedSetBool(String boo,boolean bool){
        this.edit = pref.edit();
            edit.putBoolean(boo,bool);
            edit.apply();
        }
    Boolean SharedGetBool(String boo){

       return  pref.getBoolean(boo, false);

    }







    ArrayList<Boolean> SharedGetArrayBool(ArrayList<Boolean> array){
        int size =SharedGetDataInt("sizebooldata");
        int i = -1;
        do {
            i++;
            array.add(pref.getBoolean("booldata" + "_" + i, false));

        } while (i<size);
        return array;


    }
    ArrayList<String> SharedGetArray(String s){
        Gson gson = new Gson();
        String json = pref.getString(s,"");
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        return gson.fromJson(json,type);



     }


}
