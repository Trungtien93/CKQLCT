package com.example.ckqlct;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ckqlct.Bottom_fragment.BuyFragment;
import com.example.ckqlct.Bottom_fragment.ManagerFragment;
import com.example.ckqlct.Bottom_fragment.StaticticalFragment;
import com.example.ckqlct.Nav_fragment.AboutFragment;
import com.example.ckqlct.Nav_fragment.HomeFragment;
import com.example.ckqlct.Nav_fragment.LogoutFragment;
import com.example.ckqlct.Nav_fragment.SettingsFragment;
import com.example.ckqlct.Nav_fragment.ShareFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {


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

    //    private void showBottomDialog(){
//
//    }
    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Upload a Video is clicked",Toast.LENGTH_SHORT).show();

            }
        });

        shortsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Create a short is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        liveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Go live is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable (Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
    // Hàm thay thế Fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frament_layout, fragment);
        fragmentTransaction.commit();
    }
}