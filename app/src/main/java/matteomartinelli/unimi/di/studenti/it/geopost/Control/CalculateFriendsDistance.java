package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.location.Location;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;

/**
 * Created by Teo on 15/01/2018.
 */

public class CalculateFriendsDistance {

    public static final String FRIEND_LOCATION = "FRIEND LOCATION";
    public static final String LOGGED_USER_LOCATION = "loggedUserLocation";

    public static ArrayList<User> settingForEachUserTheDistanceAndSortTheList(ArrayList<User> friendList, User loggedUser) {
        for (int i = 0; i < friendList.size(); i++) {
            User u = friendList.get(i);
            float distance;

            distance = getFriendDistance(u, loggedUser);
            friendList.get(i).getLastState().setDistanceToLoggedUser(distance);

        }
        Collections.sort(friendList);
        return friendList;

    }

    private static float getFriendDistance(User friend, User loggedUser) {
        double friendLatitude = friend.getLastState().getLatitude();
        double friendLongitude = friend.getLastState().getLongitude();
        double loggedUserLongitude = loggedUser.getLastState().getLatitude();
        double loggedUserLatitude = loggedUser.getLastState().getLongitude();
        Location friendLocation = new Location(FRIEND_LOCATION);
        Location loggedUserLocation = new Location(LOGGED_USER_LOCATION);
        friendLocation.setLongitude(friendLongitude);
        friendLocation.setLatitude(friendLatitude);
        loggedUserLocation.setLongitude(9.254575);
        loggedUserLocation.setLatitude(45.547676);
        return loggedUserLocation.distanceTo(friendLocation);
    }
}
