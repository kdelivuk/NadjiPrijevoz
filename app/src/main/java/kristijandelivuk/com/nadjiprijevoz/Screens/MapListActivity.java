package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.Iterator;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.Route;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;

public class MapListActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    // recycler
    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;

    // routes
    private List<RouteModel> mRoutes;

    private void initializeData() {

        mRoutes = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    Log.v("list", String.valueOf(list.size()));

                    for (ParseObject item : list) {

                        ParseUser user = (ParseUser) item.get("creator");

                        JSONArray jsonArray = item.getJSONArray("points");
                        ArrayList<PointModel> points = new ArrayList<PointModel>();

                        for (int i = 0; i<jsonArray.length(); i++) {

                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.v("objectID", object.getString("objectId"));

                                ParseQuery query = ParseQuery.getQuery("Point");
                                query.whereEqualTo("objectId", object.getString("objectId"));
                                ParseObject pointP = query.getFirst();
                                Log.v("lat", pointP.get("lat").toString());
                                Log.v("lng", pointP.get("lng").toString());
                                points.add(new PointModel(pointP.getDouble("lat") , pointP.getDouble("lng")));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                        }

                        RouteModel route = new RouteModel(
                                item.get("destination").toString(),
                                item.get("startingPoint").toString(),
                                new User("test", "test", "test", "12345", "test@test.test"),
                                new ArrayList<User>(),
                                points,
                                Integer.parseInt(item.get("numberOfSpaces").toString())
                        );

                        Log.v("route", route.toString());
                        mRoutes.add(route);


                    }

                    mAdapter = new RVAdapter(MapListActivity.this, mRoutes);

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        initializeData();

        Log.v("list", String.valueOf(mRoutes.size()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile_item) {
            Intent intent = new Intent(MapListActivity.this, NewRouteActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}

class RVAdapter extends RecyclerView.Adapter<RVAdapter.RouteViewHolder> {

    Context mContext;
    int position = 0;
    List<RouteModel> routesCV;
    Route rt;


    public RVAdapter(Context context , List<RouteModel> routes) {
        this.mContext = context;
        this.routesCV = routes;
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
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        holder.destination.setText(routesCV.get(position).getDestination());
        holder.startingPoint.setText(routesCV.get(position).getStartingPoint());

        GoogleMap thisMap = holder.map.getMap();
        //then move map to 'location'
        if (thisMap != null) {

            ArrayList<PointModel> mapPosition = routesCV.get(position).getPoints();
            ArrayList<LatLng> points = new ArrayList<LatLng>();



            for (PointModel item : mapPosition) {

                Log.v("lat,lng", item.getLatitude() + " , " + item.getLongitude());
                points.add(new LatLng(item.getLatitude(),item.getLongitude()));
            }



            Log.v("Points size" , " " + points.size());
            for (LatLng item : points) {
                thisMap.addMarker(new MarkerOptions().position(item));
            }

            rt = new Route();

            rt.drawRoute(thisMap, mContext, new ArrayList<LatLng>(points), "en", true);

            zoomMapToLatLngBounds(holder.linearlayout, thisMap, points);
        }

    }

    @Override
    public void onViewRecycled(RouteViewHolder holder)
    {
        // Cleanup MapView here?
        if (holder.map != null)
        {
            holder.map.getMap().clear();

        }
    }

    @Override
    public int getItemCount() {
        return routesCV.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        Context mContext;
        CardView cv;
        TextView destination;
        TextView startingPoint;
        MapView map;
        List<RouteModel> routesCV;
        int position = 0;
        LinearLayout linearlayout;

        RouteViewHolder(View itemView, List<RouteModel> routesCV, Context context) {
            super(itemView);

            mContext = context;
            this.routesCV = routesCV;
            cv = (CardView) itemView.findViewById(R.id.cv);
            destination = (TextView) itemView.findViewById(R.id.destinationCV);
            startingPoint = (TextView) itemView.findViewById(R.id.startingPointCV);
            map = (MapView) itemView.findViewById(R.id.map_view);
            linearlayout = (LinearLayout) itemView.findViewById(R.id.linearlayout);

            map.onCreate(null);
            map.onResume();
            map.getMapAsync(this);

        }


        @Override
        public void onMapReady(GoogleMap googleMap) {

            MapsInitializer.initialize(mContext);




        }
    }
}
