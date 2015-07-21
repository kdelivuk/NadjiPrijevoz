package kristijandelivuk.com.nadjiprijevoz;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import kristijandelivuk.com.nadjiprijevoz.model.Route;
import kristijandelivuk.com.nadjiprijevoz.model.User;


public class NewRouteActivity extends ActionBarActivity {

    EditText mStartingPoint;
    EditText mDestination;
    EditText mNumberOfSpaces;
    Button mButtonCreate;
    Route mNewRoute;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        mStartingPoint = (EditText) findViewById(R.id.editStartingPoint);
        mDestination = (EditText) findViewById(R.id.editDestination);
        mNumberOfSpaces = (EditText) findViewById(R.id.editSpacesAvailable);
        mButtonCreate = (Button) findViewById(R.id.buttonCreate);

        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRoute();
            }
        });

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

        mNewRoute = new Route(
                mDestination.getText().toString(),
                mStartingPoint.getText().toString(),
                user,
                Integer.parseInt(mNumberOfSpaces.getText().toString())
        );

        ParseObject parseRoute = new ParseObject("Route");
        parseRoute.put("destination" , mNewRoute.getDestination());
        parseRoute.put("startingPoint" , mNewRoute.getStartingPoint());
        parseRoute.put("numberOfSpaces" , mNewRoute.getSpacesAvailable());
        parseRoute.put("creator" , currentUser);

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




}
