package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
//import android.app.Fragment;

import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;


import cz.msebera.android.httpclient.Header;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.Geocoding;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.Pager;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RestCall;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.TaskDelegate;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject.USER_BUNDLE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_LOGOUT;

public class PersonalProfileFragment extends Fragment implements TabLayout.OnTabSelectedListener, TaskDelegate, OnMapReadyCallback{
    public static final String LOGGING_OUT = "Logging out...";
    private TextView userName, userState, lastPosition;
    private RecyclerView.LayoutManager lm;
    private boolean done;
    private Context context;
    private Activity currentActitvity;
    private ViewPager statusContainer;
    private TabLayout tabLayout;
    private ProgressDialog dialog;
    private UserBundleToSave userBundle;
    private User loggedUser;
    private TaskDelegate delegate;
    private SupportMapFragment mapFragment;


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
        View v = inflater.inflate(R.layout.fragment_personal_profile, container, false);
        settingXmlViews(v);
        currentActitvity = getActivity();
        context = getActivity();
        if (currentActitvity instanceof OverviewActivity) {
             if( getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
                 ((OverviewActivity) currentActitvity).getSupportActionBar().hide();
             }else if( getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
                 currentActitvity.setTitle("Profile");
                 ((OverviewActivity) currentActitvity).getSupportActionBar().show();
             }
        }

        delegate = this;
        userBundle = (UserBundleToSave) RWObject.readObject(context, USER_BUNDLE);
        if(userBundle!=null)
            loggedUser = userBundle.getPersonalProfile();
        if (loggedUser != null) {
            userName.setText(loggedUser.getUserName());
            if(loggedUser.getLastState()!= null) {
                userState.setText(loggedUser.getLastState().getStato());
                String userLastStatusPosition = Geocoding.getAdressFromCoord(loggedUser.getLastState(), context);
                lastPosition.setText(userLastStatusPosition);
            }
            else userState.setText("");

        }else
            userName.setText(UtilitySharedPreference.getLoggedUsername(context));

        dialog = new ProgressDialog(context);
        setHasOptionsMenu(true);
        if( getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT)
            settingTabLayout();
        else{
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.personalMap);
            mapFragment.getMapAsync(this);
        }
        return v;
    }

    private void settingTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    }

    private void settingXmlViews(View v) {
        userName = v.findViewById(R.id.userName);
        userState = v.findViewById(R.id.userState);
        lastPosition = v.findViewById(R.id.lastPosition);
        statusContainer = v.findViewById(R.id.statusContainer);
        tabLayout = v.findViewById(R.id.tabLayout);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            settingThePager();
        }
    }

    private void settingThePager() {
        Pager adapter = new Pager(getChildFragmentManager(), tabLayout.getTabCount());
        statusContainer.setAdapter(adapter);
        statusContainer.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(this);
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
    public void onPause() {
        super.onPause();
        if( getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT){
            if(statusContainer==null){
                settingThePager();
                settingTabLayout();
            }
            statusContainer.setCurrentItem(0);
        }

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
                String cookie = UtilitySharedPreference.getSavedCookie(context);
                done = UtilitySharedPreference.logoutTheCurrentUser(context);
                if (done) {
                    tryToLogout(cookie);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void tryToLogout(String cookie) {
        dialog.onStart();
        RestCall.get(REL_URL_LOGOUT + cookie, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200)
                    delegate.waitToComplete(LOGGING_OUT);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                delegate.waitToComplete(statusCode + "");
            }
        });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        statusContainer.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void waitToComplete(String s) {
        if (s.equals(LOGGING_OUT)) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            currentActitvity.finish();
        }
    }

    public int getScreenOrientation()
    {
        Display getOrient = currentActitvity.getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
