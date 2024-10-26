package com.example.ckqlct;

import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
    public static final String DATABASE_NAME = "QLCT.db";
    private static final String PREFS_NAME = "MyPrefsFile";  // Tên file SharedPreferences
    private static final String LOGIN_STATUS_KEY = "isLoggedIn";  // Khóa lưu trạng thái đăng nhập
    SQLiteDatabase db;
    EditText edtusername,edtpassword;
    Button eregister,elogin;
    boolean isAllFields = false;
    private void initDB() {
        db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        String sql;
        try {
            if (!isTableExists(db, "tbluser")) {
                sql = "CREATE TABLE tbluser (id_user INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,";
                sql += " username TEXT NOT NULL,";
                sql += " password TEXT NOT NULL,";
                sql += " email TEXT NOT NULL UNIQUE,";
                sql += " fullname TEXT NULL,";
                sql += " registration_date DATETIME DEFAULT (DATETIME('now')))";
                db.execSQL(sql);

                // Insert default admin user
                sql = "INSERT INTO tbluser (username, password, email, fullname, registration_date) " +
                        "VALUES ('admin', 'admin', 'admin@gmail.com', '', '2024-10-23')";
                db.execSQL(sql);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Khởi tạo cơ sở dữ liệu không thành công", Toast.LENGTH_LONG).show();
        }
    }

    private boolean CheckAllField (String username, String password)
    {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(),"Please enter name",Toast.LENGTH_LONG).show();
            return false;

        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),"please enter proper password",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        // Initialize the database
        initDB();  // Add this line to initialize database

        eregister = findViewById(R.id.register1);
        elogin= findViewById(R.id.login);
        edtusername = findViewById(R.id.username);
        edtpassword = findViewById(R.id.password);
//
//        Intent i = getIntent();
//        String a = " ";
//        String b = " ";
//        try {
//            a = i.getStringExtra("number1");
//            b = i.getStringExtra("number2");
//        } catch (NumberFormatException e) {
//            Log.d("error1", "user not give input");
//
//        }
//        username.setText(a);
//        password.setText(b);

        eregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(Login.this,RegisterActivity.class);

                startActivity(in);


            }
        });

        // Kiểm tra trạng thái đăng nhập trước khi hiển thị màn hình đăng nhập
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = settings.getBoolean(LOGIN_STATUS_KEY, false);
        if (isLoggedIn) {
            // Nếu đã đăng nhập trước đó, chuyển sang MainActivity
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        elogin.setOnClickListener(view -> {
            String username = edtusername.getText().toString();
            String password = edtpassword.getText().toString();

            if (CheckAllField(username, password)) {
                if (isUser(username, password)) {
                    // Đăng nhập thành công, lưu trạng thái vào SharedPreferences
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(LOGIN_STATUS_KEY, true);
                    editor.apply();

                    // Chuyển đến MainActivity
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(Login.this, "Tên người dùng hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean isUser(String username, String password) {
        try {
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            // Truy vấn chỉ kiểm tra username và password
            Cursor c = db.rawQuery("SELECT * FROM tbluser WHERE username = ? AND password = ?", new String[]{username, password});
            if (c.moveToFirst()) {  // Chỉ cần kiểm tra nếu có kết quả
                return true;  // Người dùng hợp lệ
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Lỗi đăng nhập", Toast.LENGTH_LONG).show();
            Log.e("LoginError", ex.getMessage());  // In lỗi ra log để dễ theo dõi
        }
        return false;  // Người dùng không hợp lệ
    }


    private boolean isTableExists(SQLiteDatabase database, String tableName) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name" + "= '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

}
