package com.app.john.prm391x_googlemap_trungnqfx00077;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    SupportPlaceAutocompleteFragment mInputFrom;
    SupportPlaceAutocompleteFragment mInputTo;

    Place mFromPlace;
    Place mToPlace;

    Button mDirectionBtn;
    TextView mDistanceTV;
    TextView mTimeRouteTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mDirectionBtn = findViewById(R.id.mDirectionBtn);
        mDistanceTV = findViewById(R.id.mDistanceTV);
        mTimeRouteTV = findViewById(R.id.mTimeRouteTV);

        mInputFrom = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_from);
        if (mInputFrom != null) {
            mInputFrom.setHint("From");
            mInputFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    mFromPlace = place;
                }

                @Override
                public void onError(Status status) {
                    Log.d("mInputFrom: ", status.getStatusMessage());
                }
            });
        }

        mInputTo = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_to);
        if (mInputTo != null) {
            mInputTo.setHint("To");
            mInputTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    mToPlace = place;
                }

                @Override
                public void onError(Status status) {
                    Log.d("mInputTo: ", status.getStatusMessage());
                }
            });
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
