package com.example.ckqlct.Bottom_fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


import androidx.fragment.app.Fragment;

import com.example.ckqlct.R;
import com.example.ckqlct.Transaction;
import com.example.ckqlct.TransactionAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BuyFragment extends Fragment {

    private EditText edtNgayTu, edtNgayDen;
    private ListView lstchiTieu;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;
    private String startDate, endDate;
    TextView emptyDataText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);

        // Initialize SharedPreferences and database
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        db = getActivity().openOrCreateDatabase("QLCTCK.db", Context.MODE_PRIVATE, null);

        // Initialize views
        edtNgayTu = view.findViewById(R.id.edtNgayTu);
        edtNgayDen = view.findViewById(R.id.edtNgayDen);
        lstchiTieu = view.findViewById(R.id.lstchiTieu);
        emptyDataText = view.findViewById(R.id.emptyDataText1);

        Spinner loai = view.findViewById(R.id.spnloai);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Income", "Expense"});
        loai.setAdapter(adapter);

        loai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int userId = sharedPreferences.getInt("id_user", -1);

                if (userId != -1) {
                    displayData(loai.getSelectedItem().toString(), userId);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Set DatePicker dialogs
        edtNgayTu.setOnClickListener(v -> showDatePickerDialog(edtNgayTu));
        edtNgayDen.setOnClickListener(v -> showDatePickerDialog(edtNgayDen));

        return view;
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                    editText.setText(selectedDate);

                    // Update start or end date
                    if (editText == edtNgayTu) {
                        startDate = selectedDate;
                    } else {
                        endDate = selectedDate;
                    }

                    // Check if both dates are selected
                    if (startDate != null && endDate != null) {
                        int userId = sharedPreferences.getInt("id_user", -1);
                        String tableName = ((Spinner) getView().findViewById(R.id.spnloai)).getSelectedItem().toString();
                        displayData(tableName, userId); // Use spinner value to determine table name
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    private void displayData(String tableName, int userId) {
        // Check if startDate or endDate is null
        emptyDataText.setText("Danh sách trống!");
        if (startDate == null || endDate == null) {
            Toast.makeText(getContext(), "Please select both start and end dates.", Toast.LENGTH_SHORT).show();
            return; // Skip the query if dates are not set
        }

        String query;
        Cursor cursor;

        // Determine query based on the table name
        if ("Income".equals(tableName)) {
            query = "SELECT i.rowid AS _id, i.datetime, i.note, it.income_name, i.income_total " +
                    "FROM Income i " +
                    "JOIN Income_Type it ON i.incomeType_id = it.incomeType_id " +
                    "WHERE i.id_user = ? AND i.datetime BETWEEN ? AND ? " +
                    "ORDER BY i.datetime DESC";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        }else {
            query = "SELECT e.id_expense AS _id, e.datetime, e.note, et.expense_name, e.expense_total " +
                    "FROM Expense e " +
                    "JOIN Expense_Type et ON e.expenseType_id = et.expenseType_id " +
                    "WHERE e.id_user = ? AND e.datetime BETWEEN ? AND ? " +
                    "ORDER BY e.datetime DESC";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        }


        // Check if there are no results
        if (cursor.getCount() == 0) {
            emptyDataText.setVisibility(View.VISIBLE);  // Show empty message
            emptyDataText.setText("Danh sách trống!");
            lstchiTieu.setAdapter(null);  // Clear ListView content
        } else {
            emptyDataText.setVisibility(View.GONE);  // Hide empty message if data is present

            // Set up adapter
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getContext(),
                    R.layout.list_item_income,
                    cursor,
                    new String[]{"datetime", "income_name", "note"}, // Update according to table structure
                    new int[]{R.id.txtDate, R.id.txtIncomeName, R.id.txtNote},
                    0
            ) {
                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    super.bindView(view, context, cursor);

                    TextView txtIncomeTotal = view.findViewById(R.id.txtIncomeTotal);
                    int totalColumnIndex = cursor.getColumnIndex("income_total");

                    // Check if the index is valid and get the total amount
                    if (totalColumnIndex != -1) {
                        String totalAmountStr = cursor.getString(totalColumnIndex);
                        try {
                            double totalAmount = Double.parseDouble(totalAmountStr);
                            // Format the total amount as VNĐ
                            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                            txtIncomeTotal.setText("- " + numberFormat.format(totalAmount));
                        } catch (NumberFormatException e) {
                            txtIncomeTotal.setText("- 0");
                            e.printStackTrace();
                        }
                    } else {
                        // Handle the case where the column does not exist
                        txtIncomeTotal.setText("- 0");
                    }
                }
            };
            lstchiTieu.setAdapter(adapter);  // Set adapter if data is present
        }
    }
}
