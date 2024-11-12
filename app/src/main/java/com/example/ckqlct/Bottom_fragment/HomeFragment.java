package com.example.ckqlct.Bottom_fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView greetingText, ten, chitieu, thuNhap, thang, emptyDataText;
    private PieChart pieChart;
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
        // Initialize LineChart
        pieChart = view.findViewById(R.id.pieChart);

        // Set greeting message based on the time of day
        setGreetingMessage();

        // Display user information from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String fullname = sharedPreferences.getString("fullname", "Guest");
        int userId = sharedPreferences.getInt("id_user", -1);
        ten.setText(fullname);

        // Show current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        String monthName = "Tháng " + currentMonth + " / " + currentYear;
        thang.setText(monthName);

        // Open the database
        db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        updateTotalIncomeAndExpense(userId);
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

        double totalIncome = 0;
        double totalExpense = 0;

        // Query for total income
        String incomeQuery = "SELECT SUM(income_total) AS total_income FROM Income WHERE id_user = ? " +
                "AND strftime('%m', datetime) = ? AND strftime('%Y', datetime) = ?";
        Cursor incomeCursor = db.rawQuery(incomeQuery, new String[]{String.valueOf(userId), String.valueOf(currentMonth), String.valueOf(currentYear)});
        if (incomeCursor != null && incomeCursor.moveToFirst()) {
            totalIncome = incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow("total_income"));
            chitieu.setText(formatCurrency(totalIncome));
            incomeCursor.close();
        }

        // Query for total expense
        String expenseQuery = "SELECT SUM(expense_total) AS total_expense FROM Expense WHERE id_user = ? " +
                "AND strftime('%m', datetime) = ? AND strftime('%Y', datetime) = ?";
        Cursor expenseCursor = db.rawQuery(expenseQuery, new String[]{String.valueOf(userId), String.valueOf(currentMonth), String.valueOf(currentYear)});
        if (expenseCursor != null && expenseCursor.moveToFirst()) {
            totalExpense = expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow("total_expense"));
            thuNhap.setText(formatCurrency(totalExpense));
            expenseCursor.close();
        }

        // Update the PieChart with income and expense percentages
        updatePieChart(totalIncome, totalExpense);
    }

    private void updatePieChart(double totalIncome, double totalExpense) {
        double total = totalIncome + totalExpense;
        if (total > 0) {
            float incomePercentage = (float) ((totalIncome / total) * 100);
            float expensePercentage = (float) ((totalExpense / total) * 100);

            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(incomePercentage, "Income"));
            entries.add(new PieEntry(expensePercentage, "Expense"));

            PieDataSet dataSet = new PieDataSet(entries, "");

// Define custom colors for the slices
            List<Integer> colors = new ArrayList<>();
            colors.add(Color.parseColor("#FF5252")); // Red color for Income
            colors.add(Color.parseColor("#76FF03")); // Green color for Expense
            dataSet.setColors(colors);  // Set the custom colors

// Create PieData and set it to the pie chart
            PieData pieData = new PieData(dataSet);
            pieChart.setData(pieData);

// Disable percentage text inside the chart
            pieChart.setUsePercentValues(false);

// Set properties for displaying the legend outside
            pieChart.setDrawEntryLabels(false);  // Disable the default entry labels (percentages inside the slices)
            pieChart.setDrawSlicesUnderHole(false);  // Ensures there is no overlap of text on slices

// Enable the legend and position it outside
            Legend legend = pieChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setDrawInside(false);
            legend.setXEntrySpace(10f);  // Space between entries
            legend.setYEntrySpace(10f);  // Space between entries

// Customize the legend labels with percentage values
            String incomeLabel = "Chi Tiêu " + String.format(Locale.US, "(%.1f%%)", incomePercentage);
            String expenseLabel = "Thu Nhập " + String.format(Locale.US, "(%.1f%%)", expensePercentage);

            entries.get(0).setLabel(incomeLabel);  // Set label for income with percentage
            entries.get(1).setLabel(expenseLabel); // Set label for expense with percentage

// Change the text color of the legend to white (for both Income and Expense)
            legend.setTextColor(Color.WHITE);
            legend.setTextSize(10);

// Refresh the chart with updated data
            pieChart.invalidate();


        }
    }

    private List<Transaction> getLatestTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();

        // Query for the 3 most recent income transactions
        String query = "SELECT i.income_total, i.note, it.income_type, it.income_name, i.datetime " +
                "FROM Income i " +
                "JOIN Income_Type it ON i.incomeType_id = it.incomeType_id " +
                "WHERE i.id_user = ? " +
                "ORDER BY i.datetime DESC LIMIT 3";  // Limit to 3 most recent records

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String type = "Income";  // Set type as Income for the income query
                String name = cursor.getString(cursor.getColumnIndexOrThrow("income_name"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("income_total"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
                String formattedTotal = "- " + formatCurrency(total);

                transactions.add(new Transaction(type, name, formattedTotal, note, date));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Query for the 3 most recent expense transactions
        String query1 = "SELECT e.expense_total, e.note, et.expense_type, et.expense_name, e.datetime " +
                "FROM Expense e " +
                "JOIN Expense_Type et ON e.expenseType_id = et.expenseType_id " +
                "WHERE e.id_user = ? " +
                "ORDER BY e.datetime DESC LIMIT 3";  // Limit to 3 most recent records

        Cursor cursor1 = db.rawQuery(query1, new String[]{String.valueOf(userId)});
        if (cursor1 != null && cursor1.moveToFirst()) {
            do {
                String type = "Expense";  // Set type as Expense for the expense query
                String name = cursor1.getString(cursor1.getColumnIndexOrThrow("expense_name"));
                double total = cursor1.getDouble(cursor1.getColumnIndexOrThrow("expense_total"));
                String note = cursor1.getString(cursor1.getColumnIndexOrThrow("note"));
                String date = cursor1.getString(cursor1.getColumnIndexOrThrow("datetime"));
                String formattedTotal = "+ " + formatCurrency(total);

                transactions.add(new Transaction(type, name, formattedTotal, note, date));
            } while (cursor1.moveToNext());
            cursor1.close();
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
