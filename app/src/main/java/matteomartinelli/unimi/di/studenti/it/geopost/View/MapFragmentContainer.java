package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import matteomartinelli.unimi.di.studenti.it.geopost.R;


public class MapFragmentContainer extends Fragment implements OnMapReadyCallback{
    View v;
    private MapFragment mapFragment;
    public MapFragmentContainer() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        // Inflate the layout for this fragment
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
