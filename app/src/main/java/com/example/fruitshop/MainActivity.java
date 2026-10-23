package com.example.fruitshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitshop.activities.FruitActivity;
import com.example.fruitshop.activities.LoginActivity;
import com.example.fruitshop.activities.OrderActivity;
import com.example.fruitshop.dal.DBHelper;
import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Customer;

// Màn hình mở đầu: kiểm tra đã đăng nhập chưa (SharedPreferences), rồi hiện chức năng theo role
public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvRole;
    private Button btnFruits, btnOrders, btnLogout;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);

        // Chưa đăng nhập -> chuyển sang màn hình Login
        if (!prefs.getBoolean(LoginActivity.KEY_LOGGED_IN, false)) {
            goToLogin();
            return;
        }

        DBHelper dbHelper = new DBHelper(this);
        Account account = dbHelper.getAccountById(prefs.getInt(LoginActivity.KEY_ACCOUNT_ID, -1));
        if (account == null) {
            logout();
            return;
        }

        setContentView(R.layout.activity_main);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvRole = findViewById(R.id.tvRole);
        btnFruits = findViewById(R.id.btnFruits);
        btnOrders = findViewById(R.id.btnOrders);
        btnLogout = findViewById(R.id.btnLogout);

        if (account.isAdmin()) {
            tvWelcome.setText("Xin chào, " + account.getUsername());
            tvRole.setText("Vai trò: Admin");
            btnFruits.setText("Quản lý trái cây");
            btnOrders.setText("Quản lý đơn hàng");
        } else {
            Customer customer = dbHelper.getCustomerByAccountId(account.getId());
            tvWelcome.setText("Xin chào, " + customer.getFullname());
            tvRole.setText("Vai trò: Khách hàng");
            btnFruits.setText("Xem & đặt trái cây");
            btnOrders.setText("Lịch sử đơn hàng");
        }

        btnFruits.setOnClickListener(v -> startActivity(new Intent(this, FruitActivity.class)));
        btnOrders.setOnClickListener(v -> startActivity(new Intent(this, OrderActivity.class)));
        btnLogout.setOnClickListener(v -> logout());
    }

    // Đăng xuất: xóa thông tin tài khoản đăng nhập khỏi SharedPreferences
    private void logout() {
        prefs.edit().clear().apply();
        goToLogin();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
