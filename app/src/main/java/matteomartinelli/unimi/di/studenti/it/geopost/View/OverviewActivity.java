package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import matteomartinelli.unimi.di.studenti.it.geopost.Model.DataAreReadyEvent;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.RefreshEvent;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.TrackerIsReady;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject.USER_BUNDLE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_FOLLOWER;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_PROFILE;


public class OverviewActivity extends AppCompatActivity implements TaskDelegate, MapFragmentContainer.OnGPSTrackerPass {

    public static final String PROFILE = "profile";
    public static final String MAP_FRAGMENT = "mapFragment";
    public static final String USERS_LIST = "usersList";
    public static final int MIN_DISTANCE = 150;
    public static final String NO_INTERNET = "NO internet connection available :( \n\tLoading local data...";
    public static final String NO_LOCAL_DATA = "NoLocalData";
    public static final String DO_NOT_DISCONNECT = "DoNotDisconnect";
    public static final String SELECTED_FRAGMENT = "SelectedFragment";
    public static final String MAP_FRAGMENT_ADD_NEW_FRIEND = "MapFragmentAddNewFriend";
    public static final String USERLIST_FRAGMENT_ADD_NEW_FRIEND = "UserlistFragmentAddNewFriend";
    public static final String USERLIST_REFRESH_RECYCLER = "Userlist_Refresh_Recycler";
    private FragmentManager fm;
    private boolean start = false;
    private BottomNavigationView navigation;
    private float x1, x2, y1, y2;
    private ProgressDialog dialog;
    private TaskDelegate delegate;
    private String friendListToParse, personalProfileToParse, userCookie;
    private ArrayList<User> friendList;
    private boolean isRecivedList = false, isRecivedProfile = false;
    private User loggedUser;
    ArrayList<UserState> userOldStates;
    private RelativeLayout mainLayout;
    private UserState userLastStateFromLocalData;
    private UserBundleToSave userBundle;
    private int userChoice = 0;
    private ArrayList<Integer> stack;
    private GPSTracker gpsTracker;
    private boolean toAdd = true, doNotDisconnect = false, sendEvent = false, moveCameraToAddedUser = false, isReady=false,doSorting=true;
    private Context context;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    navigation.setVisibility(View.VISIBLE);
                    if (userChoice != 0) restoreDefBgColor();
                    if (toAdd) pushUserChoiceInStack(R.id.navigation_list);
                    UsersListFragment listFragment = new UsersListFragment();
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
                    navigation.setVisibility(View.VISIBLE);
                    if (userChoice != 0) restoreDefBgColor();
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


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().hide();
        context = this;
        setContentView(R.layout.activity_overview);
        stack = new ArrayList<>();
        EventBus.getDefault().register(this);
        fm = getSupportFragmentManager();
        settingXmlWidgets();
        inizalizeUserData();
        loggedUser = new User();
        delegate = this;

        readUserDataFromInternalStorage();

