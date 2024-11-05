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
    private Button btnThem, btnXoa, btnExit;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private HashMap<String, List<String>> incomeTypeToNamesMap; // Bản đồ loại thu nhập -> danh sách tên chi tiêu
    private Map<String, Integer> incomeNameToIdMap = new HashMap<>();


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
                // Không làm gì khi không có mục nào được chọn
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
                                // Format the date in YYYY-MM-DD format
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = dateFormat.format(new Date(selectedYear - 1900, selectedMonth, selectedDay));

                                edtNgay.setText(formattedDate); // Update the EditText with formatted date
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();

            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Xử lý sự kiện khi nhấn nút Thêm
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = preferences.getInt("id_user", -1); // Ensure you are using the correct key to get user ID

                // Check if userId is valid
                if (userId == -1) {
                    Toast.makeText(ThemChiTieu.this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
                    return; // Exit the method if the user ID is invalid
                }

                // Lấy incomeName đã chọn từ Spinner
                String selectedIncomeName = spntenChiTieu.getSelectedItem().toString();

                // Lấy incomeType_id tương ứng từ incomeNameToIdMap
                int incomeTypeId = incomeNameToIdMap.get(selectedIncomeName);

                String totalIncomeStr = edttongTien.getText().toString().trim();
                String date = edtNgay.getText().toString().trim();
                String note = edtghiChu.getText().toString().trim();

                // Kiểm tra và chuyển đổi totalIncomeStr thành số
                double totalIncome = totalIncomeStr.isEmpty() ? 0 : Double.parseDouble(totalIncomeStr);

                // Thực hiện lệnh INSERT vào bảng Income
                ContentValues values = new ContentValues();
                values.put("income_total", totalIncome);
                values.put("incomeType_id", incomeTypeId);
                values.put("note", note);
                values.put("id_user", userId); // Use the retrieved user ID
                values.put("datetime", date);

                long result = db.insert("Income", null, values);
                if (result == -1) {
                    Toast.makeText(ThemChiTieu.this, "Thêm chi tiêu thất bại!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ThemChiTieu.this, "Thêm chi tiêu thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void clearFields() {
        edttongTien.setText("");
        edtghiChu.setText("");
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0  

        int year = calendar.get(Calendar.YEAR);

        // Định dạng ngày thành chuỗi
        String dateString = day + "/" + month + "/" + year;

        // Hiển thị ngày lên EditText
        edtNgay.setText(dateString);
        Toast.makeText(getApplicationContext(),"Đã xoá trắng các trường",Toast.LENGTH_LONG).show();
    }

    // Phương thức để load dữ liệu từ bảng Income_Type vào incomeTypeToNamesMap
    private void loadIncomeTypeData() {
        Cursor cursor = db.rawQuery("SELECT incomeType_id, income_type, income_name FROM Income_Type", null);

        int incomeTypeIdIndex = cursor.getColumnIndex("incomeType_id");
        int incomeTypeIndex = cursor.getColumnIndex("income_type");
        int incomeNameIndex = cursor.getColumnIndex("income_name");

        if (incomeTypeIdIndex == -1 || incomeTypeIndex == -1 || incomeNameIndex == -1) {
            Toast.makeText(this, "Cột không tồn tại trong bảng Income_Type", Toast.LENGTH_LONG).show();
            return;
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int incomeTypeId = cursor.getInt(incomeTypeIdIndex);
                String incomeType = cursor.getString(incomeTypeIndex);
                String incomeName = cursor.getString(incomeNameIndex);

                if (!incomeTypeToNamesMap.containsKey(incomeType)) {
                    incomeTypeToNamesMap.put(incomeType, new ArrayList<>());
                }
                incomeTypeToNamesMap.get(incomeType).add(incomeName);

                // Thêm vào map để dễ truy xuất ID từ tên chi tiêu
                incomeNameToIdMap.put(incomeName, incomeTypeId);
            }
            cursor.close();
        }
    }
}
