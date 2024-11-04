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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
        TextView thang = view.findViewById(R.id.txtThang);
        lstHome = view.findViewById(R.id.lstHome);

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
        int userId = sharedPreferences.getInt("isUser", -1); // Assuming 'isUser' stores the user ID

        // Get the current month and year
        calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // MONTH is 0-based in Calendar
        int currentYear = calendar.get(Calendar.YEAR);

        // Format current month to Vietnamese format
        String monthName = currentMonth + " / " + currentYear; // Example: "11 / 2024"
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
        } else {
            chitieu.setText("User not found");
        }

        return view;
    }

    public void displayHistoryTransaction(int userId) {
        // Initialize the list to store data
        ArrayList<HashMap<String, String>> transactionList = new ArrayList<>();

        // Open the database for reading
        db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

        // SQL query to retrieve data from HistoryTransaction for the specified user
        String query = "SELECT transaction_type, transaction_name, transaction_total, transaction_note, datetime " +
                "FROM HistoryTransaction WHERE id_user = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        // Check if cursor has data
        if (cursor.moveToFirst()) {
            // Get the column indices (safe check for -1)
            int transactionTypeIndex = cursor.getColumnIndex("transaction_type");
            int transactionNameIndex = cursor.getColumnIndex("transaction_name");
            int transactionTotalIndex = cursor.getColumnIndex("transaction_total");
            int transactionNoteIndex = cursor.getColumnIndex("transaction_note");
            int datetimeIndex = cursor.getColumnIndex("datetime");

            // Loop through the cursor to populate the transactionList
            do {
                HashMap<String, String> transaction = new HashMap<>();

                // Check each index before accessing the data to prevent errors
                if (transactionTypeIndex != -1) {
                    transaction.put("transaction_type", cursor.getString(transactionTypeIndex));
                }
                if (transactionNameIndex != -1) {
                    transaction.put("transaction_name", cursor.getString(transactionNameIndex));
                }
                if (transactionTotalIndex != -1) {
                    transaction.put("transaction_total", String.valueOf(cursor.getDouble(transactionTotalIndex)));
                }
                if (transactionNoteIndex != -1) {
                    transaction.put("transaction_note", cursor.getString(transactionNoteIndex));
                }
                if (datetimeIndex != -1) {
                    transaction.put("datetime", cursor.getString(datetimeIndex));
                }

                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Adapter to bind data to lstHome
        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(),
                transactionList,
                R.layout.list_item_transaction, // Custom layout for each row
                new String[]{"transaction_type", "transaction_name", "transaction_total", "transaction_note", "datetime"},
                new int[]{R.id.txtTransactionType, R.id.txtTransactionName, R.id.txtTransactionTotal, R.id.txtTransactionNote, R.id.txtTransactionDate}
        );

        lstHome.setAdapter(adapter);
    }




//    private void displayRecentTransactions(int userId, ListView lstHome) {
//        String transactionQuery = "SELECT transaction_name, datetime, transaction_total " +
//                "FROM HistoryTransaction WHERE id_user = ? ORDER BY datetime DESC LIMIT 10";
//
//        Cursor transactionCursor = db.rawQuery(transactionQuery, new String[]{String.valueOf(userId)});
//
//        if (transactionCursor != null) {
//            // Log the cursor columns for debugging
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < transactionCursor.getColumnCount(); i++) {
//                sb.append(transactionCursor.getColumnName(i)).append("\n");
//            }
//            Log.d("Cursor Columns", sb.toString());
//
//            if (transactionCursor.moveToFirst()) {
//                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//                        getContext(),
//                        R.layout.list_item_home,
//                        transactionCursor,
//                        new String[]{"transaction_name", "datetime", "transaction_total"},
//                        new int[]{R.id.txtTransactionName, R.id.txtDate, R.id.txtTransactionTotal},
//                        0
//                ) {
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        View view = super.getView(position, convertView, parent);
//
//                        if (transactionCursor.moveToPosition(position)) {
//                            // Set total formatted as currency (VND)
//                            TextView txtTransactionTotal = view.findViewById(R.id.txtTransactionTotal);
//                            int totalIndex = transactionCursor.getColumnIndex("transaction_total");
//                            if (totalIndex != -1) {
//                                double total = transactionCursor.getDouble(totalIndex);
//                                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
//                                String formattedTotal = numberFormat.format(total).replace("₫", "đ");
//                                txtTransactionTotal.setText(formattedTotal);
//                            } else {
//                                Log.e("Column Error", "Column 'transaction_total' not found!");
//                            }
//
//                            // Format the date
//                            TextView txtDate = view.findViewById(R.id.txtDate);
//                            int dateIndex = transactionCursor.getColumnIndex("datetime");
//                            if (dateIndex != -1) {
//                                String dateStr = transactionCursor.getString(dateIndex);
//                                txtDate.setText(formatDate(dateStr)); // Pass the date string instead of index
//                            } else {
//                                Log.e("Column Error", "Column 'datetime' does not exist!");
//                            }
//                        }
//
//                        return view;
//                    }
//                };
//
//                lstHome.setAdapter(adapter);
//            } else {
//                Toast.makeText(getContext(), "No recent transactions found!", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getContext(), "Cursor is null!", Toast.LENGTH_SHORT).show();
//        }
//    }
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
