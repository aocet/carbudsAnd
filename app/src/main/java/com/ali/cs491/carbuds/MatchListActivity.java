package com.ali.cs491.carbuds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MatchListActivity extends AppCompatActivity {

    int USER_ID;
    String TOKEN;
    private MatchListTask mMatchTask = null;

    List<ChatListUser> users = new ArrayList<ChatListUser>();
    MatchListAdapter mMessageListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        ListView mListView = (ListView) findViewById(R.id.list);
        mMessageListAdapter =new MatchListAdapter(this, users);
        mListView.setAdapter(mMessageListAdapter);

        mMatchTask = new MatchListTask();
        mMatchTask.execute((Void) null);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id)
            {
                Intent intent = new Intent(MatchListActivity.this, ChatActivity.class);
                ChatListUser us = (ChatListUser) mMessageListAdapter.getItem(position);
                intent.putExtra("user_id", us.getId());
                intent.putExtra("matchId", us.getMatchId());
                intent.putExtra("name", us.getName());
                intent.putExtra("exchange", us.getExchange());
                intent.putExtra("surname", us.getUsername());
                intent.putExtra("queue", us.getQueue());
                intent.putExtra("intersectionPolyline", us.getIntersectionPolyline());
                MatchListActivity.this.startActivity(intent);
            }

        });
    }

    public class MatchListTask extends AsyncTask<Void, Void, Boolean> {

        private final int userId;

        MatchListTask() {
            readShared();
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
                    users.add(new ChatListUser(USER_ID, matchId, name, lastName,
                            exchange, queue,
                            intersectionPolyline));
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
                mMessageListAdapter.notifyDataSetChanged();
            } else {
            }
        }

        @Override
        protected void onCancelled() {
            mMatchTask = null;
        }
    }
    public void readShared(){
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED",Context.MODE_PRIVATE);
        TOKEN = sharedPref.getString("token", "");
        USER_ID = sharedPref.getInt("user_id", -1);
    }
}
