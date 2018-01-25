package matteomartinelli.unimi.di.studenti.it.geopost.Model;

/**
 * Created by matteoma on 1/25/2018.
 */

public class DataAreReadyEvent {
    public final boolean dataReady;

    public DataAreReadyEvent(boolean dataReady) {
        this.dataReady = dataReady;
    }
}
