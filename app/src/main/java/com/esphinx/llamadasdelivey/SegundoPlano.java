package com.esphinx.llamadasdelivey;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SegundoPlano extends Service {
    int callCheck = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intencion, int flags, int idArranque) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Delivery llamdas")
                .setContentText("Recibiendo llamdas")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        final String myPhone = preferences.getString("phone", "phone doesn't exist");
        final String ruc = preferences.getString("ruc", "phone doesn't exist");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TelephonyManager telephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            assert telephony != null;
            telephony.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    if (TelephonyManager.CALL_STATE_RINGING == state) {
                        if(callCheck == 0){
                            saveClientPhone(phoneNumber, myPhone, ruc);
                            callCheck++;
                        }
                    }else if(TelephonyManager.CALL_STATE_OFFHOOK == state || TelephonyManager.CALL_STATE_IDLE == state) {
                        if(callCheck >= 1) {
                            callCheck--;
                        }
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);

            startForeground(10, builder.build());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void saveClientPhone(String phoneClient, String myPhone, String ruc) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://nikkeihuaral.com/delivery_celular/save_phone.php?mi_celular="+myPhone+"&cliente_celular="+phoneClient+"&ruc="+ruc+"&fecha_hora="+getCurrentDate();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.print("DATA: " +response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print("DATA: " +error);
            }
        });
        queue.add(stringRequest);
    }

    private String getCurrentDate() {
        String currentDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = null;
            now = LocalDateTime.now();
            currentDate = dtf.format(now);
        }
        return currentDate;
    }
}
