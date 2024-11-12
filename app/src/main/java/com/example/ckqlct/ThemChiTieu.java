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

public class ThemChiTieu extends Activity {
    private Spinner spnloaiChiTieu, spntenChiTieu;
    private EditText edttongTien, edtNgay, edtghiChu;
    private Button btnThem, btnXoa, btnExit, btnAddlist;
    private DataQLCT dbHelper;
    private SQLiteDatabase db;
    private HashMap<String, List<String>> incomeTypeToNamesMap;
    private Map<String, Integer> incomeNameToIdMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themchitieu_layout);

        dbHelper = new DataQLCT (this);
        db = dbHelper.getWritableDatabase();

        spnloaiChiTieu = findViewById(R.id.spnLoaiChiTieu);
        spntenChiTieu = findViewById(R.id.spnTenChiTieu);
        edttongTien = findViewById(R.id.edtTongTien);
        edtNgay = findViewById(R.id.edtNgay);
        edtghiChu = findViewById(R.id.edtGhichu);
        btnThem = findViewById(R.id.btnThem);
        btnXoa = findViewById(R.id.btnXoa);
        btnExit = findViewById(R.id.btnDong);
        btnAddlist = findViewById(R.id.btnAddList);

        incomeTypeToNamesMap = new HashMap<>();
        loadIncomeTypeData();

        // Cài đặt Adapter cho Spinner loại chi tiêu
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(incomeTypeToNamesMap.keySet()));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnloaiChiTieu.setAdapter(typeAdapter);

        btnAddlist.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Thêm chi tiêu", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ThemChiTieu.this, DoanhMuc_income.class);
            startActivity(intent);
        });

        spnloaiChiTieu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = spnloaiChiTieu.getSelectedItem().toString();
                List<String> incomeNames = incomeTypeToNamesMap.get(selectedType);

                if (incomeNames != null) {
                    ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(ThemChiTieu.this, android.R.layout.simple_spinner_item, incomeNames);
                    nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spntenChiTieu.setAdapter(nameAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        edtNgay.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ThemChiTieu.this,
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
                Toast.makeText(ThemChiTieu.this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spntenChiTieu.getSelectedItem() == null) {
                Toast.makeText(ThemChiTieu.this, "Vui lòng chọn tên chi tiêu!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedIncomeName = spntenChiTieu.getSelectedItem().toString();
            int incomeTypeId = incomeNameToIdMap.getOrDefault(selectedIncomeName, -1);

            String totalIncomeStr = edttongTien.getText().toString().trim();
            String date = edtNgay.getText().toString().trim();
            String note = edtghiChu.getText().toString().trim();

            double totalIncome;
            try {
                totalIncome = Double.parseDouble(totalIncomeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(ThemChiTieu.this, "Vui lòng nhập số hợp lệ cho Tổng tiền!", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("income_total", totalIncome);
            values.put("incomeType_id", incomeTypeId);
            values.put("note", note);
            values.put("id_user", userId);
            values.put("datetime", date);

            long result = db.insert("Income", null, values);
            if (result == -1) {
                Toast.makeText(ThemChiTieu.this, "Thêm chi tiêu thất bại!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ThemChiTieu.this, "Thêm chi tiêu thành công!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIncomeTypeData();

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(incomeTypeToNamesMap.keySet()));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnloaiChiTieu.setAdapter(typeAdapter);
    }

    private void clearFields() {
        edttongTien.setText("");
        edtghiChu.setText("");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        edtNgay.setText(currentDate);

        Toast.makeText(getApplicationContext(), "Đã xoá trắng các trường", Toast.LENGTH_LONG).show();
    }

    private void loadIncomeTypeData() {
        Cursor cursor = db.rawQuery("SELECT incomeType_id, income_type, income_name FROM Income_Type", null);

        int incomeTypeIdIndex = cursor.getColumnIndex("incomeType_id");
        int incomeTypeIndex = cursor.getColumnIndex("income_type");
        int incomeNameIndex = cursor.getColumnIndex("income_name");

        if (incomeTypeIdIndex == -1 || incomeTypeIndex == -1 || incomeNameIndex == -1) {
            Toast.makeText(this, "Cột không tồn tại trong bảng Income_Type", Toast.LENGTH_LONG).show();
            return;
        }

        incomeTypeToNamesMap.clear();
        incomeNameToIdMap.clear();

        while (cursor.moveToNext()) {
            int incomeTypeId = cursor.getInt(incomeTypeIdIndex);
            String incomeType = cursor.getString(incomeTypeIndex);
            String incomeName = cursor.getString(incomeNameIndex);

            if (!incomeTypeToNamesMap.containsKey(incomeType)) {
                incomeTypeToNamesMap.put(incomeType, new ArrayList<>());
            }
            incomeTypeToNamesMap.get(incomeType).add(incomeName);
            incomeNameToIdMap.put(incomeName, incomeTypeId);
        }
        cursor.close();
    }
}
