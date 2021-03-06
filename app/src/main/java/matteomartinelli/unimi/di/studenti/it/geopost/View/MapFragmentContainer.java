package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.AutoCompleteTextViewAdapter;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.CalculateFriendsDistance;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.GPSTracker;

import matteomartinelli.unimi.di.studenti.it.geopost.Control.JSONParser;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.MarkerPlacer;

import matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RestCall;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.TaskDelegate;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.PositionEvent;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.RefreshEvent;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.TrackerIsReady;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
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
import static matteomartinelli.unimi.di.studenti.it.geopost.View.OverviewActivity.MAP_FRAGMENT_ADD_NEW_FRIEND;


public class MapFragmentContainer extends Fragment implements OnMapReadyCallback, TaskDelegate, View.OnClickListener {
    public static final String ADD_FRIEND = "AddFriend";
    public static final String SEARCH_FRIEND = "SearchFriend";
    public static final String ADD_STATUS = "AddStatus";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final String HAVE_A_POSITION = "HaveAPosition";
    public static final String GO_TO_ADDED_USER = "GoToAddedUser";

    private View v;
    private Context context;
    private Activity currentActivity;
    private SupportMapFragment mapFragment;
    private ProgressDialog dialog;
    private TaskDelegate delegate;
    private ArrayList<User> friendList;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FloatingActionButton addStatus;
    private UserBundleToSave userBundle;
    private User loggedUser;
    private UserState userLastStateFromLocalData;
    private ArrayList<UserState> userOldStates;
    private RelativeLayout mRelative;
    private PopupWindow addStatusPopUp;
    private EditText newStatus;
    private double latitude, longitude;
    private String userNewStatus, userToadd;
    private GoogleMap gMap;
    private AutoCompleteTextView searchBar;
    private ArrayList<String> usernames;
    private AutoCompleteTextViewAdapter ATVAdapter;
    private String cookie;
    private AlertDialog alert;
    private GPSTracker gpsTracker;
    private boolean positionUpdate = false, permissionGranted = false, moveToSelectedUser = false, moveToAddedUser = false, find = false,doSorting = true;
    private OnGPSTrackerPass GPSTrackerPasser;
    private boolean moveToUserPosition = false;
    private AlertDialog.Builder builder;

    public MapFragmentContainer() {
        // Required empty public constructor
    }

