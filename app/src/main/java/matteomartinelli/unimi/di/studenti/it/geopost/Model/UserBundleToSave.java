package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by teo on 14/01/18.
 */

public class UserBundleToSave implements Serializable{
    private ArrayList<User> friends;
    private User personalProfile;

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }
}
