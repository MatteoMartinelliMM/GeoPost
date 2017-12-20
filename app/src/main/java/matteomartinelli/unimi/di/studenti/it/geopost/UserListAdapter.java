package matteomartinelli.unimi.di.studenti.it.geopost;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.Friend;

/**
 * Created by utente2.academy on 12/20/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    ArrayList<Friend> amici;

    public UserListAdapter(String classCaller) {
        amici = new ArrayList<Friend>();
    }

    public UserListAdapter(ArrayList<Friend> amici) {
        this.amici = amici;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView singlePackage;
        public TextView username, stato, posizione;
        public ImageView userProPic;

        public ViewHolder(View v) {
            super(v);
            singlePackage = itemView.findViewById(R.id.friendContainer);
            username = v.findViewById(R.id.username);
            stato = v.findViewById(R.id.stato);
            posizione = v.findViewById(R.id.distanza);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (parent != null)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_element_list, null);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend friend = amici.get(position);
        String sUsername = friend.getUsername();
        String sStato = friend.getStato().getStato();
        LatLng latLng = friend.getStato().getLatLng();

    }

    @Override
    public int getItemCount() {
        return amici.size();
    }


}