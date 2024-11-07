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
import android.widget.ListView;
import android.widget.TextView;

import com.example.ckqlct.R;
import com.example.ckqlct.Transaction;
import com.example.ckqlct.TransactionAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView greetingText, ten, chitieu, thuNhap, thang, emptyDataText;
    private ListView lstHome;
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "QLCTCK.db";

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ten = view.findViewById(R.id.userName);
        greetingText = view.findViewById(R.id.greetingText);
        chitieu = view.findViewById(R.id.txtchiTieu);
        thuNhap = view.findViewById(R.id.txtthuNhap);
        thang = view.findViewById(R.id.txtThang);
        lstHome = view.findViewById(R.id.lstHome);
        emptyDataText = view.findViewById(R.id.emptyDataText);

        // Set greeting message based on the time of day
        setGreetingMessage();

        // Display user information from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String fullname = sharedPreferences.getString("fullname", "Guest");
        ten.setText(fullname);

        // Show current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        String monthName = "Tháng " + currentMonth + " / " + currentYear;
        thang.setText(monthName);

        // Open the database
        db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTransactionData();
    }

    private void refreshTransactionData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id_user", -1);

        if (userId != -1) {
            // Update income and expense totals
            updateTotalIncomeAndExpense(userId);

            // Fetch the latest transactions
            List<Transaction> transactions = getLatestTransactions(userId);
            if (transactions.isEmpty()) {
                emptyDataText.setVisibility(View.VISIBLE);
                emptyDataText.setText("Không có dữ liệu.");
                lstHome.setVisibility(View.GONE);
            } else {
                TransactionAdapter adapter = new TransactionAdapter(getActivity(), transactions);
                lstHome.setAdapter(adapter);
                lstHome.setVisibility(View.VISIBLE);
                emptyDataText.setVisibility(View.GONE);
            }
        }
    }

    private void updateTotalIncomeAndExpense(int userId) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        // Query for total income
        String incomeQuery = "SELECT SUM(income_total) AS total_income FROM Income WHERE id_user = ? " +
                "AND strftime('%m', datetime) = ? AND strftime('%Y', datetime) = ?";
        Cursor incomeCursor = db.rawQuery(incomeQuery, new String[]{String.valueOf(userId), String.valueOf(currentMonth), String.valueOf(currentYear)});
        if (incomeCursor != null && incomeCursor.moveToFirst()) {
            double totalIncome = incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow("total_income"));
            chitieu.setText(formatCurrency(totalIncome));
            incomeCursor.close();
        }

        // Query for total expense
        String expenseQuery = "SELECT SUM(expense_total) AS total_expense FROM Expense WHERE id_user = ? " +
                "AND strftime('%m', datetime) = ? AND strftime('%Y', datetime) = ?";
        Cursor expenseCursor = db.rawQuery(expenseQuery, new String[]{String.valueOf(userId), String.valueOf(currentMonth), String.valueOf(currentYear)});
        if (expenseCursor != null && expenseCursor.moveToFirst()) {
            double totalExpense = expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow("total_expense"));
            thuNhap.setText(formatCurrency(totalExpense));
            expenseCursor.close();
        }
    }

    private List<Transaction> getLatestTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();

        String query = "SELECT i.income_total, i.note, it.income_type, it.income_name, i.datetime " +
                "FROM Income i " +
                "JOIN Income_Type it ON i.incomeType_id = it.incomeType_id " +
                "WHERE i.id_user = ? AND DATE(i.datetime) >= DATE('now', '-3 days') " +
                "ORDER BY i.datetime DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("income_type"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("income_name"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("income_total"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
                String formattedTotal = formatCurrency(total);

                transactions.add(new Transaction(type, name, formattedTotal, note, date));
            } while (cursor.moveToNext());
            cursor.close();
        }

        String query1 = "SELECT i.expense_total, i.note, it.expense_type, it.expense_name, i.datetime " +
                "FROM Expense i " +
                "JOIN Expense_Type it ON i.expenseType_id = it.expenseType_id " +
                "WHERE i.id_user = ? AND DATE(i.datetime) >= DATE('now', '-3 days') " +
                "ORDER BY i.datetime DESC";

        Cursor cursor1 = db.rawQuery(query1, new String[]{String.valueOf(userId)});
        if (cursor1 != null && cursor1.moveToFirst()) {
            do {
                String type = cursor1.getString(cursor1.getColumnIndexOrThrow("expense_type"));
                String name = cursor1.getString(cursor1.getColumnIndexOrThrow("expense_name"));
                double total = cursor1.getDouble(cursor1.getColumnIndexOrThrow("expense_total"));
                String note = cursor1.getString(cursor1.getColumnIndexOrThrow("note"));
                String date = cursor1.getString(cursor1.getColumnIndexOrThrow("datetime"));
                String formattedTotal = formatCurrency(total);

                transactions.add(new Transaction(type, name, formattedTotal, note, date));
            } while (cursor.moveToNext());
            cursor.close();
        }


        return transactions;
    }

    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(amount);
    }

    private void setGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        if (time >= 0 && time < 12) {
            greetingText.setText("Buổi sáng tốt lành nhé!");
        } else if (time >= 12 && time < 16) {
            greetingText.setText("Buổi trưa mát mẻ nha");
        } else if (time >= 16 && time < 21) {
            greetingText.setText("Buổi chiều vui vẻ nhe");
        } else {
            greetingText.setText("Chúc bạn ngủ ngon!");
        }
    }
}
