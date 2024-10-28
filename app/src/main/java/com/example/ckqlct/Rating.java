package com.example.ckqlct;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Rating extends AppCompatActivity {

    private EditText edtGhichu;
    private Button btnThem, btnClear;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db; // Để truy cập cơ sở dữ liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating); // Đảm bảo bạn đã đặt tên layout đúng

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase(); // Khởi tạo db ở đây

        // Khởi tạo view
        edtGhichu = findViewById(R.id.edtGhichu);
        btnThem = findViewById(R.id.btnThem);
        btnClear = findViewById(R.id.btnClear);

        // Xử lý sự kiện cho nút "Gửi"
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = preferences.getInt("isUser", -1);  // -1 if not logged in
                if (userId != -1) {
                    String userName = getUserName(userId); // Lấy user_name từ cơ sở dữ liệu

                    if (userName != null) {
                        ContentValues values = new ContentValues();
                        values.put("id_user", userId);
                        values.put("user_name", userName); // Sử dụng user_name lấy từ cơ sở dữ liệu
                        values.put("note", edtGhichu.getText().toString());

                        // Chèn dữ liệu vào cơ sở dữ liệu
                        long result = db.insert("Rating", null, values);

                        // Kiểm tra nếu thêm thành công
                        if (result != -1) {
                            // Hiển thị thông báo cảm ơn
                            Toast.makeText(Rating.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Thông báo lỗi nếu thêm không thành công
                            Toast.makeText(Rating.this, "Đã xảy ra lỗi. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Rating.this, "Không tìm thấy tên người dùng!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Rating.this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện cho nút "Clear"
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });
    }

    // Phương thức để lấy user_name từ bảng User
    @SuppressLint ("Range")
    private String getUserName(int userId) {
        String userName = null;
        String query = "SELECT user_name FROM User WHERE id_user = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userName = cursor.getString(cursor.getColumnIndex("user_name"));
            }
            cursor.close(); // Đừng quên đóng con trỏ
        }
        return userName;
    }

    // Phương thức để xóa các trường nhập
    private void clearFields() {
        edtGhichu.setText("");
    }
}
