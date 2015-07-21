package kristijandelivuk.com.nadjiprijevoz;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import kristijandelivuk.com.nadjiprijevoz.model.User;


public class RegisterActivity extends ActionBarActivity {

    Button mRegister;
    EditText mName;
    EditText mPassword;
    EditText mSurname;
    EditText mPhone;
    EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegister = (Button) findViewById(R.id.buttonRegister);

        mName = (EditText) findViewById(R.id.editName);
        mSurname = (EditText) findViewById(R.id.editSurname);
        mPhone = (EditText) findViewById(R.id.editPhone);
        mEmail = (EditText) findViewById(R.id.editEmail);
        mPassword = (EditText) findViewById(R.id.editPassword);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser() {

        String name = mName.getText().toString().trim();
        String surname = mSurname.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        String email = mEmail.getText().toString().trim();

        String username = generateUsername(name, surname);

        ParseUser newUser = new ParseUser();

        newUser.setUsername(username);
        Log.v("username", username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        Log.v("password", password);
        newUser.put("name", name);
        newUser.put("surname" , surname);
        newUser.put("phone", phone);

        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
                    startActivity(intent);
                } else {
                    Log.v("Error:", e.toString());
                }
            }
        });

    }

    public String generateUsername(String name, String surname) {
        return name.substring(0,1).toLowerCase() + surname.toLowerCase();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
