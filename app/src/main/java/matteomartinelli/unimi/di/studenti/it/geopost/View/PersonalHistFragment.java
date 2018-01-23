package matteomartinelli.unimi.di.studenti.it.geopost.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject;
import matteomartinelli.unimi.di.studenti.it.geopost.Control.UserStateAdapter;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserBundleToSave;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static matteomartinelli.unimi.di.studenti.it.geopost.Control.RWObject.USER_BUNDLE;


public class PersonalHistFragment extends Fragment {
    private Context context;
    private UserStateAdapter userStateAdapter;
    private User loggedUser;
    private UserBundleToSave userBundleToSave;
    private RecyclerView userStateHist;
    private LinearLayoutManager lm;
    private TextView noSocialLife;

    public PersonalHistFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_personal_hist, container, false);
        userBundleToSave = new UserBundleToSave();
        loggedUser = new User();
        context = getActivity();
        userBundleToSave = (UserBundleToSave) RWObject.readObject(context, USER_BUNDLE);
        if(userBundleToSave!=null)
            loggedUser = userBundleToSave.getPersonalProfile();
        userStateHist = v.findViewById(R.id.userStatusList);
        noSocialLife = v.findViewById(R.id.noSocialLife);
        lm = new LinearLayoutManager(context);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (loggedUser.getUserStates() != null) {
            userStateAdapter = new UserStateAdapter(loggedUser.getUserStates(), context);
            userStateHist.setLayoutManager(lm);
            userStateHist.setAdapter(userStateAdapter);
        } else if(loggedUser.getLastState() != null) noSocialLife.setText(":(\nSeems you didn't post anything");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
