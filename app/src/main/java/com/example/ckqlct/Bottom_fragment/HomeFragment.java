package com.example.ckqlct.Bottom_fragment;

//import static com.example.ckqlct.Login.DATABASE_NAME;

//import static com.example.ckqlct.DatabaseHelper.DATABASE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ckqlct.R;
import com.example.ckqlct.Transaction;
import com.example.ckqlct.TransactionAdapter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView greetingText;
    private ListView lstHome;
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
        TextView chitieu = view.findViewById(R.id.txtchiTieu);
        TextView thuNhap = view.findViewById(R.id.txtthuNhap);
        TextView thang = view.findViewById(R.id.txtThang);
        lstHome = view.findViewById(R.id.lstHome);
        TextView emptyDataText = view.findViewById(R.id.emptyDataText);

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
        // Get user ID from SharedPreferences
        int userId = sharedPreferences.getInt("id_user", -1); // Assuming 'isUser' stores the user ID
        Log.d("HomeFragment", "User ID: " + userId); // Debug log for user ID
        // Get the current month and year
        calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // MONTH is 0-based in Calendar
        int currentYear = calendar.get(Calendar.YEAR);

        // Format current month to Vietnamese format
        String monthName = "Tháng " + currentMonth + " / " + currentYear; // Example: "11 / 2024"
        thang.setText(monthName); // Display the current month

        // Check if userId is valid
        if (userId != -1) {
            // Initialize database
            db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

            // Query to get total income for the current month and user
            String query = "SELECT SUM(income_total) AS total_income FROM Income WHERE id_user = ? " +
                    "AND strftime('%m', datetime) = ? AND strftime('%Y', datetime) = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(currentMonth), String.valueOf(currentYear)});

            if (cursor != null) {
                // Move to the first row
                if (cursor.moveToFirst()) {
                    // Safely get the index of total_income
                    int totalIncomeIndex = cursor.getColumnIndex("total_income");
                    if (totalIncomeIndex != -1) {
                        // Get the total income value
                        double totalIncome = cursor.getDouble(totalIncomeIndex);
                        // Format as currency
                        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        chitieu.setText(numberFormat.format(totalIncome));
                    } else {
                        chitieu.setText("0 VNĐ"); // Default if no income found
                    }
                } else {
                    chitieu.setText("0 VNĐ"); // Default if no income found
                }
                cursor.close(); // Don't forget to close the cursor
            } else {
                chitieu.setText("Database query failed");
            }

            // Check if userId is valid
            if (userId != -1) {
                // Fetch the latest transactions for the user
                List<Transaction> transactions = getLatestTransactions(userId);

                // Check if transactions list is empty
                if (transactions.isEmpty()) {
                    // Show a message that there is no data
                    emptyDataText.setVisibility(View.VISIBLE);
                    emptyDataText.setText("Không có dữ liệu."); // Display a message in Vietnamese
                    lstHome.setVisibility(View.GONE); // Hide the ListView if no data
                } else {
                    // Update the ListView with the transactions
                    TransactionAdapter adapter = new TransactionAdapter(getActivity(), transactions);
                    lstHome.setAdapter(adapter);
                    lstHome.setVisibility(View.VISIBLE); // Show the ListView
                    emptyDataText.setVisibility(View.GONE); // Hide the no data message
                }
            } else {
                // Handle case where user is not logged in or invalid userId
                emptyDataText.setVisibility(View.VISIBLE);
                emptyDataText.setText("Vui lòng đăng nhập."); // Prompt the user to log in
                lstHome.setVisibility(View.GONE); // Hide the ListView
            }
        }
        if (userId != -1) {
            // Initialize database
            db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

            // Query to get total income for the current month and user
            String query = "SELECT SUM(expense_total) AS total_expense FROM Expense WHERE id_user = ? " +
                    "AND strftime('%m', datetime) = ? AND strftime('%Y', datetime) = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(currentMonth), String.valueOf(currentYear)});

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
        return view;
    }
    // Update this method to check if there are any transactions for the given user
    private List<Transaction> getLatestTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();

        String query = "SELECT i.income_total, i.note, it.income_type, it.income_name, i.datetime " +
                "FROM Income i " +
                "JOIN Income_Type it ON i.incomeType_id = it.incomeType_id " +
                "WHERE i.id_user = ? AND DATE(i.datetime) >= DATE('now', '-3 days') " +
                "ORDER BY i.datetime DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("income_type"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("income_name"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("income_total"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));

                // Format currency and date here
                String formattedTotal = formatCurrency(total);
            //    String formattedDate = formatDate(date);

                transactions.add(new Transaction(type, name, formattedTotal, note, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(amount);
    }

//    private String formatDate(String dateStr) {
//        String formattedDate = "";
//        try {
//            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//            SimpleDateFormat targetFormat = new SimpleDateFormat("dd 'Thg' MM, yyyy", new Locale("vi", "VN"));
//            Date date = originalFormat.parse(dateStr);
//            formattedDate = targetFormat.format(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return formattedDate;
//    }
}
