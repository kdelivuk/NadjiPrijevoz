package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.ParseCommunicator;
import kristijandelivuk.com.nadjiprijevoz.helper.TypefaceSpan;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.navigation.NavigationDrawerFragment;


public class FullScreenMapActivity extends AppCompatActivity implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = FullScreenMapActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;
    private List<Marker> mMarkers;
    private boolean doubleClickIndicator;

    // GoogleApiClient

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // LifeCycle

    protected void getMarkersFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> routeObjects, com.parse.ParseException e) {
                if (e == null) {

                    for (ParseObject object : routeObjects) {
                        String id = object.getObjectId().toString();
                        double longitude = object.getParseGeoPoint("startingPointGeo").getLongitude();
                        double latitude = object.getParseGeoPoint("startingPointGeo").getLatitude();
                        String title = object.getString("startingPoint");
                        String destination = object.getString("destination");

                        Marker marker = mGoogleMap.addMarker(
                                new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .title("#id: " + id)
                                        .snippet(title + " - " + destination + "\nStart date: " + object.getString("date") + "\nStart time: " + object.getString("time")));


                        mMarkers.add(marker);

                    }
                }
            }

        });
    }

    // Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_map);

        SpannableString s = new SpannableString("Pregled obliznjih ruta");
        s.setSpan(new TypefaceSpan(this, "Choplin.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        doubleClickIndicator = false;

        mMarkers = new ArrayList<>();
        buildGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleMap = mapFragment.getMap();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(s);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        getMarkersFromParse();

        mGoogleMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater(), this));

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (doubleClickIndicator == false) {
                    doubleClickIndicator = true;
                } else {
                    doubleClickIndicator = false;
                    String selected = marker.getTitle().toString().substring(5);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");
                    query.whereEqualTo("objectId", selected);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, com.parse.ParseException e) {

                            RouteModel selectedRoute = ParseCommunicator.getInstance().convertToRouteModel(parseObject);

                            Intent intent = new Intent(FullScreenMapActivity.this, RouteDetailActivity.class);
                            intent.putExtra("selectedRoute", selectedRoute);
                            startActivity(intent);

                        }
                    });
                }

                return false;
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (lastLocation == null) {
            Toast.makeText(FullScreenMapActivity.this, "There is currently no connectivity" , Toast.LENGTH_LONG).show();
        }
        else {

            ParseGeoPoint point = new ParseGeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put("location", point);
            currentUser.saveInBackground();

            mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                    .zoom(12)
                    .bearing(90)
                    .tilt(30)
                    .build();

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
        Toast.makeText(FullScreenMapActivity.this, "Location services suspended. Please reconnect." , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Please try to reconnect.");
        Toast.makeText(FullScreenMapActivity.this, "Connection failed. Please try to reconnect." , Toast.LENGTH_LONG).show();
    }
}
