package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.drm.DrmStore;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;


public class OverviewActivity extends AppCompatActivity {

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
    private MapFragment mapFragment;
    private float x1, x2, y1, y2;
    private ActionBar actionBar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    getSupportActionBar().show();
                    if (start) {
                        ft.replace(R.id.fragContainer, listFragment, PROFILE + "|" + MAP_FRAGMENT);
                    } else {
                        ft.add(R.id.fragContainer, listFragment, USERS_LIST);
                    }
                    ft.commit();
                    return true;
                case R.id.navigation_map:
                    getSupportActionBar().hide();
                    MapFragment mapFragment = new MapFragment();
                    ft = fm.beginTransaction();
                    if (start)
                        ft.replace(R.id.fragContainer, mapFragment, PROFILE + "|" + USERS_LIST);
                    else
                        ft.add(R.id.fragContainer, mapFragment, MAP_FRAGMENT);
                    ft.commit();
                    return true;
                case R.id.navigation_profile:
                    getSupportActionBar().show();
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
        setContentView(R.layout.activity_overview);
        fm = getFragmentManager();
        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        listFragment = new UsersListFragment();
        profileFragment = new PersonalProfileFragment();
        mapFragment = new MapFragment();

        navigation.setSelectedItemId(R.id.navigation_map);
        start = true;
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
}
