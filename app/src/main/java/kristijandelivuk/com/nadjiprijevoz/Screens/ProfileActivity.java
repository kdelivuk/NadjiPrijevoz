package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;
import kristijandelivuk.com.nadjiprijevoz.model.navigation.NavigationDrawerFragment;


public class ProfileActivity extends AppCompatActivity {

    private TextView mUserNameAndSurname;
    private TextView mUserPhoneNumber;
    private TextView mUserEmail;
    private Toolbar mToolbar;
    private List<RouteModel> mRoutes;
    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;
    private User user;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);



        user = getIntent().getParcelableExtra("targetUser");
        Log.v("username", user.getUsername());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mUserNameAndSurname = (TextView) findViewById(R.id.textNameSurname);
        mUserPhoneNumber = (TextView) findViewById(R.id.textPhone);
        mUserEmail = (TextView) findViewById(R.id.textEmail);



        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(llm);

        try {
            userId = getUserId();
            Log.v("userId" , userId + " username " + user.getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setupCurrentUser();
        findSignedRoutesForCurrentUser();

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

    }


    private String getUserId() throws ParseException {


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", user.getUsername());
        ParseUser parseUser = query.getFirst();
        String userId = parseUser.getObjectId();

        return userId;

    }

    private void setupCurrentUser() {


        if (user != null) {
            mUserEmail.setText(user.getEmail());
            mUserPhoneNumber.setText(user.getPhoneNumber());
            mUserNameAndSurname.setText(user.getName() + " " + user.getSurname());
        }
    }

    private void findSignedRoutesForCurrentUser() {
        mRoutes = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    Log.v("list.length" , list.size() + "");

                    for (ParseObject item : list) {

                        JSONArray jsonArray = item.getJSONArray("passangers");

                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {

                                try {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Log.v("objec" , object.toString());
                                    String id = object.getString("objectId");
                                    Log.v("comparison" , id + "=" + userId);

                                    if (id.equals(userId)) {
                                        Log.v("true" , "true");
                                        mRoutes.add(convertToRouteModel(item));
                                        Log.v("mroutes" , mRoutes.size() + "");
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    }

                    mAdapter = new RVAdapter(ProfileActivity.this, mRoutes);

                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    Log.v("error", e.toString());
                }
            }
        });
    }

    private RouteModel convertToRouteModel(ParseObject item) {

        ParseUser creator = item.getParseUser("creator");

        User creatorUser = new User(
          creator.getUsername(),
          creator.getString("name"),
          creator.getString("surname"),
          creator.getString("phone"),
          creator.getEmail()
        );

        ArrayList<User> passangers = new ArrayList<>();
        JSONArray jsonArrayPassangers = item.getJSONArray("passangers");

        if (jsonArrayPassangers != null) {
            for (int i = 0; i < jsonArrayPassangers.length(); i++) {

                try {
                    JSONObject object = jsonArrayPassangers.getJSONObject(i);
                    Log.v("objectID", object.getString("objectId"));
                    String id = object.getString("objectId");

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("objectId", id);
                    ParseUser parseUser = query.getFirst();

                    passangers.add(new User(
                            parseUser.getUsername(),
                            parseUser.getString("name"),
                            parseUser.getString("surname"),
                            parseUser.getString("phone"),
                            parseUser.getEmail()
                    ));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }



            }
        }

        JSONArray jsonArrayPoints = item.getJSONArray("points");
        final ArrayList<PointModel> points = new ArrayList<PointModel>();

        for (int i = 0; i < jsonArrayPoints.length(); i++) {

            try {
                JSONObject object = jsonArrayPoints.getJSONObject(i);
                //Log.v("objectID", object.getString("objectId"));

                ParseQuery query = ParseQuery.getQuery("Point");
                query.whereEqualTo("objectId", object.getString("objectId"));
                ParseObject pointP = query.getFirst();
                //Log.v("lat", pointP.get("lat").toString());
                //Log.v("lng", pointP.get("lng").toString());
                points.add(new PointModel(pointP.getDouble("lat"), pointP.getDouble("lng")));
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

        }

        RouteModel route = new RouteModel(
                item.getString("destination"),
                item.getString("startingPoint"),
                creatorUser,
                passangers,
                points,
                item.getInt("spacesAvailable"),
                item.getObjectId(),
                item.getString("time"),
                item.getString("date")
        );

        return route;
    }
}
