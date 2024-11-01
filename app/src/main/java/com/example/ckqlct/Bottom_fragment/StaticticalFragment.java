package com.example.ckqlct.Bottom_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ckqlct.Changepass;
import com.example.ckqlct.Nav_fragment.SettingsFragment;
import com.example.ckqlct.R;
import com.example.ckqlct.Rating;
import com.example.ckqlct.ThemChiTieu;

public class StaticticalFragment extends Fragment {

    private TextView ten, txtChangeArea, txtRateApp;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;

    public StaticticalFragment() {
        // Required empty public constructor
    }

    public static StaticticalFragment newInstance(String param1, String param2) {
        StaticticalFragment fragment = new StaticticalFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statictical, container, false);

        // Initialize views from the fragment's layout
        ten = view.findViewById(R.id.txtTitle);
        txtChangeArea = view.findViewById(R.id.txtChangeArea);
        txtRateApp = view.findViewById(R.id.txtRateApp);
        TextView thongtin = view.findViewById(R.id.txtPersonalInfo);
        TextView Caidat = view.findViewById(R.id.txtSettings);

        // Initialize SharedPreferences and database
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        db = getActivity().openOrCreateDatabase("QLCTCK.db", Context.MODE_PRIVATE, null);

        // Load fullname from database if not in SharedPreferences
        String fullname = sharedPreferences.getString("fullname", null);
        if (fullname == null) {
            int userId = sharedPreferences.getInt("id_user", -1);  // Get user ID
            if (userId != -1) {
                Cursor cursor = db.rawQuery("SELECT fullname FROM User WHERE id_user = ?", new String[]{String.valueOf(userId)});
                if (cursor.moveToFirst()) {
                    fullname = cursor.getString(0);
                    // Save fullname to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("fullname", fullname);
                    editor.apply();
                }
                cursor.close();
            }
        }

        // Display fullname in the TextView
        ten.setText(fullname != null ? fullname : "Guest");

        // Navigate to SettingsFragment when "Personal Info" is clicked
        thongtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsFragment settingsFragment = new SettingsFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frament_layout, settingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Set up onClick listeners for Change Password and Rate App
        txtChangeArea.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Changepass.class);
            startActivity(intent);
        });

        txtRateApp.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Rating.class);
            startActivity(intent);
        });
        Caidat.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ThemChiTieu.class);
            startActivity(intent);
        });

        return view;
    }
}
