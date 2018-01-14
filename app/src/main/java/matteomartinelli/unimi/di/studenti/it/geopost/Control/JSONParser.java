package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;

/**
 * Created by Teo on 14/01/2018.
 */

public class JSONParser {
    public static List<User> getFollowedUsers(String toParse) {
        List friendList = new ArrayList();
        JSONArray JSONfriendList;
        try {
            JSONfriendList = new JSONArray(toParse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return friendList;
    }
}
