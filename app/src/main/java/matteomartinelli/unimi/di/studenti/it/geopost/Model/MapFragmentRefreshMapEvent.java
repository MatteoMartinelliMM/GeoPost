package matteomartinelli.unimi.di.studenti.it.geopost.Model;

/**
 * Created by matteoma on 1/24/2018.
 */

public class MapFragmentRefreshMapEvent {
    public final String doRefresh;

    public MapFragmentRefreshMapEvent(String doRefresh) {
        this.doRefresh = doRefresh;
    }
}