package com.example.exploresagradafamilia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.exploresagradafamilia.Permissions.PermissionUtility;
import com.example.exploresagradafamilia.ViewModel.AddSightplaceViewModel;
import com.example.exploresagradafamilia.ViewModel.ListSightplaceViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
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

    private AppCompatActivity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_launcher);
        progressBar = findViewById(R.id.progressBar);

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

        new ViewModelProvider(this)
                .get(ListSightplaceViewModel.class)
                .getItems().observe(this, this::roomObserver);

        //register the callback that keep monitored the internet connection
        registerNetworkCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline() && needContent) {
            snackbar.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.PERMISSION_REQUEST_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadResources();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.funcionality_limited);
                    builder.setMessage(getString(R.string.writing_access_needed) +
                            getString(R.string.cannot_mangage_images));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> {/*DO NOTHING*/});
                    builder.show();
                }
                return;
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void doFakeProgress() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                //this repeats every 8 ms
                if (i <= progressBar.getMax()) {
                    i++;
                } else {
                    startMainActivity();
                    //closing the timer
                    cancel();
                }
            }
        }, 0, DELAY_PROGRESS);
    }

    private void downloadResources() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
            // Instantiate the RequestQueue.
            requestQueue = Volley.newRequestQueue(this);
            String url = getString(R.string.content_url);
            // Request a jsonArray response from the provided URL.
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, response -> {
                        new CreateSightplace().execute(response);
                    }, error -> Log.d("LAB", error.toString()));
            jsonArrayRequest.setTag(TAG);

            // Add the request to the RequestQueue.
            requestQueue.add(jsonArrayRequest);
        } else {
            PermissionUtility.askForStoragePermissions(activity);
        }
    }

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
            //api > 25
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    if (needContent) {
                        snackbar.dismiss();
                        downloadResources();
                        needContent = false;
                    }
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    if (needContent) {
                        snackbar.show();
                    }
                }
            });
        } else {
            if (needContent) {
                snackbar.show();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //check presenza elementi room
    //if true -> fast skip
    //if false -> if check connection
    //      if true -> get http -> room populate -> progress update -> intent
    //      if false -> snack bar (link settings)
    private void roomObserver(List<Sightplace> sightplaces) {
        if (!doNotUpdate) {
            if (sightplaces.isEmpty()) {
                doNotUpdate = true;
                if (isOnline()) {
                    downloadResources();
                } else {
                    snackbar.show();
                    needContent = true;
                }
            } else {
                doFakeProgress();
            }
        }
    }

    private class CreateSightplace extends AsyncTask<JSONArray, Integer, Integer> {

        Exception e;

        //create sightplaces downloads images, insert in DB
        protected Integer doInBackground(JSONArray... jsonArrays) {
            JSONArray response = jsonArrays[0];
            Sightplace sightplace = null;
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonobject = response.getJSONObject(i);
                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(jsonobject.get("url").toString()).openConnection().getInputStream());

                    sightplace = new Sightplace(
                            Double.parseDouble(jsonobject.get("latitude").toString()),
                            Double.parseDouble(jsonobject.get("longitude").toString()),
                            jsonobject.get("description").toString(),
                            jsonobject.get("title").toString(),
                            Double.parseDouble(jsonobject.get("rating").toString()),
                            jsonobject.get("location").toString(),
                            Integer.parseInt(jsonobject.get("major").toString()),
                            Integer.parseInt(jsonobject.get("minor").toString()),
                            jsonobject.get("url").toString(),
                            Utility.saveImage(activity,
                                    jsonobject.get("major").toString(),
                                    jsonobject.get("minor").toString(),
                                    jsonobject.get("title").toString(),
                                    bitmap));

                    roomAddSightplace(sightplace);
                }
            } catch (Exception e) {
                this.e = e;
                publishProgress(0);
            }
            return 1;
        }


        protected void onProgressUpdate(Integer... progress) {
            Log.d("LAUNCHER", e.getMessage());
        }

        protected void onPostExecute(Integer integers) {
            startMainActivity();
        }
    }

}
