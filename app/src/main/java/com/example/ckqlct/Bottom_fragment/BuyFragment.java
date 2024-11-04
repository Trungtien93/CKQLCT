package com.example.ckqlct.Bottom_fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Locale;


import androidx.fragment.app.Fragment;

import com.example.ckqlct.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BuyFragment extends Fragment {

    private EditText edtNgayTu, edtNgayDen;
    private ListView lstchiTieu;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;
    private String startDate, endDate;

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

        Spinner loai = view.findViewById(R.id.spnloai);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Income", "Expense"});
        loai.setAdapter(adapter);

        loai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int userId = sharedPreferences.getInt("isUser", -1);
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
                        int userId = sharedPreferences.getInt("isUser", -1);
                        displayData("Income", userId); // Update with the correct table name
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void displayData(String tableName, int userId) {
        // Check if startDate or endDate is null
        if (startDate == null || endDate == null) {
            Toast.makeText(getContext(), "Please select both start and end dates.", Toast.LENGTH_SHORT).show();
            return; // Skip the query if dates are not set
        }

        String query;
        Cursor cursor;

        if ("Income".equals(tableName)) {
            query = "SELECT i.rowid AS _id, i.datetime, i.note, it.income_name, i.income_total " +
                    "FROM Income i " +
                    "JOIN Income_Type it ON i.incomeType_id = it.incomeType_id " +
                    "WHERE i.id_user = ? AND i.datetime BETWEEN ? AND ? " +
                    "ORDER BY i.datetime DESC";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        } else {
            query = "SELECT rowid AS _id, datetime, note, expense_name, expense_total " +
                    "FROM Expense " +
                    "WHERE id_user = ? AND datetime BETWEEN ? AND ? " +
                    "ORDER BY datetime DESC";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "Danh sách trống!", Toast.LENGTH_SHORT).show();
            lstchiTieu.setAdapter(null);
        } else {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getContext(),
                    R.layout.list_item_income,
                    cursor,
                    new String[]{"datetime", "income_name", "note"}, // Exclude income_total here
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
                        // Convert the total amount string to a number
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
            lstchiTieu.setAdapter(adapter);
        }
    }
}
