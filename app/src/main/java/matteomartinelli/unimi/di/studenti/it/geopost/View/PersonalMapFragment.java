package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import matteomartinelli.unimi.di.studenti.it.geopost.Control.MarkerPlacer;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.PositionEvent;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.R;


public class PersonalMapFragment extends Fragment implements OnMapReadyCallback {
    private View v;
    private SupportMapFragment personalMapFragment;
    private GoogleMap gMap;
    private User loggedUser;

    public PersonalMapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_personal_map, container, false);
        personalMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.myMapFragment);
        personalMapFragment.getMapAsync(this);
        PersonalProfileFragment parent = (PersonalProfileFragment) getParentFragment();
        loggedUser = parent.getLoggedUser();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        MarkerPlacer.fillIntTheMapWithUserStatus(gMap, loggedUser);
        moveCameraToLastState(loggedUser);

    }

    private void moveCameraToLastState(User loggedUser) {
        if (loggedUser != null && loggedUser.getLastState()!=null) {
            double lat = loggedUser.getLastState().getLatitude();
            double lon = loggedUser.getLastState().getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            gMap.moveCamera(zoom);
            gMap.animateCamera(zoom);
        }
    }
}
