package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import matteomartinelli.unimi.di.studenti.it.geopost.Control.UserListAdapter;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

public class UsersListFragment extends Fragment {
    private Friend amico1, amico2;
    private ArrayList<Friend> amici;
    private UserState stato1, stato2;
    private UserListAdapter userListAdapter;
    private RecyclerView userList;
    private RecyclerView.LayoutManager lm;
    private AutoCompleteTextView searchBar;
    private Activity currentActivity;
    private Context context;

    public UsersListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_list, container, false);
        init();

        userList = v.findViewById(R.id.userList);
        lm = new LinearLayoutManager(v.getContext());
        userList.setLayoutManager(lm);
        userListAdapter = new UserListAdapter(amici);
        userList.setAdapter(userListAdapter);
        searchBar = v.findViewById(R.id.searchBar);
        searchBar.setVisibility(View.INVISIBLE);
        searchBar.setVisibility(View.GONE);
        setHasOptionsMenu(true);
        currentActivity = getActivity();
        context = getActivity();
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

    private void init() {
        LatLng latLng = new LatLng(45.533674, 9.231393);
        stato1 = new UserState(latLng, "Ciao sono il tuo nuovo amico");
        latLng = new LatLng(45.547904, 9.255229);
        stato2 = new UserState(latLng, "Ciao sono il tuo 2 nuovo amico");
        amico1 = new Friend("CiccioDuPuzzu", stato1);
        amico2 = new Friend("DIO", stato2);
        amici = new ArrayList<Friend>();
        amici.add(amico1);
        amici.add(amico2);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                if (currentActivity instanceof AppCompatActivity) {
                    ((AppCompatActivity) currentActivity).getSupportActionBar().hide();
                    searchBar.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
        return true;
    }
}
