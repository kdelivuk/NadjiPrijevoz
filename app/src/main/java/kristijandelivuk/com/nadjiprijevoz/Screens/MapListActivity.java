package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.Route;
import kristijandelivuk.com.nadjiprijevoz.helper.TypefaceSpan;
import kristijandelivuk.com.nadjiprijevoz.model.CommentModel;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;
import kristijandelivuk.com.nadjiprijevoz.model.navigation.NavigationDrawerFragment;

public class MapListActivity extends AppCompatActivity implements RVAdapter.ListClickListener {

    private Toolbar mToolbar;

    // recycler
    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;

    // routes
    private List<RouteModel> mRoutes;

    private ArrayList<User> parsePassangers;



    private void initializeData() {

        mRoutes = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {

                    for (final ParseObject item : list) {

                        ParseUser user = (ParseUser) item.get("creator");

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

                        parsePassangers = new ArrayList<>();
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
                                    ParseFile fileObject = (ParseFile) user.get("profileImage");
                                    byte[] data = new byte[0];
                                    try {
                                        data = fileObject.getData();
                                    } catch (com.parse.ParseException ex) {
                                        ex.printStackTrace();
                                    }
                                    parsePassangers.add(new User(
                                            parseUser.getUsername(),
                                            parseUser.getString("name"),
                                            parseUser.getString("surname"),
                                            parseUser.getString("phone"),
                                            parseUser.getEmail(),
                                            data
                                    ));
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }


                            }
                        }

                        ArrayList<CommentModel> comments = new ArrayList<>();
                        JSONArray jsonArrayComments = item.getJSONArray("comments");

                        if (jsonArrayComments != null) {
                            for (int i = 0; i < jsonArrayComments.length(); i++) {

                                try {
                                    JSONObject object = jsonArrayComments.getJSONObject(i);
                                    Log.v("objectID", object.getString("objectId"));
                                    String id = object.getString("objectId");

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
                                    query.whereEqualTo("objectId", id);
                                    ParseObject parseComment = query.getFirst();

                                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                                    userQuery.whereEqualTo("objectId", parseComment.get("creator").toString());
                                    ParseUser parseAuthor = userQuery.getFirst();

                                    ParseFile passangerFileObject = (ParseFile) parseAuthor.get("profileImage");
                                    byte[] authorData = new byte[0];
                                    try {
                                        authorData = passangerFileObject.getData();
                                    } catch (com.parse.ParseException ex) {
                                        ex.printStackTrace();
                                    }

                                    User author = new User(
                                            parseAuthor.getUsername(),
                                            parseAuthor.getString("name"),
                                            parseAuthor.getString("surname"),
                                            parseAuthor.getString("phone"),
                                            parseAuthor.getEmail(),
                                            authorData
                                    );

                                    comments.add(new CommentModel(parseComment.getString("message") , author));

                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        }

                        RouteModel route = new RouteModel(
                                item.get("destination").toString(),
                                item.get("startingPoint").toString(),
                                new User("test", "test", "test", "12345", "test@test.test" ,"Working at Parse is great!".getBytes()),
                                parsePassangers,
                                points,
                                Integer.parseInt(item.get("numberOfSpaces").toString()),
                                item.getObjectId(),
                                item.get("time").toString(),
                                item.get("date").toString(),
                                comments
                        );


                        route.setId(item.getObjectId());
                        Log.v("parsePassangers", parsePassangers.toString());

                        mRoutes.add(route);

                    }

                    mAdapter = new RVAdapter(MapListActivity.this, mRoutes);
                    mAdapter.setListClickListener(MapListActivity.this);

                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    Log.v("error", e.toString());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SpannableString s = new SpannableString("Pregled svih ruta");
        s.setSpan(new TypefaceSpan(this, "Choplin.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(s);

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        initializeData();

    }

    @Override
    public void selectItem(View v, int position) {
        RouteModel selectedRoute = mRoutes.get(position);
        Intent intent = new Intent(MapListActivity.this, RouteDetailActivity.class);
        intent.putExtra("selectedRoute", selectedRoute);
        startActivity(intent);
    }
}

class RVAdapter extends RecyclerView.Adapter<RVAdapter.RouteViewHolder> {

    Context mContext;
    List<RouteModel> routesCV;
    Route rt;
    private ListClickListener mListener;


    public RVAdapter(Context context , List<RouteModel> routes) {
        this.mContext = context;
        this.routesCV = routes;
    }

    public void setListClickListener(ListClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_cardview_item, parent, false);
        RouteViewHolder pvh = new RouteViewHolder(v, routesCV, mContext);
        return pvh;
    }

    private void zoomMapToLatLngBounds(final LinearLayout layout,final GoogleMap mMap, final ArrayList<LatLng> bounds){

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (int i=0; i<bounds.size(); i++) {
            boundsBuilder.include(bounds.get(i));
        }

        final LatLngBounds boundsfinal = boundsBuilder.build();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsfinal, 25));
            }
        });

    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        holder.destination.setText(" - " + routesCV.get(position).getDestination());
        holder.startingPoint.setText(routesCV.get(position).getStartingPoint());
        holder.startingDateAndTime.setText(routesCV.get(position).getDate() + "  " + routesCV.get(position).getTime());
        GoogleMap gMap = holder.map.getMap();

        if (gMap != null) {

            ArrayList<PointModel> mapPosition = routesCV.get(position).getPoints();
            ArrayList<LatLng> points = new ArrayList<LatLng>();

            for (PointModel item : mapPosition) {
                points.add(new LatLng(item.getLatitude(),item.getLongitude()));
            }

            for (LatLng item : points) {
                gMap.addMarker(new MarkerOptions().position(item));
            }

            rt = new Route();

            rt.drawRoute(gMap, mContext, new ArrayList<LatLng>(points), "en", true);
            Log.v("ZZOM" , points + "");
            Log.v("ZZOMM" , gMap + "");
            zoomMapToLatLngBounds(holder.linearlayout, gMap, points);
        }

    }

    @Override
    public void onViewRecycled(RouteViewHolder holder)
    {
        if (holder.map != null)
        {
            holder.map.getMap().clear();
        }
    }

    @Override
    public int getItemCount() {
        return routesCV.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback, View.OnClickListener {

        Context mContext;
        CardView cv;
        TextView destination;
        TextView startingPoint;
        TextView startingDateAndTime;
        MapView map;
        List<RouteModel> routesCV;
        LinearLayout linearlayout;

        RouteViewHolder(View itemView, List<RouteModel> routesCV, Context context) {
            super(itemView);

            Typeface gidole = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Gidole_Regular.ttf");

            mContext = context;
            this.routesCV = routesCV;
            cv = (CardView) itemView.findViewById(R.id.cv);
            destination = (TextView) itemView.findViewById(R.id.destinationCV);
            startingPoint = (TextView) itemView.findViewById(R.id.startingPointCV);
            startingDateAndTime = (TextView) itemView.findViewById(R.id.textDateAndTimeCV);
            destination.setTypeface(gidole);
            startingPoint.setTypeface(gidole);
            startingDateAndTime.setTypeface(gidole);

            map = (MapView) itemView.findViewById(R.id.map_view);
            linearlayout = (LinearLayout) itemView.findViewById(R.id.linearlayout);

            itemView.setOnClickListener(this);


            map.onCreate(null);
            map.onResume();
            map.getMapAsync(this);

        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            MapsInitializer.initialize(mContext);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.selectItem(v , getPosition());
            }
        }
    }

    public interface ListClickListener {
        void selectItem(View v, int position);
    }
}
