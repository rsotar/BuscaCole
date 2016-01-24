package com.buscacole.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlLineString;
import com.google.maps.android.kml.KmlPlacemark;

import java.util.List;

public class MapUtils {

    public static List<LatLng> getCoordinatesFromKml(KmlLayer kmlLayerMap) {

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
}
