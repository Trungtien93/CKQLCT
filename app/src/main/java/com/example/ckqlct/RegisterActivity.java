package com.example.ckqlct;

import static com.example.ckqlct.Login.DATABASE_NAME;

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
    SQLiteDatabase db;
    EditText eusername, eemail, epassword;
    Button register;
    boolean isAllFields = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout2);
        eusername = findViewById(R.id.username);
        eemail = findViewById(R.id.email);
        epassword = findViewById(R.id.password);
        register = findViewById(R.id.regibutton);
        // Xử lý sự kiện nhấn nút đăng ký
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = eusername.getText().toString();
                String email = eemail.getText().toString();
                String password = epassword.getText().toString();

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

        try {
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

            // Kiểm tra xem email hoặc username đã tồn tại chưa
            Cursor cursor = db.rawQuery("SELECT * FROM tbluser WHERE username = ? OR email = ?", new String[]{username, email});
            if (cursor.moveToFirst()) {
                Toast.makeText(getApplicationContext(), "Tài khoản hoặc email đã tồn tại", Toast.LENGTH_LONG).show();
                cursor.close();
                return;
            }

            // Thêm người dùng mới
            String sql = "INSERT INTO tbluser (username, password, email, registration_date) VALUES (?, ?, ?, DATETIME('now'))";
            db.execSQL(sql, new Object[]{username, password, email});

            Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_LONG).show();
            cursor.close();

            // Chuyển hướng sau khi đăng ký thành công
            Intent intent = new Intent(RegisterActivity.this, Login.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Đăng ký không thành công", Toast.LENGTH_LONG).show();
            Log.e("RegisterError", e.getMessage());
        }
    }


//    private boolean register() {
//        String username = eusername.getText().toString().trim();
//        String email = eemail.getText().toString().trim();
//        String password = epassword.getText().toString().trim();
//        if (CheakAllField(username, email, password)) {
//
//            Toast.makeText(this, "you have succesfully register", Toast.LENGTH_LONG).show();
//            return true;
//        }
//        return false;
//    }
//
//    private boolean CheakAllField (String username, String email, String password)
//    {
//        if (TextUtils.isEmpty(username)) {
//            eusername.setError("Please enter name");
//            return false;
//
//        }
//
//        if (TextUtils.isEmpty(email)) {
//            eemail.setError("please enter proper email");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            epassword.setError("please enter proper password");
//            return false;
//        }
//        return true;
    }

