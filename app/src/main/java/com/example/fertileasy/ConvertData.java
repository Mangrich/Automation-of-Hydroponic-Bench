package com.example.fertileasy;

import android.content.Context;
import android.widget.EditText;


//Classe responsável pela conversão de dados
class ConvertData {
    private ArmazenamentoLocal Al;
    private Context c;

    ConvertData(Context c, ArmazenamentoLocal Al) {
        this.c = c;
        this.Al = Al;


    }

    String editTexttoString(EditText et) {
        return et.getText().toString();
    }

    int StringtoInteger(String s) {
        try {
            int x = Integer.parseInt(s);
            Al = new ArmazenamentoLocal(c);
            Al.SharedSetDataInt("x", x);


        } catch (NumberFormatException e) {
            System.out.println("String arguments must contain valid digits, unable to create box.");


        }
        return Al.SharedGetDataInt("x");
    }

    String IntegertoString(int i) {
        return String.valueOf(i);
    }

    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
    }
}
