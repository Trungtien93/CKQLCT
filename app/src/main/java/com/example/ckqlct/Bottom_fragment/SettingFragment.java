package com.example.ckqlct.Bottom_fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ckqlct.AboutUs;
import com.example.ckqlct.Changepass;
import com.example.ckqlct.Nav_fragment.SettingsFragment;
import com.example.ckqlct.R;
import com.example.ckqlct.DataQLCT;
import com.example.ckqlct.Rating;
import com.example.ckqlct.Update_Information;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class SettingFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final String DATABASE_NAME = "QLCTCK.db";
    private TextView ten, txtChangeArea, txtRateApp;
    private Button btn_ChangeAvatar;
    private ImageView imgAvatar;
    private int UserId;
    private SQLiteDatabase db;
    private DataQLCT helper;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    public SettingFragment() {}

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
        btn_ChangeAvatar = view.findViewById(R.id.btn_ChangeAvatar); // Khởi tạo btn_ChangeAvatar

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullname = sharedPreferences.getString("fullname", "Guest");
        ten.setText(fullname);

        // Cài đặt sự kiện cho các button
        btn_ChangeAvatar.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Đổi ảnh đại diện")
                    .setItems(new CharSequence[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                        if (which == 0) {
                            // Kiểm tra quyền camera trước khi mở camera
                            if (checkAndRequestPermissions()) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraLauncher.launch(intent);
                            }
                        } else if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            galleryLauncher.launch(intent);
                        }
                    })
                    .show();
        });

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

        // Load thông tin người dùng từ database
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        UserId = preferences.getInt("id_user", -1);
        db = getActivity().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    String imagePath = saveImageToInternalStorage(photo);
                    if (imagePath != null) {
                        saveAvatarPathToDatabase(imagePath);
                        imgAvatar.setImageBitmap(photo);
                    }
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    try {
                        Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                        String imagePath = saveImageToInternalStorage(selectedImage);
                        if (imagePath != null) {
                            saveAvatarPathToDatabase(imagePath);
                            imgAvatar.setImageBitmap(selectedImage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        helper = new DataQLCT(getContext());
        helper.loadAvatarFromDatabase(imgAvatar, UserId);

        return view;
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = getContext().getFilesDir();
        Calendar calendar = Calendar.getInstance();
        String avatar = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + "ID" + UserId;
        File imageFile = new File(directory, avatar + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveAvatarPathToDatabase(String path) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("image", path);
        db.update("User", values, "id_user = ?", new String[]{String.valueOf(UserId)});
        db.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(intent);
            } else {
                Toast.makeText(getContext(), "Permission denied to access camera", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
