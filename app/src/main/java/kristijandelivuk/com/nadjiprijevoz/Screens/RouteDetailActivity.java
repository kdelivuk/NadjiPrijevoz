package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
import kristijandelivuk.com.nadjiprijevoz.helper.TypefaceSpan;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
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
    private TextView mTextStartingPoint;
    private TextView mTextDestination;
    private TextView mTextSpacesAvailable;

    private Button mJoinRoute;
    private ArrayList<ParseUser> mPassangers;
    private boolean userAlreadyInArray;
    // routes
    private RouteModel mSelectedRoute;
    private ArrayList<LatLng> mPoints;
    private ArrayList<LatLng> mUserPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        SpannableString s = new SpannableString("Detalji rute");
        s.setSpan(new TypefaceSpan(this, "Choplin.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(s);

        mRecyclerView = (RecyclerView)findViewById(R.id.passangers_recycler_view);

        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.route_detail_google_map);
        map.getMapAsync(this);
        mGoogleMap = map.getMap();

        mLayout = (LinearLayout) findViewById(R.id.layout_detail_route);

        Typeface choplin = Typeface.createFromAsset(getAssets(), "fonts/Choplin.otf");
        Typeface gidole = Typeface.createFromAsset(getAssets(), "fonts/Gidole_Regular.ttf");

        mStartingPoint = (TextView) findViewById(R.id.textStartingPointPlaceholder);
        mDestination = (TextView) findViewById(R.id.textDestinationPlaceholder);
        mSpacesAvailable = (TextView) findViewById(R.id.textSpacesAvailablePlaceholder);
        mJoinRoute = (Button) findViewById(R.id.buttonJoin);

        mTextStartingPoint = (TextView) findViewById(R.id.textStartingPoint);
        mTextDestination = (TextView) findViewById(R.id.textDestination);
        mTextSpacesAvailable = (TextView) findViewById(R.id.textSpacesAvailable);

        mTextStartingPoint.setTypeface(choplin);
        mTextDestination.setTypeface(choplin);
        mTextSpacesAvailable.setTypeface(choplin);

        mStartingPoint.setTypeface(gidole);
        mDestination.setTypeface(gidole);
        mSpacesAvailable.setTypeface(gidole);
        mJoinRoute.setTypeface(gidole);

        mPoints = new ArrayList<LatLng>();
        mUserPositions = new ArrayList<>();

        Intent intent = getIntent();
        mSelectedRoute = (RouteModel) intent.getParcelableExtra("selectedRoute");

        mStartingPoint.setText(mSelectedRoute.getStartingPoint());
        mDestination.setText(mSelectedRoute.getDestination());


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
                if (Integer.parseInt(mSpacesAvailable.getText().toString()) > 0) {
                    addUserToTheRoute();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter = new RouteDetailNavigationAdapter(RouteDetailActivity.this, mSelectedRoute.getPassangers());
        mAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(llm);

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

                                            mGoogleMap.addMarker(
                                                new MarkerOptions()
                                                        .position(new LatLng(
                                                                parseUser.getParseGeoPoint("location").getLatitude(),
                                                                parseUser.getParseGeoPoint("location").getLongitude()))
                                                        .title("Passanger: " + parseUser.getString("name") + " " + parseUser.getString("surname"))
                                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.passanger_icon))
                                            );


                                        } else {
                                            Log.v("error", e.toString());
                                        }
                                    }
                                });


                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                        mSpacesAvailable.setText(String.valueOf(mSelectedRoute.getSpacesAvailable() - mPassangers.size()));
                    }
                } else {
                    Log.v("error", e.toString());
                }
            }
        });

        if (mPassangers.size() == 0) {
            mSpacesAvailable.setText(String.valueOf(mSelectedRoute.getSpacesAvailable()));
        }
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
                    loadPassangers();

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

        ParseFile fileObject = (ParseFile) mPassangers.get(position).get("profileImage");
        byte[] data = new byte[0];
        try {
            data = fileObject.getData();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        intent.putExtra("targetUser" , new User(
                mPassangers.get(position).getUsername(),
                mPassangers.get(position).getString("name"),
                mPassangers.get(position).getString("surname"),
                mPassangers.get(position).getString("phone"),
                mPassangers.get(position).getEmail(),
                data
        ));
        startActivity(intent);
    }
}