    public interface OnGPSTrackerPass {
        void onGPSTrackerPass(GPSTracker gpsTracker);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate = this;
        usernames = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingTheContextNView(inflater, container);
        gettingUserDataFromLocal();
        accesFineLocattionPermissionChecking();
        checkSelfPermission();
        LocationManager service = (LocationManager) currentActivity.getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled)
            buildAlertMessageNoGps();
        if (permissionGranted)
            gpsTracker = new GPSTracker(context, permissionGranted);
        EventBus.getDefault().register(this);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        return v;
    }


    private void checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Permission granted");
            permissionGranted = true;
        } else {
            Log.d("MainActivity", "Permission NOT granted");
            // we request the permission. When done,
            // the onRequestPermissionsResult method is called
            ActivityCompat.requestPermissions(currentActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void accesFineLocattionPermissionChecking() {
        if (ContextCompat.checkSelfPermission(currentActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    private void settingTheContextNView(LayoutInflater inflater, final ViewGroup container) {
        v = inflater.inflate(R.layout.fragment_map, container, false);

        context = getActivity();
        currentActivity = getActivity();

        if (currentActivity instanceof OverviewActivity) {
            ((OverviewActivity) currentActivity).getBar().setVisibility(View.VISIBLE);
            if (((OverviewActivity) currentActivity).getSupportActionBar().isShowing())
                ((OverviewActivity) currentActivity).getSupportActionBar().hide();
        }

        dialog = new ProgressDialog(context);

        mRelative = v.findViewById(R.id.mRelative);
        searchBar = v.findViewById(R.id.searchBarMap);

        addStatus = v.findViewById(R.id.addStatus);
        addStatus.setOnClickListener(this);

        ATVAdapter = new AutoCompleteTextViewAdapter(context, R.layout.suggestion_element, usernames);
        searchBar.setAdapter(ATVAdapter);
        cookie = UtilitySharedPreference.getSavedCookie(context);
        usingATV();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        OverviewActivity overviewActivity = (OverviewActivity) context;
        moveToSelectedUser = UtilitySharedPreference.isMovingToASpecUser(context);
        moveToAddedUser = overviewActivity.isSendEvent();
        if (moveToAddedUser || moveToSelectedUser) //SE L'UTENTE HA SELEZIONATO UN AMICO DALLA LISTA ALLORA MUOVI LA TELECAMERA SULL AMICO ALTRIMENTI MUOVILA SULL'UTENTE
            moveToUserPosition = false;
        else
            moveToUserPosition = true;
        GPSTrackerPasser = (OnGPSTrackerPass) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        if (gpsTracker.isReady())
            GPSTrackerPasser.onGPSTrackerPass(gpsTracker);
        super.onPause();
    }

    private void gettingUserDataFromLocal() {
        userBundle = new UserBundleToSave();
        friendList = new ArrayList<>();
        loggedUser = new User();
        userBundle = (UserBundleToSave) RWObject.readObject(context, USER_BUNDLE);

        if (userBundle != null) {
            friendList = userBundle.getFriends();
            loggedUser = userBundle.getPersonalProfile();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        permissionGranted = true;
                        gpsTracker.askForNewLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        alert.show();
                        break;
                }
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


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
        boolean isMovedToADifferentLocation = MarkerPlacer.fillInTheMapWithFriendsMarkers(gMap, friendList, UtilitySharedPreference.getAddedFriendName(context));
        if (UtilitySharedPreference.isMovingToASpecUser(context)) {
            LatLng latLng = UtilitySharedPreference.getSavedLatLng(context);
            CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            gMap.animateCamera(zoom);
        }
        if(isMovedToADifferentLocation) moveToAddedUser = false;



    }

    @Override
    public void waitToComplete(String restCall) {
        dialog.dismiss();
        dialog.cancel();
        switch (restCall) {
            case ADD_STATUS:
                UserState toAddInOldStatusList;
                userNewStatus = userNewStatus.replace("+", " ");
                UserState newLastState = new UserState(latitude, longitude, userNewStatus);
                if (loggedUser.getLastState() != null) {
                    userBundle = (UserBundleToSave) RWObject.readObject(context, USER_BUNDLE);
                    loggedUser = userBundle.getPersonalProfile();
                    toAddInOldStatusList = loggedUser.getLastState();
                    loggedUser.addTheNewOldStatusOnTopOfTheList(toAddInOldStatusList);
                }
                loggedUser.setLastState(newLastState);
                userBundle.setPersonalProfile(loggedUser);
                RWObject.writeObject(context, USER_BUNDLE, userBundle);
                MarkerPlacer.addNewStatusMarker(gMap, loggedUser);
                addStatusPopUp.dismiss();
                break;

            case SEARCH_FRIEND:
                ATVAdapter = new AutoCompleteTextViewAdapter(context, R.layout.suggestion_element, usernames);
                searchBar.setAdapter(ATVAdapter);
                searchBar.showDropDown();
                break;

            case ADD_FRIEND:
                Toast.makeText(context, "Friend added :)", Toast.LENGTH_SHORT).show();
                searchBar.dismissDropDown();
                searchBar.clearComposingText();
                EventBus.getDefault().post(new RefreshEvent(MAP_FRAGMENT_ADD_NEW_FRIEND));
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
        addStatusPopUp.showAtLocation(mRelative, Gravity.CENTER, 0, -140);
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
        if(userNewStatus!=null ) {
            userNewStatus = userNewStatus.replace(" ", "+");
            String cookie = UtilitySharedPreference.getSavedCookie(context);
            gpsTracker.askForNewLocation();
            loggedUser.setCurrentLatitude(gpsTracker.getLatitude());
            loggedUser.setCurrentLongitude(gpsTracker.getLongitude());
            latitude = loggedUser.getCurrentLatitude();
            longitude = loggedUser.getCurrentLongitude();
            String sLatitude = String.valueOf(latitude);
            String sLongitude = String.valueOf(longitude);
            dialog.onStart();
            RestCallAddStatus(cookie, sLatitude, sLongitude);
        }
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


    private void usingATV() {
        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.onStart();
                userToadd = (String) ATVAdapter.getItem(i);
                UtilitySharedPreference.saveAddedFriendName(context, userToadd);
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
                    dialog.onStart();
                    RestcallForSearchUsers(editable);
                }

            }
        });

    }

    //************************************************REST CALL*****************************************************************
    private void RestCallAddStatus(String cookie, String sLatitude, String sLongitude) {
        RestCall.post(REL_URL_STATUS_UPDATE + cookie + REL_URL_MESSAGE + userNewStatus + REL_URL_LAT + sLatitude + REL_URL_LON + sLongitude, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200)
                    delegate.waitToComplete(ADD_STATUS);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                delegate.waitToComplete("Error: " + statusCode);
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
                if (statusCode == 400) {
                    String errorResult = new String(responseBody);
                    delegate.waitToComplete(errorResult);
                }
            }
        });
    }


    private void RestcallForSearchUsers(Editable editable) {
        String prova = REL_URL_USERS + cookie + REL_URL_START_NAME + editable.toString();
        String breakPoint = "bresk";
        RestCall.get(REL_URL_USERS + cookie + REL_URL_START_NAME + editable.toString(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    String toParse = new String(responseBody);
                    usernames = (ArrayList<String>) JSONParser.getUsernameToFollow(toParse, friendList);
                }

                delegate.waitToComplete(SEARCH_FRIEND);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String s = new String(responseBody);
                delegate.waitToComplete(s);
            }
        });
    }


    //************************************************END REST CALL*************************************************************
    private void buildAlertMessageNoGps() {
        builder = new AlertDialog.Builder(context);
        builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    } //TODO: fix ask for gps

    @Subscribe
    public void onLocationRecived(PositionEvent positionEvent) {
        if (moveToUserPosition)
            moveCameraToMyPosition(positionEvent);
        else{
            moveToAddedUser = false;
            loggedUser.setCurrentLongitude(gpsTracker.getLongitude());
            loggedUser.setCurrentLatitude(gpsTracker.getLatitude());
            LatLng latLng = gpsTracker.getLatLng();
            gMap.addMarker(new MarkerOptions().position(latLng).title("You are here =)"));

        }
    }

    private void moveCameraToMyPosition(PositionEvent positionEvent) {
        if (!positionUpdate && gMap != null && gpsTracker.isReady()) {
            positionUpdate = true;
            LatLng latLng = positionEvent.latLng;
            loggedUser.setCurrentLongitude(gpsTracker.getLongitude());
            loggedUser.setCurrentLatitude(gpsTracker.getLatitude());
            gMap.addMarker(new MarkerOptions().position(latLng).title("You are here =)"));
            CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            gMap.animateCamera(zoom);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) { //IL MIO FRAGMENT E' STATO DISTRUTTO --> RICONNETTO IL GOOGLE API CLIENT
        checkSelfPermission();
        gpsTracker = new GPSTracker(context, permissionGranted);
        super.onViewStateRestored(savedInstanceState);
    }

    public GPSTracker getGpsTracker() {
        return gpsTracker;
    }

    private void preparingTheUserDataToSave() {
        userBundle = (UserBundleToSave) RWObject.readObject(context, USER_BUNDLE);
        if (userBundle != null) {
            friendList = userBundle.getFriends();
            loggedUser = userBundle.getPersonalProfile();
            userOldStates = loggedUser.getUserStates();
            userLastStateFromLocalData = loggedUser.getLastState();
        }

        userBundle = new UserBundleToSave();
        userBundle.setFriends(friendList);
        if (loggedUser.getLastState() != null) {//SE LO STATO SUL SERVER E' PIU aggiornato metto lo stato che era considerato "ultimo" in cima alla lista dei vecchi stati dell' utente (riga sotto)
            if (loggedUser.getLastState().getStato() != userLastStateFromLocalData.getStato() || loggedUser.getLastState().getLongitude() != userLastStateFromLocalData.getLongitude())
                loggedUser.addTheNewOldStatusOnTopOfTheList(userLastStateFromLocalData);
        }
        loggedUser.setUserStates(userOldStates);
        userBundle.setPersonalProfile(loggedUser);
    }

}

