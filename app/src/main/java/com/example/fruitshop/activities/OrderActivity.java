package com.example.fruitshop.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.fruitshop.R;
import com.example.fruitshop.dal.DBHelper;
import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Admin: xem tất cả đơn hàng + cập nhật trạng thái. Khách hàng: xem lịch sử đơn hàng của mình
public class OrderActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ListView lvOrders;

    private DBHelper dbHelper;
    private boolean isAdmin;
    private int customerId;   // dùng khi role = CUSTOMER

    private final List<Order> orders = new ArrayList<>();
    private ArrayAdapter<Order> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        tvTitle = findViewById(R.id.tvTitle);
        lvOrders = findViewById(R.id.lvOrders);

        dbHelper = new DBHelper(this);

        // Đọc tài khoản đang đăng nhập từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        int accountId = prefs.getInt(LoginActivity.KEY_ACCOUNT_ID, -1);
        isAdmin = Account.ROLE_ADMIN.equals(prefs.getString(LoginActivity.KEY_ROLE, ""));

        if (isAdmin) {
            tvTitle.setText("Quản lý đơn hàng");
            lvOrders.setOnItemClickListener((p, view, pos, id) -> showStatusDialog(orders.get(pos)));
        } else {
            tvTitle.setText("Lịch sử đơn hàng");
            customerId = dbHelper.getCustomerByAccountId(accountId).getId();
        }

        adapter = new ArrayAdapter<Order>(this, R.layout.item_order, orders) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                return bindOrderRow(getItem(position), convertView, parent);
            }
        };
        lvOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        orders.clear();
        orders.addAll(isAdmin ? dbHelper.getAllOrders() : dbHelper.getOrdersByCustomerId(customerId));
        adapter.notifyDataSetChanged();
    }

    // Đổ dữ liệu 1 đơn hàng vào 1 dòng item_order.xml
    private View bindOrderRow(Order order, View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(this).inflate(R.layout.item_order, parent, false);
        }
        TextView tvOrderId = row.findViewById(R.id.tvOrderId);
        TextView tvStatus = row.findViewById(R.id.tvStatus);
        TextView tvDate = row.findViewById(R.id.tvDate);
        TextView tvTotal = row.findViewById(R.id.tvTotal);

        tvOrderId.setText("Đơn #" + order.getId());
        tvStatus.setText(order.getStatus());
        tvStatus.setTextColor(ContextCompat.getColor(this, statusColor(order.getStatus())));
        tvDate.setText("Ngày đặt: " + order.getOrderDate());
        tvTotal.setText(FruitActivity.formatMoney(order.getTotalAmount()));
        return row;
    }

    private int statusColor(String status) {
        if (Order.STATUS_COMPLETED.equals(status)) return R.color.primary;
        if (Order.STATUS_CONFIRMED.equals(status)) return R.color.status_confirmed;
        return R.color.status_pending;
    }

    // Admin chọn trạng thái mới: Pending / Confirmed / Completed
    private void showStatusDialog(Order order) {
        int checked = Arrays.asList(Order.STATUSES).indexOf(order.getStatus());
        new AlertDialog.Builder(this)
                .setTitle("Trạng thái đơn #" + order.getId())
                .setSingleChoiceItems(Order.STATUSES, checked, (dialog, which) -> {
                    dbHelper.updateOrderStatus(order.getId(), Order.STATUSES[which]);
                    Toast.makeText(this, "Đã cập nhật: " + Order.STATUSES[which], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadOrders();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
