package com.example.ckqlct;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class Notify {

    public static void showExitConfirmation(final Context context, final Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_launcher_round);
        builder.setTitle("Xác nhận đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?");
        builder.setCancelable(false); // Không thể thoát hộp thoại bằng cách nhấn ra ngoài

        // Nút đồng ý
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Gọi hành động khi người dùng xác nhận (chuyển sang LoginActivity)
                onConfirm.run();
            }
        });

        // Nút hủy bỏ
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context.getApplicationContext(), "Người dùng chọn hủy",Toast.LENGTH_LONG).show();
                dialog.dismiss(); // Đóng hộp thoại nếu người dùng chọn hủy
            }
        });

        // Hiển thị hộp thoại
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
