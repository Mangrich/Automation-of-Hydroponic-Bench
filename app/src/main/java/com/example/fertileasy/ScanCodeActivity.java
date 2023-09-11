package com.example.fertileasy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
//Classe responsável por ler Qrcode a partir da câmera
public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView ScannerView;
    int requestCode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerView = new ZXingScannerView(this);
        if (ContextCompat.checkSelfPermission(ScanCodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(ScanCodeActivity.this, new String[] {Manifest.permission.CAMERA}, requestCode);

        }
        setContentView(ScannerView);
    }

    @Override
    public void handleResult(Result result) {
        ArmazenamentoLocal Al = new ArmazenamentoLocal(ScanCodeActivity.this);
        ArrayList<String> bancadadadosarray;
        bancadadadosarray = Al.SharedGetArray("bancadadadosarray");
        if(bancadadadosarray.size()==12) {
            //Atualiza Qrcode
            bancadadadosarray.set(11,result.getText());
            //Exibe Qrcode na tela ConfigActivity
            ConfigActivity.MyQrcode.setText(result.getText());
            Al.SharedSetArray(bancadadadosarray, "bancadadadosarray");
        }
        else{
            bancadadadosarray.add(result.getText());
            ConfigActivity.MyQrcode.setText(result.getText());
            Al.SharedSetArray(bancadadadosarray, "bancadadadosarray");
        }
        //Volta para tela anterior depois da captura do Qrcode
        onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    //Desabilita a camera
    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }
    //Habilita a camera
    @Override
    protected void onResume() {
        super.onResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }
}
