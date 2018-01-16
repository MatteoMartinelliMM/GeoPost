package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import matteomartinelli.unimi.di.studenti.it.geopost.R;


public class PersonalMapFragment extends Fragment implements OnMapReadyCallback {
    private SupportMapFragment personalMap;
    private View v;
    public PersonalMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {
            v = inflater.inflate(R.layout.fragment_personal_map, container, false);
            personalMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.personalMap);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }


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

    }
}
