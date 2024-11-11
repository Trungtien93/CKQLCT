package com.example.ckqlct.Nav_fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.ckqlct.R;

public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Tìm TextView với id privacyPolicy
        TextView privacyPolicy = view.findViewById(R.id.privacyPolicy);

        // Thiết lập sự kiện click cho TextView
        privacyPolicy.setOnClickListener(v -> {
            // Tạo AlertDialog để hiển thị nội dung Chính sách Quyền riêng tư và Điều khoản Sử dụng
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Chính sách Quyền riêng tư và Điều khoản Sử dụng");
            builder.setMessage("1. Mục đích Thu thập Thông tin\n\n" +
                    "Chúng tôi thu thập thông tin của bạn nhằm cung cấp dịch vụ tốt hơn, bao gồm:\n\n" +
                    "- Quản lý tài khoản cá nhân.\n" +
                    "- Cải thiện trải nghiệm người dùng.\n" +
                    "- Cung cấp các dịch vụ liên quan và hỗ trợ.\n\n" +
                    "2. Loại Thông tin Thu thập\n\n" +
                    "- Thông tin cá nhân: Họ tên, địa chỉ email.\n" +
                    "- Thông tin chi tiêu và giao dịch: Được lưu trong ứng dụng và chỉ được chia sẻ khi bạn cho phép.\n\n" +
                    "3. Cách Sử dụng Thông tin\n\n" +
                    "- Xác minh danh tính.\n" +
                    "- Cải thiện tính năng và dịch vụ của ứng dụng.\n" +
                    "- Liên lạc với người dùng khi cần thiết.\n\n" +
                    "4. Bảo mật Thông tin\n\n" +
                    "- NTN Group cam kết bảo mật thông tin người dùng.\n\n" +
                    "5. Quyền Lựa chọn và Kiểm soát của Người Dùng\n\n" +
                    "- Bạn có quyền kiểm tra, cập nhật hoặc xóa thông tin cá nhân của mình.\n\n" +
                    "6. Chia sẻ Thông tin với Bên Thứ Ba\n\n" +
                    "- Khi có sự đồng ý của bạn hoặc yêu cầu từ cơ quan pháp luật.\n\n" +
                    "7. Cập nhật Chính sách Quyền riêng tư\n\n" +
                    "8. Liên hệ: privacy@ntngroup.com");
            builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

            // Hiển thị AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // Tìm Button với id contactButton
        Button contactButton = view.findViewById(R.id.contactButton);

        // Thiết lập sự kiện click cho contactButton
        contactButton.setOnClickListener(v -> {
            // Tạo AlertDialog với layout tùy chỉnh contact_info.xml
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = getLayoutInflater();
            View dialogView = inflater1.inflate(R.layout.contact_info, null);
            builder.setView(dialogView);

            // Thiết lập nút đóng cho AlertDialog
            builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

            // Hiển thị AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return view;
    }
}
