package com.example.ckqlct;

import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
    EditText username,password;
    Button eregister,elogin;
    boolean isAllFields = false;
    private boolean CheckAllField (String username, String password)
    {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(),"Please enter name",Toast.LENGTH_LONG).show();
            return false;

        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),"please enter proper password",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        eregister = findViewById(R.id.register1);
        elogin= findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        Intent i = getIntent();
        String a = " ";
        String b = " ";
        try {
            a = i.getStringExtra("number1");
            b = i.getStringExtra("number2");
        } catch (NumberFormatException e) {
            Log.d("error1", "user not give input");

        }
        username.setText(a);
        password.setText(b);
//99999999
        eregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(Login.this,RegisterActivity.class);

                startActivity(in);


            }
        });

        elogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gọi CheckAllField để kiểm tra trước khi chuyển tiếp
                if (CheckAllField(username.getText().toString(), password.getText().toString())) {
                    Intent in = new Intent(Login.this, MainActivity.class);
                    startActivity(in);
                }
            }
        });
    }

}
