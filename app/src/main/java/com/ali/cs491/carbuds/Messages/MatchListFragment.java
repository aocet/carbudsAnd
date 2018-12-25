package com.ali.cs491.carbuds.Messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ali.cs491.carbuds.SetTrip.StartSelectionActivity;
import com.ali.cs491.carbuds.Source.Connection;
import com.ali.cs491.carbuds.R;
import com.ali.cs491.carbuds.Source.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MatchListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View v;

    int USER_ID = User.user_id;
    String TOKEN = User.token;
    private MatchListTask mMatchTask = null;

    List<ChatListUser> users = new ArrayList<ChatListUser>();
    MatchListAdapter mMessageListAdapter;
    private View emptyView;
    private View fullView;
    private Button noMatchButton;
    private TextView noMatchTextView;
    private Fragment f;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public MatchListFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MatchListFragment newInstance(int sectionNumber) {
        MatchListFragment fragment = new MatchListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public boolean isExist(int matchId){
        for(int i=0 ;i<users.size(); i++){
            ChatListUser user= users.get(i);
            if(user.getMatchId() == matchId){
                return true;
            }
        }
        return false;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void showNoMatch(boolean show){
        emptyView.setVisibility(show ? View.VISIBLE: View.GONE);
        fullView.setVisibility(show? View.GONE:View.VISIBLE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_list, container, false);
        emptyView = rootView.findViewById(R.id.empty_matchlist);
        fullView = rootView.findViewById(R.id.full_matchlist_view);
        noMatchButton = rootView.findViewById(R.id.empty_matchlist_button);
        noMatchTextView = rootView.findViewById(R.id.empty_matchlist_textview);
        if(User.isTripSetted){
            noMatchButton.setText("Reload");
            noMatchTextView.setText("You dont have any match yet.");
        } else {
            noMatchButton.setText("Set Trip");
            noMatchTextView.setText("You didn't set trip yet.");
        }
        noMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noMatchButton.getText().toString().equals("Reload")){
                    if(!User.isTripSetted){
                        noMatchButton.setText("Set Trip");
                        noMatchTextView.setText("You didn't set trip yet.");
                        Toast.makeText(getContext(), "There is no available trip, please create trip", Toast.LENGTH_SHORT).show();
                    } else {
                        mMatchTask = new MatchListTask();
                        mMatchTask.execute((Void) null);
                    }
                } else {
                    if(User.isTripSetted){
                        noMatchButton.setText("Reaload");
                        noMatchTextView.setText("You dont have any match yet.");
                        mMatchTask = new MatchListTask();
                        mMatchTask.execute((Void) null);
                    } else {
                        Intent intent = new Intent(getActivity(), StartSelectionActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        v = this.getView();
        f = this;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((mMessageReceiver),
                new IntentFilter("message_list")
        );
        ListView mListView = (ListView) v.findViewById(R.id.list);
        mMessageListAdapter =new MatchListAdapter(getActivity(), users);
        mListView.setAdapter(mMessageListAdapter);

        mMatchTask = new MatchListTask();
        mMatchTask.execute((Void) null);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id)
            {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                ChatListUser us = (ChatListUser) mMessageListAdapter.getItem(position);
                intent.putExtra("user_id", us.getId());
                intent.putExtra("matchId", us.getMatchId());
                intent.putExtra("name", us.getName());
                intent.putExtra("exchange", us.getExchange());
                intent.putExtra("surname", us.getSurname());
                intent.putExtra("queue", us.getQueue());
                intent.putExtra("intersectionPolyline", us.getIntersectionPolyline());
                intent.putExtra("tripStartTime", us.getTripStart());
                intent.putExtra("startPoint", us.getStartPoint());
                intent.putExtra("endPoint", us.getEndPoint());
                intent.putExtra("isDriver", us.isDriver());
                getActivity().startActivity(intent);
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            users = new ArrayList<ChatListUser>();
            refresh();
        }
    };

    private void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(f).attach(f).commit();
    }

    public class MatchListTask extends AsyncTask<Void, Void, Boolean> {

        private final int userId;

        MatchListTask() {
            this.userId = USER_ID;
        }
        private String setupURLConnection(){
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("token", TOKEN);
            } catch(JSONException e){
                e.printStackTrace();
            }
            Connection connection= new Connection();
            connection.setConnection(Connection.GET_MATCHES, jsonObject);
            return connection.getResponseMessage();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String msg = setupURLConnection();
            JSONArray jsonarray = null;
            Log.i("Carbuds",msg);
            try {
                //users = new ArrayList<ChatListUser>();
                jsonarray = new JSONArray(msg);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    int matchId = jsonobject.getInt("match_id");
                    if(isExist(matchId)){
                        continue;
                    }
                    String intersectionPolyline = jsonobject.getString("intersection_polyline");
                    int hitchhikerId = jsonobject.getInt("hitchhiker_id");
                    int driverId = jsonobject.getInt("driver_id");
                    int id = USER_ID == driverId ? hitchhikerId : driverId;
                    String queue = (USER_ID == hitchhikerId ?
                            jsonobject.getString("hitchhiker_queue") :
                            jsonobject.getString("driver_queue"));
                    String name = (USER_ID == hitchhikerId ?
                            jsonobject.getString("driver_name") :
                            jsonobject.getString("hitchhiker_name"));
                    String lastName = (USER_ID == hitchhikerId ?
                            jsonobject.getString("driver_lastname") :
                            jsonobject.getString("hitchhiker_lastname"));
                    String exchange = jsonobject.getString("exchange_name");
                    String tripStartTime = jsonobject.getString("trip_start_time");
                    String startPoint = jsonobject.getString("start_point");
                    String endPoint = jsonobject.getString("end_point");
                    boolean isDriver = (USER_ID == driverId);
                    users.add(new ChatListUser(id, matchId, name, lastName,
                            exchange, queue, intersectionPolyline, tripStartTime,
                            startPoint, endPoint, isDriver));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(msg.equals("false\n")){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mMatchTask = null;
            if(users.isEmpty()){
                showNoMatch(true);
            } else {
                showNoMatch(false);
            }
            if (success) {

                mMessageListAdapter.notifyDataSetInvalidated();
            } else {
            }
        }

        @Override
        protected void onCancelled() {

            mMatchTask.cancel(true);
        }
    }
}
