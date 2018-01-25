package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.content.Context;

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
    public static final String FRIEND_ALREADY_ADDED = " (Friend already added)";
    private static double latitude;
    private static double longitude;
    private static boolean isEmptyUser = false;
    public static User getPersonalProfile(String toParse) {
        User u = new User();
        UserState userState = new UserState();
        try {
            JSONObject userToParse = new JSONObject(toParse);
            Iterator<String> userFileds = userToParse.keys();
            while (userFileds.hasNext()) {
                if (!isEmptyUser) {
                    String userField = userFileds.next();
                    fillInTheUserField(u, userState, userToParse, userField,null,0,null,false);
                } else break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public static List<String> getUsernameToFollow(String toParse, ArrayList<User> friendList) {
        List<String> toFollow = new ArrayList<String>();
        try {
            boolean alreadyFriend = false;
            JSONObject temp = new JSONObject(toParse);
            JSONArray list = temp.getJSONArray(USERNAMES);
            for (int i = 0; i < list.length(); i++) {
                String user = list.getString(i);
                for (User u : friendList) {
                    if(u.getUserName().equals(user)){
                        alreadyFriend = true;
                        break;
                    }
                }
                if(!alreadyFriend)
                    toFollow.add(list.getString(i));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toFollow;
    }

    public static List<User> getFollowedUsers(String toParse, String savedUsername, Context context) {
        isEmptyUser = false;
        List friendList = new ArrayList();
        JSONArray JSONfriendList;
        int count=0;
        try {
            JSONObject temp = new JSONObject(toParse);
            JSONfriendList = temp.getJSONArray(FOLLOWED);
            parsingUserByUser(friendList, JSONfriendList,count,savedUsername,context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isEmptyUser = false;
        return friendList;
    }

    private static void parsingUserByUser(List friendList, JSONArray JSONfriendList,int count,String savedUsername,Context context) throws JSONException {
        boolean found = false;
        for (int i = 0; i < JSONfriendList.length(); i++) {
            isEmptyUser = false;
            User u = new User();
            UserState userState = new UserState();
            JSONObject JSONSingleUser = JSONfriendList.getJSONObject(i);
            Iterator<String> userFileds = JSONSingleUser.keys();
            count++;
            while (userFileds.hasNext()) {
                if (!isEmptyUser) {
                    String userField = userFileds.next();
                    fillInTheUserField(u, userState, JSONSingleUser, userField,savedUsername,count, context,found);
                } else break;

            }
            if (!isEmptyUser) friendList.add(u);


        }
    }

    private static void fillInTheUserField(User u, UserState userState, JSONObject JSONSingleUser, String userField,String savedUsername,int count, Context context,boolean found) throws JSONException {

        switch (userField) {
            case USER_FIELD_NAME:
                String userName = JSONSingleUser.getString(userField);

                if(savedUsername!=null && savedUsername.equals(userName))
                    found = true;
                u.setUserName(userName);
                break;
            case USER_FIELD_MSG:
                String userMsg = JSONSingleUser.getString(userField);
                if (userMsg.equals("null")) {
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
