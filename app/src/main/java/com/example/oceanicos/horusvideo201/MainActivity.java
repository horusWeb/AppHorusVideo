package com.example.oceanicos.horusvideo201;


import android.app.AlertDialog;
import android.app.DatePickerDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    WebView WebViewMain;
    Button buttonright, buttonleft;
    RadioGroup radioGroup;
    Spinner spinner;
    String Url_image;
    String url_ini = "http://168.176.124.168/oblicuas";
    String tipo_imagen = "Snap";
    String estacion = "Cartagena";
    Integer año, mes, dia, añoD, mesD, diaD, añoU, mesU, diaU;
    String hora;
    String cam;
    Integer n,numCam;
    List Cameras;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.Station) + ":   " + estacion + "      " + getString(R.string.Date) + ":   " + año + "/" + mes + "/" + dia + "      " + getString(R.string.Hour) + ":   " + hora + " GTM      " + getString(R.string.Camera) + ":   " + cam , Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        WebViewMain = (WebView) findViewById(R.id.WebViewMain);
        WebViewMain.getSettings().setUseWideViewPort(true);
        WebViewMain.getSettings().setLoadWithOverviewMode(true);
        WebViewMain.getSettings().setBuiltInZoomControls(true);
        WebViewMain.getSettings().setDisplayZoomControls(false);

     /*   DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int densityDpi = metrics.densityDpi;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;
            WebViewMain.getLayoutParams().width =metrics.heightPixels*1024/768;*/


        buttonleft = (Button) findViewById(R.id.buttonleft);
        buttonright = (Button) findViewById(R.id.buttonright);
        buttonleft.setOnClickListener(this);
        buttonright.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        Calendar calendar = Calendar.getInstance();
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH)+1;
        año = calendar.get(Calendar.YEAR);

       if (existurl(URL_final_Date(año,mes,dia))){ //revisamos si tenemos datos de la fecha actual para mostrar
           mostrarimagenfecha();
       }else{                                        //no tenemos datos de la fecha actual, proponemos una fecha alterna (la mas cercana)
           while (existurl(URL_final_año(año)) != true){
               año = año - 1;
               mes = 12;
           }
           while (existurl(URL_final_mes(año, mes)) != true){
               mes = mes - 1;
               dia = 31;
           }
           while (existurl(URL_final_Date(año, mes, dia)) != true){
               dia = dia -1;
           }
           AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
           alertDialogBuilder.setMessage(getString(R.string.error_1) +" "+ año +"/" + mes + "/"+ dia+".")
                   .setCancelable(true);
           AlertDialog alertDialog = alertDialogBuilder.create();
           alertDialog.show();
           mostrarimagenfecha();
       }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButtontimex) {
                    tipo_imagen = "Timex";
                   mostrarimagen();
                } else if (i == R.id.radioButtonsnap) {
                    tipo_imagen = "Snap";
                    mostrarimagen();
                } else if (i == R.id.radioButtonvar) {
                    tipo_imagen = "Var";
                    mostrarimagen();
                }

            }
        });
    }

    public void mostrarimagen() {
        String url_mes, url_dia, url_año;
        url_dia = Integer.toString(dia);
        if (dia < 10) {url_dia = "0" + Integer.toString(dia);}
        url_mes = Integer.toString(mes);
        if (mes < 10) {url_mes = "0" + Integer.toString(mes);}
        url_año = Integer.toString(año);
        Url_image = url_ini + "/" + estacion.toUpperCase() + "/" + url_año + "/" + url_mes + "/" + url_dia + "/" + cam + "/" + url_año.substring(2, 4) + "." + url_mes + "." + url_dia + "." + hora + ".GMT." + estacion + "." + cam + "." + tipo_imagen + ".1024X768.HORUS.jpg";
        if (existurl(Url_image)){
            WebViewMain.loadUrl(Url_image);
        }else{
           Toast.makeText(MainActivity.this, "lo sentimos no tenemos esa imagen",Toast.LENGTH_SHORT).show();
        }
    }
    public void mostrarimagenfecha() {
        List listacam = InfoWebCam(URL_final_Date(año,mes,dia));
        Cameras = new ArrayList();
        int i;
        for (i=0;i<=(listacam.size()-1);i++){
            if (! InfoWebHora(URL_final_Date(año,mes,dia) + listacam.get(i) + "/").isEmpty()){
                Cameras.add(listacam.get(i));
            }
        }
        if (!Cameras.isEmpty()) {
            cam = (String) Cameras.get(0);
            n = Cameras.size();
            numCam = 1;

            cambiarspinner();
            hora = (String) InfoWebHora(URL_final_Date(año, mes, dia) + cam + "/").get(0);
            mostrarimagen();
        }/*else {
        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.error_fecha_sin_camaras),Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL, 0,0);
        }*/


    }
    public void cambiarspinner(){
        //spinner para seleccionar la hora
        spinner = (Spinner) findViewById(R.id.spinner);
        List list = new ArrayList();

        list = InfoWebHora(URL_final_Date(año,mes,dia) + cam + "/");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Hora = Integer.parseInt(((String.valueOf(spinner.getSelectedItem())).trim()).substring(0,2)) + 5;
                hora = (String) spinner.getSelectedItem();
                mostrarimagen();
                //Toast.makeText(MainActivity.this, Hora,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // hora = 6 + 5;
            }
        });
    }
    public String URL_final_Date(int año, int mes, int dia){
        String url_mes, url_dia, url_año;
        url_dia = Integer.toString(dia);
        if (dia < 10) {url_dia = "0" + Integer.toString(dia);}
        url_mes = Integer.toString(mes);
        if (mes < 10) {url_mes = "0" + Integer.toString(mes);}
        url_año = Integer.toString(año);
        return url_ini + "/" + estacion.toUpperCase() + "/" + url_año + "/" + url_mes + "/" + url_dia + "/";
    }
    public String URL_final_año(int año){
        String url_año = Integer.toString(año);
        return url_ini + "/" + estacion.toUpperCase() + "/" + url_año;
    }
    public  String URL_final_mes(int año,int mes){
        String url_mes = Integer.toString(mes);
        if (mes < 10) {url_mes = "0" + Integer.toString(mes);}
        String url_año = Integer.toString(año);
        return url_ini + "/" + estacion.toUpperCase() + "/" + url_año + "/" + url_mes;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonleft:
               if (numCam == 1) {
                   numCam=n;
                   cam = (String) Cameras.get(n-1);
                   mostrarimagen();
               }else{
                   numCam = numCam - 1;
                   cam = (String) Cameras.get(numCam-1);
                   mostrarimagen();
               }
                break;
            case R.id.buttonright:
                if(numCam == n){
                    numCam = 1;
                    cam = (String) Cameras.get(0);
                    mostrarimagen();
                }else{
                    numCam = numCam + 1;
                    cam = (String) Cameras.get(numCam - 1);
                    mostrarimagen();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_OBLICUAS) {
            acction_OBLICUAS();
        } else if (id == R.id.action_PANORAMICAS){
            acction_PANORAMICAS();
        } else if (id == R.id.action_RECTIFICADAS){
            acction_RECTIFICADAS();
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Date) {
            Date();
        } else if (id == R.id.nav_History) {

        } else if (id == R.id.nav_Operational) {

        } else if (id == R.id.nav_Prediction) {

        } else if (id == R.id.nav_Questions) {

        } else if (id == R.id.nav_share) {
            Share();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Date() {
        mes = mes - 1;
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this, año, mes, dia);
        datePickerDialog.show();
    }
    public void mostrarfechamasreciente(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
        alertDialogBuilder.setMessage(getString(R.string.error_2) +" "+ año +"/" + mes + "/"+ dia+".")
                .setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        mostrarimagenfecha();
    }
    public void mostrarfechamasantigua(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
        alertDialogBuilder.setMessage(getString(R.string.error_3) +" "+ año +"/" + mes + "/"+ dia+".")
                .setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        mostrarimagenfecha();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        año = i;
        mes = i1 + 1;
        dia = i2;
        verificarFecha();
    }
    public void verificarFecha(){
        if (existurl(URL_final_Date(año, mes, dia)) == true) {
            mostrarimagenfecha();
        } else {
            List años = InfoAño(url_ini + "/" + estacion.toUpperCase() + "/");
            // Toast.makeText(MainActivity.this, años.toString(), Toast.LENGTH_SHORT).show();
            if (año > (Integer) años.get(años.size() - 1)) {
                año = (Integer) años.get(años.size() - 1);
                List meses = InfoMes(URL_final_año(año));
                mes = (Integer) meses.get(meses.size() - 1);
                List dias = InfoDia(URL_final_mes(año, mes));
                dia = (Integer) dias.get(dias.size() - 1);
                mostrarfechamasreciente();
            } else if (año.equals(años.get(años.size() - 1))) {
                List meses = InfoMes(URL_final_año(año));
                if (mes > (Integer) meses.get(meses.size() - 1)) {
                    mes = (Integer) meses.get(meses.size() - 1);
                    List dias = InfoDia(URL_final_mes(año, mes));
                    dia = (Integer) dias.get(dias.size() - 1);
                    mostrarfechamasreciente();
                } else if (mes.equals(meses.get(meses.size() - 1))) {
                    List dias = InfoDia(URL_final_mes(año, mes));
                    if (dia > (Integer) dias.get(dias.size() - 1)) {
                        dia = (Integer) dias.get(dias.size() - 1);
                        mostrarfechamasreciente();
                    }
                }
            } else if (año < (Integer) años.get(0)){
                año = (Integer) años.get(0);
                List meses = InfoMes(URL_final_año(año));
                mes = (Integer) meses.get(0);
                List dias = InfoDia(URL_final_mes(año, mes));
                dia = (Integer) dias.get(0);
                mostrarfechamasantigua();
            } else if (año.equals(años.get(0))){
                List meses = InfoMes(URL_final_año(año));
                if (mes < (Integer) meses.get(0)) {
                    mes = (Integer) meses.get(0);
                    List dias = InfoDia(URL_final_mes(año, mes));
                    dia = (Integer) dias.get(0);
                    mostrarfechamasantigua();
                } else if (mes.equals(meses.get(0))){
                    List dias = InfoDia(URL_final_mes(año, mes));
                    if (dia < (Integer) dias.get(0)){
                        dia = (Integer) dias.get(0);
                        mostrarfechamasantigua();
                    }
                }
            }else{
                añoD = año;
                mesD = mes;
                diaD = dia;
                while (existurl(URL_final_año(añoD)) != true){
                    añoD = añoD - 1;
                    mesD = 12;
                }
                while (existurl(URL_final_mes(añoD, mesD)) != true){
                    mesD = mesD - 1;
                    diaD = 31;
                }
                while (existurl(URL_final_Date(añoD, mesD, diaD)) != true){
                    diaD = diaD - 1;
                }
                añoU = año;
                mesU = mes;
                diaU = dia;
                while (existurl(URL_final_año(añoU)) != true){
                    añoU = añoU + 1;
                    mesU = 1;
                }
                while (existurl(URL_final_mes(añoU, mesU)) != true){
                    mesU = mesU + 1;
                    diaU = 1;
                }
                while (existurl(URL_final_Date(añoU, mesU, diaU)) != true){
                    diaU = diaU + 1;
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogTheme);
                alertDialogBuilder.setMessage(getString(R.string.error_4) + ".")
                        .setCancelable(false)
                        .setPositiveButton(añoU +"/" + mesU + "/"+ diaU, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                año = añoU;
                                mes = mesU;
                                dia = diaU;
                                mostrarimagenfecha();
                            }
                        })
                        .setNegativeButton(añoD +"/" + mesD + "/"+ diaD, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                año = añoD;
                                mes = mesD;
                                dia = diaD;
                                mostrarimagenfecha();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                //  mostrarimagenfecha();
            }

        }
    }
    public void Share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, Url_image);
        startActivity(Intent.createChooser(intent, "Share with"));
    }
    public List InfoAño (String website){
        List<Integer> lista = new ArrayList();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(website);
        try{
            HttpResponse response;
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"), 8);
            String line;
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            while ((line = reader.readLine()) != null){
                try{
                    lista.add(Integer.parseInt(line.substring(13, 17)));
                }catch (Exception e){};
            }
            is.close();
        }   catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }

        return (List) lista;
    }
    public List InfoMes (String website){
        List<Integer> lista = new ArrayList();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(website);
        try{
            HttpResponse response;
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"), 8);
            String line;
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            while ((line = reader.readLine()) != null){
                try{
                    lista.add(Integer.parseInt(line.substring(13, 15)));
                }catch (Exception e){};
            }
            is.close();
        }   catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }

        return (List) lista;
    }
    public List InfoDia (String website){
        List<Integer> lista = new ArrayList();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(website);
        try{
            HttpResponse response;
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"), 8);
            String line;
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            while ((line = reader.readLine()) != null){
                try{
                    lista.add(Integer.parseInt(line.substring(13, 15)));
                }catch (Exception e){};
            }
            is.close();
        }   catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }

        return (List) lista;
    }
    public List InfoWebHora (String website){
        List<String> lista = new ArrayList();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(website);
        try{
            HttpResponse response;
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"), 8);
            String line ;
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            while ((line = reader.readLine()) != null){
                try{
                    Pattern pat = Pattern.compile("([0-9]|\\.)+");
                    Matcher mat = pat.matcher(line.substring(22, 30));
                    if (mat.matches()) {
                        lista.add(line.substring(22, 30));
                    }
                }catch (Exception e){};
            }
            is.close();
        }   catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }
        Set<String> s = new LinkedHashSet<String>(lista);
        lista.clear();
        lista.addAll(s);
        return lista;
        //return (List) lista;
    }
    public List InfoWebCam (String website){
        List<String> list = new ArrayList();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(website);
        try{
            HttpResponse response;
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"), 8);
            String line;
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            while ((line = reader.readLine()) != null){
                try{

                    list.add(line.substring(13, 15));

                }catch (Exception e){};
            }
            is.close();
        }   catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }
        return (List) list;
    }
    public boolean existurl(String website){
        if (InfoWebCam(website).size() != 0){
            return true;
        }else{
            return false;
        }
    }
  /*  public void acction_OBLICUAS(){
        Toast.makeText(MainActivity.this, "Selected Obliques", Toast.LENGTH_SHORT).show();
    }
    public void acction_PANORAMICAS(){
        Toast.makeText(MainActivity.this, "Users Premium Only $$$$$", Toast.LENGTH_SHORT).show();
    }
    public void acction_RECTIFICADAS(){
        Toast.makeText(MainActivity.this, "Users Premium Only $$$$$", Toast.LENGTH_SHORT).show();
    }*/




};
