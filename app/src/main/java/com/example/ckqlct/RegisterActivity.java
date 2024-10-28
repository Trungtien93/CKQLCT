package com.example.ckqlct;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    private DatabaseHelper dbHelper;
    EditText eusername, eemail, epassword;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout2);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các thành phần giao diện
        eusername = findViewById(R.id.username);
        eemail = findViewById(R.id.email);
        epassword = findViewById(R.id.password);
        register = findViewById(R.id.regibutton);

        // Xử lý sự kiện nhấn nút đăng ký
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = eusername.getText().toString().trim();
                String email = eemail.getText().toString().trim();
                String password = epassword.getText().toString().trim();

                // Gọi phương thức đăng ký
                registerUser(username, email, password);
            }
        });
    }

    private void registerUser(String username, String email, String password) {
        // Kiểm tra xem các trường có rỗng không
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
            return;
        }

        // Kiểm tra xem email có đúng định dạng không
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Email không hợp lệ", Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            // Kiểm tra xem username hoặc email đã tồn tại chưa
            cursor = db.rawQuery("SELECT * FROM User WHERE user_name = ? OR email = ?", new String[]{username, email});
            if (cursor != null && cursor.moveToFirst()) {
                Toast.makeText(getApplicationContext(), "Tài khoản hoặc email đã tồn tại", Toast.LENGTH_LONG).show();
                return;
            }

            // Thêm người dùng mới
            String sql = "INSERT INTO User (user_name, pass_word, email, datetime) VALUES (?, ?, ?, DATETIME('now'))";
            db.execSQL(sql, new Object[]{username, password, email});

            Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_LONG).show();

            // Chuyển hướng sau khi đăng ký thành công
            Intent intent = new Intent(RegisterActivity.this, Login.class);
            startActivity(intent);
            finish(); // Kết thúc RegisterActivity để không quay lại sau khi đăng ký
        } catch (Exception e) {
            Toast.makeText(this, "Đăng ký không thành công", Toast.LENGTH_LONG).show();
            Log.e("RegisterError", e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}
