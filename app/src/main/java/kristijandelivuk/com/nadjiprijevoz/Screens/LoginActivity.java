package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import kristijandelivuk.com.nadjiprijevoz.R;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private TextView mTitle;
    private TextView mSubtitle;
    private Button mLogin;
    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Enabling Parse

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "w62mIaRwUtHlC8llfmbTaCb0Z7vjgCrDgI4j1HZY", "yE28Zo8hEZbBmc2NwD03LSzoEeL6uotJfs9dl61p");

        ParseUser.enableRevocableSessionInBackground();

        // Fallback if user stayed logged in

        ParseUser currentUser = new ParseUser().getCurrentUser();
        if (currentUser != null) {
            currentUser.logOut();
        }

        // Init

        mTitle = (TextView) findViewById(R.id.titleLabel);
        mSubtitle = (TextView) findViewById(R.id.subtitleLabel);
        mUsername = (EditText) findViewById(R.id.editUsername);
        mPassword = (EditText) findViewById(R.id.editPassword);
        mLogin = (Button) findViewById(R.id.loginButton);
        mRegister = (Button) findViewById(R.id.buttonRegister);

        // Font

        Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/robotoregular.ttf");

        mTitle.setTypeface(robotoRegular);
        mSubtitle.setTypeface(robotoRegular);
        mUsername.setTypeface(robotoRegular);
        mPassword.setTypeface(robotoRegular);

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

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {

                if (e == null) {
                    Intent intent = new Intent(LoginActivity.this, FullScreenMapActivity.class);
                    startActivity(intent);
                } else {
                    Log.v("Log in error: ", e.toString());
                }

            }
        });
    }

    private void registerUser() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
