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
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Rating extends AppCompatActivity {

    private EditText edtGhichu;
    private Button btnThem, btnClear;
    private DataQLCT dbHelper;
    ListView lstRating;
    private SQLiteDatabase db; // Để truy cập cơ sở dữ liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating); // Đảm bảo bạn đã đặt tên layout đúng

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DataQLCT (this);
        db = dbHelper.getWritableDatabase(); // Khởi tạo db ở đây

        // Khởi tạo view
        edtGhichu = findViewById(R.id.edtGhichu);
        btnThem = findViewById(R.id.btnThem);
        btnClear = findViewById(R.id.btnClear);
        lstRating = findViewById(R.id.lstRating);

        // Load dữ liệu khi khởi động
        loadRatings();

        // Xử lý sự kiện cho nút "Gửi"
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = preferences.getInt("id_user", -1);  // -1 if not logged in

                if (userId != -1) {
                    String userName = getUserName(userId); // Lấy user_name từ cơ sở dữ liệu

                    if (userName != null) {
                        ContentValues values = new ContentValues();
                        values.put("id_user", userId);
                        values.put("user_name", userName);
                        values.put("note", edtGhichu.getText().toString());

                        long result = db.insert("Rating", null, values);

                        if (result != -1) {
                            Toast.makeText(Rating.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                            edtGhichu.setText(""); // Clear the input field
                            loadRatings(); // Refresh the list
                        } else {
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
    @SuppressLint("Range")
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
    // Phương thức để tải và hiển thị đánh giá trong ListView
    @SuppressLint("Range")
    private void loadRatings() {
        ArrayList<RatingItem> ratingList = new ArrayList<>();
        String query = "SELECT user_name, note, time FROM Rating ORDER BY time DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String userName = cursor.getString(cursor.getColumnIndex("user_name"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                ratingList.add(new RatingItem(userName, note, time));
            }
            cursor.close();
        }

        // Sử dụng RatingAdapter để hiển thị dữ liệu
        RatingAdapter adapter = new RatingAdapter(this, ratingList);
        lstRating.setAdapter(adapter);
    }

}
