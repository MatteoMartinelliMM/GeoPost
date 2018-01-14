package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.RestCall;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.TaskDelegate;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_LOGIN;

public class LoginActivity extends AppCompatActivity implements TaskDelegate {

    private EditText userName, password;
    private User loggedUser;
    private TaskDelegate delegate;
    private String sUsername,sPwd;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hidingTheTitleBar();
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        delegate = this;
        loggedUser = new User();
        dialog = new ProgressDialog(this);

    }

    public void login(View v) {
        sUsername = userName.getText().toString();
        sPwd = password.getText().toString();
        final RequestParams loginParams = new RequestParams();
        loginParams.put("username", sUsername);
        loginParams.put("password", sPwd);
        dialog.onStart();
        RestCall.post(REL_URL_LOGIN, loginParams, new AsyncHttpResponseHandler() {
            String cookie;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cookie = new String(responseBody);
                    loggedUser = new User();
                    loggedUser.setCookie(cookie);
                }
                delegate.waitToComplete("" + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 400)
                    delegate.waitToComplete("" + statusCode);

            }
        });

    }

    private void hidingTheTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    public void waitToComplete(String s) {
        dialog.dismiss();
        dialog.cancel();
        if (s.equals("400"))
            Toast.makeText(getApplicationContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
        else {
            loggedUser.setUserName(sUsername);
            UtilitySharedPreference.addLoggedUser(this,loggedUser);
            Toast.makeText(getApplicationContext(), "Login succesful", Toast.LENGTH_SHORT).show();
            Intent intent;
            intent = new Intent(this, OverviewActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
