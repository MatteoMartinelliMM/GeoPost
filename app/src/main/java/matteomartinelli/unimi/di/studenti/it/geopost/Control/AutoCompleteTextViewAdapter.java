package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.R;

import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_START_NAME;
import static matteomartinelli.unimi.di.studenti.it.geopost.Model.RelativeURLConstants.REL_URL_USERS;

/**
 * Created by matteoma on 1/17/2018.
 */

public class AutoCompleteTextViewAdapter extends ArrayAdapter {
    private ArrayList<String> username;
    private boolean isNetAvaliable;
    private Context context;
    private String cookie;
    public AutoCompleteTextViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> username) {
        super(context, resource, username);
        this.username = username;
        this.context = context;
        cookie = UtilitySharedPreference.getSavedCookie(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.suggestion_element, parent, false);
        }
        String singleUserName = username.get(position);
        if (singleUserName != null) {
            TextView lblName = (TextView) view.findViewById(R.id.username);
            lblName.setText(singleUserName);
        }
        return view;
    }
    @Nullable
    @Override
    public Object getItem(int position) {
        return username.get(position);
    }

    @Override
    public int getCount() {
        return username.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence!=null){
                    RestCall.get(REL_URL_USERS + cookie + REL_URL_START_NAME + charSequence, null, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200){
                                String toParse = new String(responseBody);
                                username = (ArrayList<String>) JSONParser.getUsernameToFollow(toParse);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults != null && filterResults.count>0){
                    notifyDataSetChanged();
                }else
                    notifyDataSetInvalidated();
            }
        };
        return myFilter;
    }
}
