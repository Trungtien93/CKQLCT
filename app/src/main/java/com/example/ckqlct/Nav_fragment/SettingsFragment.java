package com.example.ckqlct.Nav_fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ckqlct.Login;
import com.example.ckqlct.Notify;
import com.example.ckqlct.R;

public class SettingsFragment extends Fragment {

    private EditText ten, email;
    private Button btnUpdate, btnExit;
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "QLCTCK.db";

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ten = view.findViewById(R.id.edithoten);
        email = view.findViewById(R.id.editmail);
        btnUpdate = view.findViewById(R.id.btn_edit_profile);
        btnExit = view.findViewById(R.id.btn_logout);

        // Initialize the database
        db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

        // Load user data from the database based on id_user
        loadUserData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newFullname = ten.getText().toString().trim();
                String newEmail = email.getText().toString().trim();

                // Validate input
                if (newFullname.isEmpty() || newEmail.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserInfo(newFullname, newEmail);

                    // Update SharedPreferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("fullname", newFullname);
                    editor.putString("email", newEmail);
                    editor.apply();

                    Toast.makeText(getActivity(), "Information updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle logout or exit action
               getActivity().finish();
            }
        });
        return view;
    }

    // Method to load user data from the database
    private void loadUserData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id_user", -1);

        if (userId != -1) {
            Cursor cursor = db.rawQuery("SELECT fullname, email FROM User WHERE id_user = ?", new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                String currentFullname = cursor.getString(0);
                String currentEmail = cursor.getString(1);

                // Set initial values in EditText fields
                ten.setText(currentFullname);
                email.setText(currentEmail);
            } else {
                Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(getActivity(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInfo(String fullname, String email) {
        try {
            // Fetch user ID from SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("id_user", -1);  // Retrieve id_user

            if (userId != -1) {
                String updateQuery = "UPDATE User SET fullname = ?, email = ? WHERE id_user = ?";
                db.execSQL(updateQuery, new Object[]{fullname, email, userId});
            } else {
                Toast.makeText(getActivity(), "User ID not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to update information", Toast.LENGTH_SHORT).show();
        }
    }
}
