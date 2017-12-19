package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by utente2.academy on 12/19/2017.
 */

public class RestCall {
    private static final String BASE_URL = "https://ewserver.di.unimi.it/mobicomp/geopost";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        client.get(getAbsoluteUrl(url), null, asyncHttpResponseHandler);
    }

    public static void post(String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        client.post(getAbsoluteUrl(url), null, asyncHttpResponseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
