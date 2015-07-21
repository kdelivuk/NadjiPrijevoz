package kristijandelivuk.com.nadjiprijevoz;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class LoginActivity extends ActionBarActivity {

    EditText mUsername;
    EditText mPassword;
    TextView mTitle;
    TextView mSubtitle;
    Button mLogin;
    Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "w62mIaRwUtHlC8llfmbTaCb0Z7vjgCrDgI4j1HZY", "yE28Zo8hEZbBmc2NwD03LSzoEeL6uotJfs9dl61p");
        ParseUser.enableRevocableSessionInBackground();



        ParseUser currentUser = new ParseUser().getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            currentUser.logOut();
        }

        // Init

        mTitle = (TextView) findViewById(R.id.titleLabel);
        mSubtitle = (TextView) findViewById(R.id.subtitleLabel);
        mUsername = (EditText) findViewById(R.id.editUsername);
        mPassword = (EditText) findViewById(R.id.editPassword);
        mLogin = (Button) findViewById(R.id.loginButton);
        mRegister = (Button) findViewById(R.id.buttonRegister);


        Typeface bosomFont = Typeface.createFromAsset(getAssets(), "fonts/besom.ttf");

        mTitle.setTypeface(bosomFont);
        mSubtitle.setTypeface(bosomFont);
        mUsername.setTypeface(bosomFont);
        mPassword.setTypeface(bosomFont);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void loginUser() {

        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        ParseUser currentUser = new ParseUser();
        currentUser.setUsername(username);
        currentUser.setPassword(password);

        currentUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {

                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });

    }

    private void registerUser() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
