package matteomartinelli.unimi.di.studenti.it.geopost.Model;

/**
 * Created by utente2.academy on 12/19/2017.
 */

public class User {
    private String userName;
    private String ultimoMessaggio;
    private String posizione; //TODO: trovare dataType coerente
    private String cookie;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public User(String userName, String ultimoMessaggio, String posizione, String cookie) {
        this.userName = userName;
        this.ultimoMessaggio = ultimoMessaggio;
        this.posizione = posizione;
        this.cookie = cookie;

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
