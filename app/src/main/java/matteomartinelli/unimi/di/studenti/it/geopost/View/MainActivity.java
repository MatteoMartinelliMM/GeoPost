package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import matteomartinelli.unimi.di.studenti.it.geopost.Control.UtilitySharedPreference;
import matteomartinelli.unimi.di.studenti.it.geopost.R;
import matteomartinelli.unimi.di.studenti.it.geopost.View.LoginActivity;
import matteomartinelli.unimi.di.studenti.it.geopost.View.OverviewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent;
        if(UtilitySharedPreference.checkIfUserIsLogged(this)){
            intent = new Intent(this,OverviewActivity.class);
            startActivity(intent);
            finish();
        }else{
            intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
