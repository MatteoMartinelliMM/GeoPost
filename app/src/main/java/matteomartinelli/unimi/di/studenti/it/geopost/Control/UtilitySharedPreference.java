package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;

/**
 * Created by utente2.academy on 12/19/2017.
 */

public class UtilitySharedPreference {


    public static final String LOGGED_USER = "LoggedUser";
    public static final String USER_POST = "UserPost";
    public static final String COOKIE = "Cookie";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String MOVE_THE_CAMERA = "MoveTheCamera";


    public static boolean checkIfUserIsLogged(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains(LOGGED_USER);
    }

    public static void addLoggedUser(Context context, User u) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGGED_USER, u.getUserName());
        editor.putString(COOKIE, u.getCookie());
        editor.commit();
    }

    public static String getLoggedUsername(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(LOGGED_USER, "");
    }

     public static String getSavedCookie(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(COOKIE,"");
    }


    public static boolean logoutTheCurrentUser(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        return !checkIfUserIsLogged(context);
    }

    public static void saveLatLngToPass(Context context,double lat, double lon){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String sLat = String.valueOf(lat);
        String sLng = String.valueOf(lon);
        Log.i("LatLng",sLat+sLng);
        editor.putString(LATITUDE,sLat);
        editor.putString(LONGITUDE,sLng);
        editor.putBoolean(MOVE_THE_CAMERA,true);
        editor. commit();
    }

    public static LatLng getSavedLatLng(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        double lat=-1000,lng=-1000;
        String sLat = preferences.getString(LATITUDE,"");
        String sLng = preferences.getString(LONGITUDE,"");
        if(!(sLat.equals("") && sLng.equals(""))) {
            lat = Double.parseDouble(sLat);
            lng = Double.parseDouble(sLng);
        }
        return new LatLng(lat,lng);
    }

    public static boolean isMovingToASpecUser(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(MOVE_THE_CAMERA, false);
    }



}
