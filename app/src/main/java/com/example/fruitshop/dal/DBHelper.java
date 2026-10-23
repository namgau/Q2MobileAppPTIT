package com.example.fruitshop.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Customer;
import com.example.fruitshop.models.Fruit;
import com.example.fruitshop.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Lớp truy cập dữ liệu (DAL): tạo database SQLite gồm 4 bảng và các hàm thêm/sửa/xóa/truy vấn
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fruit_shop.db";
    private static final int DB_VERSION = 1;

    // Tên bảng. ORDER là từ khóa của SQL nên tên bảng Order phải đặt trong dấu "..."
    public static final String TABLE_ACCOUNT = "Account";
    public static final String TABLE_FRUIT = "Fruit";
    public static final String TABLE_CUSTOMER = "Customer";
    public static final String TABLE_ORDER = "\"Order\"";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Chỉ chạy 1 lần duy nhất khi file database chưa tồn tại
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ACCOUNT + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "role TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_FRUIT + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "category TEXT NOT NULL, "
                + "price REAL NOT NULL, "
                + "quantity INTEGER NOT NULL, "
                + "expiryDate TEXT NOT NULL, "
                + "description TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_CUSTOMER + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "fullname TEXT NOT NULL, "
                + "phone TEXT, "
                + "email TEXT, "
                + "accountId INTEGER, "
                + "FOREIGN KEY(accountId) REFERENCES " + TABLE_ACCOUNT + "(id))");

        db.execSQL("CREATE TABLE " + TABLE_ORDER + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "customerId INTEGER NOT NULL, "
                + "orderDate TEXT NOT NULL, "
                + "totalAmount REAL NOT NULL, "
                + "status TEXT NOT NULL, "
                + "FOREIGN KEY(customerId) REFERENCES " + TABLE_CUSTOMER + "(id))");

        insertSampleData(db);
    }

    // Khi tăng DB_VERSION: xóa bảng cũ và tạo lại
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRUIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        onCreate(db);
    }

    // Dữ liệu mẫu để đăng nhập thử ngay sau khi cài app
    private void insertSampleData(SQLiteDatabase db) {
        insertAccount(db, "admin", "123", Account.ROLE_ADMIN);
        long kh1 = insertAccount(db, "kh01", "123", Account.ROLE_CUSTOMER);
        long kh2 = insertAccount(db, "kh02", "123", Account.ROLE_CUSTOMER);

        long c1 = insertCustomer(db, "Nguyễn Văn Nam", "0901234567", "nam.nv@gmail.com", kh1);
        insertCustomer(db, "Trần Thị Hoa", "0912345678", "hoa.tt@gmail.com", kh2);

        // Hạn sử dụng tính theo ngày cài app: số âm = đã hết hạn
        db.insert(TABLE_FRUIT, null, fruitValues(new Fruit(0, "Táo Envy", "Nhập khẩu", 120000, 50,
                dateAfter(20), "Táo New Zealand, giòn ngọt")));
        db.insert(TABLE_FRUIT, null, fruitValues(new Fruit(0, "Nho xanh không hạt", "Nhập khẩu", 150000, 30,
                dateAfter(7), "Nho Úc, quả to, không hạt")));
        db.insert(TABLE_FRUIT, null, fruitValues(new Fruit(0, "Cam sành", "Trong nước", 35000, 80,
                dateAfter(15), "Cam sành Vĩnh Long, nhiều nước")));
        db.insert(TABLE_FRUIT, null, fruitValues(new Fruit(0, "Xoài cát Hòa Lộc", "Trong nước", 60000, 40,
                dateAfter(5), "Xoài chín cây, thơm ngọt")));
        db.insert(TABLE_FRUIT, null, fruitValues(new Fruit(0, "Dâu tây Đà Lạt", "Trong nước", 200000, 20,
                dateAfter(-2), "Dâu tây tươi Đà Lạt")));
        db.insert(TABLE_FRUIT, null, fruitValues(new Fruit(0, "Chuối già Nam Mỹ", "Trong nước", 25000, 60,
                dateAfter(-1), "Chuối chín vàng")));

        db.insert(TABLE_ORDER, null, orderValues(new Order(0, (int) c1,
                dateAfter(-3) + " 09:15", 240000, Order.STATUS_COMPLETED)));
        db.insert(TABLE_ORDER, null, orderValues(new Order(0, (int) c1,
                dateAfter(-1) + " 14:30", 105000, Order.STATUS_PENDING)));
    }

    // Ngày hôm nay + days, dạng yyyy-MM-dd
    private String dateAfter(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return new SimpleDateFormat(Fruit.DATE_FORMAT, Locale.US).format(cal.getTime());
    }

    private long insertAccount(SQLiteDatabase db, String username, String password, String role) {
        ContentValues v = new ContentValues();
        v.put("username", username);
        v.put("password", password);
        v.put("role", role);
        return db.insert(TABLE_ACCOUNT, null, v);
    }

    private long insertCustomer(SQLiteDatabase db, String fullname, String phone, String email, long accountId) {
        ContentValues v = new ContentValues();
        v.put("fullname", fullname);
        v.put("phone", phone);
        v.put("email", email);
        v.put("accountId", accountId);
        return db.insert(TABLE_CUSTOMER, null, v);
    }

    // ===================== ACCOUNT =====================

    // Đăng nhập: trả về Account nếu đúng username + password, sai thì trả về null
    public Account login(String username, String password) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_ACCOUNT
                + " WHERE username = ? AND password = ?", new String[]{username, password});
        Account account = null;
        if (c.moveToFirst()) {
            account = cursorToAccount(c);
        }
        c.close();
        return account;
    }

    public Account getAccountById(int id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_ACCOUNT + " WHERE id = ?",
                new String[]{String.valueOf(id)});
        Account account = null;
        if (c.moveToFirst()) {
            account = cursorToAccount(c);
        }
        c.close();
        return account;
    }

    private Account cursorToAccount(Cursor c) {
        return new Account(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("username")),
                c.getString(c.getColumnIndexOrThrow("password")),
                c.getString(c.getColumnIndexOrThrow("role")));
    }

    // ===================== CUSTOMER =====================

    public Customer getCustomerByAccountId(int accountId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_CUSTOMER + " WHERE accountId = ?",
                new String[]{String.valueOf(accountId)});
        Customer customer = null;
        if (c.moveToFirst()) {
            customer = cursorToCustomer(c);
        }
        c.close();
        return customer;
    }

    private Customer cursorToCustomer(Cursor c) {
        return new Customer(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("fullname")),
                c.getString(c.getColumnIndexOrThrow("phone")),
                c.getString(c.getColumnIndexOrThrow("email")),
                c.getInt(c.getColumnIndexOrThrow("accountId")));
    }

    // ===================== FRUIT =====================

    // Tìm theo tên hoặc loại (keyword rỗng = lấy tất cả).
    // onlyAvailable = true: chỉ lấy trái cây còn hàng (dùng cho khách hàng)
    public List<Fruit> searchFruits(String keyword, boolean onlyAvailable) {
        String sql = "SELECT * FROM " + TABLE_FRUIT + " WHERE (name LIKE ? OR category LIKE ?)";
        if (onlyAvailable) {
            sql += " AND quantity > 0";
        }
        sql += " ORDER BY name";
        String like = "%" + keyword + "%";

        List<Fruit> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(sql, new String[]{like, like});
        while (c.moveToNext()) {
            list.add(cursorToFruit(c));
        }
        c.close();
        return list;
    }

    private Fruit cursorToFruit(Cursor c) {
        return new Fruit(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("category")),
                c.getDouble(c.getColumnIndexOrThrow("price")),
                c.getInt(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("expiryDate")),
                c.getString(c.getColumnIndexOrThrow("description")));
    }

    private ContentValues fruitValues(Fruit fruit) {
        ContentValues v = new ContentValues();
        v.put("name", fruit.getName());
        v.put("category", fruit.getCategory());
        v.put("price", fruit.getPrice());
        v.put("quantity", fruit.getQuantity());
        v.put("expiryDate", fruit.getExpiryDate());
        v.put("description", fruit.getDescription());
        return v;
    }

    // Trả về id của dòng mới, -1 nếu lỗi
    public long addFruit(Fruit fruit) {
        return getWritableDatabase().insert(TABLE_FRUIT, null, fruitValues(fruit));
    }

    // Trả về số dòng bị sửa (1 = thành công)
    public int updateFruit(Fruit fruit) {
        return getWritableDatabase().update(TABLE_FRUIT, fruitValues(fruit), "id = ?",
                new String[]{String.valueOf(fruit.getId())});
    }

    // Trả về số dòng bị xóa (1 = thành công)
    public int deleteFruit(int id) {
        return getWritableDatabase().delete(TABLE_FRUIT, "id = ?",
                new String[]{String.valueOf(id)});
    }

    // ===================== ORDER =====================

    private ContentValues orderValues(Order order) {
        ContentValues v = new ContentValues();
        v.put("customerId", order.getCustomerId());
        v.put("orderDate", order.getOrderDate());
        v.put("totalAmount", order.getTotalAmount());
        v.put("status", order.getStatus());
        return v;
    }

    // Đặt hàng: thêm đơn + trừ số lượng trái cây trong kho.
    // Dùng transaction để 2 thao tác cùng thành công hoặc cùng bị hủy
    public boolean placeOrder(Order order, Fruit fruit, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            if (db.insert(TABLE_ORDER, null, orderValues(order)) == -1) {
                return false;
            }
            db.execSQL("UPDATE " + TABLE_FRUIT + " SET quantity = quantity - ? WHERE id = ?",
                    new Object[]{quantity, fruit.getId()});
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    // Admin: xem tất cả đơn hàng, mới nhất lên đầu
    public List<Order> getAllOrders() {
        return queryOrders("SELECT * FROM " + TABLE_ORDER + " ORDER BY id DESC", null);
    }

    // Khách hàng: lịch sử đơn hàng của chính mình
    public List<Order> getOrdersByCustomerId(int customerId) {
        return queryOrders("SELECT * FROM " + TABLE_ORDER + " WHERE customerId = ? ORDER BY id DESC",
                new String[]{String.valueOf(customerId)});
    }

    private List<Order> queryOrders(String sql, String[] args) {
        List<Order> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(sql, args);
        while (c.moveToNext()) {
            list.add(new Order(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getInt(c.getColumnIndexOrThrow("customerId")),
                    c.getString(c.getColumnIndexOrThrow("orderDate")),
                    c.getDouble(c.getColumnIndexOrThrow("totalAmount")),
                    c.getString(c.getColumnIndexOrThrow("status"))));
        }
        c.close();
        return list;
    }

    // Admin cập nhật trạng thái đơn: Pending / Confirmed / Completed
    public int updateOrderStatus(int orderId, String status) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        return getWritableDatabase().update(TABLE_ORDER, v, "id = ?",
                new String[]{String.valueOf(orderId)});
    }
}
