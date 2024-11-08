package com.example.ckqlct;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Changepass extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private ImageView ivToggleCurrentPassword, ivToggleNewPassword, ivToggleConfirmPassword;
    private boolean isPasswordVisible = false; // Biến theo dõi trạng thái hiển thị mật khẩu
    private Button btnUpdatePassword;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pass);

        // Khởi tạo các thành phần
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        // Khởi tạo DatabaseHelper và cơ sở dữ liệu
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Xử lý sự kiện cập nhật mật khẩu
        btnUpdatePassword.setOnClickListener(v -> updatePassword());

        ivToggleCurrentPassword = findViewById(R.id.ivToggleCurrentPassword);
        ivToggleNewPassword = findViewById(R.id.ivToggleNewPassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);

        // Đặt sự kiện click cho mỗi icon con mắt
        ivToggleCurrentPassword.setOnClickListener(v -> toggleAllPasswordVisibility());
        ivToggleNewPassword.setOnClickListener(v -> toggleAllPasswordVisibility());
        ivToggleConfirmPassword.setOnClickListener(v -> toggleAllPasswordVisibility());
    }

    // Hàm ẩn/hiện tất cả các trường mật khẩu cùng lúc
    private void toggleAllPasswordVisibility() {
        // Đổi trạng thái hiển thị mật khẩu
        isPasswordVisible = !isPasswordVisible;

        // Cập nhật inputType của tất cả các EditText
        if (isPasswordVisible) {
            // Hiển thị mật khẩu
            etCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);

            // Cập nhật icon mắt mở cho tất cả các ImageView
            ivToggleCurrentPassword.setImageResource(R.drawable.ic_eye);
            ivToggleNewPassword.setImageResource(R.drawable.ic_eye);
            ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye);
        } else {
            // Ẩn mật khẩu
            etCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            // Cập nhật icon mắt đóng cho tất cả các ImageView
            ivToggleCurrentPassword.setImageResource(R.drawable.ic_eye_off);
            ivToggleNewPassword.setImageResource(R.drawable.ic_eye_off);
            ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
        }

        // Đảm bảo con trỏ ở cuối của mỗi EditText sau khi đổi chế độ
        etCurrentPassword.setSelection(etCurrentPassword.getText().length());
        etNewPassword.setSelection(etNewPassword.getText().length());
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
    }
    private void updatePassword() {
        // Lấy id_user từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id_user", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldPasswordInput = etCurrentPassword.getText().toString().trim();
        String newPasswordInput = etNewPassword.getText().toString().trim();
        String confirmPasswordInput = etConfirmPassword.getText().toString().trim();

        // Kiểm tra các trường không được để trống
        if (oldPasswordInput.isEmpty() || newPasswordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!!!!!!!!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Xác minh mật khẩu cũ từ bảng User
        if (!verifyOldPassword(userId, oldPasswordInput)) {
            Toast.makeText(this, "Mật khẩu hiện tại chưa đúng!!!!!!!!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra xem mật khẩu mới và xác nhận mật khẩu có trùng nhau không
        if (!newPasswordInput.equals(confirmPasswordInput)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận mật khẩu mới chưa trùng nhau!!!!!!!!", Toast.LENGTH_SHORT).show();
            return;
        }else if(newPasswordInput.equals(oldPasswordInput)){
            Toast.makeText(this, "Mật khẩu mới bị trùng với mật khẩu cũ!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
            return;
        }


        // Cập nhật mật khẩu trong bảng User và thêm bản ghi vào bảng Passwordchange
        if (updateUserPassword(userId, newPasswordInput)) {
            savePasswordChangeHistory(userId, oldPasswordInput, newPasswordInput);
            Toast.makeText(this, "Mật khẩu đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity sau khi thay đổi mật khẩu thành công
        } else {
            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verifyOldPassword(int userId, String oldPasswordInput) {
        Cursor cursor = db.rawQuery("SELECT pass_word FROM User WHERE id_user = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            String currentPassword = cursor.getString(0);
            cursor.close();
            return currentPassword.equals(oldPasswordInput);
        }
        cursor.close();
        return false;
    }

    private boolean updateUserPassword(int userId, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("pass_word", newPassword);
        int rowsUpdated = db.update("User", values, "id_user = ?", new String[]{String.valueOf(userId)});
        return rowsUpdated > 0;
    }

    private void savePasswordChangeHistory(int userId, String oldPassword, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("id_user", userId);
        values.put("user_name", getUsername(userId)); // Lấy tên người dùng từ id_user
        values.put("old_password", oldPassword);
        values.put("new_password", newPassword);
        db.insert("Passwordchange", null, values);
    }

    private String getUsername(int userId) {
        Cursor cursor = db.rawQuery("SELECT user_name FROM User WHERE id_user = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            String username = cursor.getString(0);
            cursor.close();
            return username;
        }
        cursor.close();
        return "";
    }

}
