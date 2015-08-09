package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.Route;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;


public class NewRouteActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = NewRouteActivity.class.getSimpleName();

    private TextView mStartingPoint;
    private TextView mDestination;
    private EditText mNumberOfSpaces;
    private Button mButtonCreate;
    private TextView mDisplayDate;
    private ImageButton mPickDate;

    private List<Marker> routeMarkers;
    private Route rt;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mGoogleMap;
    private ArrayList<LatLng> points;

    private boolean routeIsCalculated;

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

        routeIsCalculated = false;

        final Calendar c = Calendar.getInstance();

        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.create_route_google_map);
        map.getMapAsync(this);
        mGoogleMap = map.getMap();
        rt = new Route();

        buildGoogleApiClient();

        mStartingPoint = (TextView) findViewById(R.id.textStartingLocationPlaceholder);
        mDestination = (TextView) findViewById(R.id.textDestinationPlaceholder);
        mNumberOfSpaces = (EditText) findViewById(R.id.editSpacesAvailable);
        mButtonCreate = (Button) findViewById(R.id.buttonCreate);
        mDisplayDate = (TextView) findViewById(R.id.showDate);
        mPickDate = (ImageButton) findViewById(R.id.imageDatePickerButton);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(NewRouteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mDisplayDate.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
                    }
                }, mYear, mMonth, mDay);

                dpd.show();
            }
        });


        routeMarkers = new ArrayList<>();
        points = new ArrayList<>();

        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (routeIsCalculated) {
                    try {
                        createNewRoute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    rt.clearRoute();
                    rt.drawRoute(mGoogleMap, NewRouteActivity.this, new ArrayList<LatLng>(points), "en", false);
                    routeIsCalculated = true;
                    mButtonCreate.setText("Save Route");

                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                if (routeMarkers.size() < 10) {
                    routeMarkers.add(mGoogleMap.addMarker(new MarkerOptions().position(latLng)));
                    points.add(latLng);
                    Log.v("size", String.valueOf(points.size()));
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
            Toast.makeText(NewRouteActivity.this, "There is currently no connectivity", Toast.LENGTH_LONG).show();
        } else {
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

    public void setupStartingAndEndingLocation() throws IOException{
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addressesBegin = gcd.getFromLocation(points.get(0).latitude, points.get(0).longitude, 1);
        if (addressesBegin.size() > 0) {

            for (Address address : addressesBegin) {
                if (address.getLocality() != "") {
                    mStartingPoint.setText(address.getLocality());
                    break;
                }
            }
        }

        List<Address> addressesEnd = gcd.getFromLocation(points.get(points.size() - 1).latitude, points.get(points.size() - 1).longitude, 1);
        if (addressesEnd.size() > 0) {

            for (Address address : addressesEnd) {
                if (address.getLocality() != "") {
                    mDestination.setText(address.getLocality());
                    break;
                }
            }

        }
    }

    public void createNewRoute() throws IOException {

        setupStartingAndEndingLocation();

        ParseUser currentUser = ParseUser.getCurrentUser();

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

        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(points.get(0).latitude, points.get(0).longitude);

        ParseObject parseRoute = new ParseObject("Route");
        parseRoute.put("destination" , mNewRoute.getDestination());
        parseRoute.put("startingPoint" , mNewRoute.getStartingPoint());
        parseRoute.put("numberOfSpaces" , mNewRoute.getSpacesAvailable());
        parseRoute.put("creator" , currentUser);
        parseRoute.put("startingPointGeo", parseGeoPoint);
        parseRoute.put("points", parsePoints);


        parseRoute.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null) {
                    Toast.makeText(NewRouteActivity.this, "Route created successfully.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NewRouteActivity.this, "Error while creating route: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
        Toast.makeText(NewRouteActivity.this, "Location services suspended. Please reconnect." , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Please try to reconnect.");
        Toast.makeText(NewRouteActivity.this, "Connection failed. Please try to reconnect." , Toast.LENGTH_LONG).show();
    }
}
