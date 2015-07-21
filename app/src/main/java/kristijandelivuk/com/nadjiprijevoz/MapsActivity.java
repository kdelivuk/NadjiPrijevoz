package kristijandelivuk.com.nadjiprijevoz;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.model.Route;
import kristijandelivuk.com.nadjiprijevoz.model.User;

public class MapsActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Toolbar mToolbar;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    // recycler
    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;

    // routes
    private List<Route> mRoutes;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initializeData(){

        mRoutes = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    Log.v("list", String.valueOf(list.size()));

                    for (ParseObject item : list) {

                        ParseUser user = (ParseUser) item.get("creator");

                        Route route = new Route(
                                item.get("destination").toString(),
                                item.get("startingPoint").toString(),
                                new User("test", "test", "test", "12345", "test@test.test"),
                                Integer.parseInt(item.get("numberOfSpaces").toString())
                        );

                        Log.v("route", route.toString());
                        mRoutes.add(route);

                    }

                    mAdapter = new RVAdapter(MapsActivity.this, mRoutes);

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
        setUpMapIfNeeded();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        initializeData();

        buildGoogleApiClient();
        mGoogleApiClient.connect();

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
            Intent intent = new Intent(MapsActivity.this, NewRouteActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

    }

    private String currentLocation = "";

    @Override
    public void onConnected(Bundle bundle) {


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if (mLastLocation != null) {
            currentLocation = String.valueOf(mLastLocation.getLatitude());
            currentLocation += " , " + String.valueOf(mLastLocation.getLongitude());
            Toast.makeText(this, currentLocation , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "koji kurac oces" , Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

class RVAdapter extends RecyclerView.Adapter<RVAdapter.RouteViewHolder> {

    Context mContext;

    List<Route> routesCV;

    public RVAdapter(Context context , List<Route> routes) {
        this.mContext = context;
        this.routesCV = routes;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_cardview_item, parent, false);
        RouteViewHolder pvh = new RouteViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        holder.destination.setText(routesCV.get(position).getDestination());
        holder.startingPoint.setText(routesCV.get(position).getStartingPoint());
    }

    @Override
    public int getItemCount() {
        return routesCV.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView destination;
        TextView startingPoint;


        RouteViewHolder(View itemView) {
            super(itemView);

            cv = (CardView) itemView.findViewById(R.id.cv);
            destination = (TextView) itemView.findViewById(R.id.destinationCV);
            startingPoint = (TextView) itemView.findViewById(R.id.startingPointCV);

        }

    }
}
