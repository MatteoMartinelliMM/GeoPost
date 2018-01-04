package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;

/**
 * Created by Teo on 04/01/2018.
 */

public class Geocoding {

    public static String getAdressFromCoord(UserState stato, Context context){
        String address = " ";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context ,Locale.getDefault());
        LatLng temp = stato.getLatLng();
        double lat = temp.latitude;
        double lon = temp.longitude;
        try {
            addresses = geocoder.getFromLocation(lat,lon,1);
            address = addresses.get(0).getAddressLine(0)+" ";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
