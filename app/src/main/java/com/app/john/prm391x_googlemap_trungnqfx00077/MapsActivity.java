package com.app.john.prm391x_googlemap_trungnqfx00077;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.app.john.prm391x_googlemap_trungnqfx00077.Constants.MY_PERMISSIONS_REQUEST_LOCATION;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;

    SupportPlaceAutocompleteFragment mInputFrom;
    SupportPlaceAutocompleteFragment mInputTo;

    Place mFromPlace;
    String[] mFromCoord;
    LatLng mFromLatLng;

    Place mToPlace;
    String[] mToCoord;
    LatLng mToLatLng;

    Button mDirectionBtn;
    TextView mDistanceTV;
    TextView mTimeRouteTV;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mMyLocation;

    private boolean mPermissionDenied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initMap();
        initViews();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initViews() {
        findViewById(R.id.mDirectionBtn).setOnClickListener(this);
        findViewById(R.id.mMyLocationBtn).setOnClickListener(this);
        mDistanceTV = findViewById(R.id.mDistanceTV);
        mTimeRouteTV = findViewById(R.id.mTimeRouteTV);

        mInputFrom = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_from);
        if (mInputFrom != null) {
            mInputFrom.setHint("Origin");
            mInputFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    mFromPlace = place;
                    mFromLatLng = mFromPlace.getLatLng();
                    mFromCoord = new String[2];
                    mFromCoord[0] = String.valueOf(mFromPlace.getLatLng().latitude);
                    mFromCoord[1] = String.valueOf(mFromPlace.getLatLng().longitude);
                }

                @Override
                public void onError(Status status) {
                    Log.d("mInputFrom: ", status.getStatusMessage());
                }
            });
        }

        mInputTo = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_to);
        if (mInputTo != null) {
            mInputTo.setHint("Destination");
            mInputTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    mToPlace = place;
                    mToLatLng = mToPlace.getLatLng();
                    mToCoord = new String[2];
                    mToCoord[0] = String.valueOf(mToPlace.getLatLng().latitude);
                    mToCoord[1] = String.valueOf(mToPlace.getLatLng().longitude);
                }

                @Override
                public void onError(Status status) {
                    Log.d("mInputTo: ", status.getStatusMessage());
                }
            });
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION
                );
            }
        } else {
            getLastLocation();
        }

    }

    @SuppressLint("all")
    private void getLastLocation() {
        if (mMap != null) mMap.setMyLocationEnabled(true);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mMyLocation = location;
                            focusOnMyLocation(location);
                        }
                    }
                });
    }

    private void focusOnMyLocation(Location location) {
        LatLng lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15f));
        mMap.addMarker(new MarkerOptions()
                .title("Your location")
                .position(lastLatLng));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getLastLocation();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    mPermissionDenied = true;
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDirectionBtn:
                if (isOnline()) {
                    if (mFromPlace != null && mToPlace != null) {
                        String url = createDirectionURL();
                        DontKnowHowToNameIt dontKnowHowToNameIt = new DontKnowHowToNameIt();
                        dontKnowHowToNameIt
                                .execute(url);
                    } else {
                        /*Toast.makeText(
                                this,
                                "Please choose origin place and destination place.",
                                Toast.LENGTH_LONG
                        ).show();*/
                        showCustomAlert("Please choose origin place and destination place.");
                    }
                } else {
                    /*Toast.makeText(
                            this,
                            "Networking is not available",
                            Toast.LENGTH_LONG
                    ).show();*/
                    showCustomAlert("Networking is not available");
                }
                break;
            case R.id.mMyLocationBtn:
                focusOnMyLocation(mMyLocation);
                break;
        }
    }

    private String createDirectionURL() {
        return Constants.DIRECTION_URL
                + Constants.PARAM_ORIGIN + mFromCoord[0] + ", " + mFromCoord[1] + "&"
                + Constants.PARAM_DESTINATION + mToCoord[0] + ", " + mToCoord[1] + "&"
                + Constants.PARAM_KEY + getString(R.string.browse_google);
    }

    /**
     * Source: https://developer.android.com/training/basics/network-ops/managing
     *
     * @return whether network is available or not
     */
    public boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class DontKnowHowToNameIt extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url[0]).build();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null && response.isSuccessful()) {
                if (response.body() != null) {
                    try {
                        return response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "Failed";
        }

        @Override
        protected void onPostExecute(String s) {
            mMap.clear();

            Log.d("onPostExecute: ", s);

            JsonObject jsDirection = new JsonParser().parse(s).getAsJsonObject();
            JsonObject firstRoute = (JsonObject) jsDirection.getAsJsonArray(Constants.MEMBER_ROUTES).get(0);
            String points = firstRoute.getAsJsonObject("overview_polyline").get("points").getAsString();
            //
            JsonArray legs = firstRoute.getAsJsonArray("legs");
            JsonObject legsFirstChild = (JsonObject) legs.get(0);
            String distance = legsFirstChild.getAsJsonObject("distance").get("text").getAsString();
            String duration = legsFirstChild.getAsJsonObject("duration").get("text").getAsString();

            // Log for testing
            Log.d("onPostExecute: ", points);
            Log.d("onPostExecute: ", distance);
            Log.d("onPostExecute: ", duration);

            List<LatLng> decodedPath = PolyUtil.decode(points);
            mMap.addPolyline(new PolylineOptions()
                    .color(Color.BLUE)
                    .addAll(decodedPath));
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mFromPlace.getLatLng().latitude, mFromPlace.getLatLng().longitude),
                            16f)
            );

            mMap.addMarker(new MarkerOptions().position(mFromLatLng));
            mMap.addMarker(new MarkerOptions().position(mToLatLng));

            mDistanceTV.setText(distance);
            mTimeRouteTV.setText(duration);
        }
    }

    public void showCustomAlert(String message) {
        Context context = getApplicationContext();

        LayoutInflater inflater = getLayoutInflater();

        View viewToast = inflater.inflate(R.layout.my_custom_toast_layout, null);
        TextView toastMessageTV = viewToast.findViewById(R.id.mMyCustomToast_MessageTV);
        toastMessageTV.setText(message);

        Toast toast = new Toast(context);

        // Set layout to toast
        toast.setView(viewToast);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
