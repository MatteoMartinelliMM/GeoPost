package matteomartinelli.unimi.di.studenti.it.geopost.Model;

/**
 * Created by utente2.academy on 12/20/2017.
 */

public class Friend {
    private String username;
    private UserState stato;

    public Friend(String username, UserState stato) {
        this.username = username;
        this.stato = stato;
    }

    public Friend() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserState getStato() {
        return stato;
    }

    public void setStato(UserState stato) {
        this.stato = stato;
    }


}
