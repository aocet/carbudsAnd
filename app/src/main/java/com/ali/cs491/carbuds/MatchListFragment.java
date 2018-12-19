package com.ali.cs491.carbuds;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import static android.support.constraint.Constraints.TAG;



public class MatchListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View v;

    int USER_ID = LoginActivity.user_id;
    String TOKEN = LoginActivity.token;
    private MatchListTask mMatchTask = null;

    List<ChatListUser> users = new ArrayList<ChatListUser>();
    MatchListAdapter mMessageListAdapter;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_list, container, false);

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        v = this.getView();

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
                getActivity().startActivity(intent);
            }

        });


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
                jsonarray = new JSONArray(msg);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String intersectionPolyline = jsonobject.getString("intersection_polyline");
                    int hitchhikerId = jsonobject.getInt("hitchhiker_id");
                    int driverId = jsonobject.getInt("driver_id");
                    int matchId = jsonobject.getInt("match_id");
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
                    users.add(new ChatListUser(USER_ID, matchId, name, lastName,
                            exchange, queue, intersectionPolyline, tripStartTime,
                            startPoint, endPoint));
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

            if (success) {
                mMessageListAdapter.notifyDataSetInvalidated();
            } else {
            }
        }

        @Override
        protected void onCancelled() {
            mMatchTask = null;
        }
    }
}