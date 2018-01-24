package matteomartinelli.unimi.di.studenti.it.geopost.Model;

/**
 * Created by matteoma on 1/24/2018.
 */

public class MapIsReadyEvent {
    public final boolean isMapReady;

    public MapIsReadyEvent(boolean isMapReady) {
        this.isMapReady = isMapReady;
    }
}
