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
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlLineString;
import com.google.maps.android.kml.KmlPlacemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

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


    public void retrieveFileFromResource() {

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

            coordenadasLinea19 = getCoordinatesFromKml(kmlLayerRamalLinea19);
            coordenadasLinea17Rojo = getCoordinatesFromKml(kmlLayerRamalLinea19);
            coordenadasLinea9Viamonte = getCoordinatesFromKml(kmlLayerRamalLinea19);

            //moveCameraToKml(kmlLayer);
        } catch (IOException e) {
            Log.e("IException", "rober");
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }



    public List<LatLng> getCoordinatesFromKml(KmlLayer kmlLayerMap) {

        //Retrieve the first container in the KML layer
        KmlContainer container = kmlLayerMap.getContainers().iterator().next();

        //Retrieve the first placemark in the nested container
        KmlPlacemark placemark = container.getPlacemarks().iterator().next();

        //Retrieve a LineString object in a placemark
        KmlLineString lineString = (KmlLineString) placemark.getGeometry();

        //Retrieve an Array from objects contained in KmlLineString
        List<LatLng> coordinatesList = lineString.getGeometryObject();
        return coordinatesList;

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
