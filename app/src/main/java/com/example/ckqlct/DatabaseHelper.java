package com.example.ckqlct;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên cơ sở dữ liệu và phiên bản
    private static final String DATABASE_NAME = "QLCTCK.db";
    private static final int DATABASE_VERSION = 1;

    // Câu lệnh SQL để tạo các bảng
    private static final String CREATE_TABLE_USER = "CREATE TABLE User (" +
            "id_user INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_name NVARCHAR(50) NOT NULL UNIQUE, " +
            "pass_word NVARCHAR(255) NOT NULL, " +
            "fullname NVARCHAR(100), " +
            "email NVARCHAR(100) NOT NULL UNIQUE, " +
            "datetime DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");";

    private static final String CREATE_TABLE_RATING = "CREATE TABLE Rating (" +
            "id_rating INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_user INTEGER, " +
            "user_name NVARCHAR(255), " +
            "note NVARCHAR(255), " +
            "time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(id_user) REFERENCES User(id_user)" +
            ");";

    private static final String CREATE_TABLE_HISTORY_TRANSACTION = "CREATE TABLE HistoryTransaction (" +
            "id_transaction INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_user INTEGER, " +
            "transaction_type NVARCHAR(20) NOT NULL, " +
            "transaction_name NVARCHAR(100) NOT NULL, " +
            "transaction_total DECIMAL(18, 2) NOT NULL, " +
            "datetime DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "transaction_note NVARCHAR(255), " +
            "FOREIGN KEY(id_user) REFERENCES User(id_user)" +
            ");";

    // Bảng loại thu nhập
    private static final String CREATE_TABLE_INCOME_TYPE = "CREATE TABLE Income_Type (" +
            "incomeType_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "income_type NVARCHAR(50) NOT NULL, " +
            "income_name NVARCHAR(100) NOT NULL" +
            ");";

    // Bảng loại chi tiêu
    private static final String CREATE_TABLE_EXPENSE_TYPE = "CREATE TABLE Expense_Type (" +
            "expenseType_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "expense_type NVARCHAR(50) NOT NULL, " +
            "expense_name NVARCHAR(100) NOT NULL" +
            ");";

    // Bảng thu nhập
    private static final String CREATE_TABLE_INCOME = "CREATE TABLE Income (" +
            "id_income INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_user INTEGER, " +
            "incomeType_id INTEGER, " +
            "income_total DECIMAL(18, 2) NOT NULL, " +
            "datetime DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "note NVARCHAR(255), " +
            "FOREIGN KEY(id_user) REFERENCES User(id_user), " +
            "FOREIGN KEY(incomeType_id) REFERENCES Income_Type(incomeType_id)" +
            ");";

    // Bảng chi tiêu
    private static final String CREATE_TABLE_EXPENSE = "CREATE TABLE Expense (" +
            "id_expense INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_user INTEGER, " +
            "expenseType_id INTEGER, " +
            "expense_total DECIMAL(18, 2) NOT NULL, " +
            "datetime DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "note NVARCHAR(255), " +
            "FOREIGN KEY(id_user) REFERENCES User(id_user), " +
            "FOREIGN KEY(expenseType_id) REFERENCES Expense_Type(expenseType_id)" +
            ");";

    private static final String CREATE_TABLE_PASSWORD_CHANGE = "CREATE TABLE Passwordchange (" +
            "id_password INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_user INTEGER, " +
            "user_name NVARCHAR(255), " +
            "old_password NVARCHAR(255) NOT NULL, " +
            "new_password NVARCHAR(255) NOT NULL, " +
            "datetime DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(id_user) REFERENCES User(id_user)" +
            ");";

    private static final String CREATE_TABLE_STATISTICAL = "CREATE TABLE Statistical (" +
            "id_statistical INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_user INTEGER, " +
            "income_total DECIMAL(18, 2) NOT NULL, " +
            "expense_total DECIMAL(18, 2) NOT NULL, " +
            "start_date DATETIME NOT NULL, " +
            "end_date DATETIME NOT NULL, " +
            "FOREIGN KEY(id_user) REFERENCES User(id_user)" +
            ");";

    private static final String CREATE_TABLE_VERSION = "CREATE TABLE Version (" +
            "id_version INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "version_name NVARCHAR(50), " +
            "version_new NVARCHAR(50), " +
            "version_old NVARCHAR(50)" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo các bảng khi cơ sở dữ liệu được tạo
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_RATING);
        db.execSQL(CREATE_TABLE_HISTORY_TRANSACTION);
        db.execSQL(CREATE_TABLE_INCOME_TYPE);
        db.execSQL(CREATE_TABLE_EXPENSE_TYPE);
        db.execSQL(CREATE_TABLE_INCOME);
        db.execSQL(CREATE_TABLE_EXPENSE);
        db.execSQL(CREATE_TABLE_PASSWORD_CHANGE);
        db.execSQL(CREATE_TABLE_STATISTICAL);
        db.execSQL(CREATE_TABLE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng và tạo lại nếu phiên bản cơ sở dữ liệu thay đổi
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Rating");
        db.execSQL("DROP TABLE IF EXISTS HistoryTransaction");
        db.execSQL("DROP TABLE IF EXISTS Income_Type");
        db.execSQL("DROP TABLE IF EXISTS Expense_Type");
        db.execSQL("DROP TABLE IF EXISTS Income");
        db.execSQL("DROP TABLE IF EXISTS Expense");
        db.execSQL("DROP TABLE IF EXISTS Passwordchange");
        db.execSQL("DROP TABLE IF EXISTS Statistical");
        db.execSQL("DROP TABLE IF EXISTS Version");
        onCreate(db);
    }
}
