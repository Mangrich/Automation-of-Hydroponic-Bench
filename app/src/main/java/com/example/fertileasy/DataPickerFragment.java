package com.example.fertileasy;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;



public class DataPickerFragment extends DialogFragment
{




    int Ano,Mes,Dia;
    @Override
// Classe que constrói caixa de dialogo com calendário, define-se parâmetros visuais(Ano,Mes,Dia)
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {


        final Calendar calendario = Calendar.getInstance();
        Ano = calendario.get(Calendar.YEAR);
        Mes = calendario.get(Calendar.MONTH);
        Dia = calendario.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), Ano, Mes, Dia);
    }








}
