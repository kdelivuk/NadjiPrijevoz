package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.Route;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;


public class NewRouteActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    EditText mStartingPoint;
    EditText mDestination;
    EditText mNumberOfSpaces;
    Button mButtonCreate;
    User currentUser;
    List<Marker> routeMarkers;
    private Route rt;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mGoogleMap;
    private ArrayList<LatLng> points;


    // GoogleApiClient

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.create_route_google_map);
        map.getMapAsync(this);
        mGoogleMap = map.getMap();
        rt = new Route();

        buildGoogleApiClient();

        mStartingPoint = (EditText) findViewById(R.id.editStartingPoint);
        mDestination = (EditText) findViewById(R.id.editDestination);
        mNumberOfSpaces = (EditText) findViewById(R.id.editSpacesAvailable);
        mButtonCreate = (Button) findViewById(R.id.buttonCreate);

        routeMarkers = new ArrayList<Marker>();
        points = new ArrayList<LatLng>();

        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rt.clearRoute();
                //rt.drawRoute(mGoogleMap, NewRouteActivity.this, points.get(0), points.get(1), "en");
                rt.drawRoute(mGoogleMap, NewRouteActivity.this, new ArrayList<LatLng>(points), "en", false);
                createNewRoute();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.v("size" , String.valueOf(routeMarkers.size()));
                if (routeMarkers.size() < 10) {
                    routeMarkers.add(mGoogleMap.addMarker(new MarkerOptions().position(latLng)));
                    points.add(latLng);
                    //mGoogleMap.addMarker(new MarkerOptions().position(latLng));
                }

            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Iterator<LatLng> itLatLng = points.listIterator();

                while (itLatLng.hasNext()) {

                    LatLng item = itLatLng.next();

                    if ((item.latitude == marker.getPosition().latitude) &&
                            (item.longitude == marker.getPosition().longitude)) {

                        itLatLng.remove();

                    }

                }

                Iterator<Marker> it = routeMarkers.listIterator();

                while (it.hasNext()) {

                    Marker item = it.next();

                    if ((item.getPosition().latitude == marker.getPosition().latitude) &&
                            (item.getPosition().longitude == marker.getPosition().longitude)) {

                        it.remove();
                        marker.remove();

                    }

                }

                return false;
            }
        });


    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation == null) {
            // No Connectivity Toast
        }
        else {

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude() , mLastLocation.getLongitude()) , 12));

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    public void createNewRoute() {

        ParseUser currentUser = ParseUser.getCurrentUser();

        Log.v("currentuser" , currentUser.toString());

        User user = new User(
                currentUser.getUsername(),
                currentUser.get("name").toString(),
                currentUser.get("surname").toString(),
                currentUser.get("phone").toString(),
                currentUser.getEmail()
        );

        RouteModel mNewRoute = new RouteModel(
                mDestination.getText().toString(),
                mStartingPoint.getText().toString(),
                user,
                new ArrayList<User>(),
                new ArrayList<PointModel>(),
                Integer.parseInt(mNumberOfSpaces.getText().toString())
        );

        ParseObject point;

        ArrayList<ParseObject> parsePoints = new ArrayList<ParseObject>();

        Iterator<LatLng> itLatLng = points.listIterator();

        while (itLatLng.hasNext()) {

            LatLng item = itLatLng.next();

            point = new ParseObject("Point");

            point.put("lat", item.latitude);
            point.put("lng", item.longitude);

            parsePoints.add(point);


        }

        Iterator<ParseObject> iterat = parsePoints.listIterator();

        while (iterat.hasNext()) {
            ParseObject item = iterat.next();
            Log.v("point" , item.get("lat") + " ");
            Log.v("point" , item.get("lng") + " ");

        }

        ParseObject parseRoute = new ParseObject("Route");
        parseRoute.put("destination" , mNewRoute.getDestination());
        parseRoute.put("startingPoint" , mNewRoute.getStartingPoint());
        parseRoute.put("numberOfSpaces" , mNewRoute.getSpacesAvailable());
        parseRoute.put("creator" , currentUser);
        parseRoute.put("points", parsePoints);


        parseRoute.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e==null) {

                    Log.v("done" , "");
                } else {
                    Log.v("error" , e.toString());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
