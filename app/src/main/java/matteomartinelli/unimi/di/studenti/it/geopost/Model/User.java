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
    private double currentLatitude;
    private double currentLongitude;

    public User(String userName, ArrayList<UserState> userStates) {
        this.userName = userName;
        this.userStates = userStates;


    }

    public User() {
        userStates = new ArrayList<>();
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

    public void addTheNewOldStatusOnTopOfTheList(UserState newOld){
         userStates.add(0,newOld);
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    @Override
    public int compareTo(@NonNull User o) {
        return Float.compare(this.getLastState().getDistanceToLoggedUser(),o.getLastState().getDistanceToLoggedUser());
    }


}

