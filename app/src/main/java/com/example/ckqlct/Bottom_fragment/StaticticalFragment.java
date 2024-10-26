package com.example.ckqlct.Bottom_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.ckqlct.Login;
import com.example.ckqlct.Nav_fragment.AccountFragment;
import com.example.ckqlct.Nav_fragment.SettingsFragment;
import com.example.ckqlct.R;
import com.example.ckqlct.RegisterActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaticticalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaticticalFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StaticticalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StaticticalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StaticticalFragment newInstance(String param1, String param2) {
        StaticticalFragment fragment = new StaticticalFragment ();
        Bundle args = new Bundle ();
        args.putString (ARG_PARAM1, param1);
        args.putString (ARG_PARAM2, param2);
        fragment.setArguments (args);
        return fragment;
    }
    TextView thongtin ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (getArguments () != null) {
            mParam1 = getArguments ().getString (ARG_PARAM1);
            mParam2 = getArguments ().getString (ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statictical, container, false);

        // Initialize views from the fragment's layout
        TextView ten = view.findViewById(R.id.txtTitle);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String fullname = sharedPreferences.getString("fullname", "Guest");  // Default to "Guest" if no username found

        ten.setText(fullname);  // Display the username
        thongtin = view.findViewById(R.id.txtPersonalInfo);
        thongtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo instance của SettingsFragment
                SettingsFragment settingsFragment = new SettingsFragment();

                // Bắt đầu một giao dịch fragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Thay thế (replace) StaticticalFragment bằng SettingsFragment
                transaction.replace(R.id.frament_layout, settingsFragment);

                // Thêm vào back stack để người dùng có thể quay lại nếu cần
                transaction.addToBackStack(null);

                // Hoàn tất giao dịch
                transaction.commit();
            }
        });
        SQLiteDatabase db;
        EditText eusername, eemail;
        Button updatethongtin, btnexit;
        return view;
    }
}