package matteomartinelli.unimi.di.studenti.it.geopost.Model;

/**
 * Created by utente2.academy on 12/19/2017.
 */

public class User {
    private String userName;
    private String ultimoMessaggio;
    private String posizione; //TODO: trovare dataType coerente

    public User(String userName, String ultimoMessaggio, String posizione) {
        this.userName = userName;
        this.ultimoMessaggio = ultimoMessaggio;
        this.posizione = posizione;
    }

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUltimoMessaggio() {
        return ultimoMessaggio;
    }

    public void setUltimoMessaggio(String ultimoMessaggio) {
        this.ultimoMessaggio = ultimoMessaggio;
    }

    public String getPosizione() {
        return posizione;
    }

    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }
}
