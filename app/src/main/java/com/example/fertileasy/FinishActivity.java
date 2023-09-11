package com.example.fertileasy;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


//Classe responsável por fechar o aplicativo
public class FinishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.finishAffinity();
            }
            else{
                super.finish();
            }
        }
    }
}
