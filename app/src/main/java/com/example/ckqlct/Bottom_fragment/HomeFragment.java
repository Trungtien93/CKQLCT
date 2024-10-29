package com.example.ckqlct.Bottom_fragment;

//import static com.example.ckqlct.Login.DATABASE_NAME;

//import static com.example.ckqlct.DatabaseHelper.DATABASE_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.ckqlct.R;
import java.util.Calendar;

public class HomeFragment extends Fragment {
    TextView greetingText;
    private SQLiteDatabase db;
    private String DATABASE_NAME = "QLCTCK.db";

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView ten = view.findViewById(R.id.userName);
        greetingText = view.findViewById(R.id.greetingText);

        // Lấy fullname từ SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String fullname = sharedPreferences.getString("fullname", "Guest"); // Mặc định là "Guest" nếu không tìm thấy fullname

        ten.setText(fullname);  // Hiển thị fullname

    // Set the greeting message based on the time of day
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        if (time >= 0 && time < 12) {
            greetingText.setText("Buổi sáng tốt lành nhé!");
        } else if (time >= 12 && time < 16) {
            greetingText.setText("Buổi trưa mát mẻ nha");
        } else if (time >= 16 && time < 21) {
            greetingText.setText("Buổi chiều vui vẻ nhe");
        } else if (time >= 21 && time < 24) {
            greetingText.setText("Chúc bạn ngủ ngon!");
        } else {
            greetingText.setText("Xin chào người dùng");
        }

        return view;
    }
}
