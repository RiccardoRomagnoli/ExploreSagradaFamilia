package com.example.exploresagradafamilia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.exploresagradafamilia.ViewModel.AddSightplaceViewModel;
import com.example.exploresagradafamilia.ViewModel.ListSightplaceViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends AppCompatActivity {

    private Timer timer;
    private ProgressBar progressBar;
    private int DELAY_PROGRESS = 8;
    private boolean launchMain = false;
    private boolean launchManually = false;
    private boolean doNotUpdate = false;
    private boolean needContent = false;

    private final static String TAG = "DATA_REQUEST";
    private static boolean isNetworkConnected = false;
    RequestQueue requestQueue;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;
        setContentView(R.layout.activity_launcher);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        snackbar = Snackbar.make(
                findViewById(R.id.launch_activity),
                R.string.no_intern_available,
                Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.settings, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build intent that displays the App settings screen.
                Utility.setSettingsIntent(activity);
            }
        });

        //check presenza elementi room
        //if true -> fast skip
        //if false -> if check connection
        //if true -> get http -> room populate -> progress update -> intent
        //if false -> snack bar (link settings)
        ListSightplaceViewModel model = new ViewModelProvider(this).get(ListSightplaceViewModel.class);
        model.getItems().observe(this, sightplaces -> {
            Toast.makeText(getApplicationContext(), "*Places size: *\n" + sightplaces.size(), Toast.LENGTH_LONG).show();
            if (!doNotUpdate) {
                if (sightplaces.isEmpty()) {
                    doNotUpdate = true;
                    if (isOnline()) {
                        downloadResurces();
                        doProgress();
                    } else {
                        snackbar.show();
                        needContent = true;
                        DELAY_PROGRESS *= 2;
                    }
                } else {
                    launchMain = true;
                    doProgress();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //register the callback that keep monitored the internet connection
        registerNetworkCallback(this);
    }

    private void doProgress() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                //this repeats every 8 ms
                if (i <= progressBar.getMax()) {
                    progressBar.setProgress(i);
                    i++;
                } else {
                    if (launchMain) {
                        startMainActivity();
                    } else {
                        launchManually = true;
                    }
                    //closing the timer
                    cancel();
                }
            }
        }, 0, DELAY_PROGRESS);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void downloadResurces() {
        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(this);
        String url = "https://agriturismomarcheok.it/web-services/getSightplaces.php";
        // Request a jsonArray response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonobject = response.getJSONObject(i);
                                Sightplace sightplace = new Sightplace(
                                        jsonobject.get("imageB64").toString(),
                                        Double.parseDouble(jsonobject.get("latitude").toString()),
                                        Double.parseDouble(jsonobject.get("longitude").toString()),
                                        jsonobject.get("description").toString(),
                                        jsonobject.get("title").toString()
                                );
                                roomAddSightplace(sightplace);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        launchMain = true;
                        if (launchManually)
                            startMainActivity();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LAB", error.toString());
                    }
                });
        jsonArrayRequest.setTag(TAG);
        // Add the request to the RequestQueue.
        requestQueue.add(jsonArrayRequest);
    }

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
            if (needContent) {
                snackbar.dismiss();
                downloadResurces();
                doProgress();
                needContent = false;
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            Toast.makeText(getApplicationContext(), "*No conn*\n", Toast.LENGTH_LONG).show();

            isNetworkConnected = false;
            if (needContent) {
                snackbar.show();
            }
        }
    };

    private void roomAddSightplace(Sightplace sightplace) {
        AddSightplaceViewModel model = new ViewModelProvider(this).get(AddSightplaceViewModel.class);
        //when the list of the items changed, the adapter gets the new list.
        model.addItem(sightplace);
    }

    private void registerNetworkCallback(Activity activity) {
        Log.d("NET", "registerNetworkCallback");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            //api 24, android 7
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                //Class deprecated since API 29 (android 10) but used for android 5 and 6
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = networkInfo != null && networkInfo.isConnected();
            }
        } else {
            isNetworkConnected = false;
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
