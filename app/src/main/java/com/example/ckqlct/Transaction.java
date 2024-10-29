package com.example.ckqlct;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Transaction extends AppCompatActivity {

    private Spinner spinnerLoaiGD, spinnerTenGD;
    private EditText edtTongTien, edtNgay, edtGhichu;
    private Button btnThem, btnXoa, btnDong;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction); // Tên layout

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Khởi tạo view
        spinnerLoaiGD = findViewById(R.id.spinnerloaigd);
        spinnerTenGD = findViewById(R.id.spinnerten);
        edtTongTien = findViewById(R.id.edtTongTien);
        edtNgay = findViewById(R.id.edtNgay);
        edtGhichu = findViewById(R.id.edtGhichu);
        btnThem = findViewById(R.id.btnThem);
        btnXoa = findViewById(R.id.btnxoa);
        btnDong = findViewById(R.id.btndong);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = preferences.getInt("isUser", -1);

                if (userId != -1) {
                    String transactionType = spinnerLoaiGD.getSelectedItem().toString();
                    String transactionName = spinnerTenGD.getSelectedItem().toString();
                    String totalAmount = edtTongTien.getText().toString();
                    String date = edtNgay.getText().toString();
                    String note = edtGhichu.getText().toString();

                    if (validateFields(totalAmount, date)) {
                        try {
                            // Thay đổi parse cho tổng tiền
                            double amount = Double.parseDouble(totalAmount);

                            // Gọi phương thức thêm trong DatabaseHelper
                            long result = dbHelper.addTransaction(userId, transactionType, transactionName, amount, date, note);

                            if (result != -1) {
                                Toast.makeText(Transaction.this, "Thêm giao dịch thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Transaction.this, "Lỗi khi thêm giao dịch. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(Transaction.this, "Tổng tiền không hợp lệ. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(Transaction.this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Xử lý sự kiện cho nút "Xóa"
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        // Xử lý sự kiện cho nút "Đóng"
        btnDong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity
            }
        });
    }

    // Kiểm tra hợp lệ dữ liệu
    private boolean validateFields(String totalAmount, String date) {
        if (totalAmount.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Phương thức để xóa các trường nhập
    private void clearFields() {
        edtTongTien.setText("");
        edtNgay.setText("");
        edtGhichu.setText("");
    }
}
