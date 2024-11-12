package com.example.ckqlct.Nav_fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ckqlct.ChiTieuAdapter;
import com.example.ckqlct.ChiTieuItem;
import com.example.ckqlct.DataQLCT;
import com.example.ckqlct.R;

import java.util.ArrayList;

public class ShareFragment extends Fragment {

    private EditText edtType, edtName;
    private Button btnThem, btnClear;
    private DataQLCT dbHelper;
    private ListView lstChiTieu;
    private SQLiteDatabase db;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> incomeTypeList; // Danh sách lưu loại thu nhập

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DataQLCT (getActivity());
        db = dbHelper.getWritableDatabase();

        // Khởi tạo view
        edtType = view.findViewById(R.id.edtType);
        edtName = view.findViewById(R.id.edtName);
        btnThem = view.findViewById(R.id.btnThem);
        btnClear = view.findViewById(R.id.btnExit);
        lstChiTieu = view.findViewById(R.id.lstChiTieu);

        // Khởi tạo danh sách loại thu nhập và adapter
        incomeTypeList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, incomeTypeList);
        lstChiTieu.setAdapter(adapter);

        // Tải dữ liệu khi khởi động
        loadChiTieu();

        // Xử lý sự kiện cho nút "Thêm"
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                int userId = preferences.getInt("id_user", -1);  // -1 nếu không đăng nhập

                // Kiểm tra xem người dùng đã đăng nhập chưa
                if (userId == -1) {
                    Toast.makeText(getActivity(), "Bạn cần đăng nhập để thêm loại thu nhập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy dữ liệu từ các trường EditText
                String incomeType = edtType.getText().toString().trim();
                String incomeName = edtName.getText().toString().trim();

                // Kiểm tra dữ liệu nhập vào
                if (incomeType.isEmpty() || incomeName.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo ContentValues để thêm dữ liệu vào bảng Income_Type
                ContentValues values = new ContentValues();
                values.put("income_type", incomeType);
                values.put("income_name", incomeName);

                // Chèn dữ liệu vào bảng Income_Type
                long result = db.insert("Income_Type", null, values);

                if (result != -1) {
                    Toast.makeText(getActivity(), "Thêm loại thu nhập thành công!", Toast.LENGTH_SHORT).show();
                    edtType.setText("");
                    edtName.setText("");
                    loadChiTieu(); // Tải lại danh sách loại thu nhập sau khi thêm
                } else {
                    Toast.makeText(getActivity(), "Thêm thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @SuppressLint("Range")
    private void loadChiTieu() {
        ArrayList<ChiTieuItem> chiTieuList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT income_type, income_name FROM Income_Type", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndex("income_type"));
                String name = cursor.getString(cursor.getColumnIndex("income_name"));
                chiTieuList.add(new ChiTieuItem(type, name));
            }
            cursor.close();
        }

        // Sử dụng ChiTieuAdapter để hiển thị dữ liệu
        ChiTieuAdapter adapter = new ChiTieuAdapter(getActivity(), chiTieuList);
        lstChiTieu.setAdapter(adapter);
    }
}
