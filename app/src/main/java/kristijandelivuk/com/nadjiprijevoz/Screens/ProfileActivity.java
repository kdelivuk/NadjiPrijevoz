package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseUser;

import kristijandelivuk.com.nadjiprijevoz.R;


public class ProfileActivity extends ActionBarActivity {

    RatingBar mUserRating;
    TextView mUserNameAndSurname;
    TextView mUserPhoneNumber;
    TextView mUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUserRating = (RatingBar) findViewById(R.id.ratingBar);
        mUserNameAndSurname = (TextView) findViewById(R.id.textNameAndSurname);
        mUserPhoneNumber = (TextView) findViewById(R.id.textPhone);
        mUserEmail = (TextView) findViewById(R.id.textEmail);

        ParseUser user = new ParseUser().getCurrentUser();

        mUserEmail.setText(user.getEmail());
        mUserPhoneNumber.setText(user.get("phone").toString());
        mUserNameAndSurname.setText(user.get("name").toString() + " " + user.get("surname").toString());
        mUserRating.setMax(5);
        mUserRating.setRating(3);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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