package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by utente2.academy on 12/19/2017.
 */

public class User {
    private String userName;
    private String ultimoMessaggio;
    private ArrayList<UserState> userStates;
    private String cookie;
    private LatLng currentPosition;
    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public User(String userName, String ultimoMessaggio, ArrayList<UserState> userStates, String cookie) {
        this.userName = userName;
        this.ultimoMessaggio = ultimoMessaggio;
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

    public String getUltimoMessaggio() {
        return ultimoMessaggio;
    }

    public void setUltimoMessaggio(String ultimoMessaggio) {
        this.ultimoMessaggio = ultimoMessaggio;
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
}
