package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.app.ActionBar;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.CalculateFriendsDistance;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.CheckNetStatus;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.GPSTracker;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.JSONParser;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RestCall;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.TaskDelegate;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject.USER_BUNDLE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_FOLLOWER;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_PROFILE;


public class OverviewActivity extends AppCompatActivity implements TaskDelegate {

    public static final String PROFILE = "profile";
    public static final String MAP_FRAGMENT = "mapFragment";
    public static final String USERS_LIST = "usersList";
    public static final int MIN_DISTANCE = 150;
    public static final String NO_INTERNET = "NO internet connection available :( \n\tLoading local data...";
    public static final String NO_LOCAL_DATA = "NoLocalData";
    public static final String DO_NOT_DISCONNECT = "DoNotDisconnect";
    public static final String SELECTED_FRAGMENT = "SelectedFragment";
    private FragmentManager fm;
    private boolean start = false;
    private BottomNavigationView navigation;
    private UsersListFragment listFragment;
    private PersonalProfileFragment profileFragment;
    private MapFragmentContainer mapFragment;
    private float x1, x2, y1, y2;
    private ActionBar actionBar;
    private ProgressDialog dialog;
    private TaskDelegate delegate;
    private String friendListToParse, personalProfileToParse;
    private ArrayList<User> friendList;
    private boolean isRecivedList = false, isRecivedProfile = false;
    private User personalProfile;
    private RelativeLayout mainLayout;
    private UserBundleToSave userBundle;
    private int userChoice = 0;
    private ArrayList<Integer> stack;
    private boolean toAdd = true, doNotDisconnect = false;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    //if(userChoice!=0)   restoreDefBgColor();
                    if (toAdd) pushUserChoiceInStack(R.id.navigation_list);
                    if (start) {
                        ft.replace(R.id.fragContainer, listFragment, PROFILE + "|" + MAP_FRAGMENT);
                    } else {
                        ft.add(R.id.fragContainer, listFragment, USERS_LIST);
                    }
                    ft.commit();
                    return true;
                case R.id.navigation_map:
                    restoreDefBgColor();
                    MapFragmentContainer mapFragment = new MapFragmentContainer();
                    ft = fm.beginTransaction();
                    if (start)
                        ft.replace(R.id.fragContainer, mapFragment, PROFILE + "|" + USERS_LIST);
                    else
                        ft.add(R.id.fragContainer, mapFragment, MAP_FRAGMENT);
                    ft.commitAllowingStateLoss();
                    return true;
                case R.id.navigation_profile:
                    //if(userChoice!=0) restoreDefBgColor();
                    if (toAdd) pushUserChoiceInStack(R.id.navigation_profile);
                    PersonalProfileFragment profileFragment = new PersonalProfileFragment();
                    ft = fm.beginTransaction();
                    if (start)
                        ft.replace(R.id.fragContainer, profileFragment, USERS_LIST + "|" + MAP_FRAGMENT);
                    else
                        ft.add(R.id.fragContainer, profileFragment, PROFILE);
                    ft.commit();
                    return true;
            }
            return false;
        }
    };

    private void restoreDefBgColor() {
        mainLayout.setBackgroundColor(getResources().getColor(R.color.defBgColor));
        if (toAdd) pushUserChoiceInStack(R.id.navigation_map);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_overview);
        stack = new ArrayList<>();
        fm = getSupportFragmentManager();
        settingXmlWidgets();
        inizalizeTheFragments();
        friendList = new ArrayList<>();
        personalProfile = new User();
        delegate = this;
        String userCookie = UtilitySharedPreference.getSavedCookie(this);

        dialog = new ProgressDialog(this);
        if (CheckNetStatus.isInternetAvailable(this)) {
            dialog.onStart();
            gettingFriendListFromServer(userCookie);
            dialog.onStart();
            gettingPersonalProfileFromServer(userCookie);
        } else {
            dialog.onStart();
            Snackbar.make(mainLayout, NO_INTERNET, Snackbar.LENGTH_SHORT).show();
            userBundle = (UserBundleToSave) RWObject.readObject(this, USER_BUNDLE);
            if (userBundle != null) {
                delegate.waitToComplete("");
                friendList = userBundle.getFriends();
                personalProfile = userBundle.getPersonalProfile();
            } else
                delegate.waitToComplete(NO_LOCAL_DATA);
        }


    }

    private void inizalizeTheFragments() {
        listFragment = new UsersListFragment();
        profileFragment = new PersonalProfileFragment();
        mapFragment = new MapFragmentContainer();
    }

    private void settingXmlWidgets() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setVisibility(View.INVISIBLE);
        mainLayout = findViewById(R.id.container);
    }

    private void gettingPersonalProfileFromServer(String userCookie) {
        String s = REL_URL_PROFILE + userCookie;
        RestCall.get(REL_URL_PROFILE + userCookie, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    personalProfileToParse = new String(responseBody);
                    personalProfile = JSONParser.getPersonalProfile(personalProfileToParse);
                    isRecivedProfile = true;
                }
                delegate.waitToComplete("" + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                delegate.waitToComplete("Error: " + statusCode);
            }
        });
    }

    private void gettingFriendListFromServer(String userCookie) {
        RestCall.get(REL_URL_FOLLOWER + userCookie, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    friendListToParse = new String(responseBody);
                    friendList = (ArrayList<User>) JSONParser.getFollowedUsers(friendListToParse);
                    isRecivedList = true;
                }
                delegate.waitToComplete(statusCode + "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 400) delegate.waitToComplete("Error " + statusCode);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                float deltaY = Math.abs(y2 - y1);
                float deltaX = Math.abs(x2 - x1);
                if (deltaX > MIN_DISTANCE && deltaY < MIN_DISTANCE) {
                    if (x1 > x2) {
                        switch (navigation.getSelectedItemId()) {
                            case R.id.navigation_map:
                                navigation.setSelectedItemId(R.id.navigation_profile);
                                break;
                            case R.id.navigation_list:
                                navigation.setSelectedItemId(R.id.navigation_map);
                                break;
                        }

                    } else {
                        switch (navigation.getSelectedItemId()) {
                            case R.id.navigation_map:
                                navigation.setSelectedItemId(R.id.navigation_list);
                                break;
                            case R.id.navigation_profile:
                                navigation.setSelectedItemId(R.id.navigation_map);
                                break;
                        }
                    }
                }

        }
        return super.onTouchEvent(event);
    }

    @Override
    public void waitToComplete(String s) {
        dialog.dismiss();
        dialog.cancel();
        if (s.equals("200") && isRecivedList && isRecivedProfile) {
            friendList = CalculateFriendsDistance.settingForEachUserTheDistanceAndSortTheList(friendList, personalProfile);
            userBundle = new UserBundleToSave();
            userBundle.setFriends(friendList);
            userBundle.setPersonalProfile(personalProfile);
            RWObject.writeObject(this, USER_BUNDLE, userBundle);

        } else if (!s.equals("200"))
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        else if(NO_LOCAL_DATA.equals(s)) {
            Toast.makeText(this,"Data are not available",Toast.LENGTH_SHORT).show();
        }
        /*if(userChoice!=0 && userChoice!=R.id.navigation_map){
            navigation.setSelectedItemId(userChoice);
        }else*/ //TODO: vedere perchè scompare bottom nav
            navigation.setSelectedItemId(R.id.navigation_map);
        start = true;

    }

    public BottomNavigationView getBar() {
        return navigation;
    }

    @Override
    public void onBackPressed() {
        if (stack.size() == 1)
            super.onBackPressed();
        else {
            toAdd = false;
            navigation.setSelectedItemId((popCurrentUserChoiceAndTakeTheLastOne()));
            toAdd = true;
        }
    }

    private void pushUserChoiceInStack(int selectedId) {
        stack.add(0, selectedId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        userChoice = navigation.getSelectedItemId();
        outState.putInt(SELECTED_FRAGMENT,userChoice);
        outState.putBoolean(DO_NOT_DISCONNECT,doNotDisconnect);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        userChoice =  savedInstanceState.getInt(SELECTED_FRAGMENT);
        doNotDisconnect = savedInstanceState.getBoolean(DO_NOT_DISCONNECT);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private int popCurrentUserChoiceAndTakeTheLastOne() {
        stack.remove(0);
        int choice = stack.get(0);

        return choice;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        GPSTracker gpsTracker = mapFragment.getGpsTracker();
        if(gpsTracker!=null)
            GPSTracker.disconnect();
        doNotDisconnect = false;
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            doNotDisconnect = true;
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) doNotDisconnect = true;
    }

    public BottomNavigationView getNavigation() {
        return navigation;
    }
}
