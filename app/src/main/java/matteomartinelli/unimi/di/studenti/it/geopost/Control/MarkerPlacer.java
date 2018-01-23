package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;

/**
 * Created by matteoma on 1/16/2018.
 */

public class MarkerPlacer {
    public static final int USER_LAST_STATUS = 1;
    public static final int USER_OLD_STATUS = 2;
    public static final int FRIENDS_STATUS = 0;
    private static LatLng latLng;
    private static double latitude;
    private static double longitude;
    private static String userName;
    private static String status;

    public static void fillInTheMapWithFriendsMarkers(GoogleMap map, ArrayList<User> friendList) {
        for (User u : friendList) {
            settingTheMarkerData(u, map, FRIENDS_STATUS);

        }

    }


    public static void addNewStatusMarker(GoogleMap map, User personalProfile) {
        settingTheMarkerData(personalProfile, map, USER_LAST_STATUS);
    }

    public static void fillIntTheMapWithUserStatus(GoogleMap map, User personalProfile) {
        if(personalProfile.getLastState()!=null){
            settingTheMarkerData(personalProfile,map,USER_LAST_STATUS);
        }
        if(personalProfile.getUserStates()!=null){
            settingTheMarkerData(personalProfile,map,USER_OLD_STATUS);
        }
    }

    private static void settingTheMarkerData(User u, GoogleMap map, int typeOfMarkersToAdd) {
        MarkerOptions newMarkerOption;
        latitude = u.getLastState().getLatitude();
        longitude = u.getLastState().getLongitude();
        latLng = new LatLng(latitude, longitude);
        userName = u.getUserName();
        status = u.getLastState().getStato();
        newMarkerOption = new MarkerOptions().position(latLng).title(userName).snippet(status);
        switch (typeOfMarkersToAdd){
            case FRIENDS_STATUS:
                newMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            case USER_LAST_STATUS:
                newMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                break;
            case USER_OLD_STATUS:
                newMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).alpha(0.7f);
                break;
        }
        map.addMarker(newMarkerOption);
    }


}
