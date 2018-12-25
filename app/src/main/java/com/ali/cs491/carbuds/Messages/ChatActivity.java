package com.ali.cs491.carbuds.Messages;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ali.cs491.carbuds.Messages.Message;
import com.ali.cs491.carbuds.R;
import com.ali.cs491.carbuds.Source.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView mOutput;
    private String QUEUE_NAME = "";
    private String EXCHANGE_NAME = "";
    private String message = "";
    private String name = "";
    private int USER_ID = User.user_id;
    DbHelper mDbHelper;
    Handler handler = new Handler();
    String bodyOfMessage = "";
    String nameOfMessage = "";
    String timeOfMessage = "";
    String recieveTimeOfMessage = "";
    int idOfMessage = -1;

    EditText messageField;

    /**
     * Called when the activity is first created.
     */

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();


        int id = intent.getIntExtra("user_id", -1);
        int match_id = intent.getIntExtra("matchId", -1);
        String name = intent.getStringExtra("name");
        String surName = intent.getStringExtra("surname");
        String exchange = intent.getStringExtra("exchange");
        String queue = intent.getStringExtra("queue");
        String intersectionPolyline = intent.getStringExtra("intersectionPolyline");
        String tripStartTime = intent.getStringExtra("tripStartTime");
        String startPoint = intent.getStringExtra("startPoint");
        String endPoint = intent.getStringExtra("endPoint");
        Boolean isDriver = intent.getBooleanExtra("isDriver", false);

        TextView chatUserName = (TextView) findViewById(R.id.chatUserName);
        if (name.length() > 0) {

            String firstLetter = name.substring(0, 1).toUpperCase();
            String restLetters = name.substring(1, name.length());
            String label = firstLetter+restLetters;
            chatUserName.setText(label);
        }



        CircleImageView profilePic = (CircleImageView)findViewById(R.id.chatPicture);
        Glide.with(profilePic)
                .load("http://35.205.45.78/get_user_image?user_image_id="+id)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(profilePic);

        EXCHANGE_NAME = exchange;
        QUEUE_NAME = queue;


        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(mLinearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);


        mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                DbContract.FeedEntry.COLUMN_MESSAGE,
                DbContract.FeedEntry.COLUMN_SENDER_NAME,
                DbContract.FeedEntry.COLUMN_TIME,
                DbContract.FeedEntry.COLUMN_SENDER_ID
        };

        String selection = DbContract.FeedEntry.COLUMN_EXCHANGE_NAME + " = ?";
        String[] selectionArgs = {EXCHANGE_NAME};

        Cursor cursor = db.query(
                DbContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DbContract.FeedEntry._ID));
            String message = cursor.getString(cursor.getColumnIndex(DbContract.FeedEntry.COLUMN_MESSAGE));
            int sender_id = cursor.getInt(cursor.getColumnIndex(DbContract.FeedEntry.COLUMN_SENDER_ID));
            String sender_name = cursor.getString(cursor.getColumnIndex(DbContract.FeedEntry.COLUMN_SENDER_NAME));
            String message_time = cursor.getString(cursor.getColumnIndex(DbContract.FeedEntry.COLUMN_TIME));
            User us = new User(sender_name, sender_id);
            Message ms = new Message(message, us, Helpers.timeFormatter(message_time));
            messageList.add(ms);
        }
        cursor.close();

        mMessageRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mMessageRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (messageList.size() > 0) {
                                mMessageRecycler.smoothScrollToPosition(messageList.size()-1);
                            }

                        }
                    }, 100);
                }
            }
        });

        messageField = (EditText) findViewById(R.id.edittext_chatbox);
        messageField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    return sendMessage();

                }
                return false;
            }
        });

        Button sendButton = (Button) findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        startTimerThread();



        Button b = (Button) findViewById(R.id.info_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InfoMatchActivity.class);
                intent.putExtra("user_id", id);
                intent.putExtra("match_id", match_id);
                intent.putExtra("name", name);
                intent.putExtra("exchange", exchange);
                intent.putExtra("surname", surName);
                intent.putExtra("queue", queue);
                intent.putExtra("intersectionPolyline", intersectionPolyline);
                intent.putExtra("tripStartTime", tripStartTime);
                intent.putExtra("startPoint", startPoint);
                intent.putExtra("endPoint", endPoint);
                intent.putExtra("isDriver", isDriver);
                startActivity(intent);
            }
        });


    }

    public boolean sendMessage() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("Name", name);
            obj.put("Id", USER_ID);
            obj.put("Body", messageField.getText().toString());
            obj.put("timestamp", Calendar.getInstance().getTime());

            new send().execute(obj.toString());
            messageField.setText("");
            messageField.setHint("Enter message");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    private class send extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... Message) {

            try {
                IConnectToRabbitMQ rabbitmq = new IConnectToRabbitMQ("35.205.45.78",
                        5672, "rabbitmq", "rabbitmq", EXCHANGE_NAME,
                        "fanout", QUEUE_NAME);
                rabbitmq.connectToRabbitMQ();
                //rabbitmq.getChannel().exchangeDeclare(rabbitmq.getExchange(), rabbitmq.getExchangeType(), true);
                //rabbitmq.getChannel().queueDeclare(rabbitmq.getQueue(), false, false,
                //false, null);
                String tempstr = "";
                for (int i = 0; i < Message.length; i++)
                    tempstr += Message[i];

                rabbitmq.getChannel().basicPublish(rabbitmq.getExchange(), rabbitmq.getQueue(), null,
                        tempstr.getBytes());

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            // TODO Auto-generated method stub
            return null;

        }

    }

    private void startTimerThread() {

        Runnable runnable = new Runnable() {
            private long startTime = System.currentTimeMillis();
            private String text = "";

            public void run() {
                try {
                    IConnectToRabbitMQ rabbitmq = new IConnectToRabbitMQ("35.205.45.78",
                            5672, "rabbitmq", "rabbitmq", EXCHANGE_NAME,
                            "fanout", QUEUE_NAME);
                    rabbitmq.connectToRabbitMQ();
                    //rabbitmq.getChannel().queueDeclare(rabbitmq.getQueue(), false, false,
                    //      false, null);
                    DeliverCallback deliverCallback = new DeliverCallback() {
                        @Override
                        public void handle(String consumerTag, Delivery delivery) throws IOException {
                            String message = new String(delivery.getBody(), "UTF-8");
                            System.out.println(" [x] Received '" + message + "'");
                            text = message;
                            try {
                                JSONObject json = new JSONObject(message);
                                bodyOfMessage = json.getString("Body");
                                nameOfMessage = json.getString("Name");
                                timeOfMessage = json.getString("timestamp");
                                idOfMessage = json.getInt("Id");
                                recieveTimeOfMessage = Helpers.timeFormatter(timeOfMessage);

                                SQLiteDatabase db = mDbHelper.getWritableDatabase();


                                ContentValues values = new ContentValues();
                                values.put(DbContract.FeedEntry.COLUMN_EXCHANGE_NAME, EXCHANGE_NAME);
                                values.put(DbContract.FeedEntry.COLUMN_MESSAGE, bodyOfMessage);
                                values.put(DbContract.FeedEntry.COLUMN_SENDER_ID, idOfMessage);
                                values.put(DbContract.FeedEntry.COLUMN_SENDER_NAME, nameOfMessage);
                                values.put(DbContract.FeedEntry.COLUMN_TIME, timeOfMessage);

                                long newRowId = db.insert(DbContract.FeedEntry.TABLE_NAME, null, values);


                                handler.post(new Runnable() {
                                    public void run() {
                                        messageList.add(new Message(bodyOfMessage, new User(nameOfMessage, idOfMessage), recieveTimeOfMessage));
                                        mMessageAdapter.notifyDataSetChanged();
                                        mMessageRecycler.smoothScrollToPosition(mMessageRecycler.getAdapter().getItemCount()-1);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    rabbitmq.getChannel().basicConsume(rabbitmq.getQueue(), true, deliverCallback, new CancelCallback() {
                        @Override
                        public void handle(String consumerTag) throws IOException {
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        new Thread(runnable).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
