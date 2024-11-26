package com.example.ckqlct;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemThuNhap extends Activity {
    private Spinner spnLoaiThuNhap, spnTenThuNhap;
    private EditText edtTongTien, edtNgay, edtGhiChu;
    private Button btnThem, btnXoa, btnExit, btnAddList;
    private DataQLCT dbHelper;
    private SQLiteDatabase db;
    private HashMap<String, List<String>> expenseTypeToNamesMap;
    private Map<String, Integer> expenseNameToIdMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themthunhap_layout);

        dbHelper = new DataQLCT (this);
        db = dbHelper.getWritableDatabase();

        spnLoaiThuNhap = findViewById(R.id.spnLoaiThuNhap);
        spnTenThuNhap = findViewById(R.id.spnTenThuNhap);
        edtTongTien = findViewById(R.id.edtTongTien);
        edtNgay = findViewById(R.id.edtNgay);
        edtGhiChu = findViewById(R.id.edtGhichu);
        btnThem = findViewById(R.id.btnThem);
        btnXoa = findViewById(R.id.btnXoa);
        btnExit = findViewById(R.id.btnDong);
        btnAddList = findViewById(R.id.btnAddList);

        expenseTypeToNamesMap = new HashMap<>();
        loadExpenseTypeData();

        // Adapter for expense types
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(expenseTypeToNamesMap.keySet()));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLoaiThuNhap.setAdapter(typeAdapter);

        btnAddList.setOnClickListener(v -> {
            Intent intent = new Intent(ThemThuNhap.this, DoanhMuc_Expense.class);
            startActivity(intent);
        });

        spnLoaiThuNhap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = spnLoaiThuNhap.getSelectedItem().toString();
                List<String> expenseNames = expenseTypeToNamesMap.get(selectedType);

                if (expenseNames != null) {
                    ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(ThemThuNhap.this, android.R.layout.simple_spinner_item, expenseNames);
                    nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnTenThuNhap.setAdapter(nameAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        edtNgay.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ThemThuNhap.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = dateFormat.format(new Date(selectedYear - 1900, selectedMonth, selectedDay));
                        edtNgay.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        btnXoa.setOnClickListener(view -> clearFields());
        btnExit.setOnClickListener(view -> finish());

        btnThem.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            int userId = preferences.getInt("id_user", -1);

            if (userId == -1) {
                Toast.makeText(ThemThuNhap.this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spnTenThuNhap.getSelectedItem() == null) {
                Toast.makeText(ThemThuNhap.this, "Vui lòng chọn tên thu nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedExpenseName = spnTenThuNhap.getSelectedItem().toString();
            int expenseTypeId = expenseNameToIdMap.getOrDefault(selectedExpenseName, -1);

            String totalExpenseStr = edtTongTien.getText().toString().trim();
            String date = edtNgay.getText().toString().trim();
            String note = edtGhiChu.getText().toString().trim();

            double totalExpense;
            try {
                totalExpense = Double.parseDouble(totalExpenseStr);
            } catch (NumberFormatException e) {
                Toast.makeText(ThemThuNhap.this, "Vui lòng nhập số hợp lệ cho Tổng tiền!", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("expense_total", totalExpense);
            values.put("expenseType_id", expenseTypeId);
            values.put("note", note);
            values.put("id_user", userId);
            values.put("datetime", date);

            long result = db.insert("Expense", null, values);
            if (result == -1) {
                Toast.makeText(ThemThuNhap.this, "Thêm thu nhập thất bại!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ThemThuNhap.this, "Thêm thu nhập thành công!", Toast.LENGTH_SHORT).show();
            }
            clearFields();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenseTypeData();

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(expenseTypeToNamesMap.keySet()));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLoaiThuNhap.setAdapter(typeAdapter);
    }

    private void clearFields() {
        edtTongTien.setText("");
        edtGhiChu.setText("");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        edtNgay.setText(currentDate);

        Toast.makeText(getApplicationContext(), "Đã xoá trắng các trường", Toast.LENGTH_LONG).show();
    }

    private void loadExpenseTypeData() {
        Cursor cursor = db.rawQuery("SELECT expenseType_id, expense_type, expense_name FROM Expense_Type", null);

        int expenseTypeIdIndex = cursor.getColumnIndex("expenseType_id");
        int expenseTypeIndex = cursor.getColumnIndex("expense_type");
        int expenseNameIndex = cursor.getColumnIndex("expense_name");

        if (expenseTypeIdIndex == -1 || expenseTypeIndex == -1 || expenseNameIndex == -1) {
            Toast.makeText(this, "Cột không tồn tại trong bảng Expense_Type", Toast.LENGTH_LONG).show();
            return;
        }

        expenseTypeToNamesMap.clear();
        expenseNameToIdMap.clear();

        while (cursor.moveToNext()) {
            int expenseTypeId = cursor.getInt(expenseTypeIdIndex);
            String expenseType = cursor.getString(expenseTypeIndex);
            String expenseName = cursor.getString(expenseNameIndex);

            if (!expenseTypeToNamesMap.containsKey(expenseType)) {
                expenseTypeToNamesMap.put(expenseType, new ArrayList<>());
            }
            expenseTypeToNamesMap.get(expenseType).add(expenseName);
            expenseNameToIdMap.put(expenseName, expenseTypeId);
        }
        cursor.close();
    }
}
