package com.example.ckqlct;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Changepass extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private ImageView ivToggleCurrentPassword, ivToggleNewPassword, ivToggleConfirmPassword;
    private boolean isPasswordVisible = false; // Biến theo dõi trạng thái hiển thị mật khẩu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pass);

        // Khởi tạo các thành phần
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

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

}
