package com.example.usuario.cargoex;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.cargo.usuario.cargoex.R;
import com.example.usuario.cargoex.util.Modal;
import com.example.usuario.cargoex.util.SqliteCertificaciones;
import com.example.usuario.cargoex.util.SqliteHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DatosRetiroNormal extends AppCompatActivity {
    String numero;
    SharedPreferences prefs ;
    Intent modal;
    private FusedLocationProviderClient client;
    String latitud,longitud,codigo,rut;
    EditText rutCliente,dv,email,telefono,nombreCliente;
    ArrayList<String> lista;
    SqliteCertificaciones conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datosretironomal);
        modal= new Intent(this, Modal.class);
        rutCliente=(EditText)findViewById(R.id.rutCliente);
        nombreCliente=(EditText)findViewById(R.id.nombreR);
        dv=(EditText)findViewById(R.id.dv);
        email=(EditText)findViewById(R.id.email);
        telefono=(EditText)findViewById(R.id.telefono);
        conn = new SqliteCertificaciones(this,"bd_certificaciones",null,R.string.versionDB);

        //pongo nombre
        TextView nombre= (TextView) findViewById(R.id.nombre);
        prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
        String name = prefs.getString("nombre", "");
        nombre.setText(name);
        //pongo fecha actual en la vista
        TextView fecha = (TextView) findViewById(R.id.fecha);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy ", Locale.getDefault());
        Date date = new Date();
        String fechaHoy = dateFormat.format(date);
        fecha.setText(fechaHoy);

        //registtro accion
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date2 = new Date();
        String fecha2 = dateFormat2.format(date2);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(DatosRetiroNormal.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(DatosRetiroNormal.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //tomo latitud y longitud
                    latitud = location.getLatitude() + "";
                    longitud = location.getLongitude() + "";
                }
            }
        });

        String rutIngresado= getIntent().getStringExtra("rutIngresado");
        String dvIngresado= getIntent().getStringExtra("dvIngresado");
        String emailIngresado= getIntent().getStringExtra("emailIngresado");
        String telefonoIngresado= getIntent().getStringExtra("telefonoIngresado");

        if(rutIngresado!=null){
            rutCliente.setText(rutIngresado);
        }
        if(dvIngresado!=null){
            dv.setText(dvIngresado);
        }
        if(emailIngresado!=null){
            email.setText(emailIngresado);
        }
        if(telefonoIngresado!=null){
            telefono.setText(telefonoIngresado);
        }
        //recibo la lista y esta solo se manda a los demas metodos
        lista=getIntent().getStringArrayListExtra("lista");

    }
    public boolean numeroVerificador(String s){
        try{
            int i = Integer.parseInt(s);
            return true;
        }catch (NumberFormatException n){
            if(s.equals("K")||s.equals("k")){
                return true;
            }
            return false;
        }
    }
    public boolean ultimaPosicionAccion(){
        String codigo = prefs.getString("codigo", "");
        Log.e("ULTIMA ACCION","llego al boton ");
        SQLiteDatabase db = conn.getReadableDatabase();
        Log.e("ULTIMA ACCION", "Va a consultar acciones");
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM acciones WHERE id = " + codigo, null);
            Log.e("ULTIMA ACCION", "tama??o de las acciones es " + cursor.getCount());
            cursor.moveToLast();
            if(cursor.getString(2).equals("")|| cursor.getString(2).equals(null)||cursor.getString(2).equals("null")){
                cursor.close();
                db.close();
                return false;
            }else{
                Log.e("ULTIMA ACCION",cursor.getString(0)+" ---"+cursor.getString(1)+"---"+cursor.getString(2)+"---"+cursor.getString(3)+"---"+cursor.getString(4)+"---"+cursor.getString(5));
                latitud=cursor.getString(2);
                longitud=cursor.getString(3);
                cursor.close();
                db.close();
                return true;
            }
        } catch (Exception e) {
            Log.e("ULTIMA ACCION","no encontro al consultar");
            db.close();
            return false;
        }
    }
    public boolean ultimaPosicionGestion(){
        String codigo = prefs.getString("codigo", "");
        //  Log.e("ULTIMA ACCION","llego al boton ");
        SQLiteDatabase db = conn.getReadableDatabase();
        //   Log.e("ULTIMA ACCION", "Va a consultar acciones");
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM certificaciones WHERE codChofer = " + codigo, null);
            //     Log.e("ULTIMA ACCION", "tama??o de las acciones es " + cursor.getCount());
            cursor.moveToLast();
            if(cursor.getString(4).equals("")|| cursor.getString(4).equals(null)||cursor.getString(4).equals("null")){
                cursor.close();
                db.close();
                return false;
            }else{
                Log.e("ULTIMA ACCION",cursor.getString(0)+" ---"+cursor.getString(1)+"---"+cursor.getString(2)+"---"+cursor.getString(3)+"---"+cursor.getString(4)+"---"+cursor.getString(5));
                latitud=cursor.getString(4);
                longitud=cursor.getString(5);
                cursor.close();
                db.close();
                return true;
            }
        } catch (Exception e) {
            Log.e("ULTIMA ACCION","no encontro al consultar");
            db.close();
            return false;
        }
    }
    public void continuar(View v){
        if( nombreCliente.getText().toString().equals("")||dv.getText().toString().equals("")|| rutCliente.getText().toString().equals("")){
            modal.putExtra("error", "INGRESE DATOS FALTANTES");
            startActivity(modal);
        }else if(rutCliente.length()>8 ||rutCliente.length()<5||!numeroVerificador(dv.getText().toString())){
            modal.putExtra("error", "Ingrese Formato valido de rut ej: 26208287-3");
            startActivity(modal);
        }else{
            /*
            //registtro accion
            if (ActivityCompat.checkSelfPermission(DatosRetiroNormal.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { }
            client.getLastLocation().addOnSuccessListener(DatosRetiroNormal.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
            SQLiteDatabase db = conn.getWritableDatabase();
            ContentValues values = new ContentValues();
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date date2 = new Date();
            String fecha2 = dateFormat2.format(date2);
            latitud = location.getLatitude() + "";
            longitud = location.getLongitude() + "";
            if (latitud == null || longitud == null || latitud.equals("") || longitud.equals("") || latitud.equals(" ") || longitud.equals(" ")){
                if(ultimaPosicionAccion()){
                    values.put("id", codigo);
                    values.put("fechaIngreso", fecha2);
                    values.put("latitud", latitud);
                    values.put("longitud", longitud);
                    values.put("accion", "datosRetiroNormal");
                    values.put("fechaEnvio", "null");
                    Long id_result = db.insert("acciones", codigo, values);
                    db.close();
                }else if(ultimaPosicionGestion()){
                    values.put("id", codigo);
                    values.put("fechaIngreso", fecha2);
                    values.put("latitud", latitud);
                    values.put("longitud", longitud);
                    values.put("accion", "datosRetiroNormal");
                    values.put("fechaEnvio", "null");
                    Long id_result = db.insert("acciones", codigo, values);
                    db.close();
                }
            } else {
                values.put("id", codigo);
                values.put("fechaIngreso", fecha2);
                values.put("latitud", latitud);
                values.put("longitud", longitud);
                values.put("accion", "datosRetiroNormal");
                values.put("fechaEnvio", "null");
                Long id_result = db.insert("acciones", codigo, values);
                db.close();
            }
                }
            });
                */
            Intent intent = new Intent(this, Retiro.class);
            intent.putExtra("codigoAuditoria","false");
            intent.putExtra("lista",lista);
            intent.putExtra("nombreCliente",nombreCliente.getText().toString());
            intent.putExtra("od",numero);
            intent.putExtra("rutCliente",rutCliente.getText().toString());
            intent.putExtra("dvCliente",dv.getText().toString());
            if(email.length()==0){
                intent.putExtra("emailCliente","FALSE");
            }else{
                intent.putExtra("emailCliente",email.getText().toString());
            }
            if(telefono.length()==0){
                intent.putExtra("telefonoCliente","FALSE");
            }else{
                intent.putExtra("telefonoCliente",telefono.getText().toString());
            }
            intent.putExtra("bio","false");
            startActivityForResult(intent,777);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == 777 ) {
            if (resultCode == RESULT_OK) {

                // get String data from Intent
                String returnString = data.getStringExtra("procesado");
                if(returnString.equals("true")){
                    File files = new File(Environment.getExternalStorageDirectory().getPath()+"/Cargoex");
                    try {
                        FileUtils.deleteDirectory(files);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent output = new Intent();
                    output.putExtra("procesado", "true");
                    setResult(RESULT_OK, output);
                    this.finish();
                }
                // set text view with string
            }
        }
    }
    private boolean isNetDisponible() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
        return (actNetInfo != null && actNetInfo.isConnected());
    }
    public Boolean isOnlineNet() {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
