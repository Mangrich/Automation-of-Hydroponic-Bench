package com.example.fertileasy;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;



import java.util.Calendar;


// Classe que constrói caixa de dialogo com relógio, define-se parâmetros visuais(Hora,minuto,formato da hora)
public class TimePickerFragment extends DialogFragment
{
    int Hora,Minuto;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        final Calendar c = Calendar.getInstance();
        Hora = c.get(Calendar.HOUR_OF_DAY);
        Minuto = c.get(Calendar.MINUTE);



        return new TimePickerDialog(
                getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(),
                Hora,
                Minuto,
                true);

    }


}