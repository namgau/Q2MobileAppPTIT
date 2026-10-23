package com.example.fruitshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitshop.MainActivity;
import com.example.fruitshop.R;
import com.example.fruitshop.dal.DBHelper;
import com.example.fruitshop.models.Account;

public class LoginActivity extends AppCompatActivity {

    // Tên file SharedPreferences và các key lưu thông tin tài khoản đăng nhập
    public static final String PREF_NAME = "login_prefs";
    public static final String KEY_LOGGED_IN = "isLoggedIn";
    public static final String KEY_ACCOUNT_ID = "accountId";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_ROLE = "role";

    private EditText edtUsername, edtPassword;
    private Button btnLogin;

    private DBHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DBHelper(this);
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Vui lòng nhập tên đăng nhập");
            edtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        Account account = dbHelper.login(username, password);
        if (account == null) {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu thông tin tài khoản đăng nhập vào SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putInt(KEY_ACCOUNT_ID, account.getId());
        editor.putString(KEY_USERNAME, account.getUsername());
        editor.putString(KEY_ROLE, account.getRole());
        editor.apply();

        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
