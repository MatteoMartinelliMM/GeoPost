package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;

/**
 * Created by Teo on 14/01/2018.
 */

public class JSONParser {

    public static final String FOLLOWED = "followed";
    public static final String USER_FIELD_NAME = "username";
    public static final String USER_FIELD_MSG = "msg";
    public static final String USER_FIELD_LATITUDE = "lat";
    public static final String USER_FIELD_LONGITUDE = "lon";
    private static double latitude;
    private static double longitude;
    private static boolean isEmptyUser = false;


    public static List<User> getFollowedUsers(String toParse) {
        List friendList = new ArrayList();
        JSONArray JSONfriendList;
        try {
            JSONObject temp = new JSONObject(toParse);
            JSONfriendList = temp.getJSONArray(FOLLOWED);
            parsingUserByUser(friendList, JSONfriendList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return friendList;
    }

    private static void parsingUserByUser(List friendList, JSONArray JSONfriendList) throws JSONException {
        for(int i = 0 ; i<JSONfriendList.length();i++){
            isEmptyUser = false;
            User u = new User();
            UserState userState = new UserState();
            JSONObject JSONSingleUser = JSONfriendList.getJSONObject(i);
            Iterator<String> userFileds = JSONSingleUser.keys();

            while (userFileds.hasNext()){
                if(!isEmptyUser) {
                    String userField = userFileds.next();
                    fillInTheUserField(u, userState, JSONSingleUser, userField);
                }else break;

            }
            if(!isEmptyUser) friendList.add(u);

        }
    }

    private static void fillInTheUserField(User u, UserState userState, JSONObject JSONSingleUser,  String userField) throws JSONException {

        switch (userField){
            case USER_FIELD_NAME:
                String userName = JSONSingleUser.getString(userField);
                u.setUserName(userName);
                break;
            case USER_FIELD_MSG:
                String userMsg = JSONSingleUser.getString(userField);
                if(userMsg == "null"){
                    isEmptyUser = true;
                    break;
                }
                userState.setStato(userMsg);
                break;
            case USER_FIELD_LATITUDE:
                String userLatitude = JSONSingleUser.getString(userField);
                latitude = Double.parseDouble(userLatitude);
                break;
            case USER_FIELD_LONGITUDE:
                String userLongitude = JSONSingleUser.getString(userField);
                longitude = Double.parseDouble(userLongitude);
                userState.setLatLng(new LatLng(latitude,longitude));
                u.setLastState(userState);
                break;

        }
    }
}
