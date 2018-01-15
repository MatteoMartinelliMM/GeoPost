package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;



import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

/**
 * Created by utente2.academy on 12/20/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    ArrayList<User> friends;
    Location myLocation;

    public UserListAdapter(String classCaller) {
        friends = new ArrayList<User>();

    }

    public UserListAdapter(ArrayList<User> friends, Location myLocation) {
        this.friends = friends;
        this.myLocation = myLocation;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView singleFriend;
        public TextView username, stato, posizione;
        public ImageView userProPic;

        public ViewHolder(View v) {
            super(v);
            singleFriend = itemView.findViewById(R.id.friendContainer);
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
        User friend = friends.get(position);
        String sUsername = friend.getUserName();
        String sStato = friend.getLastState().getStato();
        double latitude = friend.getLastState().getLatitude();
        double longitude = friend.getLastState().getLongitude();
        LatLng latLng = new LatLng(latitude,longitude);
        Location friendLocation = new Location("FRIEND LOCATION");
        float distance = myLocation.distanceTo(friendLocation)/1000;

        holder.username.setText(sUsername);
        holder.stato.setText(sStato);
        if(distance>=2)
            holder.posizione.setText(String.format("%.2f",distance)+" km");

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


}
