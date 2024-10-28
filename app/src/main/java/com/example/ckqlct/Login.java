package com.example.ckqlct;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
    private DatabaseHelper dbHelper;
    private EditText edtUsername, edtPassword;
    private Button btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các thành phần giao diện
        btnRegister = findViewById(R.id.register1);
        btnLogin = findViewById(R.id.login);
        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);

        // Xử lý sự kiện nhấn nút Đăng ký
        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút Đăng nhập
        btnLogin.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Kiểm tra các trường không được để trống
            if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập tài khoản", Toast.LENGTH_LONG).show();
                edtUsername.requestFocus();
            } else if (password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_LONG).show();
                edtPassword.requestFocus();
            } else {
                int id = isUser(username, password);
                if(id != -1){
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true); // Đặt trạng thái đăng nhập
                    editor.putInt ("isUser", id); // Lưu tên người dùng
                    editor.apply();


                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Đóng Activity Login
                } else {
                    Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu bị sai", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Kiểm tra người dùng
    private int isUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM User WHERE user_name = ? AND pass_word = ?", new String[]{username, password});
            if(cursor.moveToFirst ())
                return cursor.getInt (0);// Người dùng hợp lệ
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi đăng nhập", Toast.LENGTH_LONG).show();
            Log.e("LoginError", e.getMessage());
            return -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }
}
