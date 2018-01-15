package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by matteoma on 1/15/2018.
 */

public class UserBundleToSave implements Serializable {
    private User personalProfile;

    private ArrayList<User> friends;

    public UserBundleToSave() {
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public User getPersonalProfile() {
        return personalProfile;
    }

    public void setPersonalProfile(User personalProfile) {
        this.personalProfile = personalProfile;
    }
}
