package com.sohailhaider.omlate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
    EditText et;
    String Email;
    String Password;
    private Sessions session = new Sessions(getApplicationContext());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = (Button) findViewById(R.id.loginbutton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et = (EditText) findViewById(R.id.email);
                if( et.getText().toString().trim().equals(""))
                {
                    et.setError( "Your email address is required!" );
                    et.setHint("Email Address Here.");
                    et.requestFocus();
                    return;
                }
                if( !Patterns.EMAIL_ADDRESS.matcher(et.getText().toString()).matches())
                {
                    et.setError( "Invalid email address!" );
                    et.setHint("Valid Email Address.");
                    et.requestFocus();
                    return;
                }
                Email = et.getText().toString();

                et = (EditText) findViewById(R.id.password);
                if( et.getText().toString().trim().equals(""))
                {
                    et.setError( "Your password is required!" );
                    et.requestFocus();
                    return;
                }
                Password = et.getText().toString();
                session.putString("email", Email);
            }
        });
    }
}
