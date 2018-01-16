package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import matteomartinelli.unimi.di.studenti.it.geopost.View.PersonalHistFragment;
import matteomartinelli.unimi.di.studenti.it.geopost.View.PersonalMapFragment;

/**
 * Created by matteoma on 1/16/2018.
 */

public class Pager extends FragmentPagerAdapter {
    PersonalHistFragment personalHistFragment;
    PersonalMapFragment personalMapFragment;
    android.app.FragmentManager manager;
    int tabCount;
    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(personalHistFragment == null){
                    personalHistFragment = new PersonalHistFragment();
                }
                return personalHistFragment;

            case 1:
                if(personalMapFragment == null){
                    personalMapFragment = new PersonalMapFragment();
                }
                return personalMapFragment;

                default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
