package com.example.ckqlct.Bottom_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ckqlct.AboutUs;
import com.example.ckqlct.Changepass;
import com.example.ckqlct.Nav_fragment.SettingsFragment;
import com.example.ckqlct.R;
import com.example.ckqlct.Rating;
import com.example.ckqlct.Update_Information;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingFragment extends Fragment {

    private TextView ten, txtChangeArea, txtRateApp;
    private SharedPreferences sharedPreferences;
    private Button button;
    private ImageView imageView;

    private static final int CAMERA_REQUEST_CODE = 1;

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

        // Kiểm tra xem có ảnh lưu trong SharedPreferences không
        String imagePath = sharedPreferences.getString("profile_image_path", null);
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap); // Hiển thị ảnh đã lưu
            }
        }

        // Load fullname từ SharedPreferences
        String updatedFullname = sharedPreferences.getString("fullname", "Guest");
        ten.setText(updatedFullname);
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
        button = view.findViewById(R.id.button);
        imageView = view.findViewById(R.id.imageView);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Load fullname from SharedPreferences if available
        String fullname = sharedPreferences.getString("fullname", "Guest");
        ten.setText(fullname);

        // Chức năng chụp ảnh
        button.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo); // Hiển thị ảnh chụp

            // Lưu ảnh vào bộ nhớ trong
            saveImageToStorage(photo);
        }
    }

    private void saveImageToStorage(Bitmap photo) {
        FileOutputStream fos = null;
        try {
            // Tạo thư mục nếu chưa có
            File directory = new File(getActivity().getFilesDir(), "AppImages");
            if (!directory.exists()) {
                directory.mkdir(); // Tạo thư mục nếu chưa có
            }

            // Tạo file ảnh
            File imageFile = new File(directory, "profile_image.png");

            // Ghi ảnh vào file
            fos = new FileOutputStream(imageFile);
            photo.compress(Bitmap.CompressFormat.PNG, 100, fos); // Lưu ảnh dưới định dạng PNG
            fos.flush();

            // Lưu đường dẫn ảnh vào SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profile_image_path", imageFile.getAbsolutePath());
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
