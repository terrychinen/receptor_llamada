package com.esphinx.llamadasdelivey;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private TextView txtSalida;
    private Button btnLogOut;
    private SharedPreferences preferences;
    private Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("login", MODE_PRIVATE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy ");
            LocalDateTime now = null;
            now = LocalDateTime.now();
            Toast.makeText(MainActivity.this, "Fecha: " +dtf.format(now), Toast.LENGTH_LONG).show();
            System.out.println(dtf.format(now));

        }

        txtSalida = findViewById(R.id.salida);
        btnLogOut = findViewById(R.id.btn_log_out);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        /*TelephonyManager telephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                if (TelephonyManager.CALL_STATE_RINGING == state) {
                    SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                    String myPhone = preferences.getString("phone", "phone doesn't exist");
                    String ruc = preferences.getString("ruc", "phone doesn't exist");
                    txtSalida.setText(phoneNumber);
                    saveClientPhone(phoneNumber, myPhone, ruc);
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);*/

        verifyPermissions();

        intent = new Intent(this, SegundoPlano.class);

        startService(new Intent(intent));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void verifyPermissions(){
        @SuppressLint("InlinedApi") String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.FOREGROUND_SERVICE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {

            startService(new Intent(this, SegundoPlano.class));

//            TelephonyManager telephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//            telephony.listen(new PhoneStateListener() {
//                @Override
//                public void onCallStateChanged(int state, String phoneNumber) {
//                    if (TelephonyManager.CALL_STATE_RINGING == state) {
//                        String myPhone = preferences.getString("phone", "phone doesn't exist");
//                        String ruc = preferences.getString("ruc", "phone doesn't exist");
//                        txtSalida.setText(phoneNumber);
//                        saveClientPhone(phoneNumber, myPhone, ruc);
//                    }
//                }
//            }, PhoneStateListener.LISTEN_CALL_STATE);
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    @Override
    public void onBackPressed() {

    }

    private void deletePreferences(){
        SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
        editor.clear().apply();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void showDialog() {
        String[] options = {"Cancelar", "Aceptar"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 1) {
                    final ProgressDialog progress = new ProgressDialog(MainActivity.this);
                    progress.setTitle("Saliendo");
                    progress.setMessage("Espere un momento...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();

                    stopService(intent);
                    deletePreferences();

                    progress.dismiss();
                }
            }
        });
        builder.show();
    }

    private void saveClientPhone(String phoneClient, String myPhone, String ruc) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://nikkeihuaral.com/delivery_celular/save_phone.php?mi_celular="+myPhone+"&cliente_celular="+phoneClient+"&ruc="+ruc;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print("DATA: " +error);
            }
        });
        queue.add(stringRequest);
    }

}