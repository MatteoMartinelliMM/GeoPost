package matteomartinelli.unimi.di.studenti.it.geopost.Model;

/**
 * Created by matteoma on 1/24/2018.
 */

public class MapFragmentRefreshMapEvent {
    public final User u;
    public final int position;


    public MapFragmentRefreshMapEvent(User u,int position) {
        this.u = u;
        this.position = position;
    }
}
