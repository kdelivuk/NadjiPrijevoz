package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;

import com.parse.GetCallback;
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
import kristijandelivuk.com.nadjiprijevoz.helper.ParseCommunicator;
import kristijandelivuk.com.nadjiprijevoz.helper.TypefaceSpan;
import kristijandelivuk.com.nadjiprijevoz.model.CommentModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;
import kristijandelivuk.com.nadjiprijevoz.model.navigation.NavigationDrawerFragment;

/**
 * Created by kdelivuk on 31/08/15.
 */
public class CommentListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ArrayList<CommentModel> mComments;

    // recycler
    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;

    // routes
    private RouteModel mSelectedRoute;

    private void initializeData() {

        mComments = new ArrayList<>();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Route");
        query.whereEqualTo("objectId", mSelectedRoute.getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                if (e == null) {
                    Log.v("ParseObject" , parseObject + "");
                    JSONArray jsonArray = parseObject.getJSONArray("comments");

                    if (jsonArray != null) {

                        Log.v("ParseObject" , jsonArray.length() + "");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.v("ParseObject", object.getString("objectId"));
                                String id = object.getString("objectId");

                                ParseQuery query = ParseQuery.getQuery("Comment");
                                query.whereEqualTo("objectId", id);
                                ParseObject parseObjectQuery = query.getFirst();


                                Log.v("ParseObject", parseObjectQuery.getObjectId() + "");

                                ParseUser creator = parseObjectQuery.getParseUser("creator");

                                ParseFile fileObject = (ParseFile) creator.get("profileImage");
                                byte[] creatorData = new byte[0];
                                try {
                                    creatorData = fileObject.getData();
                                } catch (com.parse.ParseException e1) {
                                    e1.printStackTrace();
                                }

                                User creatorUser = new User(
                                        creator.getUsername(),
                                        creator.getString("name"),
                                        creator.getString("surname"),
                                        creator.getString("phone"),
                                        creator.getEmail(),
                                        creatorData
                                );


                                CommentModel comment = new CommentModel(
                                        parseObjectQuery.getString("message"),
                                        creatorUser

                                );

                                mComments.add(comment);
                                Log.v("mcomments", mComments.size() + "");



                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                    Log.v("mcomments" , mComments.size() + "");
                    mAdapter = new CommentAdapter(mComments);

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
        setContentView(R.layout.activity_view_all_comments);

        SpannableString s = new SpannableString("Pregled svih komentara");
        s.setSpan(new TypefaceSpan(this, "Choplin.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(s);

        Intent intent = getIntent();
        mSelectedRoute = (RouteModel) intent.getParcelableExtra("selectedRouteComments");

        mRecyclerView = (RecyclerView)findViewById(R.id.comments_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        initializeData();

    }
}
