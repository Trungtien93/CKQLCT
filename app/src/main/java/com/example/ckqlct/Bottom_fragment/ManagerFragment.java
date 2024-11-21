package com.example.ckqlct.Bottom_fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.example.ckqlct.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ManagerFragment extends Fragment {
    private SQLiteDatabase db;
    private String DATABASE_NAME = "QLCTCK.db";
    private Calendar calendar;  // Ensure this is initialized properly
    private TextView HeaderText;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManagerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ManagerFragment newInstance(String param1, String param2) {
        ManagerFragment fragment = new ManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manager, container, false);
        HeaderText = view.findViewById(R.id.txtManagerHeader);
        TextView chiTieu = view.findViewById(R.id.txtIncome);
        TextView thuNhap = view.findViewById(R.id.txtExpense);
        TextView Back = view.findViewById(R.id.txtBack);
        TextView Next = view.findViewById(R.id.txtNext);
        LinearLayout lnchiTieu = view.findViewById(R.id.lnchiTieu);
        LinearLayout lnthuNhap = view.findViewById(R.id.lnthuNhap);
//        LineChart chart = view.findViewById(R.id.chart);
//
//        // Prepare data entries for the chart
//        List<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(1f, 100f));
//        entries.add(new Entry(2f, 200f));
//        entries.add(new Entry(3f, 150f));
//        entries.add(new Entry(4f, 300f));
//
//        // Create dataset and customize it
//        LineDataSet dataSet = new LineDataSet(entries, "Example Data");
//        dataSet.setColor(getResources().getColor(R.color.green, null));  // Customize color
//        dataSet.setValueTextColor(getResources().getColor(R.color.red , null));  // Customize text color
//        dataSet.setLineWidth(2f);  // Customize line width
//
//        // Create LineData and set it to the chart
//        LineData lineData = new LineData(dataSet);
//        chart.setData(lineData);
//        chart.invalidate();  // Refresh the chart

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Get user ID from SharedPreferences
        int userId = sharedPreferences.getInt("id_user", -1); // Assuming 'isUser' stores the user ID
        Log.d("ManagerFragment", "User ID: " + userId); // Debug log for user ID
        // Get the current month and year
        calendar = Calendar.getInstance();
        updateHeaderDate();
        // Check if userId is valid
        if (userId != -1) {
            // Initialize database
            db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            updateIncomeForCurrentDate(userId, chiTieu);
            updateExpenseForCurrentDate(userId, thuNhap);
        }
        // Set click listeners for Back and Next
        Back.setOnClickListener(v -> {
            // Subtract one month from the current date
            calendar.add(Calendar.MONTH, -1);
            updateHeaderDate();
            updateIncomeForCurrentDate(userId, chiTieu); // Update income based on new month
            updateExpenseForCurrentDate(userId, thuNhap);
        });
        Next.setOnClickListener(v -> {
            // Get the current month and year to limit the 'Next' action
            Calendar currentDate = Calendar.getInstance();
            int currentMonth = currentDate.get(Calendar.MONTH);
            int currentYear = currentDate.get(Calendar.YEAR);

            // Only allow adding a month if it does not exceed the current month and year
            if (calendar.get(Calendar.YEAR) < currentYear ||
                    (calendar.get(Calendar.YEAR) == currentYear && calendar.get(Calendar.MONTH) < currentMonth)) {
                calendar.add(Calendar.MONTH, 1);
                updateHeaderDate();
                updateIncomeForCurrentDate(userId, chiTieu);

            }
            Toast.makeText(getContext(),"Không vươt qua số tháng hiện tại",Toast.LENGTH_LONG).show();
        });
//        lnchiTieu.setOnClickListener(v -> {
//            if (userId != -1) {
//                Map<String, Float> incomeData = getIncomeDataForPieChart(userId);
//                displayPieChart(incomeData);
//            } else {
//                Toast.makeText(getContext(), "User ID is not available", Toast.LENGTH_SHORT).show();
//            }
//        });

        return view;
    }
    // Update the displayed date on the header
    private void updateHeaderDate() {
        int displayMonth = calendar.get(Calendar.MONTH) + 1; // Adjust for 0-based month
        int displayYear = calendar.get(Calendar.YEAR);
        String monthName = "Tháng " + displayMonth + " / " + displayYear;
        HeaderText.setText(monthName);
    }

    // Method to update income for the currently displayed month
    private void updateIncomeForCurrentDate(int userId, TextView chiTieu) {
        int month = calendar.get(Calendar.MONTH) + 1; // Adjust for 0-based month
        int year = calendar.get(Calendar.YEAR);

        String query = "SELECT SUM(income_total) AS total_income FROM Income WHERE id_user = ? " +
                "AND strftime('%m', datetime) = ? AND strftime('%Y', datetime) = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.format("%02d", month), String.valueOf(year)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int totalIncomeIndex = cursor.getColumnIndex("total_income");
                if (totalIncomeIndex != -1) {
                    double totalIncome = cursor.getDouble(totalIncomeIndex);
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    chiTieu.setText(numberFormat.format(totalIncome));
                } else {
                    chiTieu.setText("0 VNĐ");
                }
            } else {
                chiTieu.setText("0 VNĐ");
            }
            cursor.close();
        } else {
            chiTieu.setText("Database query failed");
        }
    }
    // Method to update income for the currently displayed month
    private void updateExpenseForCurrentDate(int userId, TextView thuNhap) {
        int month = calendar.get(Calendar.MONTH) + 1; // Adjust for 0-based month
        int year = calendar.get(Calendar.YEAR);

        String query = "SELECT SUM(expense_total) AS total_expense FROM Expense WHERE id_user = ? " +
                    "AND strftime('%m', datetime) = ? AND strftime('%Y', datetime) = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.format("%02d", month), String.valueOf(year)});
        if (cursor != null) {
                // Move to the first row
                if (cursor.moveToFirst()) {
                    // Safely get the index of total_income
                    int totalExpenseIndex = cursor.getColumnIndex("total_expense");
                    if (totalExpenseIndex != -1) {
                        // Get the total income value
                        double totalExpense = cursor.getDouble(totalExpenseIndex);
                        // Format as currency
                        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        thuNhap.setText(numberFormat.format(totalExpense ));
                    } else {
                        thuNhap.setText("0 VNĐ"); // Default if no income found
                    }
                } else {
                    thuNhap.setText("0 VNĐ"); // Default if no income found
                }
                cursor.close(); // Don't forget to close the cursor
            } else {
                thuNhap.setText("Database query failed");
            }
        }
}