package com.example.ckqlct;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ThemChiTieu extends Activity {
    private Spinner spnloaiChiTieu, spntenChiTieu;
    private EditText edttongTien, edtNgay, edtghiChu;
    private Button btnThem, btnXoa, btnExit;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private HashMap<String, List<String>> incomeTypeToNamesMap; // Bản đồ loại thu nhập -> danh sách tên chi tiêu

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themchitieu_layout);

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Khởi tạo view
        spnloaiChiTieu = findViewById(R.id.spnLoaiChiTieu);
        spntenChiTieu = findViewById(R.id.spnTenChiTieu);
        edttongTien = findViewById(R.id.edtTongTien);
        edtNgay = findViewById(R.id.edtNgay);
        edtghiChu = findViewById(R.id.edtGhichu);
        btnThem = findViewById(R.id.btnThem);
        btnXoa = findViewById(R.id.btnXoa);
        btnExit = findViewById(R.id.btnDong);

        // Khởi tạo bản đồ loại thu nhập -> tên chi tiêu từ cơ sở dữ liệu
        incomeTypeToNamesMap = new HashMap<>();
        loadIncomeTypeData();

        // Thiết lập adapter cho Spinner loại chi tiêu
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(incomeTypeToNamesMap.keySet()));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnloaiChiTieu.setAdapter(typeAdapter);

        // Đăng ký lắng nghe sự kiện thay đổi cho Spinner loại chi tiêu
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

        // Set a click listener to open the DatePickerDialog
        edtNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ThemChiTieu.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                edtNgay.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        // Xử lý sự kiện khi nhấn nút Thêm
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = preferences.getInt("id_user", -1); // -1 nếu không đăng nhập

                // Kiểm tra đăng nhập
                if (userId == -1) {
                    Toast.makeText(ThemChiTieu.this, "Bạn cần đăng nhập để thêm chi tiêu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy dữ liệu từ các trường nhập liệu
                String selectedType = spnloaiChiTieu.getSelectedItem().toString();
                String incomeName = spntenChiTieu.getSelectedItem().toString();
                String totalIncomeStr = edttongTien.getText().toString().trim();
                String date = edtNgay.getText().toString().trim();
                String note = edtghiChu.getText().toString().trim();

                // Chuyển đổi tổng thu nhập sang số
                double totalIncome;
                try {
                    totalIncome = Double.parseDouble(totalIncomeStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(ThemChiTieu.this, "Vui lòng nhập số tiền hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy ID của loại chi tiêu từ incomeTypeToNamesMap
                Cursor typeCursor = db.rawQuery("SELECT incomeType_id FROM Income_Type WHERE income_type = ?", new String[]{selectedType});
                int incomeTypeId = -1;
                if (typeCursor.moveToFirst()) {
                    incomeTypeId = typeCursor.getInt(0);
                }
                typeCursor.close();

                if (incomeTypeId == -1) {
                    Toast.makeText(ThemChiTieu.this, "Loại chi tiêu không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thêm dữ liệu vào bảng Income
                ContentValues values = new ContentValues();
                values.put("income_total", totalIncome);
                values.put("incomeType_id", incomeTypeId);
                values.put("note", note);
                values.put("id_user", userId);
                values.put("datetime", date);

                long result = db.insert("Income", null, values);
                if (result != -1) {
                    Toast.makeText(ThemChiTieu.this, "Thêm chi tiêu thành công!", Toast.LENGTH_SHORT).show();
                    edttongTien.setText("");
                    edtNgay.setText("");
                    edtghiChu.setText("");
                } else {
                    Toast.makeText(ThemChiTieu.this, "Thêm thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Phương thức để load dữ liệu từ bảng Income_Type vào incomeTypeToNamesMap
    private void loadIncomeTypeData() {
        Cursor cursor = db.rawQuery("SELECT income_type, income_name FROM Income_Type", null);
        // Kiểm tra nếu cột tồn tại
        int incomeTypeIndex = cursor.getColumnIndex("income_type");
        int incomeNameIndex = cursor.getColumnIndex("income_name");

        if (incomeTypeIndex == -1 || incomeNameIndex == -1) {
            // Cột không tồn tại trong bảng, báo lỗi
            Toast.makeText(this, "Cột 'income_type' hoặc 'income_name' không tồn tại trong bảng Income_Type", Toast.LENGTH_LONG).show();
            return;
        }

// Đảm bảo các chỉ số cột là hợp lệ
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String incomeType = cursor.getString(incomeTypeIndex);
                String incomeName = cursor.getString(incomeNameIndex);

                // Kiểm tra hoặc khởi tạo danh sách tên chi tiêu cho mỗi loại chi tiêu
                if (!incomeTypeToNamesMap.containsKey(incomeType)) {
                    incomeTypeToNamesMap.put(incomeType, new ArrayList<String>());
                }
                incomeTypeToNamesMap.get(incomeType).add(incomeName);
            }
            cursor.close();
        }
    }
}
