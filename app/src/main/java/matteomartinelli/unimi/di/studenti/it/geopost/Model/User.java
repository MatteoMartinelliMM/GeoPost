package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by utente2.academy on 12/19/2017.
 */

public class User implements Serializable, Comparable<User> {
    private String userName;
    private ArrayList<UserState> userStates;
    private UserState lastState;
    private String cookie;


    public User(String userName, ArrayList<UserState> userStates) {
        this.userName = userName;
        this.userStates = userStates;


    }

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public ArrayList<UserState> getUserStates() {
        return userStates;
    }

    public void setUserStates(ArrayList<UserState> userStates) {
        this.userStates = userStates;
    }

    public UserState getLastState() {
        return lastState;
    }

    public void setLastState(UserState lastState) {
        this.lastState = lastState;
    }


    @Override
    public int compareTo(@NonNull User o) {
        float u1 = this.getLastState().getDistanceToLoggedUser();
        float u2 = o.getLastState().getDistanceToLoggedUser();
        if(u1<u2) return 1;
        else if(u1>u2) return -1;
        return 0;
    }
}

