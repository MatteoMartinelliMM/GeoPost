
package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;


import android.location.Location;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.AutoCompleteTextViewAdapter;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.CalculateFriendsDistance;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.JSONParser;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.MarkerPlacer;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RestCall;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.TaskDelegate;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UserListAdapter;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject.USER_BUNDLE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_FOLLOW;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_LAT;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_LON;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_MESSAGE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_START_NAME;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_STATUS_UPDATE;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_USERNAME;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_USERS;
import static matteomartinelli.unimi.di.studenti.it.geopost.View.MapFragmentContainer.ADD_FRIEND;
import static matteomartinelli.unimi.di.studenti.it.geopost.View.MapFragmentContainer.SEARCH_FRIEND;

public class UsersListFragment extends Fragment implements TaskDelegate {
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
    private ArrayList<String> usernames;
    private AutoCompleteTextViewAdapter ATVAdapter;
    private ProgressDialog dialog;
    private String cookie;
    private TaskDelegate delegate;

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

        setHasOptionsMenu(true);

        searchBar = v.findViewById(R.id.searchBar);

        currentActivity = getActivity();
        context = getActivity();
        final LocationManager manager = (LocationManager) currentActivity.getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        if(currentActivity instanceof OverviewActivity) {
            currentActivity.setTitle("Friend list");;
            ((OverviewActivity) currentActivity).getSupportActionBar().hide();
        }
        dialog = new ProgressDialog(context);
        delegate = this;

        cookie = UtilitySharedPreference.getSavedCookie(context);

        userBundle = new UserBundleToSave();
        friendList = new ArrayList<>();
        personalProfile = new User();
        userBundle = (UserBundleToSave) RWObject.readObject(context, USER_BUNDLE);
        if(userBundle!= null) {
            friendList = userBundle.getFriends();
            personalProfile = userBundle.getPersonalProfile();
        }
        usernames = new ArrayList<>();
        settingTheAdapters(v);
        usingATV();
        return v;

    }

    private void settingTheAdapters(View v) {
        //LISTA AMICI ADAPTER
        userList = v.findViewById(R.id.userList);
        lm = new LinearLayoutManager(context);
        userList.setLayoutManager(lm);
        //TODO IMPLEMENTARE CON ULTIMA POSIZIONE
        Location personalLocation = new Location("MyLocation");
        personalLocation.setLatitude(45.547767);
        personalLocation.setLongitude(9.254693);

        userListAdapter = new UserListAdapter(friendList, personalLocation);
        userList.setAdapter(userListAdapter);

        //ATV ADAPTER
        ATVAdapter = new AutoCompleteTextViewAdapter(context, R.layout.suggestion_element, usernames);
        searchBar.setAdapter(ATVAdapter);
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


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
    }

    private void usingATV() {
        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.onStart();
                String userToadd = (String) ATVAdapter.getItem(i);
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
                if (statusCode == 400) {
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
                    usernames = (ArrayList<String>) JSONParser.getUsernameToFollow(toParse,friendList);
                }

                delegate.waitToComplete(SEARCH_FRIEND);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String s = new String(responseBody);
            }
        });
    }

    @Override
    public void waitToComplete(String restCall) {

        dialog.dismiss();
        dialog.cancel();
        switch (restCall) {
            case SEARCH_FRIEND:
                ATVAdapter = new AutoCompleteTextViewAdapter(context, R.layout.suggestion_element, usernames);
                searchBar.setAdapter(ATVAdapter);
                searchBar.showDropDown();
                break;
            case ADD_FRIEND:
                Toast.makeText(context, "Friend added :)", Toast.LENGTH_SHORT).show();
                searchBar.dismissDropDown();
                searchBar.clearComposingText();
                break;
            default:
                Toast.makeText(context, restCall, Toast.LENGTH_SHORT).show();

        }

    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
