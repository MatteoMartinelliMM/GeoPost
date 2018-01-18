package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.app.ActionBar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.CalculateFriendsDistance;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.JSONParser;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RestCall;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.TaskDelegate;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_FOLLOWER;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_PROFILE;


public class OverviewActivity extends AppCompatActivity implements TaskDelegate {

    public static final String PROFILE = "profile";
    public static final String MAP_FRAGMENT = "mapFragment";
    public static final String USERS_LIST = "usersList";
    public static final int MIN_DISTANCE = 150;
    private FragmentManager fm;
    private TextView mTextMessage;
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



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_list:

                    if (start) {
                        ft.replace(R.id.fragContainer, listFragment, PROFILE + "|" + MAP_FRAGMENT);
                    } else {
                        ft.add(R.id.fragContainer, listFragment, USERS_LIST);
                    }
                    ft.commit();
                    return true;
                case R.id.navigation_map:
                    mainLayout.setBackgroundColor(getResources().getColor(R.color.defBgColor));

                    MapFragmentContainer mapFragment = new MapFragmentContainer();
                    ft = fm.beginTransaction();
                    if (start)
                        ft.replace(R.id.fragContainer, mapFragment, PROFILE + "|" + USERS_LIST);
                    else
                        ft.add(R.id.fragContainer, mapFragment, MAP_FRAGMENT);
                    ft.commitAllowingStateLoss();
                    return true;
                case R.id.navigation_profile:

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_overview);

        fm = getSupportFragmentManager();
        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setVisibility(View.INVISIBLE);
        mainLayout = findViewById(R.id.container);

        listFragment = new UsersListFragment();
        profileFragment = new PersonalProfileFragment();
        mapFragment = new MapFragmentContainer();
        friendList = new ArrayList<>();
        personalProfile = new User();
        delegate = this;
        String userCookie = UtilitySharedPreference.getSavedCookie(this);
        dialog = new ProgressDialog(this);
        dialog.onStart();
        gettingFriendStatusFromServer(userCookie);
        dialog.onStart();
        gettingPersonalProfileFromServer(userCookie);


    }

    private void gettingPersonalProfileFromServer(String userCookie) {
        RestCall.get(REL_URL_PROFILE + userCookie, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    personalProfileToParse = new String(responseBody);
                    personalProfile = JSONParser.getPersonalProfile(personalProfileToParse);
                    isRecivedProfile = true;
                }
                delegate.waitToComplete(""+statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                delegate.waitToComplete("Error: "+statusCode);
            }
        });
    }

    private void gettingFriendStatusFromServer(String userCookie) {
        RestCall.get(REL_URL_FOLLOWER+userCookie, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    friendListToParse = new String(responseBody);
                    friendList = (ArrayList<User>) JSONParser.getFollowedUsers(friendListToParse);
                    isRecivedList = true;
                }
                delegate.waitToComplete(statusCode+"");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode==400) delegate.waitToComplete("Error "+ statusCode );
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

    private void hidingTheTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void waitToComplete(String s) {
        dialog.dismiss();
        dialog.cancel();
        if(s.equals("200") && isRecivedList && isRecivedProfile){
            friendList = CalculateFriendsDistance.settingForEachUserTheDistanceAndSortTheList(friendList,personalProfile);
            userBundle = new UserBundleToSave();
            userBundle.setFriends(friendList);
            userBundle.setPersonalProfile(personalProfile);
            RWObject.writeObject(this,RWObject.USER_BUNDLE,userBundle);
            UserBundleToSave prova = (UserBundleToSave) RWObject.readObject(this,RWObject.USER_BUNDLE);
            String se  = "CIAO";

        }else if(!s.equals("200"))
            Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
        navigation.setSelectedItemId(R.id.navigation_map);
        start = true;

    }
    public BottomNavigationView getBar(){
        return navigation;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
