package com.example.ckqlct;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DoanhMuc_income extends AppCompatActivity {

    private EditText edtType, edtName;
    private Button btnThem, btnClear;
    private DataQLCT dbHelper;
    private ListView lstChiTieu;
    private SQLiteDatabase db;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> incomeTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doanhmuc_income); // Đổi layout XML cho Activity

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DataQLCT (this);
        db = dbHelper.getWritableDatabase();

        // Khởi tạo view
        edtType = findViewById(R.id.edtType);
        edtName = findViewById(R.id.edtName);
        btnThem = findViewById(R.id.btnThem);
        btnClear = findViewById(R.id.btnExit);
        lstChiTieu = findViewById(R.id.lstChiTieu);

        // Khởi tạo danh sách loại thu nhập và adapter
        incomeTypeList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, incomeTypeList);
        lstChiTieu.setAdapter(adapter);

        // Tải dữ liệu khi khởi động
        loadChiTieu();

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        // Xử lý sự kiện cho nút "Thêm"
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                int userId = preferences.getInt("id_user", -1);  // -1 nếu không đăng nhập

                // Kiểm tra xem người dùng đã đăng nhập chưa
                if (userId == -1) {
                    Toast.makeText(DoanhMuc_income.this, "Bạn cần đăng nhập để thêm loại thu nhập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy dữ liệu từ các trường EditText
                String incomeType = edtType.getText().toString().trim();
                String incomeName = edtName.getText().toString().trim();

                // Kiểm tra dữ liệu nhập vào
                if (incomeType.isEmpty() || incomeName.isEmpty()) {
                    Toast.makeText(DoanhMuc_income.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo ContentValues để thêm dữ liệu vào bảng Income_Type
                ContentValues values = new ContentValues();
                values.put("income_type", incomeType);
                values.put("income_name", incomeName);

                // Chèn dữ liệu vào bảng Income_Type
                long result = db.insert("Income_Type", null, values);

                if (result != -1) {
                    Toast.makeText(DoanhMuc_income.this, "Thêm loại thu nhập thành công!", Toast.LENGTH_SHORT).show();
                    edtType.setText("");
                    edtName.setText("");
                    loadChiTieu(); // Tải lại danh sách loại thu nhập sau khi thêm
                } else {
                    Toast.makeText(DoanhMuc_income.this, "Thêm thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @SuppressLint("Range")
    private void loadChiTieu() {
        ArrayList<ChiTieuItem> chiTieuList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT income_type, income_name FROM Income_Type", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndex("income_type"));
                String name = cursor.getString(cursor.getColumnIndex("income_name"));
                chiTieuList.add(new ChiTieuItem(type, name));
            }
            cursor.close();
        }

        // Sử dụng ChiTieuAdapter để hiển thị dữ liệu
        ChiTieuAdapter adapter = new ChiTieuAdapter(this, chiTieuList);
        lstChiTieu.setAdapter(adapter);
    }
}

