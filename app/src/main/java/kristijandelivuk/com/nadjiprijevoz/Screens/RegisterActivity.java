package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

import kristijandelivuk.com.nadjiprijevoz.R;


public class RegisterActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mName;
    private EditText mPassword;
    private EditText mSurname;
    private EditText mPhone;
    private EditText mEmail;
    private int usernameCounter;
    private boolean didNotGenerateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameCounter = 0;
        didNotGenerateUser = true;

        mRegister = (Button) findViewById(R.id.buttonRegister);

        mName = (EditText) findViewById(R.id.editName);
        mSurname = (EditText) findViewById(R.id.editSurname);
        mPhone = (EditText) findViewById(R.id.editPhone);
        mEmail = (EditText) findViewById(R.id.editEmail);
        mPassword = (EditText) findViewById(R.id.editPassword);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerUser();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void registerUser() throws ParseException {
        Log.v("fdafa", "r424");


        /*while (didNotGenerateUser) {
            String newUsername = username;

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", newUsername);
            ParseUser userFound = query.getFirst();

            if (userFound != null) {
                usernameCounter++;
                newUsername = username + usernameCounter;
            } else {
                didNotGenerateUser = false;
            }
        }*/



        Resources res = getResources();
        Drawable d = res.getDrawable(R.mipmap.default_profile);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        final ParseFile file = new ParseFile("profileImage" , bitmapdata);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

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
                newUser.put("profileImage", file);

                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            Intent intent = new Intent(RegisterActivity.this, FullScreenMapActivity.class);
                            startActivity(intent);
                        } else {
                            Log.v("Error:", e.toString());
                        }
                    }
                });
            }
        });


    }

    public String generateUsername(String name, String surname) {
        return name.substring(0,1).toLowerCase() + surname.toLowerCase();
    }

}
