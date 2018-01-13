package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


import matteomartinelli.unimi.di.studenti.it.geopost.Control.Geocoding;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UserStateAdapter;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.Friend;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

public class PersonalProfileFragment extends Fragment {
    private UserState stato1,stato2,stato3,stato4,stato5;
    private ArrayList<UserState> states;
    private TextView userName,userState, lastPosition;
    private RecyclerView storyLine;
    private RecyclerView.LayoutManager lm;
    private UserStateAdapter userStateAdapter;
    private boolean done;
    private Context context;
    private Activity currentActitvity;
   public PersonalProfileFragment() {
        // Required empty public constructor
    }

  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_personal_profile, container, false);
        userName = v.findViewById(R.id.userName);
        userState = v.findViewById(R.id.userState);
        lastPosition = v.findViewById(R.id.lastPosition);
        storyLine = v.findViewById(R.id.oldPost);
        currentActitvity = getActivity();
        context = getActivity();
        lm = new LinearLayoutManager(v.getContext());
        init();
        if(!states.isEmpty()){
            UserState stato = states.get(0);
            userState.setText(stato.getStato());
            String address = Geocoding.getAdressFromCoord(stato,getActivity());
            lastPosition.setText(address);
            storyLine.setLayoutManager(lm);
            userStateAdapter = new UserStateAdapter(states,getActivity());
            storyLine.setAdapter(userStateAdapter);
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
    private void init(){
        states = new ArrayList<>();
        LatLng latLng = new LatLng(45.533674, 11.231393);
        stato1 = new UserState(latLng,"Ciao questo è il mio primo stato");
        latLng = new LatLng(45.547904, 9.255229);
        stato2 = new UserState(latLng,"Ciao questo è il mio secondo stato");
        latLng = new LatLng(45.533674, 12.231393);
        stato3 = new UserState(latLng,"Ciao questo è il mio terzo stato");
        latLng = new LatLng(45.533674, 6.231393);
        stato4 = new UserState(latLng,"Buongiorno a tutti questo è bensì già il mio 4 stato nonostante non m" +
                "i piacciano i social puntoi virgola fine");
        latLng = new LatLng(44.533674, 11.231393);
        stato5 = new UserState(latLng,"ei bebi");
        states.add(0,stato1);
        states.add(0,stato2);
        states.add(0,stato3);
        states.add(0,stato4);
        states.add(0,stato5);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_logout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                done = UtilitySharedPreference.logoutTheCurrentUser(context);
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                currentActitvity.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
