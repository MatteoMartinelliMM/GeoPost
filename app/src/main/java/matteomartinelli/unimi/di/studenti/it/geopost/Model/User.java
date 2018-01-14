package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by utente2.academy on 12/19/2017.
 */

public class User implements Serializable{
    private String userName;
    private ArrayList<UserState> userStates;
    private UserState lastState;
    private String cookie;
    private LatLng currentPosition;
    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public User(String userName, ArrayList<UserState> userStates, String cookie) {
        this.userName = userName;
        this.userStates = userStates;
        this.cookie = cookie;

    }

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public ArrayList<UserState> getUserStates() {
        return userStates;
    }

    public void setUserStates(ArrayList<UserState> userStates) {
        this.userStates = userStates;
    }

    public LatLng getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(LatLng currentPosition) {
        this.currentPosition = currentPosition;
    }

    public UserState getLastState() {
        return lastState;
    }

    public void setLastState(UserState lastState) {
        this.lastState = lastState;
    }
}
