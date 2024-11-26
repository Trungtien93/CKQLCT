package com.example.ckqlct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ckqlct.Bottom_fragment.BuyFragment;
import com.example.ckqlct.Bottom_fragment.HomeFragment;
import com.example.ckqlct.Bottom_fragment.ManagerFragment;
import com.example.ckqlct.Bottom_fragment.SettingFragment;
import com.example.ckqlct.Nav_fragment.AboutFragment;
import com.example.ckqlct.Nav_fragment.SettingsFragment;
import com.example.ckqlct.Nav_fragment.ShareFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {
    EditText edtDay;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gán view cho DrawerLayout, Toolbar và BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load fragment đầu tiên nếu chưa có
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Thay thế Fragment theo lựa chọn của NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_settings) {
                replaceFragment(new SettingsFragment());
            }
            else if (item.getItemId() == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.nav_share) {
                replaceFragment(new ShareFragment());
            } else if (item.getItemId() == R.id.nav_about) {
                replaceFragment(new AboutFragment());
            } else if (item.getItemId() == R.id.nav_logout) {
                // Hiển thị cảnh báo trước khi đăng xuất
                Notify.showExitConfirmation(MainActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        // Chuyển sang LoginActivity nếu người dùng xác nhận
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        finish();  // Đóng MainActivity
                    }
                });
            }
            return true;
        });

        // Thay thế Fragment theo lựa chọn của BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.manager) {
                replaceFragment(new ManagerFragment());
            } else if (item.getItemId() == R.id.buy) {
                replaceFragment(new BuyFragment());
            } else if (item.getItemId() == R.id.statictical) {
                replaceFragment(new SettingFragment());
            }
            return true;
        });

        // Sự kiện cho FAB
        fab.setOnClickListener(view -> showBottomDialog());
    }

    // Phương thức để thay thế Fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frament_layout, fragment);
        fragmentTransaction.commit();
    }

    // Phương thức hiển thị BottomSheetDialog
    private void showBottomDialog() {
        // Tạo BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.add_transaction);

        // Khởi tạo các view từ layout
        TextView textTitle = bottomSheetDialog.findViewById(R.id.textTitle);
        Button btnAddIncome = bottomSheetDialog.findViewById(R.id.btnAddIncome);
        Button btnAddExpense = bottomSheetDialog.findViewById(R.id.btnAddExpense);

        // Thiết lập sự kiện cho các nút
        btnAddIncome.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Thêm chi tiêu", Toast.LENGTH_SHORT).show();
            // Mở Activity mới
            Intent intent = new Intent(MainActivity.this, ThemChiTieu.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        btnAddExpense.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Thêm Thu Nhập", Toast.LENGTH_SHORT).show();
            // Mở Activity mới
            Intent intent = new Intent(MainActivity.this, ThemThuNhap.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        // Hiển thị BottomSheetDialog
        bottomSheetDialog.show();
    }

    // Hàm này sẽ được sử dụng để quản lý các phần không cần thiết hiện thị hoặc ẩn
    private void toggleExpenseSections(LinearLayout section) {
        section.setVisibility(section.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }
}
