package com.example.fruitshop.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.fruitshop.models.Fruit;
import com.example.fruitshop.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Admin: thêm / sửa / xóa / tìm kiếm / xem trái cây.
// Khách hàng: xem trái cây còn hàng, tìm kiếm, kiểm tra hạn sử dụng, đặt hàng (chặn trái cây hết hạn)
public class FruitActivity extends AppCompatActivity {

    private TextView tvTitle;
    private EditText edtSearch;
    private Button btnAdd;
    private ListView lvFruits;

    private DBHelper dbHelper;
    private boolean isAdmin;
    private int customerId;

    private final List<Fruit> fruits = new ArrayList<>();
    private ArrayAdapter<Fruit> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);

        tvTitle = findViewById(R.id.tvTitle);
        edtSearch = findViewById(R.id.edtSearch);
        btnAdd = findViewById(R.id.btnAdd);
        lvFruits = findViewById(R.id.lvFruits);

        dbHelper = new DBHelper(this);

        // Đọc tài khoản đang đăng nhập từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        int accountId = prefs.getInt(LoginActivity.KEY_ACCOUNT_ID, -1);
        isAdmin = Account.ROLE_ADMIN.equals(prefs.getString(LoginActivity.KEY_ROLE, ""));

        if (isAdmin) {
            tvTitle.setText("Quản lý trái cây");
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener(v -> showFruitDialog(null));
            lvFruits.setOnItemClickListener((p, view, pos, id) -> showFruitDialog(fruits.get(pos)));
        } else {
            customerId = dbHelper.getCustomerByAccountId(accountId).getId();
            tvTitle.setText("Trái cây đang bán");
            btnAdd.setVisibility(View.GONE);   // khách hàng không được thêm/sửa/xóa
            lvFruits.setOnItemClickListener((p, view, pos, id) -> showOrderDialog(fruits.get(pos)));
        }

        adapter = new ArrayAdapter<Fruit>(this, R.layout.item_fruit, fruits) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                return bindFruitRow(getItem(position), convertView, parent);
            }
        };
        lvFruits.setAdapter(adapter);

        // Tìm kiếm ngay khi gõ
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { }
            @Override public void afterTextChanged(Editable s) { loadFruits(); }
        });

        loadFruits();
    }

    // Admin thấy tất cả, khách hàng chỉ thấy trái cây còn hàng (quantity > 0)
    private void loadFruits() {
        String keyword = edtSearch.getText().toString().trim();
        fruits.clear();
        fruits.addAll(dbHelper.searchFruits(keyword, !isAdmin));
        adapter.notifyDataSetChanged();
    }

    // Đổ dữ liệu 1 trái cây vào 1 dòng item_fruit.xml; trái cây hết hạn hiện màu đỏ
    private View bindFruitRow(Fruit fruit, View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(this).inflate(R.layout.item_fruit, parent, false);
        }
        TextView tvName = row.findViewById(R.id.tvName);
        TextView tvPrice = row.findViewById(R.id.tvPrice);
        TextView tvDetail = row.findViewById(R.id.tvDetail);
        TextView tvExpiry = row.findViewById(R.id.tvExpiry);
        TextView tvDescription = row.findViewById(R.id.tvDescription);

        tvName.setText(fruit.getName());
        tvPrice.setText(formatMoney(fruit.getPrice()));
        tvDetail.setText(fruit.getCategory() + "  •  Số lượng: " + fruit.getQuantity());
        tvDescription.setText(fruit.getDescription());

        if (fruit.isExpired()) {
            tvExpiry.setText("HSD: " + formatDate(fruit.getExpiryDate()) + "  •  ĐÃ HẾT HẠN");
            tvExpiry.setTextColor(ContextCompat.getColor(this, R.color.expired));
        } else {
            tvExpiry.setText("HSD: " + formatDate(fruit.getExpiryDate()) + "  •  Còn hạn");
            tvExpiry.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
        return row;
    }

    // ===================== ADMIN: THÊM / SỬA / XÓA =====================

    // fruit == null -> thêm mới; khác null -> sửa / xóa
    private void showFruitDialog(Fruit fruit) {
        View form = LayoutInflater.from(this).inflate(R.layout.dialog_fruit, null);
        EditText edtName = form.findViewById(R.id.edtName);
        EditText edtCategory = form.findViewById(R.id.edtCategory);
        EditText edtPrice = form.findViewById(R.id.edtPrice);
        EditText edtQuantity = form.findViewById(R.id.edtQuantity);
        EditText edtExpiry = form.findViewById(R.id.edtExpiry);
        EditText edtDescription = form.findViewById(R.id.edtDescription);

        if (fruit != null) {
            edtName.setText(fruit.getName());
            edtCategory.setText(fruit.getCategory());
            edtPrice.setText(String.valueOf((long) fruit.getPrice()));
            edtQuantity.setText(String.valueOf(fruit.getQuantity()));
            edtExpiry.setText(fruit.getExpiryDate());
            edtDescription.setText(fruit.getDescription());
        }

        // Bấm ô hạn sử dụng -> mở lịch chọn ngày, lưu dạng yyyy-MM-dd
        edtExpiry.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (picker, y, m, d) ->
                    edtExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(fruit == null ? "Thêm trái cây" : "Cập nhật trái cây")
                .setView(form)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null);
        if (fruit != null) {
            builder.setNeutralButton("Xóa", (d, w) -> {
                dbHelper.deleteFruit(fruit.getId());
                Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                loadFruits();
            });
        }
        AlertDialog dialog = builder.show();

        // Gán lại nút Lưu để dữ liệu sai thì báo lỗi mà không đóng dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String category = edtCategory.getText().toString().trim();
            String priceText = edtPrice.getText().toString().trim();
            String quantityText = edtQuantity.getText().toString().trim();
            String expiry = edtExpiry.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (name.isEmpty()) { edtName.setError("Nhập tên trái cây"); return; }
            if (category.isEmpty()) { edtCategory.setError("Nhập loại"); return; }
            if (priceText.isEmpty() || Double.parseDouble(priceText) <= 0) {
                edtPrice.setError("Giá phải lớn hơn 0");
                return;
            }
            if (quantityText.isEmpty()) { edtQuantity.setError("Nhập số lượng"); return; }
            if (expiry.isEmpty()) {
                Toast.makeText(this, "Chọn hạn sử dụng", Toast.LENGTH_SHORT).show();
                return;
            }

            Fruit data = new Fruit(fruit == null ? 0 : fruit.getId(), name, category,
                    Double.parseDouble(priceText), Integer.parseInt(quantityText), expiry, description);
            if (fruit == null) {
                dbHelper.addFruit(data);
                Toast.makeText(this, "Đã thêm trái cây", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateFruit(data);
                Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            loadFruits();
        });
    }

    // ===================== KHÁCH HÀNG: ĐẶT HÀNG =====================

    private void showOrderDialog(Fruit fruit) {
        if (fruit.isExpired()) {
            new AlertDialog.Builder(this)
                    .setTitle("Không thể đặt hàng")
                    .setMessage(fruit.getName() + " đã hết hạn sử dụng ("
                            + formatDate(fruit.getExpiryDate()) + ").")
                    .setPositiveButton("Đóng", null)
                    .show();
            return;
        }

        View form = LayoutInflater.from(this).inflate(R.layout.dialog_order, null);
        TextView tvInfo = form.findViewById(R.id.tvInfo);
        EditText edtQuantity = form.findViewById(R.id.edtQuantity);

        tvInfo.setText(fruit.getDescription()
                + "\nGiá: " + formatMoney(fruit.getPrice())
                + "\nCòn lại: " + fruit.getQuantity()
                + "\nHSD: " + formatDate(fruit.getExpiryDate()));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Đặt " + fruit.getName())
                .setView(form)
                .setPositiveButton("Đặt hàng", null)
                .setNegativeButton("Hủy", null)
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String text = edtQuantity.getText().toString().trim();
            int quantity;
            try {
                quantity = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                edtQuantity.setError("Nhập số lượng hợp lệ");
                return;
            }

            if (quantity <= 0) {
                edtQuantity.setError("Số lượng phải lớn hơn 0");
                return;
            }
            if (quantity > fruit.getQuantity()) {
                edtQuantity.setError("Chỉ còn " + fruit.getQuantity());
                return;
            }

            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date());
            Order order = new Order(0, customerId, now,
                    quantity * fruit.getPrice(), Order.STATUS_PENDING);
            if (dbHelper.placeOrder(order, fruit, quantity)) {
                Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadFruits();
            } else {
                Toast.makeText(this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===================== HÀM ĐỊNH DẠNG =====================

    // 120000 -> "120.000 đ"
    public static String formatMoney(double amount) {
        return String.format(Locale.US, "%,.0f đ", amount).replace(',', '.');
    }

    // "2026-10-30" -> "30/10/2026"
    public static String formatDate(String yyyyMMdd) {
        String[] p = yyyyMMdd.split("-");
        return p.length == 3 ? p[2] + "/" + p[1] + "/" + p[0] : yyyyMMdd;
    }
}
