package kristijandelivuk.com.nadjiprijevoz.model.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.Screens.FullScreenMapActivity;
import kristijandelivuk.com.nadjiprijevoz.Screens.MapListActivity;
import kristijandelivuk.com.nadjiprijevoz.Screens.NewRouteActivity;
import kristijandelivuk.com.nadjiprijevoz.Screens.ProfileActivity;
import kristijandelivuk.com.nadjiprijevoz.model.User;

/**
 * Created by kdelivuk on 30/07/15.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerAdapter.ClickListener {

    private static String TAG = NavigationDrawerFragment.class.getSimpleName();

    public static final String PREF_FILE_NAME = "navstatus";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mContainerView;
    private NavigationDrawerAdapter mDrawerAdapter;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private RecyclerView mRecyclerView;


    public NavigationDrawerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.navigation_drawer_fragment, container, mFromSavedInstanceState  );

        mRecyclerView = (RecyclerView) layout.findViewById(R.id.navigation_drawer_recycler_view);
        mDrawerAdapter = new NavigationDrawerAdapter(getActivity(), getData());
        mDrawerAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mDrawerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public static List<NavigationDrawerItem> getData() {
        List<NavigationDrawerItem> navigationDrawerItemList = new ArrayList<NavigationDrawerItem>();

        String[] navigationDrawerTitles = { "Nova ruta" , "Pregled obližnjih ruta" , "Pregled svih ruta " , "Profil" , };
        int[] navigationDrawerImages = { R.mipmap.newroute_image , R.mipmap.fullscreen_image , R.mipmap.recycler_image , R.mipmap.profile_image };

        for (int i = 0; i<navigationDrawerTitles.length; i++) {

            NavigationDrawerItem item = new NavigationDrawerItem(navigationDrawerTitles[i], navigationDrawerImages[i]);
            navigationDrawerItemList.add(item);
        }

        return navigationDrawerItemList;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {

        mContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWER, String.valueOf(mUserLearnedDrawer));
                }

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mContainerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences (Context context, String preferenceName, String preferenceValue) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences (Context context, String preferenceName, String defaultValue) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    @Override
    public void selectedItem(View view, int position) {
        Log.v("position" , position + "");
        switch (position) {
            case 1:
                startActivity(new Intent(getActivity() , NewRouteActivity.class));
                break;
            case 2:
                startActivity(new Intent(getActivity() , FullScreenMapActivity.class));
                break;
            case 3:
                startActivity(new Intent(getActivity() , MapListActivity.class));
                break;
            case 4:
                Intent intent = new Intent(getActivity() , ProfileActivity.class);

                ParseQuery query = ParseUser.getCurrentUser().getQuery();
                ParseUser user = null;
                try {
                    user = (ParseUser) query.getFirst();
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                }
                ParseFile fileObject = (ParseFile) user.get("profileImage");
                byte[] data = new byte[0];
                try {
                    data = fileObject.getData();
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                }

                intent.putExtra("targetUser" , new User(
                        ParseUser.getCurrentUser().getUsername(),
                        ParseUser.getCurrentUser().getString("name"),
                        ParseUser.getCurrentUser().getString("surname"),
                        ParseUser.getCurrentUser().getString("phone"),
                        ParseUser.getCurrentUser().getEmail(),
                        data
                ));
                startActivity(intent);
                break;
        }
    }
}
