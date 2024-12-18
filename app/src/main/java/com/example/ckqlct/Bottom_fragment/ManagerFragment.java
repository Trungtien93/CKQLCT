package com.example.ckqlct.Bottom_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import com.example.ckqlct.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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
                updateExpenseForCurrentDate(userId, thuNhap);

            }
            Toast.makeText(getContext(),"Không vươt qua số tháng hiện tại",Toast.LENGTH_LONG).show();
        });
        lnchiTieu.setOnClickListener(v -> {
            if (userId != -1) {
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentYear = calendar.get(Calendar.YEAR);
                showIncomePieChart(userId, currentMonth, currentYear); // Pass month and year
            } else {
                Toast.makeText(getContext(), "User ID is not available", Toast.LENGTH_SHORT).show();
            }
        });
        lnthuNhap.setOnClickListener(v -> {
            if (userId != -1) {
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentYear = calendar.get(Calendar.YEAR);
                showExpensePieChart(userId, currentMonth, currentYear); // Pass month and year
            } else {
                Toast.makeText(getContext(), "User ID is not available", Toast.LENGTH_SHORT).show();
            }
        });
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
    private void showIncomePieChart(int userId, int month, int year) {
        // Open or create the database
        db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

        // Query income_name and income_total for the selected month and year
        String query = "SELECT it.income_name, SUM(i.income_total) AS total_income " +
                "FROM Income i " +
                "JOIN Income_Type it ON i.incomeType_id = it.incomeType_id " +
                "WHERE i.id_user = ? AND strftime('%m', i.datetime) = ? AND strftime('%Y', i.datetime) = ? " +
                "GROUP BY it.income_name";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.format("%02d", month),
                String.valueOf(year)
        });

        Map<String, Double> incomeData = new HashMap<>();
        double totalIncome = 0;

        if (cursor != null) {
            // Debug column names
            Log.d("Cursor Columns", Arrays.toString(cursor.getColumnNames()));

            while (cursor.moveToNext()) {
                int incomeNameIndex = cursor.getColumnIndex("income_name");
                int totalIncomeIndex = cursor.getColumnIndex("total_income");

                if (incomeNameIndex != -1 && totalIncomeIndex != -1) {
                    String incomeName = cursor.getString(incomeNameIndex);
                    double incomeTotal = cursor.getDouble(totalIncomeIndex);
                    incomeData.put(incomeName, incomeTotal);
                    totalIncome += incomeTotal;
                }
            }
            cursor.close();
        } else {
            Toast.makeText(getContext(), "Database query failed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalIncome == 0) {
            Toast.makeText(getContext(), "Chưa có chi phí phát sinh trong tháng vừa chọn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate percentages and create PieEntries
        double threshold = 0.05; // 5% threshold for small entries
        double othersTotal = 0;
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : incomeData.entrySet()) {
            String incomeName = entry.getKey();
            double incomeTotal = entry.getValue();
            float percentage = (float) ((incomeTotal / totalIncome) * 100);

            if (percentage < (threshold * 100)) {
                othersTotal += incomeTotal; // Group small entries
            } else {
                pieEntries.add(new PieEntry(percentage, incomeName));
            }
        }

        if (othersTotal > 0) {
            float othersPercentage = (float) ((othersTotal / totalIncome) * 100);
            pieEntries.add(new PieEntry(othersPercentage, "Chi phí khác"));
        }

        // Layout setup (title + chart)
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);

        // Add Title TextView
        TextView title = new TextView(getContext());
        title.setText("Thống kê chi tiêu tháng " + month + " / " + year);
        title.setTextSize(20f);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(title);

        // Setup PieChart
        PieChart pieChart = new PieChart(getContext());
        LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800 // Adjust chart height
        );
        pieChart.setLayoutParams(chartParams);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(16f);
        pieData.setValueTextColor(android.graphics.Color.BLACK);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setEntryLabelTextSize(0f);

        // Configure Legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(16f);
        legend.setWordWrapEnabled(true);

        // Add PieChart to Layout
        layout.addView(pieChart);

        // Add ChartValueSelectedListener
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    String label = ((PieEntry) e).getLabel();
                    Toast.makeText(getContext(), "Bạn vừa chọn: " + label, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // No action required
            }
        });

        // Display the Layout in a Dialog
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);

        // Set dialog size
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);

        dialog.setCancelable(true);
        dialog.setOnDismissListener(dialogInterface -> {
            if (pieChart != null) pieChart.clear();
        });

        dialog.show();
    }

    private void showExpensePieChart(int userId, int month, int year) {
        // Open or create the database
        db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

        // Query income_name and income_total for the selected month and year
        String query = "SELECT et.expense_name, SUM(e.expense_total) AS total_expense " +
                "FROM Expense e " +
                "JOIN Expense_Type et ON e.expenseType_id = et.expenseType_id " +
                "WHERE e.id_user = ? AND strftime('%m', e.datetime) = ? AND strftime('%Y', e.datetime) = ? " +
                "GROUP BY et.expense_name";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.format("%02d", month),
                String.valueOf(year)
        });

        Map<String, Double> expenseData = new HashMap<>();
        double totalExpense = 0;

        if (cursor != null) {
            // Debug column names
            Log.d("Cursor Columns", Arrays.toString(cursor.getColumnNames()));

            while (cursor.moveToNext()) {
                int expenseNameIndex = cursor.getColumnIndex("expense_name");
                int totalExpenseIndex = cursor.getColumnIndex("total_expense");

                if (expenseNameIndex != -1 && totalExpenseIndex != -1) {
                    String expenseName = cursor.getString(expenseNameIndex);
                    double expenseTotal = cursor.getDouble(totalExpenseIndex);
                    expenseData.put(expenseName, expenseTotal);
                    totalExpense += expenseTotal;
                }
            }
            cursor.close();
        } else {
            Toast.makeText(getContext(), "Database query failed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalExpense == 0) {
            Toast.makeText(getContext(), "Chưa có thu nhập trong tháng vừa chọn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate percentages and create PieEntries
        double threshold = 0.05; // 5% threshold for small entries
        double othersTotal = 0;
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : expenseData.entrySet()) {
            String expenseName = entry.getKey();
            double expenseTotal = entry.getValue();
            float percentage = (float) ((expenseTotal / totalExpense) * 100);

            if (percentage < (threshold * 100)) {
                othersTotal += expenseTotal; // Group small entries
            } else {
                pieEntries.add(new PieEntry(percentage, expenseName));
            }
        }

        if (othersTotal > 0) {
            float othersPercentage = (float) ((othersTotal / totalExpense) * 100);
            pieEntries.add(new PieEntry(othersPercentage, "Thu nhập khác"));
        }

        // Layout setup (title + chart)
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);

        // Add Title TextView
        TextView title = new TextView(getContext());
        title.setText("Thống kê thu nhập tháng " + month + " / " + year);
        title.setTextSize(20f);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(title);

        // Setup PieChart
        PieChart pieChart = new PieChart(getContext());
        LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800 // Adjust chart height
        );
        pieChart.setLayoutParams(chartParams);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(16f);
        pieData.setValueTextColor(android.graphics.Color.BLACK);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setEntryLabelTextSize(0f);

        // Configure Legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(16f);
        legend.setWordWrapEnabled(true);

        // Add PieChart to Layout
        layout.addView(pieChart);

        // Add ChartValueSelectedListener
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    String label = ((PieEntry) e).getLabel();
                    Toast.makeText(getContext(), "Bạn vừa chọn: " + label, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // No action required
            }
        });

        // Display the Layout in a Dialog
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);

        // Set dialog size
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);

        dialog.setCancelable(true);
        dialog.setOnDismissListener(dialogInterface -> {
            if (pieChart != null) pieChart.clear();
        });

        dialog.show();
    }
}