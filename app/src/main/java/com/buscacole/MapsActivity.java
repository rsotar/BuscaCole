package com.buscacole;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.buscacole.utils.MapUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = "BuscaCole: " + MapsActivity.class.getSimpleName();

    LatLng latLngTucuman = new LatLng(-26.8333, -65.2);

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    protected static final int RESULT_SPEECH = 1;
    private ImageButton btnSpeak;
    private TextView txtText;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this); //Location Listener
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //in fragment class callback
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> voiceToTextResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(voiceToTextResult.get(0));

                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocationName(voiceToTextResult.get(0), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0) {
                        double destinoLat = addresses.get(0).getLatitude();
                        double destinoLong = addresses.get(0).getLongitude();
                        LatLng destinoCoordinates = new LatLng(destinoLat, destinoLong);
                        Log.i(TAG, destinoCoordinates.toString());
                    }
                }
                break;
            }

        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker in Tucuman.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(latLngTucuman).title("Tucuman"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngTucuman, 13));
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        final LatLng actualLatLng = new LatLng(currentLatitude, currentLongitude);

        /********************************************************************/
        txtText = (TextView) findViewById(R.id.txtText);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("destino");

                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        /********************************************************************/

        //Loading KmlFiles in mMap and set actual position
        retrieveFileFromResource(actualLatLng);

        MarkerOptions options = new MarkerOptions().position(actualLatLng).title("I am here!");

        //Seeting mMap
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngTucuman, 13));

    }

    public void retrieveFileFromResource(LatLng actualLatLng) {

        List<LatLng> coordenadasLinea19;
        List<LatLng> coordenadasLinea17Rojo;
        List<LatLng> coordenadasLinea9Viamonte;

        try {
            KmlLayer kmlLayerRamalRojo17 = new KmlLayer(mMap, R.raw.ramal_rojo_17, getApplicationContext());
            KmlLayer kmlLayerRamalViamonte9 = new KmlLayer(mMap, R.raw.ramal_viamonte_9, getApplicationContext());
            KmlLayer kmlLayerRamalLinea19 = new KmlLayer(mMap, R.raw.ramal_linea_19, getApplicationContext());

            kmlLayerRamalRojo17.addLayerToMap();
            kmlLayerRamalViamonte9.addLayerToMap();
            kmlLayerRamalLinea19.addLayerToMap();


            coordenadasLinea19 = MapUtils.getCoordinatesFromKml(kmlLayerRamalLinea19);
            coordenadasLinea17Rojo = MapUtils.getCoordinatesFromKml(kmlLayerRamalRojo17);
            coordenadasLinea9Viamonte = MapUtils.getCoordinatesFromKml(kmlLayerRamalViamonte9);

            float minDistanceLinea19 = minDistance(actualLatLng, coordenadasLinea19);
            float minDistanceLinea17Rojo = minDistance(actualLatLng, coordenadasLinea17Rojo);
            float minDistanceLinea9Viamonte = minDistance(actualLatLng, coordenadasLinea9Viamonte);

            Log.i("Linea 19 - Min Distance: ", Float.toString(minDistanceLinea19));
            Log.i("Linea 17 Rojo - Min Distance: ", Float.toString(minDistanceLinea17Rojo));
            Log.i("Linea 9 Viamonte - Min Distance: ", Float.toString(minDistanceLinea9Viamonte));

        } catch (IOException e) {
            Log.e("IOException: ", e.toString());
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException: ", e.toString());
        }
    }

    private float minDistance(LatLng actualLatLng, List<LatLng> recorridoLinea) {

        Location actualLocation = new Location("");
        actualLocation.setLatitude(actualLatLng.latitude);
        actualLocation.setLongitude(actualLatLng.longitude);

        Location recorridoLocation = new Location("");
        recorridoLocation.setLatitude(recorridoLinea.get(0).latitude);
        recorridoLocation.setLongitude(recorridoLinea.get(0).longitude);

        float minDistanceInMeters = actualLocation.distanceTo(recorridoLocation);
        for (LatLng position : recorridoLinea) {
            recorridoLocation.setLatitude(position.latitude);
            recorridoLocation.setLongitude(position.longitude);

            //Log.i("Min distance: ", Float.toString(actualLocation.distanceTo(recorridoLocation)));
            if (actualLocation.distanceTo(recorridoLocation) < minDistanceInMeters) {
                minDistanceInMeters = actualLocation.distanceTo(recorridoLocation);
            }
        }

        return minDistanceInMeters;
    }

}
