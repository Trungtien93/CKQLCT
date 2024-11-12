package com.example.ckqlct;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ThuNhap extends AppCompatActivity {
    private EditText edtExpenseType, edtExpenseName;
    private Button btnAddExpense, btnExit;
    private DataQLCT dbHelper;
    private SQLiteDatabase db;
    private ListView lstExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doanhmuc_expense); // Ensure this layout exists and has correct IDs

        // Initialize database
        dbHelper = new DataQLCT (this);
        db = dbHelper.getWritableDatabase();

        // Initialize views
        edtExpenseType = findViewById(R.id.edtType);
        edtExpenseName = findViewById(R.id.edtName);
        btnAddExpense = findViewById(R.id.btnThem);
        btnExit = findViewById(R.id.btnExit);
        lstExpense = findViewById(R.id.lstExpense);

        // Handle "Add Income" button click
        btnAddExpense.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            int userId = preferences.getInt("id_user", -1);

            // Check if the user is logged in
            if (userId == -1) {
                Toast.makeText(ThuNhap.this, "Bạn cần đăng nhập để thêm thu nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve data from EditText fields
            String expenseType = edtExpenseType.getText().toString().trim();
            String expenseName = edtExpenseName.getText().toString().trim();

            // Validate input
            if (expenseType.isEmpty() || expenseName.isEmpty()) {
                Toast.makeText(ThuNhap.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert data into the Income_Type table
            ContentValues values = new ContentValues();
            values.put("expense_type", expenseType);
            values.put("expense_name", expenseName);

            long result = db.insert("Expense_Type", null, values);

            if (result != -1) {
                Toast.makeText(ThuNhap.this, "Thêm thu nhập thành công!", Toast.LENGTH_SHORT).show();
                edtExpenseType.setText("");
                edtExpenseName.setText("");
                // Optionally, loadIncomeList(); // Refresh the list after adding a new income
            } else {
                Toast.makeText(ThuNhap.this, "Thêm thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Optionally, handle "Clear" button
        btnExit.setOnClickListener(v -> {
            edtExpenseType.setText("");
            edtExpenseName.setText("");
            Toast.makeText(this, "Đã xóa dữ liệu nhập", Toast.LENGTH_SHORT).show();
        });
    }
}