        dialog = new ProgressDialog(this);
        if (CheckNetStatus.isInternetAvailable(this)) {
            dialog.onStart();
            gettingFriendListFromServer(userCookie,null);
            dialog.onStart();
            gettingPersonalProfileFromServer(userCookie);
        } else if (userBundle != null)
            Snackbar.make(mainLayout, NO_INTERNET, Snackbar.LENGTH_SHORT).show();
        else
            Snackbar.make(mainLayout, "No local data available :(", Snackbar.LENGTH_SHORT).show();


    }

    private void readUserDataFromInternalStorage() {
        userCookie = UtilitySharedPreference.getSavedCookie(this);
        userBundle = (UserBundleToSave) RWObject.readObject(this, USER_BUNDLE);
        if (userBundle != null) {
            friendList = userBundle.getFriends();
            loggedUser = userBundle.getPersonalProfile();
            userOldStates = loggedUser.getUserStates();
            userLastStateFromLocalData = loggedUser.getLastState();
        }
    }

    private void inizalizeUserData() {
        friendList = new ArrayList<>();
        loggedUser = new User();
        userOldStates = new ArrayList<>();
        userLastStateFromLocalData = new UserState();
    }


    private void settingXmlWidgets() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setVisibility(View.INVISIBLE);
        mainLayout = findViewById(R.id.container);
    }

    private void gettingPersonalProfileFromServer(String userCookie) {
        RestCall.get(REL_URL_PROFILE + userCookie, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    personalProfileToParse = new String(responseBody);
                    loggedUser = JSONParser.getPersonalProfile(personalProfileToParse);
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

    private void gettingFriendListFromServer(String userCookie, final String savedUsername) {
        RestCall.get(REL_URL_FOLLOWER + userCookie, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    friendListToParse = new String(responseBody);
                    friendList = (ArrayList<User>) JSONParser.getFollowedUsers(friendListToParse,savedUsername,context);
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
            preparingTheUserDataToSave();
            RWObject.writeObject(this, USER_BUNDLE, userBundle);
        } else if (!s.equals("200"))
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

        if (userChoice != 0 && userChoice != R.id.navigation_map) { //La selezione del tab dipende da quale fragment stava utilizzando l'utente prima di ruotare lo schermo
            navigation.setSelectedItemId(userChoice);
        } else
            navigation.setSelectedItemId(R.id.navigation_map);
        start = true;

    }

    private void preparingTheUserDataToSave() {
        userBundle = new UserBundleToSave();
        userBundle.setFriends(friendList);
        if(loggedUser.getLastState()!= null ) {//SE LO STATO SUL SERVER E' PIU aggiornato metto lo stato che era considerato "ultimo" in cima alla lista dei vecchi stati dell' utente (riga sotto)
            if (loggedUser.getLastState().getStato() != userLastStateFromLocalData.getStato() || loggedUser.getLastState().getLongitude() != userLastStateFromLocalData.getLongitude())
                loggedUser.addTheNewOldStatusOnTopOfTheList(userLastStateFromLocalData);
        }
        loggedUser.setUserStates(userOldStates);
        userBundle.setPersonalProfile(loggedUser);
    }

    public BottomNavigationView getBar() {
        return navigation;
    }

    //GESTIONE STORICO NAVIGAZIONE FRAGMENT
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

    private int popCurrentUserChoiceAndTakeTheLastOne() {
        stack.remove(0);
        int choice = stack.get(0);

        return choice;
    }
    //FINE GESTIONE STORICO NAVIGAZIONE FRAMENT


    //SALVO E RECUPERO DATI DA RIPRENDERE DOPO LA ROTAZIONE DELLO SCHERMO
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        userChoice = navigation.getSelectedItemId();
        outState.putInt(SELECTED_FRAGMENT, userChoice);
        outState.putBoolean(DO_NOT_DISCONNECT, doNotDisconnect);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        userChoice = savedInstanceState.getInt(SELECTED_FRAGMENT);
        doNotDisconnect = savedInstanceState.getBoolean(DO_NOT_DISCONNECT);
    }
    //FINE SALVO E RECUPERO DATI DA RIPRENDERE DOPO LA ROTAZIONE DELLO SCHERMO


    @Override
    protected void onStop() {
        MapFragmentContainer mapFragment = new MapFragmentContainer();
        GPSTracker gpsTracker = mapFragment.getGpsTracker();
        if (gpsTracker != null)
            GPSTracker.disconnect();
        doNotDisconnect = false;
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            doNotDisconnect = true;
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            doNotDisconnect = true;
    }

    @Override
    public void onGPSTrackerPass(GPSTracker gpsTracker) {
        this.gpsTracker = gpsTracker;
        if(doSorting){
            friendList = CalculateFriendsDistance.settingForEachUserTheDistanceAndSortTheList(friendList, loggedUser,gpsTracker);
            userBundle.setFriends(friendList);
            RWObject.writeObject(this, USER_BUNDLE, userBundle);
            doSorting=false;
        }
    }

    public GPSTracker getGpsTracker() {
        return gpsTracker;
    }

    public boolean moveCameraToAddedUser() {
        return moveCameraToAddedUser;
    }

    @Subscribe
    public void refreshUserList(RefreshEvent typeOfRefresh) {
        String refreshType = typeOfRefresh.refreshType;
        switch (refreshType) {
            case MAP_FRAGMENT_ADD_NEW_FRIEND:
                sendEvent=true;
                String savedNewFriendName = UtilitySharedPreference.getAddedFriendName(this);
                gettingFriendListFromServer(userCookie,savedNewFriendName);
                break;
            case USERLIST_FRAGMENT_ADD_NEW_FRIEND:
                break;
            case USERLIST_REFRESH_RECYCLER:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public boolean isSendEvent() {
        return sendEvent;
    }
}
