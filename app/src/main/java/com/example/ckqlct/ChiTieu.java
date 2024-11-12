package com.example.ckqlct;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChiTieu extends AppCompatActivity {
    private EditText edtType, edtName;
    private Button btnThem, btnClear;
    private DataQLCT dbHelper;
    ListView lstChiTieu;
    private SQLiteDatabase db; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doanhmuc_income); // Đảm bảo bạn đã đặt tên layout đúng

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DataQLCT (this);
        db = dbHelper.getWritableDatabase(); // Khởi tạo db ở đây

        // Khởi tạo view
        edtType = findViewById(R.id.edtType);
        edtName = findViewById(R.id.edtName);
        btnThem = findViewById(R.id.btnThem);
        btnClear = findViewById(R.id.btnExit);
        lstChiTieu = findViewById(R.id.lstChiTieu);
        // Load dữ liệu khi khởi động
        //loadRatings();

        // Xử lý sự kiện cho nút "Gửi"
        // Xử lý sự kiện cho nút "Thêm"
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = preferences.getInt("id_user", -1);  // -1 nếu không đăng nhập

                // Kiểm tra xem người dùng đã đăng nhập chưa
                if (userId == -1) {
                    Toast.makeText(ChiTieu.this, "Bạn cần đăng nhập để thêm loại thu nhập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy dữ liệu từ các trường EditText
                String incomeType = edtType.getText().toString().trim();
                String incomeName = edtName.getText().toString().trim();

                // Kiểm tra dữ liệu nhập vào
                if (incomeType.isEmpty() || incomeName.isEmpty()) {
                    Toast.makeText(ChiTieu.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo ContentValues để thêm dữ liệu vào bảng Income_Type
                ContentValues values = new ContentValues();
                values.put("income_type", incomeType);
                values.put("income_name", incomeName);

                // Chèn dữ liệu vào bảng Income_Type
                long result = db.insert("Income_Type", null, values);

                if (result != -1) {
                    Toast.makeText(ChiTieu.this, "Thêm loại thu nhập thành công!", Toast.LENGTH_SHORT).show();
                    edtType.setText("");
                    edtName.setText("");
                    //loadChiTieu(); // Tải lại danh sách loại thu nhập sau khi thêm
                } else {
                    Toast.makeText(ChiTieu.this, "Thêm thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
