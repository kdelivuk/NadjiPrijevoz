package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.Route;
import kristijandelivuk.com.nadjiprijevoz.helper.TypefaceSpan;
import kristijandelivuk.com.nadjiprijevoz.model.CommentModel;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;
import kristijandelivuk.com.nadjiprijevoz.model.navigation.NavigationDrawerFragment;


public class NewRouteActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = NewRouteActivity.class.getSimpleName();

    private TextView mStartingPoint;
    private TextView mDestination;
    private EditText mNumberOfSpaces;
    private Button mButtonCreate;
    private TextView mDisplayDate;
    private ImageButton mPickDate;
    private TextView mDisplayTime;
    private ImageButton mPickTime;
    private TextView mTextSpacesAvailable;

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

        SpannableString s = new SpannableString("Nova ruta");
        s.setSpan(new TypefaceSpan(this, "Choplin.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        mDisplayTime = (TextView) findViewById(R.id.showTime);
        mPickTime = (ImageButton) findViewById(R.id.imageTimePickerButton);
        mTextSpacesAvailable = (TextView) findViewById(R.id.textSpacesAvailable);

        Typeface gidole = Typeface.createFromAsset(getAssets(), "fonts/Gidole_Regular.ttf");

        mStartingPoint.setTypeface(gidole);
        mDestination.setTypeface(gidole);
        mNumberOfSpaces.setTypeface(gidole);
        mButtonCreate.setTypeface(gidole);
        mDisplayDate.setTypeface(gidole);
        mDisplayTime.setTypeface(gidole);
        mTextSpacesAvailable.setTypeface(gidole);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(s);

        mPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
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

        mPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(NewRouteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mDisplayTime.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true);

                tpd.show();
            }
        });



        routeMarkers = new ArrayList<>();
        points = new ArrayList<>();

        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (routeIsCalculated) {
                    if (!(mDisplayDate.getText().toString().trim().equals("")) &&
                            !(mDisplayTime.getText().toString().trim().equals("")) &&
                            !(mNumberOfSpaces.getText().toString().trim().equals(""))) {
                        try {
                            createNewRoute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(NewRouteActivity.this, "Unesite datum, vrijeme i broj slobodnih mjesta." , Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (points.size() >= 2) {
                        rt.clearRoute();
                        rt.drawRoute(mGoogleMap, NewRouteActivity.this, new ArrayList<LatLng>(points), "en", false);
                        routeIsCalculated = true;
                        mButtonCreate.setText("Save Route");
                        try {
                            setupStartingAndEndingLocation();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(NewRouteActivity.this, "Unesite 2 ili vise tocke u mapu." , Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

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
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 12));

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

        ParseFile fileObject = (ParseFile) currentUser.get("profileImage");
        byte[] data = new byte[0];
        try {
            data = fileObject.getData();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        User user = new User(
                currentUser.getUsername(),
                currentUser.get("name").toString(),
                currentUser.get("surname").toString(),
                currentUser.get("phone").toString(),
                currentUser.getEmail(),
                data
        );

        RouteModel mNewRoute = new RouteModel(
                mDestination.getText().toString(),
                mStartingPoint.getText().toString(),
                user,
                new ArrayList<User>(),
                new ArrayList<PointModel>(),
                Integer.parseInt(mNumberOfSpaces.getText().toString()),
                "",
                mDisplayTime.getText().toString(),
                mDisplayDate.getText().toString(),
                new ArrayList<CommentModel>()
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
        parseRoute.put("startingPoint", mNewRoute.getStartingPoint());
        parseRoute.put("numberOfSpaces", mNewRoute.getSpacesAvailable());
        parseRoute.put("creator" , currentUser);
        parseRoute.put("startingPointGeo", parseGeoPoint);
        parseRoute.put("points", parsePoints);
        parseRoute.put("date" , mNewRoute.getDate());
        parseRoute.put("time" , mNewRoute.getTime());

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
