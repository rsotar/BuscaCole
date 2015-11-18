package com.buscacole;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void startDemo() {
        try {
            //mMap = getMap();
            Log.i("*** Cargando KML *** - ", mMap.toString());
            retrieveFileFromResource();
            //retrieveFileFromUrl();
        } catch (Exception e) {
            Log.e("Exception caught", e.toString());
        }
    }


    private void retrieveFileFromResource() {
        try {
            KmlLayer kmlLayerRamalRojo17 = new KmlLayer(mMap, R.raw.ramal_rojo_17, getApplicationContext());
            KmlLayer kmlLayerRamalViamonte9 = new KmlLayer(mMap, R.raw.ramal_viamonte_9, getApplicationContext());
            KmlLayer kmlLayerRamalLinea19 = new KmlLayer(mMap, R.raw.ramal_linea_19, getApplicationContext());

            kmlLayerRamalRojo17.addLayerToMap();
            kmlLayerRamalViamonte9.addLayerToMap();
            kmlLayerRamalLinea19.addLayerToMap();
            //moveCameraToKml(kmlLayer);
        } catch (IOException e) {
            Log.e("IException", "rober");
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
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

        startDemo();
        // Add a marker in Tucuman and move the camera
        LatLng tucuman = new LatLng(-26.8333, -65.2);
        mMap.addMarker(new MarkerOptions().position(tucuman).title("Marker in Tucuman"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(tucuman,13));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tucuman, 13));
    }
}
