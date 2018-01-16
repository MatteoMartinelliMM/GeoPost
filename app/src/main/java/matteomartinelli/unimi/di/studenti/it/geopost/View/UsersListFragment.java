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


import android.location.Location;

import matteomartinelli.unimi.di.studenti.it.geopost.Control.CalculateFriendsDistance;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UserListAdapter;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject.USER_BUNDLE;

public class UsersListFragment extends Fragment {
    private User personalProfile;
    private ArrayList<User> friendList;
    private UserBundleToSave userBundle;
    private UserListAdapter userListAdapter;
    private RecyclerView userList;
    private RecyclerView.LayoutManager lm;
    private AutoCompleteTextView searchBar;
    private Activity currentActivity;
    private Context context;
    private View v;
    public UsersListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_users_list, container, false);

        hidingTheSearchBar(v);

        setHasOptionsMenu(true);

        currentActivity = getActivity();
        context = getActivity();

        return v;

    }

    private void hidingTheSearchBar(View v) {
        searchBar = v.findViewById(R.id.searchBar);
        searchBar.setVisibility(View.INVISIBLE);
        searchBar.setVisibility(View.GONE);
    }

    private void settingTheAdapter(View v) {
        userList = v.findViewById(R.id.userList);
        lm = new LinearLayoutManager(v.getContext());
        userList.setLayoutManager(lm);
        Location personalLocation = new Location("MyLocation");
        personalLocation.setLatitude(45.547767);
        personalLocation.setLongitude(9.254693);

        userListAdapter = new UserListAdapter(friendList,personalLocation);
        userList.setAdapter(userListAdapter);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userBundle = new UserBundleToSave();
        friendList = new ArrayList<>();
        personalProfile = new User();
        userBundle = (UserBundleToSave) RWObject.readObject(context, USER_BUNDLE);
        friendList = userBundle.getFriends();
        personalProfile = userBundle.getPersonalProfile();
        settingTheAdapter(v);


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
