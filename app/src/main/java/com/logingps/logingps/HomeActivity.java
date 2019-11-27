package com.logingps.logingps;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements LocationListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference coordinatesRef = db.collection("coordinates");

    TextView displayName;
    Button forceCoordinates;

    @VisibleForTesting
    private ProgressDialog mProgressDialog;
    private LocationRequest mLocationRequest;

    private static final int REQUEST_CHECK_SETTINGS = 613;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        setTitle("Home");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        for(String provider : locationManager.getAllProviders()){
            Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_LONG).show();
        }

        displayName = findViewById(R.id.display_name);
        forceCoordinates = findViewById(R.id.forceCoordinates);
        createLocationRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "Location is now on", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "User didn't allowed to change location settings", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(null, currentUser);
    }

    public void getCoordinates(View view) {
        askPermission();
        askForLocationChange();
        setUpService();
    }

    public void showReport(View view) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void askForLocationChange() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Toast.makeText(HomeActivity.this, "Location is already on", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
            }
        });
    }

    private void askPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.i("Permission", "Granted");
        } else {
            setUpService();
        }
    }



    public void setUpService() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    updateLocation(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void updateLocation(Location location) {
        SimpleDateFormat dateFormatbra = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat_minutos = new SimpleDateFormat("mm");
        SimpleDateFormat dateFormat_seg = new SimpleDateFormat("ss");
        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        String data_completa = dateFormat.format(data_atual);
        String hora_atual = dateFormat_hora.format(data_atual);
        String min_atual = dateFormat_minutos.format(data_atual);
        String segs = dateFormat_seg.format(data_atual);
        String databra = dateFormatbra.format(data_atual);


        Log.i("data_completa", data_completa);
        Log.i("data_atual", data_atual.toString());
        Log.i("hora_atual", hora_atual); // Esse é o que você quer


        double latPoint = location.getLatitude();
        double lngPoint = location.getLongitude();

        String latitude = Double.toString(latPoint);
        // Log.i("GPSLATITUDE:", latitude);
        String longitude = Double.toString(lngPoint);
        // Log.i("GPSLONGITUDE:", longitude);

        String coordenadas = latitude + "#" + longitude;

        int min = Integer.parseInt(String.valueOf(min_atual));
        int ss = Integer.parseInt(String.valueOf(segs));
        Log.i("GPSLONGITUDE:", segs);

        if ((min % 2) == 0 && (ss == 59)) {
            coordinatesRef.document(data_completa).set(coordenadas);
            Log.i("GPSLONGITUDE:", coordenadas);
            Log.i("GPSLONGITUDE:", data_completa);
        }
    }

    public void signOut(View view) {
        mAuth.signOut();
        updateUI(view, null);
    }

    private void updateUI(View view, FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            displayName.setText(user.getEmail());
        } else {
            Intent intent = new Intent(view.getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
