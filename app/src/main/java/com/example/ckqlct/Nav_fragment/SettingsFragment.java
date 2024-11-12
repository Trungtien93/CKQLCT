package com.example.ckqlct.Nav_fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ckqlct.Changepass;
import com.example.ckqlct.Login;
import com.example.ckqlct.Notify;
import com.example.ckqlct.R;
import com.example.ckqlct.ThemChiTieu;

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
        View view = inflater.inflate (R.layout.fragment_settings, container, false);


        // Initialize the database
        db = getActivity ().openOrCreateDatabase (DATABASE_NAME, Context.MODE_PRIVATE, null);

        @SuppressLint ({"MissingInflatedId", "LocalSuppress"})
        TextView Doimatkhau = view.findViewById(R.id.txtChangepassword);
        @SuppressLint ({"MissingInflatedId", "LocalSuppress"})
        TextView PersonalInfo = view.findViewById(R.id.txtPersonalInfo);

        // Set up onClick listeners for even
        Doimatkhau.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Changepass.class);
            startActivity(intent);
        });
        PersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountFragment accountFragment = new AccountFragment ();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frament_layout, accountFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }
}
