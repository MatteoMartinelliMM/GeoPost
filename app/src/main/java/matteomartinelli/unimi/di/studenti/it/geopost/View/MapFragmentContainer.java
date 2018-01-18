package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;


import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.AutoCompleteTextViewAdapter;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.JSONParser;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.MarkerPlacer;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RestCall;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.TaskDelegate;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.widget.PopupWindow.INPUT_METHOD_NEEDED;
import static matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject.USER_BUNDLE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_FOLLOW;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_LAT;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_LON;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_MESSAGE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_START_NAME;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_STATUS_UPDATE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_USERNAME;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_USERS;


public class MapFragmentContainer extends Fragment implements OnMapReadyCallback, TaskDelegate, View.OnClickListener {
    public static final String ADD_FRIEND = "AddFriend";
    public static final String SEARCH_FRIEND = "SearchFriend";
    public static final String ADD_STATUS = "AddStatus";
    View v;
    private Context context;
    private Activity currentActivity;
    private SupportMapFragment mapFragment;
    private ProgressDialog dialog;
    private TaskDelegate delegate;
    private ArrayList<User> friendList;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FloatingActionButton addStatus;
    private String toParse;
    private UserBundleToSave userBundle;
    private User personalProfile;
    private RelativeLayout mRelative;
    private PopupWindow addStatusPopUp;
    private EditText newStatus;
    private double latitude, longitude;
    private String userNewStatus;
    private GoogleMap gMap;
    private boolean moveUp = false;
    private AutoCompleteTextView searchBar;
    private ArrayList<String> usernames, usernamesTemp;
    private AutoCompleteTextViewAdapter ATVAdapter;
    private String cookie;

    public MapFragmentContainer() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendList = new ArrayList<>();
        delegate = this;
        usernames = new ArrayList<>();
        usernamesTemp = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingTheContextNView(inflater, container);

        // Inflate the layout for this fragment
        return v;
    }

    private void settingTheContextNView(LayoutInflater inflater, final ViewGroup container) {
        v = inflater.inflate(R.layout.fragment_map, container, false);
        addStatus = v.findViewById(R.id.addStatus);
        addStatus.setOnClickListener(this);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        context = getActivity();
        currentActivity = getActivity();
        dialog = new ProgressDialog(context);
        mRelative = v.findViewById(R.id.mRelative);
        searchBar = v.findViewById(R.id.searchBarMap);
        cookie = UtilitySharedPreference.getSavedCookie(context);
        ATVAdapter = new AutoCompleteTextViewAdapter(context, R.layout.suggestion_element, usernames);
        searchBar.setAdapter(ATVAdapter);
        usingATV();

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        askingForUsersGpsPermission();
        userBundle = new UserBundleToSave();
        friendList = new ArrayList<>();
        personalProfile = new User();
        userBundle = (UserBundleToSave) RWObject.readObject(context, USER_BUNDLE);
        if (userBundle != null) {
            friendList = userBundle.getFriends();
            personalProfile = userBundle.getPersonalProfile();
        }
        mapFragment.getMapAsync(this);



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // gps-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(new LatLng(45.533674, 9.254575), 15);
        gMap.moveCamera(zoom);
        gMap.animateCamera(zoom);
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.533674, 9.254575)).title("Hello Map"));
        MarkerPlacer.fillInTheMapWithFriendsMarkers(gMap, friendList);

    }

    @Override
    public void waitToComplete(String restCall) {
        dialog.dismiss();
        dialog.cancel();
        switch (restCall) {
            case ADD_STATUS:
                UserState toAddInOldStatusList = personalProfile.getLastState();
                personalProfile.addTheNewOldStatusOnTopOfTheList(toAddInOldStatusList);
                UserState newLastState = new UserState(latitude, longitude, userNewStatus);
                personalProfile.setLastState(newLastState);
                MarkerPlacer.addNewStatusMarker(gMap, personalProfile);
                userBundle.setPersonalProfile(personalProfile);
                RWObject.writeObject(context, USER_BUNDLE, userBundle);
                addStatusPopUp.dismiss();
                moveUp = false;
                break;
            case SEARCH_FRIEND:
                ATVAdapter = new AutoCompleteTextViewAdapter(context, R.layout.suggestion_element, usernames);
                searchBar.setAdapter(ATVAdapter);
                searchBar.showDropDown();
                break;
            case ADD_FRIEND:
                Toast.makeText(context,"Friend added :)",Toast.LENGTH_SHORT).show();
                searchBar.dismissDropDown();
                searchBar.clearComposingText();
                break;
            default:
                Toast.makeText(context, restCall, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View v) {
        showingAndUsingPopupWindow();
    }

    private void showingAndUsingPopupWindow() {
        settingThePopUpWindow();
    }

    private void settingThePopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.add_status, null);
        ImageButton send = customView.findViewById(R.id.submit);

        addStatusPopUp = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addStatusPopUp.setInputMethodMode(INPUT_METHOD_NEEDED);
        newStatus = customView.findViewById(R.id.status);
        addStatusPopUp.setFocusable(true);
        addStatusPopUp.update();
        addStatusPopUp.showAtLocation(mRelative, Gravity.CENTER, 0, 0);
        displayingTextCounter(customView);
        if (Build.VERSION.SDK_INT >= 21) {
            addStatusPopUp.setElevation(8.0f);
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTheNewStatusToServer();
            }
        });

    }

    private void sendTheNewStatusToServer() {
        userNewStatus = newStatus.getText().toString();
        String cookie = UtilitySharedPreference.getSavedCookie(context);
        latitude = 15.7;
        longitude = 12.4;
        String sLatitude = String.valueOf(latitude);
        String sLongitude = String.valueOf(longitude);
        dialog.onStart();
        RestCall.post(REL_URL_STATUS_UPDATE + cookie + REL_URL_MESSAGE + userNewStatus + REL_URL_LAT + sLatitude + REL_URL_LON + sLongitude, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200)
                    delegate.waitToComplete(ADD_STATUS);
                delegate.waitToComplete(ADD_STATUS);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                delegate.waitToComplete("Error: " + statusCode);
            }
        });

    }

    private void displayingTextCounter(View customView) {
        final TextView charactersTextCount = customView.findViewById(R.id.charactersCount);
        newStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charactersTextCount.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void askingForUsersGpsPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                  ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        }
    }

    private void usingATV(){
        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.onStart();
                String userToadd = (String) ATVAdapter.getItem(i);
                searchBar.clearListSelection();
                searchBar.clearComposingText();
                RestCallAddUser(userToadd);

            }
        });
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                searchBar.setTag(true);
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("") && (boolean) searchBar.getTag()) {
                    String r = editable.toString();
                    String val = cookie;
                    dialog.onStart();
                    RestcallForSearchUsers(editable);
                }

            }
        });

    }

    private void RestCallAddUser(String userToadd) {
        RestCall.get(REL_URL_FOLLOW + cookie + REL_URL_USERNAME + userToadd, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200)
                    delegate.waitToComplete(ADD_FRIEND);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode==400){
                    String errorResult = new String(responseBody);
                    delegate.waitToComplete(errorResult);
                }
            }
        });
    }

    private void RestcallForSearchUsers(Editable editable) {
        RestCall.get(REL_URL_USERS + cookie + REL_URL_START_NAME + editable.toString(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    String toParse = new String(responseBody);
                    usernames = (ArrayList<String>) JSONParser.getUsernameToFollow(toParse);
                }

                delegate.waitToComplete(SEARCH_FRIEND);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String s = new String(responseBody);
                String bo = "";
            }
        });
    }
}
