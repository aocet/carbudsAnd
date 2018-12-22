package com.ali.cs491.carbuds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class MatchmakingFragment extends Fragment implements CardStackListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public MatchmakingFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MatchmakingFragment newInstance(int sectionNumber) {
        MatchmakingFragment fragment = new MatchmakingFragment();
        fragment.currentMatchCount = 0;
        fragment.getUser_type();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    private static MapDialogFragment mapDialogFragment;
    private TextView passengerSeatView;
    private TextView genderPreferenceView;
    private TextView carModelView;
    private TextView carBrandView;
    private TextView licensePlateView;
    private TextView musicPreferenceView;
    private TextView userNameView;
    private TextView currentRoleView;
    private JSONArray jsonArray;
    public int currentMatchCount;
    private  int user_id;
    public  String token;
    private  String user_name;
    private  String user_type;

    private View formView;
    private View progressView;

    private ImageView imageView;
    private ArrayList<Profile> profiles;
    private  int profileIndex;
    private MatchmakingActivity.SendRouteTask task;
    private DrawerLayout drawerLayout;

    private View v;

    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private CardStackView cardStackView;
    private View noMatchView;
    private View cardsView;
    private Button noMatchButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_matchmaking, container, false);
        noMatchView =   v.findViewById(R.id.no_match);
        cardsView =  v.findViewById(R.id.matchmaking_cards);
        noMatchButton = v.findViewById(R.id.no_match_button);
        noMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if(currentMatchCount == 0){
            showNoMatch(true);
        } else {
            showNoMatch(false);
        }
        //setupNavigation(v);
        initialize(v);
        setupButton(v);
        return v;
    }
    private void showNoMatch(boolean show){
        noMatchView.setVisibility(show ? View.VISIBLE: View.GONE);
        cardsView.setVisibility(show? View.GONE:View.VISIBLE);
    }
    private void getUser_type(){
        String token = User.token;
        String usertype = User.userType;
        String URL ;
        if(usertype.equals("driver")) {
            URL = Connection.IP + Connection.GET_HITCHHIKER_CANDIDATE;
        } else {
            URL = Connection.IP + Connection.GET_DRIVER_CANDIDATE;
        }
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("token", token);
        } catch(JSONException e){
            e.printStackTrace();
        }
        AndroidNetworking.post(URL)
                .addJSONObjectBody(jsonObject) // posting any type of file
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("false\n")){
                            //TODO:you dont have matchmaking now
                            showNoMatch(true);
                        }
                        try {
                            jsonArray = new JSONArray(response);
                            currentMatchCount = jsonArray.length();
                            showNoMatch(false);
                            reload();
                         //   matchmaking();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        String str = error.getErrorBody();
                    }
                });
    }
    private void getCandidates(){

    }
    public void matchmaking(){
        View v = this.getView();
        initialize(v);
        setupButton(v);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "Initializing sounds...");
        matchmaking();

    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {
        Log.d("CardStackView", "onCardDragging: d = " + direction.name() + ", r = " + ratio);
    }

    private void like(){
        int index = manager.getTopPosition()-1;
        JSONObject match = null;
        try {
            match = jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("possible_match_id", match.getString("match_id"));
            jsonObject.put("token", User.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(Connection.IP + Connection.LIKE_MATCH)
                .addJSONObjectBody(jsonObject) // posting any type of file
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("false\n")){
                            //TODO:you dont have matchmaking now
                        }
                        try {
                            jsonArray = new JSONArray(response);
                            reload();
                            //   matchmaking();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        String str = error.getErrorBody();
                    }
                });
    }
    @Override
    public void onCardSwiped(Direction direction) {


        if(direction.equals(Direction.Right)){
            like();

        } else if(direction.equals(Direction.Left)){
            //begenmezse bir sey almayacak
            //token
            //match_id
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


    /*private void setupNavigation(View v) {
        // Toolbar
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // DrawerLayout
        drawerLayout = v.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // NavigationView
        NavigationView navigationView = v.findViewById(R.id.navigation_view);
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
    }*/

    private void setupCardStackView(View v) {
        initialize(v);
    }
    private void setupButton(View v) {
        View skip = v.findViewById(R.id.skip_button);
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

        View rewind = v.findViewById(R.id.rewind_button);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int index = manager.getTopPosition();
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(index);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mapDialogFragment= new MapDialogFragment();
                mapDialogFragment.setCandidateInfo(jsonObject);
                mapDialogFragment.setCancelable(true);
                mapDialogFragment.show(getFragmentManager(), "mapsfragment");
                /*RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .build();
                manager.setRewindAnimationSetting(setting);
                cardStackView.rewind();*/
            }
        });

        View like = v.findViewById(R.id.like_button);
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

    private void initialize(View v) {
        manager = new CardStackLayoutManager(getActivity(), this);
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        adapter = new CardStackAdapter(getActivity(), createSpots());
        cardStackView = (CardStackView)v.findViewById(R.id.card_stack_view);
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
        if(jsonArray!=null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    System.out.print(jsonObject.toString());
                    String URL;
                    if(User.userType.equals("driver")){
                        URL = "http://35.205.45.78/get_user_image?user_image_id=" + jsonObject.getString("hitchhiker_id");
                    } else {
                        URL = "http://35.205.45.78/get_user_image?user_image_id=" + jsonObject.getString("driver_id");
                    }
                    String name = jsonObject.getString("name");
                    String time = jsonObject.getString("trip_start_time");
                    spots.add(new Spot(name, time, URL));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
       /* spots.add(new Spot("Yasaka Shrine", "Kyoto", "https://source.unsplash.com/Xq1ntWruZQI/600x800"));
        spots.add(new Spot("Fushimi Inari Shrine", "Kyoto", "https://source.unsplash.com/NYyCqdBOKwc/600x800"));
        spots.add(new Spot("Bamboo Forest", "Kyoto", "https://source.unsplash.com/buF62ewDLcQ/600x800"));
        spots.add(new Spot("Brooklyn Bridge", "New York", "https://source.unsplash.com/THozNzxEP3g/600x800"));
        spots.add(new Spot("Empire State Building", "New York", "https://source.unsplash.com/USrZRcRS2Lw/600x800"));
        spots.add(new Spot("The statue of Liberty", "New York", "https://source.unsplash.com/PeFk7fzxTdk/600x800"));
        spots.add(new Spot("Louvre Museum", "Paris", "https://source.unsplash.com/LrMWHKqilUw/600x800"));
        spots.add(new Spot("Eiffel Tower", "Paris", "https://source.unsplash.com/HN-5Z6AmxrM/600x800"));
        spots.add(new Spot("Big Ben", "London", "https://source.unsplash.com/CdVAUADdqEc/600x800"));
        spots.add(new Spot("Great Wall of China", "China", "https://source.unsplash.com/AWh9C-QjhE4/600x800"));
        */
        return spots;
    }
}
