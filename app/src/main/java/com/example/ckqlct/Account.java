package com.example.ckqlct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ckqlct.Nav_fragment.changepass;

public class Account extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_statictical);

//        // TextView for personal information
//        TextView personalInfoTextView = findViewById(R.id.txtPersonalInfo);
//        personalInfoTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start new Activity after the ripple effect
//                Intent intent = new Intent(Account.this, Login.class);
//                startActivity(intent);
//            }
//        });
//
//        // TextView for settings
//        TextView settingsTextView = findViewById(R.id.txtSettings);
//        settingsTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Account.this, Login.class);
//                startActivity(intent);
//            }
//        });

        // Textview for local
        TextView localsTextview = findViewById (R.id.txtChangeArea);
        localsTextview.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Account.this, changepass.class);
                startActivity (intent);
            }
        });

    }
}
