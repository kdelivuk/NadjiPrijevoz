package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.ParseCommunicator;
import kristijandelivuk.com.nadjiprijevoz.helper.TypefaceSpan;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;
import kristijandelivuk.com.nadjiprijevoz.model.navigation.NavigationDrawerFragment;


public class ProfileActivity extends AppCompatActivity implements ProfileMapAdapter.ListClickListener {

    private TextView mUserNameAndSurname;
    private TextView mUserPhoneNumber;
    private TextView mUserEmail;
    private Toolbar mToolbar;
    private List<RouteModel> mRoutes;
    private ImageView mProfileImage;
    private RecyclerView mRecyclerView;
    private ProfileMapAdapter mProfileMapAdapter;
    private User user;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        SpannableString s = new SpannableString("Profil");
        s.setSpan(new TypefaceSpan(this, "Choplin.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        user = getIntent().getParcelableExtra("targetUser");
        Log.v("username", user.getUsername());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(s);

        Typeface gidole = Typeface.createFromAsset(getAssets(), "fonts/Gidole_Regular.ttf");
        Typeface choplin = Typeface.createFromAsset(getAssets(), "fonts/Choplin.otf");

        mUserNameAndSurname = (TextView) findViewById(R.id.textNameSurname);
        mUserPhoneNumber = (TextView) findViewById(R.id.textPhone);
        mUserEmail = (TextView) findViewById(R.id.textEmail);
        mProfileImage = (ImageView) findViewById(R.id.imageProfile);

        mUserNameAndSurname.setTypeface(choplin);
        mUserPhoneNumber.setTypeface(gidole);
        mUserEmail.setTypeface(gidole);

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(llm);

        try {
            userId = getUserId();
            Log.v("userId" , userId + " username " + user.getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setupCurrentUser();
        findSignedRoutesForCurrentUser();

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

    }


    private String getUserId() throws ParseException {


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", user.getUsername());
        ParseUser parseUser = query.getFirst();
        String userId = parseUser.getObjectId();

        return userId;

    }

    private void setupCurrentUser() {


        if (user != null) {
            mUserEmail.setText(user.getEmail());
            mUserPhoneNumber.setText(user.getPhoneNumber());
            mUserNameAndSurname.setText(user.getName() + " " + user.getSurname());


            byte[] data = user.getData();

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            mProfileImage.setImageBitmap(bitmap);
        }
    }

    private void findSignedRoutesForCurrentUser() {
        mRoutes = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    Log.v("list.length" , list.size() + "");

                    for (ParseObject item : list) {

                        JSONArray jsonArray = item.getJSONArray("passangers");

                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {

                                try {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Log.v("objec" , object.toString());
                                    String id = object.getString("objectId");
                                    Log.v("comparison" , id + "=" + userId);

                                    if (id.equals(userId)) {
                                        Log.v("true" , "true");
                                        mRoutes.add(ParseCommunicator.getInstance().convertToRouteModel(item));
                                        Log.v("mroutes" , mRoutes.size() + "");
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    }

                    mProfileMapAdapter = new ProfileMapAdapter(ProfileActivity.this, mRoutes);
                    mProfileMapAdapter.setListClickListener(ProfileActivity.this);
                    mRecyclerView.setAdapter(mProfileMapAdapter);
                } else {
                    Log.v("error", e.toString());
                }
            }
        });
    }


    @Override
    public void selectItem(View v, int position) {
        RouteModel selectedRoute = mRoutes.get(position);
        Intent intent = new Intent(ProfileActivity.this, RouteDetailActivity.class);
        intent.putExtra("selectedRoute", selectedRoute);
        startActivity(intent);
    }
}
