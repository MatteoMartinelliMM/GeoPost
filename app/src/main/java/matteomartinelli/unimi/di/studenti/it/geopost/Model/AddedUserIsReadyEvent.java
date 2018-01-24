package matteomartinelli.unimi.di.studenti.it.geopost.Model;

/**
 * Created by matteoma on 1/24/2018.
 */

public class AddedUserIsReadyEvent {
    public final boolean addedUserIsReady;

    public AddedUserIsReadyEvent(boolean addedUserIsReady) {
        this.addedUserIsReady = addedUserIsReady;
    }
}
