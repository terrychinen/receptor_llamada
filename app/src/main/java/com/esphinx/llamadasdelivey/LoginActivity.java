package com.esphinx.llamadasdelivey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {
    private EditText edtPhone;
    private EditText edtRuc;
    private Button btnLogin;

    private String state = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = findViewById(R.id.edt_phone);
        edtRuc = findViewById(R.id.edt_ruc);
        btnLogin = findViewById(R.id.btn_login);


        loadPreferences();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone =  edtPhone.getText().toString();
                String ruc   =  edtRuc.getText().toString();
                login(phone, ruc);
            }
        });

    }

    @Override
    public void onBackPressed() { }

    private void login(final String phone, final String ruc){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://nikkeihuaral.com/delivery_celular/login.php?celular="+phone+"&ruc="+ruc;

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Cargando");
        progress.setMessage("Espere un momento...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        if(response.equals("1")){
                            savePreferences(phone, ruc);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, "Número de celular o ruc incorrecto", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });
        queue.add(stringRequest);
    }

    private void loadPreferences(){
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        state = preferences.getString("state", "State doesn't exist");
        if(state .equals("1")){
            finish(); startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }


    private void savePreferences(String phone, String ruc) {
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("phone", phone);
        editor.putString("ruc", ruc);
        editor.putString("state", "1");

        editor.apply();
    }


}