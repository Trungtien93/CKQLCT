package com.example.ckqlct;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.ckqlct.Bottom_fragment.StaticticalFragment;
import com.example.ckqlct.Nav_fragment.AboutFragment;
import com.example.ckqlct.Nav_fragment.SettingsFragment;
import com.example.ckqlct.Nav_fragment.ShareFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;


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
        bottomNavigationView = findViewById (R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById (R.id.drawer_layout);
        NavigationView navigationView = findViewById (R.id.nav_view);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (this,drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load fragment đầu tiên nếu chưa có
        if (savedInstanceState == null) {
            //Hiện Map đầu tiên
            replaceFragment(new HomeFragment ());
            // getSupportFragmentManager().beginTransaction().replace(R.id.frament_layout, new HomeFragment ()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        //Thay thế Fragment theo lựa chọn của NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_settings) {
                replaceFragment(new SettingsFragment ());
            }else if(item.getItemId() == R.id.nav_share){
                replaceFragment(new ShareFragment ());
            }else if(item.getItemId() == R.id.nav_about){
                replaceFragment(new AboutFragment ());
            }  else if (item.getItemId() == R.id.nav_logout) {
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


//        // Thay thế Fragment theo lựa chọn của BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home)
            {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.manager) {
                replaceFragment(new ManagerFragment ());
            }else if (item.getItemId() == R.id.buy) {
                replaceFragment(new BuyFragment ());
            }else if (item.getItemId() == R.id.statictical) {
                replaceFragment(new StaticticalFragment ());
            }
            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

    }


    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet);
        edtDay = dialog.findViewById(R.id.edtNgay);

        // Khởi tạo Spinner bên trong Dialog
        Spinner loai = dialog.findViewById(R.id.spinnerloaigd);
        String loaict[] = {"Chi Tiêu", "Tiền Tiết Kiệm"};

        Spinner spnTen = dialog.findViewById(R.id.spinnerten);
        String ten[] = {"Ăn Uống", "Đi Lại", "Mua Sắm", "Chi tiêu Khác"};

        // Tạo Adapter cho Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loaict);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ten);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gắn Adapter vào Spinner
        loai.setAdapter(adapter);
        spnTen.setAdapter(adapter1);

        // Xử lý sự kiện chọn Spinner
        loai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Bạn đã chọn: " + loaict[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì nếu không có gì được chọn
            }
        });
        // Set a click listener to open the DatePickerDialog
        edtDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date as default
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Create and show DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Format and set the selected date in the EditText
                                edtDay.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        spnTen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Bạn đã chọn: " + ten[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì nếu không có gì được chọn
            }
        });

        // Hiển thị dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }



    // Phương thức để ẩn/hiện phần Chi tiêu
    private void toggleExpenseSections(LinearLayout section) {
        if (section.getVisibility() == View.GONE) {
            section.setVisibility(View.VISIBLE);
        } else {
            section.setVisibility(View.GONE);
        }
    }

    // Phương thức để ẩn/hiện phần Tiền tiết kiệm
    private void toggleSavingSections(LinearLayout section) {
        if (section.getVisibility() == View.GONE) {
            section.setVisibility(View.VISIBLE);
        } else {
            section.setVisibility(View.GONE);
        }
    }
    // Hàm thay thế Fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frament_layout, fragment);
        fragmentTransaction.commit();
    }
}