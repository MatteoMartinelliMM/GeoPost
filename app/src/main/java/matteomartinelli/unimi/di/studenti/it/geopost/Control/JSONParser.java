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
    public static final String USERNAMES = "usernames";
    private static double latitude;
    private static double longitude;
    private static boolean isEmptyUser = false;

    public static User getPersonalProfile(String toParse){
        User u = new User();
        UserState userState = new UserState();
        try {
            JSONObject userToParse = new JSONObject(toParse);
            Iterator<String> userFileds = userToParse.keys();
            while (userFileds.hasNext()){
                if(!isEmptyUser) {
                    String userField = userFileds.next();
                    fillInTheUserField(u, userState, userToParse, userField);
                }else break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public static List<String> getUsernameToFollow(String toParse){
        List<String> toFollow = new ArrayList<String>();
        try {
            JSONObject temp = new JSONObject(toParse);
            JSONArray list = temp.getJSONArray(USERNAMES);
            for(int i= 0 ; i<list.length();i++){
                toFollow.add(list.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toFollow;
    }

    public static List<User> getFollowedUsers(String toParse) {
        isEmptyUser = false;
        List friendList = new ArrayList();
        JSONArray JSONfriendList;
        try {
            JSONObject temp = new JSONObject(toParse);
            JSONfriendList = temp.getJSONArray(FOLLOWED);
            parsingUserByUser(friendList, JSONfriendList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isEmptyUser = false;
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
                if(userMsg.equals("null")){
                    isEmptyUser = true;
                    break;
                }
                userState.setStato(userMsg);
                break;
            case USER_FIELD_LATITUDE:
                String userLatitude = JSONSingleUser.getString(userField);
                latitude = Double.parseDouble(userLatitude);
                userState.setLatitude(latitude);
                break;
            case USER_FIELD_LONGITUDE:
                String userLongitude = JSONSingleUser.getString(userField);
                longitude = Double.parseDouble(userLongitude);
                userState.setLongitude(longitude);
                u.setLastState(userState);
                break;

        }
    }
}
