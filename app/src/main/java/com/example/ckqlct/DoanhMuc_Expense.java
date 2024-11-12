package com.example.ckqlct;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DoanhMuc_Expense extends AppCompatActivity {

    private EditText edtType, edtName;
    private Button btnAdd, btnExit;
    private DataQLCT dbHelper;
    private SQLiteDatabase db;
    private ListView lstExpense;
    private ArrayList<String> expenseTypeList;
    private ArrayAdapter<String> adapter;

    @SuppressLint ("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doanhmuc_expense); // Ensure doanhmuc_expense.xml exists and has the correct IDs

        // Initialize the database
        dbHelper = new DataQLCT (this);
        db = dbHelper.getWritableDatabase();

        // Initialize views
        edtType = findViewById(R.id.edtType);
        edtName = findViewById(R.id.edtName);
        btnAdd = findViewById(R.id.btnThem);
        btnExit = findViewById(R.id.btnExit);
        lstExpense = findViewById(R.id.lstExpense);

        // Initialize the expense list and adapter
        expenseTypeList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseTypeList);
        lstExpense.setAdapter(adapter);

        // Load existing expense categories when opening the activity
        loadExpenseData();

        // Handle the "Add" button click event
        btnAdd.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            int userId = preferences.getInt("id_user", -1);

            // Check if the user is logged in
            if (userId == -1) {
                Toast.makeText(DoanhMuc_Expense.this, "Bạn cần đăng nhập để thêm loại chi tiêu!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve user input
            String expenseType = edtType.getText().toString().trim();
            String expenseName = edtName.getText().toString().trim();

            // Validate input
            if (expenseType.isEmpty() || expenseName.isEmpty()) {
                Toast.makeText(DoanhMuc_Expense.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert the new expense type and name into the database
            ContentValues values = new ContentValues();
            values.put("expense_type", expenseType);
            values.put("expense_name", expenseName);

            long result = db.insert("Expense_Type", null, values);

            if (result != -1) {
                Toast.makeText(DoanhMuc_Expense.this, "Thêm loại chi tiêu thành công!", Toast.LENGTH_SHORT).show();
                edtType.setText("");
                edtName.setText("");
                loadExpenseData(); // Refresh the list of expense types
            } else {
                Toast.makeText(DoanhMuc_Expense.this, "Thêm thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("Range")
    private void loadExpenseData() {
        ArrayList<ChiTieuItem> expenseList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT expense_type, expense_name FROM Expense_Type", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndex("expense_type"));
                String name = cursor.getString(cursor.getColumnIndex("expense_name"));
                expenseList.add(new ChiTieuItem(type, name));
            }
            cursor.close();
        }

        // Set up adapter to display expense data in the list view
        ChiTieuAdapter adapter = new ChiTieuAdapter(this, expenseList);
        lstExpense.setAdapter(adapter);
    }
}
