package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GMapsActivityJUnitTest {
    @Test
    public void testLyonMarker_ReturnsTrue(){
        MarkerOptions wanted = new MarkerOptions()
                .position(
                        new LatLng(
                                34.024555845264075,
                                -118.28840694512736))
                .title("Lyon Center");
        MarkerOptions test = GMapsActivity.returnMarkerOption(
                34.024555845264075,
                -118.28840694512736,
                "Lyon Center");
        assertEquals(wanted.getTitle(), test.getTitle());
    }

    @Test
    public void testCromwellTrackMarker_ReturnsTrue(){
        MarkerOptions wanted = new MarkerOptions()
                .position(
                        new LatLng(
                                34.0222295647154,
                                -118.28784044512746
                        )
                )
                .title("Cromwell Track");
        MarkerOptions test = GMapsActivity.returnMarkerOption(
                34.0222295647154,
                -118.28784044512746,
                "Cromwell Track");
        assertEquals(wanted.getTitle(), test.getTitle());
    }

    @Test
    public void testUACLapSwimMarker_ReturnsTrue(){
        MarkerOptions wanted = new MarkerOptions()
                .position(
                        new LatLng(
                                34.024203589076414,
                                -118.2879799201736
                        )
                )
                .title("UAC Lap Swim");
        MarkerOptions test = GMapsActivity.returnMarkerOption(
                34.024203589076414,
                -118.2879799201736,
                "UAC Lap Swim"
        );
        assertEquals(test.getTitle(), wanted.getTitle());
    }
}
