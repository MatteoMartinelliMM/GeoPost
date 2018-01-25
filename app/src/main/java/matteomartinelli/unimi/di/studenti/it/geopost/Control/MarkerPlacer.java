package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;

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

    public static boolean fillInTheMapWithFriendsMarkers(GoogleMap map, ArrayList<User> friendList, String addedFriendName) {
        boolean haveToMoveCameraOnAddedUser = false;
        for (User u : friendList) {

            settingTheMarkerData(u, map, null, FRIENDS_STATUS);
            if (u.getUserName().equals(addedFriendName)) {
                moveCameraToAddedUser(map, u);
                haveToMoveCameraOnAddedUser = true;
            }
        }
        return haveToMoveCameraOnAddedUser;

    }


    public static void addNewStatusMarker(GoogleMap map, User personalProfile) {
        settingTheMarkerData(personalProfile, map, null, USER_LAST_STATUS);
    }

    public static void fillIntTheMapWithUserStatus(GoogleMap map, User personalProfile) {
        if (personalProfile.getLastState() != null)
            settingTheMarkerData(personalProfile, map, null, USER_LAST_STATUS);
        if (personalProfile.getUserStates() != null) {
            ArrayList<UserState> states = personalProfile.getUserStates();
            for (UserState state : states) {
                settingTheMarkerData(personalProfile, map, state, USER_OLD_STATUS);
            }
        }
    }

    public static void addNewFriendMarker(GoogleMap gMap, User isTheNewFriend) {
        settingTheMarkerData(isTheNewFriend, gMap, null, FRIENDS_STATUS);
    }

    private static void settingTheMarkerData(User u, GoogleMap map, UserState state, int typeOfMarkersToAdd) {
        if (map != null) {
            MarkerOptions newMarkerOption;
            if (state == null) {
                latitude = u.getLastState().getLatitude();
                longitude = u.getLastState().getLongitude();
                status = u.getLastState().getStato();
            } else {
                latitude = state.getLatitude();
                longitude = state.getLongitude();
                status = state.getStato();
            }
            latLng = new LatLng(latitude, longitude);
            userName = u.getUserName();

            newMarkerOption = new MarkerOptions().position(latLng).title(userName).snippet(status);
            switch (typeOfMarkersToAdd) {
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


    private static void moveCameraToAddedUser(GoogleMap gMap, User isTheNewFriend) {
        LatLng latLng = new LatLng(isTheNewFriend.getLastState().getLatitude(), isTheNewFriend.getLastState().getLongitude());
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        gMap.animateCamera(zoom);

    }
}
