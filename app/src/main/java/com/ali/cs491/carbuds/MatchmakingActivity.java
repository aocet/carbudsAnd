package com.ali.cs491.carbuds;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.Toolbar;

import android.view.Gravity;
import android.view.MenuItem;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;


import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;


import java.util.List;

public class MatchmakingActivity extends AppCompatActivity implements CardStackListener {
    private static ImageView imageView;
    private static ArrayList<Profile> profiles;
    private static int profileIndex;
    private SendRouteTask task;
    private DrawerLayout drawerLayout;

    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private CardStackView cardStackView;
    private void getProfiles(){
        profiles = new ArrayList<Profile>();
        //todo: delete this part after connecting server
        Profile temp;
        profiles.add(new Profile("Ali Osman", R.drawable.image_green));
        profiles.add(new Profile("Ali Osman", R.drawable.common_google_signin_btn_icon_dark));
        profiles.add(new Profile("Ali Osman", R.drawable.common_google_signin_btn_icon_light_normal_background));
        profileIndex =0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
  //      task = new SendRouteTask();
    //    task.execute((Void) null);
        getProfiles();

        setupNavigation();
        setupCardStackView();
        setupButton();

        /*imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.green);
        Button no = findViewById(R.id.noButton);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileIndex != profiles.size()-1) {
                    profileIndex++;
                } else {
                    profileIndex = 0;
                }
                if (profiles.size()!=0 ) {
                    imageView.setImageResource(profiles.get(profileIndex).getImageID());
                }
            }
        });*/
    }


