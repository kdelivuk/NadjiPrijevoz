package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.Route;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteDetailNavigationAdapter;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;

public class RouteDetailActivity extends AppCompatActivity implements OnMapReadyCallback, RouteDetailNavigationAdapter.ClickListener {

    private Toolbar mToolbar;

    // recycler
    private RecyclerView mRecyclerView;
    private RouteDetailNavigationAdapter mAdapter;
    private GoogleMap mGoogleMap;
    private LinearLayout mLayout;
    private TextView mStartingPoint;
    private TextView mDestination;
    private TextView mSpacesAvailable;
    private Button mJoinRoute;
    private ArrayList<ParseUser> mPassangers;
    private boolean userAlreadyInArray;
    // routes
    private RouteModel mSelectedRoute;
    private ArrayList<LatLng> mPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mRecyclerView = (RecyclerView)findViewById(R.id.passangers_recycler_view);

        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.route_detail_google_map);
        map.getMapAsync(this);
        mGoogleMap = map.getMap();

        mLayout = (LinearLayout) findViewById(R.id.layout_detail_route);
        mStartingPoint = (TextView) findViewById(R.id.textStartingPointPlaceholder);
        mDestination = (TextView) findViewById(R.id.textDestinationPlaceholder);
        mSpacesAvailable = (TextView) findViewById(R.id.textSpacesAvailablePlaceholder);
        mJoinRoute = (Button) findViewById(R.id.buttonJoin);


        mPoints = new ArrayList<LatLng>();

        Intent intent = getIntent();
        mSelectedRoute = (RouteModel) intent.getParcelableExtra("selectedRoute");

        mStartingPoint.setText(mSelectedRoute.getStartingPoint());
        mDestination.setText(mSelectedRoute.getDestination());
        mSpacesAvailable.setText(String.valueOf(mSelectedRoute.getSpacesAvailable()));

        for (PointModel item : mSelectedRoute.getPoints()) {
            Log.v("point" , item.getLatitude() + " " + item.getLongitude());
            LatLng tmpPoint = new LatLng(item.getLatitude(), item.getLongitude());
            mPoints.add(tmpPoint);
        }
        Log.v("mSelectedRoute", mSelectedRoute.getPassangers().toString());

        loadPassangers();


        mJoinRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserToTheRoute();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter = new RouteDetailNavigationAdapter(RouteDetailActivity.this, mSelectedRoute.getPassangers());
        mAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(RouteDetailActivity.this));

    }

    private void loadPassangers() {
        mPassangers = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");
        query.whereEqualTo("objectId", mSelectedRoute.getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                if (e == null) {

                    JSONArray jsonArray = parseObject.getJSONArray("passangers");

                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.v("objectID", object.getString("objectId"));
                                String id = object.getString("objectId");
                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                query.whereEqualTo("objectId", id);
                                query.getFirstInBackground(new GetCallback<ParseUser>() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {
                                        if (e == null) {
                                            Log.v("parseUser", parseUser.toString());
                                            mPassangers.add(parseUser);

                                            Log.v("size", mPassangers.size() + "");
                                        } else {
                                            Log.v("error", e.toString());
                                        }
                                    }
                                });


                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                } else {
                    Log.v("error", e.toString());
                }
            }
        });
    }

    private void addUserToTheRoute() {

        mPassangers = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");
        query.whereEqualTo("objectId", mSelectedRoute.getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                if (e == null) {

                    JSONArray jsonArray = parseObject.getJSONArray("passangers");

                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.v("objectID", object.getString("objectId"));
                                String id = object.getString("objectId");
                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                query.whereEqualTo("objectId", id);
                                query.getFirstInBackground(new GetCallback<ParseUser>() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {
                                        if (e == null) {
                                            Log.v("parseUser", parseUser.toString());
                                            mPassangers.add(parseUser);
                                            Log.v("size", mPassangers.size() + "");
                                        } else {
                                            Log.v("error", e.toString());
                                        }
                                    }
                                });


                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }

                    mPassangers.add(ParseUser.getCurrentUser());
                    Log.v("size", mPassangers.size() + "");

                } else {
                    Log.v("error", e.toString());
                }
            }
        });

        ParseQuery<ParseObject> queryDva = ParseQuery.getQuery("Route");
        queryDva.whereEqualTo("objectId", mSelectedRoute.getId());
        queryDva.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                Log.v("mPassangers", mPassangers.toString());
                parseObject.put("passangers", mPassangers);
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            Toast.makeText(RouteDetailActivity.this, "User added", Toast.LENGTH_LONG);
                        } else {
                            Log.v("error", e.toString());
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        Route rt = new Route();
        rt.drawRoute(mGoogleMap, RouteDetailActivity.this, new ArrayList<LatLng>(mPoints), "en", false);

        zoomMapToLatLngBounds(mLayout, mGoogleMap, mPoints);


    }

    private void zoomMapToLatLngBounds(final LinearLayout layout,final GoogleMap mMap, final ArrayList<LatLng> bounds){

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (int i=0; i<bounds.size(); i++) {
            boundsBuilder.include(bounds.get(i));
        }

        final LatLngBounds boundsfinal = boundsBuilder.build();

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsfinal, 25));
            }
        });

    }

    @Override
    public void selectedItem(View view, int position) {
        Log.v("position" , position + "");
        Intent intent = new Intent(RouteDetailActivity.this , ProfileActivity.class);
        intent.putExtra("targetUser" , new User(
                mPassangers.get(position).getUsername(),
                mPassangers.get(position).getString("name"),
                mPassangers.get(position).getString("surname"),
                mPassangers.get(position).getString("phone"),
                mPassangers.get(position).getEmail()
        ));
        startActivity(intent);
    }
}

