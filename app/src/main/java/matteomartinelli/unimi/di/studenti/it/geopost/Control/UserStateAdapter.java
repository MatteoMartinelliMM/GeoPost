package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.UserState;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

/**
 * Created by Teo on 04/01/2018.
 */

public class UserStateAdapter extends RecyclerView.Adapter<UserStateAdapter.ViewHolder> {
    private ArrayList<UserState> states;
    private Context context;
    private boolean noRedundance = false;

    public UserStateAdapter(Context context) {
        states = new ArrayList<UserState>();
        this.context = context;

    }

    public UserStateAdapter(ArrayList<UserState> states, Context context) {
        this.states = states;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView storyLine;
        public TextView stato, posizione;

        public ViewHolder(View v) {
            super(v);
            storyLine = itemView.findViewById(R.id.storyLine);
            stato = v.findViewById(R.id.oldStatus);
            posizione = v.findViewById(R.id.oldPosition);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (parent != null)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_old_states, null);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,0);
            holder.itemView.setLayoutParams(params);
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setVisibility(View.INVISIBLE);
            return;
        }
        UserState stato = states.get(position);
        String sStato = stato.getStato();
        LatLng latLng = stato.getLatLng();
        String address = Geocoding.getAdressFromCoord(stato, context);
        holder.stato.setText(sStato);
        holder.posizione.setText(address);
    }

    @Override
    public int getItemCount() {
        return states.size();
    }
}