    private void sendRoute(){
        Trip trip = RouteManager.getTrip();
        JSONObject jsonObj = new JSONObject();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Connection connection = new Connection();
        System.out.println(formatter.format(date));
        try {
            jsonObj.put("token", LoginActivity.token );
            jsonObj.put("user_id", "2"); // get id
            jsonObj.put("trip_start_point", RouteManager.getPointString(trip.getStartPoint()));
            jsonObj.put("trip_end_point", RouteManager.getPointString(trip.getEndPoint()));
            jsonObj.put("trip_start_time", formatter.format(date));
            if(trip.getUserType() == RouteManager.DRIVER) {
                jsonObj.put("available_seat", "2");
                connection.setConnection(Connection.SET_TRIP_DRIVER, jsonObj);
                connection.getResponseMessage();
            } else {
                connection.setConnection(Connection.SET_TRIP_HITCHHIKER,jsonObj);
                connection.getResponseMessage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {
        Log.d("CardStackView", "onCardDragging: d = " + direction.name() + ", r = " + ratio);
    }

    @Override
    public void onCardSwiped(Direction direction) {
        if(direction.equals(Direction.Right)){
            //TODO begenirse nolacak
        } else if(direction.equals(Direction.Left)){
            //begenmezse bir sey almayacak

        }
        if (manager.getTopPosition() == adapter.getItemCount() - 5) {
            paginate();
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {


    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }


    private void setupNavigation() {
        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        reload();
                        break;
                    case R.id.matches:
                        addFirst(1);
                        break;
                    case R.id.find_buddy:
                        addFirst(2);
                        break;
                    case R.id.set_trip_as_driver:
                        addLast(1);
                        break;
                    case R.id.set_trip_as_hitchhiker:
                        addLast(2);
                        break;
                    case R.id.update_driver_profile:
                        removeFirst(1);
                        break;
                    case R.id.update_hitchhiker_profile:
                        removeFirst(2);
                        break;
                    case R.id.settings:
                        removeLast(1);
                        break;
                    case R.id.logout:
                        removeLast(2);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void setupCardStackView() {
        initialize();
    }

    private void setupButton() {
        View skip = findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });

        View rewind = findViewById(R.id.rewind_button);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .build();
                manager.setRewindAnimationSetting(setting);
                cardStackView.rewind();
            }
        });

        View like = findViewById(R.id.like_button);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });
    }

    private void initialize() {
        manager = new CardStackLayoutManager(this, this);
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        adapter = new CardStackAdapter(this, createSpots());
        cardStackView = (CardStackView)findViewById(R.id.card_stack_view);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
    }

    private void paginate() {
        List<Spot> oldList = adapter.getSpots();
        List<Spot> newList = new ArrayList<Spot>() {{
            addAll(adapter.getSpots());
            addAll(createSpots());
        }};
        SpotDiffCallback callback = new SpotDiffCallback(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setSpots(newList);
        result.dispatchUpdatesTo(adapter);
    }

    private void reload() {
        List<Spot> oldList = adapter.getSpots();
        List<Spot> newList = createSpots();
        SpotDiffCallback callback = new SpotDiffCallback(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setSpots(newList);
        result.dispatchUpdatesTo(adapter);
    }

    private void addFirst(final int size) {
        List<Spot> oldList = adapter.getSpots();
        List<Spot> newList = new ArrayList<Spot>() {{
            addAll(adapter.getSpots());
            for (int i = 0; i < size; i++) {
                add(manager.getTopPosition(), createSpot());
            }
        }};
        SpotDiffCallback callback = new SpotDiffCallback(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setSpots(newList);
        result.dispatchUpdatesTo(adapter);
    }

    private void addLast(final int size) {
        List<Spot> oldList = adapter.getSpots();
        List<Spot> newList = new ArrayList<Spot>() {{
            addAll(adapter.getSpots());
            for (int i = 0; i < size; i++) {
                add(createSpot());
            }
        }};
        SpotDiffCallback callback = new SpotDiffCallback(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setSpots(newList);
        result.dispatchUpdatesTo(adapter);
    }

    private void removeFirst(final int size) {
        if (adapter.getSpots().isEmpty()) {
            return;
        }

        List<Spot> oldList = adapter.getSpots();
        List<Spot> newList = new ArrayList<Spot>() {{
            addAll(adapter.getSpots());
            for (int i = 0; i < size; i++) {
                remove(manager.getTopPosition());
            }
        }};
        SpotDiffCallback callback = new SpotDiffCallback(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setSpots(newList);
        result.dispatchUpdatesTo(adapter);
    }

    private void removeLast(final int size) {
        if (adapter.getSpots().isEmpty()) {
            return;
        }

        List<Spot> oldList = adapter.getSpots();
        List<Spot> newList = new ArrayList<Spot>() {{
            addAll(adapter.getSpots());
            for (int i = 0; i < size; i++) {
                remove(size() - 1);
            }
        }};
        SpotDiffCallback callback = new SpotDiffCallback(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setSpots(newList);
        result.dispatchUpdatesTo(adapter);
    }

    private Spot createSpot() {
        return new Spot("Yasaka Shrine", "Kyoto", "https://source.unsplash.com/Xq1ntWruZQI/600x800");
    }

    private List<Spot> createSpots() {
        List<Spot> spots = new ArrayList<>();
        spots.add(new Spot("Yasaka Shrine", "Kyoto", "https://source.unsplash.com/Xq1ntWruZQI/600x800"));
        spots.add(new Spot("Fushimi Inari Shrine", "Kyoto", "https://source.unsplash.com/NYyCqdBOKwc/600x800"));
        spots.add(new Spot("Bamboo Forest", "Kyoto", "https://source.unsplash.com/buF62ewDLcQ/600x800"));
        spots.add(new Spot("Brooklyn Bridge", "New York", "https://source.unsplash.com/THozNzxEP3g/600x800"));
        spots.add(new Spot("Empire State Building", "New York", "https://source.unsplash.com/USrZRcRS2Lw/600x800"));
        spots.add(new Spot("The statue of Liberty", "New York", "https://source.unsplash.com/PeFk7fzxTdk/600x800"));
        spots.add(new Spot("Louvre Museum", "Paris", "https://source.unsplash.com/LrMWHKqilUw/600x800"));
        spots.add(new Spot("Eiffel Tower", "Paris", "https://source.unsplash.com/HN-5Z6AmxrM/600x800"));
        spots.add(new Spot("Big Ben", "London", "https://source.unsplash.com/CdVAUADdqEc/600x800"));
        spots.add(new Spot("Great Wall of China", "China", "https://source.unsplash.com/AWh9C-QjhE4/600x800"));
        return spots;
    }

    public class SendRouteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            sendRoute();
            return null;
        }
    }
}
