package matteomartinelli.unimi.di.studenti.it.geopost.Control;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by matteoma on 1/20/2018.
 */

public class CheckNetStatus {

    public static boolean isInternetAvailable(Context context) {
        boolean isConnected = false;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


        return isConnected;
    }
}
