package com.example.ckqlct.Bottom_fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ckqlct.AboutUs;
import com.example.ckqlct.Changepass;
import com.example.ckqlct.DataQLCT;
import com.example.ckqlct.Nav_fragment.SettingsFragment;
import com.example.ckqlct.R;
import com.example.ckqlct.Rating;
import com.example.ckqlct.Update_Information;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class SettingFragment extends Fragment {

    private static final String DATABASE_NAME = "QLCTCK.db";
    private TextView ten, txtChangeArea, txtRateApp;
    private Button btn_ChangeAvatar;
    private ImageView imgAvatar;
    int UserId;
    SQLiteDatabase db;
    private DataQLCT helper;

    private ActivityResultLauncher<Intent> cameraLauncher; // Sử dụng Activity Result API
    private ActivityResultLauncher<Intent> galleryLauncher;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void init(View v) {
        imgAvatar = v.findViewById(R.id.imgAvatar);
        btn_ChangeAvatar = v.findViewById(R.id.btn_ChangeAvatar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views from the fragment's layout
        ten = view.findViewById(R.id.txtTitle);
        txtChangeArea = view.findViewById(R.id.txtChangeArea);
        txtRateApp = view.findViewById(R.id.txtRateApp);
        TextView thongtin = view.findViewById(R.id.txtPersonalInfo);
        TextView Caidat = view.findViewById(R.id.txtSettings);
        TextView thongTin = view.findViewById(R.id.txtCompanyInfo);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Load fullname from SharedPreferences if available
        String fullname = sharedPreferences.getString("fullname", "Guest");
        ten.setText(fullname);

        // Chuyển đến màn hình Thông tin cá nhân
        thongtin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Update_Information.class);
            startActivity(intent);
        });

        // Chuyển đến màn hình thay đổi mật khẩu
        txtChangeArea.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Changepass.class);
            startActivity(intent);
        });

        // Chuyển đến màn hình đánh giá ứng dụng
        txtRateApp.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Rating.class);
            startActivity(intent);
        });

        // Chuyển đến màn hình Cài đặt
        Caidat.setOnClickListener(view1 -> {
            SettingsFragment settingFragment = new SettingsFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frament_layout, settingFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Chuyển đến màn hình thông tin công ty
        thongTin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutUs.class);
            startActivity(intent);
        });

        helper = new DataQLCT(getContext());
        init(view);
        btn_ChangeAvatar.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Đổi ảnh đại diện")
                    .setItems(new CharSequence[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                        if (which == 0) {
                            // Tùy chọn 1: Chụp ảnh
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraLauncher.launch(intent);
                        } else if (which == 1) {
                            // Tùy chọn 2: Chọn từ thư viện
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            galleryLauncher.launch(intent);
                        }
                    })
                    .show();
        });

        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        UserId = preferences.getInt("id_user", -1); // Kiểm tra giá trị hợp lệ của UserId

        // Open the database
        db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            // Lưu ảnh vào bộ nhớ trong
                            String imagePath = saveImageToInternalStorage(photo);
                            if (imagePath != null) {
                                // Lưu đường dẫn vào CSDL
                                saveAvatarPathToDatabase(imagePath);
                                // Hiển thị ảnh lên ImageView
                                imgAvatar.setImageBitmap(photo);
                            }
                        }
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            try {
                                // Lấy đường dẫn ảnh từ thư viện
                                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                                // Lưu ảnh vào bộ nhớ trong
                                String imagePath = saveImageToInternalStorage(selectedImage);
                                if (imagePath != null) {
                                    // Lưu đường dẫn vào CSDL
                                    saveAvatarPathToDatabase(imagePath);
                                    // Hiển thị ảnh lên ImageView
                                    imgAvatar.setImageBitmap(selectedImage);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        // Kiểm tra imgAvatar có null không trước khi gọi loadAvatarFromDatabase
        if (imgAvatar != null) {
            helper.loadAvatarFromDatabase(imgAvatar, UserId);
        } else {
            Log.e("SettingFragment", "ImageView imgAvatar is null, cannot load avatar");
        }

        return view;
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = getContext().getFilesDir(); // Thư mục lưu trữ của ứng dụng
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String avatar = day + "-" + month + "-" + year + "ID" + UserId;
        File imageFile = new File(directory, avatar + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return imageFile.getAbsolutePath(); // Trả về đường dẫn của ảnh
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveAvatarPathToDatabase(String path) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("image", path);
        try {
            db.update("User", values, "id_user = ?", new String[]{String.valueOf(UserId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

