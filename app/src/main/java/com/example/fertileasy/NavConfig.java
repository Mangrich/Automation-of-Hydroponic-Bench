package com.example.fertileasy;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

//Interface que configura barra de navegação e habilita acessos a itens dessa barra.
public interface NavConfig {

    static void NavDrawerConfig(Activity act, AppCompatActivity cact, NavigationView.OnNavigationItemSelectedListener nav,String email) {
        Toolbar toolbar = act.findViewById(R.id.toolbar);
        cact.setSupportActionBar(toolbar);
        NavigationView navigationview = act.findViewById(R.id.navigation_view);
        navigationview.bringToFront();
        navigationview.setNavigationItemSelectedListener(nav);
        DrawerLayout drawerlayout = act.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(act, drawerlayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerlayout.addDrawerListener(toggle);
        View headerLayout = navigationview.getHeaderView(0);
        TextView nav_text = headerLayout.findViewById(R.id.text_nav);
        if (!(act instanceof LoginActivity)){
            nav_text.setText(email);
    }

        toggle.syncState();
    }
    static void SetItemacess(boolean valores,boolean historico,boolean lista,Activity act,boolean credenciais){
        NavigationView navigationview = act.findViewById(R.id.navigation_view);
        Menu menunav = navigationview.getMenu();
        MenuItem item_lista = menunav.findItem(R.id.lista);
        MenuItem item_historico = menunav.findItem(R.id.historico);
        MenuItem item_logout = menunav.findItem(R.id.logout);
        MenuItem item_valores = menunav.findItem(R.id.valores);
        MenuItem item_credenciais = menunav.findItem(R.id.configesp32);
        item_lista.setEnabled(lista);
        item_historico.setEnabled(historico);
        item_logout.setEnabled(true);
        item_valores.setEnabled(valores);
        item_credenciais.setEnabled(credenciais);

    }
}
