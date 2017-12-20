package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;

/**
 * Created by utente2.academy on 12/19/2017.
 */

public class UtilitySharedPreference {


    public static final String LOGGED_USER = "LoggedUser";
    public static final String USER_POST = "UserPost";
    public static final String COOKIE = "Cookie";


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

     public static void getSavedCookie(Context context,String cookie){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(COOKIE, cookie);
        editor.commit();
    }



}
