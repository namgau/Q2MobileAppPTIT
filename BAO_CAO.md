# BÁO CÁO PROJECT — FRUIT SHOP MANAGEMENT

> Ứng dụng Android quản lý cửa hàng trái cây — Java, SQLite, SharedPreferences
> Nhóm 3 thành viên — quản lý mã nguồn bằng GitHub

---

## MỤC LỤC
1. [Đề bài và cách đáp ứng](#1-đề-bài-và-cách-đáp-ứng)
2. [Công nghệ sử dụng](#2-công-nghệ-sử-dụng)
3. [Thành phần của project](#3-thành-phần-của-project)
4. [Cơ sở dữ liệu](#4-cơ-sở-dữ-liệu)
5. [Luồng hoạt động](#5-luồng-hoạt-động)
6. [Giải thích chi tiết từng file](#6-giải-thích-chi-tiết-từng-file)
7. [Phân công công việc 3 thành viên](#7-phân-công-công-việc-3-thành-viên)
8. [Hướng dẫn chạy và kiểm thử](#8-hướng-dẫn-chạy-và-kiểm-thử)
9. [Câu hỏi vấn đáp thường gặp](#9-câu-hỏi-vấn-đáp-thường-gặp)

---

## 1. Đề bài và cách đáp ứng

| # | Yêu cầu của đề | Cách làm trong project | File chính |
|---|---|---|---|
| 1 | 4 bảng `Account`, `Fruit`, `Customer`, `Order` | Tạo đúng 4 bảng, đúng tên cột, không thêm cột | `dal/DBHelper.java`, `models/*.java` |
| 2 | **Login** 2 role: **Admin** và **Customer** | Cột `role` = `ADMIN` / `CUSTOMER`, mỗi role thấy chức năng khác nhau | `LoginActivity`, `MainActivity` |
| 3 | Lưu tài khoản đăng nhập bằng **SharedPreferences** | Lưu `isLoggedIn, accountId, username, role` | `LoginActivity`, `MainActivity` |
| 4 | Lưu bảng bằng **SQLite** hoặc Room | Chọn **SQLite** (`SQLiteOpenHelper`) | `dal/DBHelper.java` |
| 5 | **Admin**: thêm, sửa, xóa, tìm kiếm, xem trái cây (tên, loại, giá, số lượng, HSD, mô tả) | Danh sách hiện đủ 6 thông tin + ô tìm kiếm + nút "Thêm" + bấm 1 dòng để Sửa/Xóa | `FruitActivity` (phần admin) |
| 6 | **Quản lý hạn sử dụng**: hiện HSD, nhận biết trái cây hết hạn, **không cho khách đặt trái cây hết hạn** | Mỗi dòng hiện `HSD: dd/MM/yyyy • Còn hạn` (xanh) hoặc `• ĐÃ HẾT HẠN` (đỏ); khách bấm trái cây hết hạn → hộp thoại "Không thể đặt hàng" | `Fruit.isExpired()`, `FruitActivity` |
| 7 | **Customer**: xem trái cây còn hàng, tìm kiếm, kiểm tra HSD, đặt hàng, xem lịch sử đơn | Danh sách trái cây `quantity > 0` + tìm kiếm + HSD từng dòng + hộp thoại đặt hàng + màn lịch sử | `FruitActivity` (phần khách), `OrderActivity` |
| 8 | **Quản lý đơn hàng**: khách đặt hàng, admin cập nhật trạng thái **Pending / Confirmed / Completed** | Đơn mới luôn là `Pending`; admin bấm vào đơn → chọn trạng thái mới | `OrderActivity`, `DBHelper.placeOrder / updateOrderStatus` |
| 9 | Làm nhóm, quản lý code bằng **GitHub/GitLab** | Repo GitHub, mỗi người 1 nhánh, gộp bằng Pull Request | Mục 7 |

**Tài khoản mẫu** (tự tạo khi cài app lần đầu, mật khẩu đều là `123`):

| Username | Role | Thông tin |
|---|---|---|
| `admin` | ADMIN | Quản trị viên |
| `kh01` | CUSTOMER | ID khách hàng = 1 — Nguyễn Văn Nam — 0901234567 — có sẵn 2 đơn hàng |
| `kh02` | CUSTOMER | ID khách hàng = 2 — Trần Thị Hoa — 0912345678 — chưa có đơn |

**Trái cây mẫu** (hạn sử dụng tính theo ngày cài app, nên luôn có cả loại còn hạn và hết hạn để thử):

| Tên | Loại | Giá | SL | HSD |
|---|---|---|---|---|
| Táo Envy | Nhập khẩu | 120.000 đ | 50 | hôm nay + 20 ngày |
| Nho xanh không hạt | Nhập khẩu | 150.000 đ | 30 | + 7 ngày |
| Cam sành | Trong nước | 35.000 đ | 80 | + 15 ngày |
| Xoài cát Hòa Lộc | Trong nước | 60.000 đ | 40 | + 5 ngày |
| Dâu tây Đà Lạt | Trong nước | 200.000 đ | 20 | **đã hết hạn 2 ngày** |
| Chuối già Nam Mỹ | Trong nước | 25.000 đ | 60 | **đã hết hạn 1 ngày** |

---

## 2. Công nghệ sử dụng

| Thành phần | Dùng gì | Lý do |
|---|---|---|
| Ngôn ngữ | Java 8 | Theo chương trình môn học |
| Giao diện | XML Layout (`LinearLayout`, `ListView`, `AlertDialog`) | Đơn giản, dễ đọc |
| Theme | Material Components (màu xanh lá `#2E7D32`) | Nút, hộp thoại đẹp sẵn |
| CSDL | SQLite qua `SQLiteOpenHelper` | Có sẵn trong Android, không cần thư viện ngoài (so sánh với Room ở **mục 4.3**) |
| Lưu phiên đăng nhập | SharedPreferences | Lưu dữ liệu nhỏ dạng key–value |
| Build | Gradle, `minSdk 21`, `compileSdk 34` | Chạy được trên hầu hết máy Android |
| Quản lý code | Git + GitHub | Yêu cầu của đề |

---

## 3. Thành phần của project

### 3.1. Cây thư mục Java (giống format project mẫu)

```
com.example.fruitshop
│
├── activities
│    ├── LoginActivity.java      ← Đăng nhập + ghi SharedPreferences
│    ├── FruitActivity.java      ← Trái cây (Admin: thêm/sửa/xóa/tìm/xem — Khách: xem/tìm/đặt hàng)
│    └── OrderActivity.java      ← Đơn hàng (Admin: cập nhật trạng thái — Khách: lịch sử)
│
├── dal
│    └── DBHelper.java           ← Tạo database SQLite 4 bảng + toàn bộ câu lệnh SQL
│
├── models
│    ├── Account.java            ← 1 dòng bảng Account
│    ├── Fruit.java              ← 1 dòng bảng Fruit (+ hàm isExpired)
│    ├── Customer.java           ← 1 dòng bảng Customer
│    └── Order.java              ← 1 dòng bảng Order (+ 3 hằng trạng thái)
│
└── MainActivity.java            ← Màn hình mở đầu: kiểm tra đăng nhập, trang chủ theo role
```

- **`models`**: chỉ chứa dữ liệu (thuộc tính + getter/setter), không có SQL, không có giao diện.
- **`dal`** (*Data Access Layer*): nơi DUY NHẤT làm việc với SQLite. Activity muốn đọc/ghi dữ liệu đều phải gọi qua `DBHelper`.
- **`activities`**: các màn hình, nhận thao tác người dùng → gọi `DBHelper` → hiển thị kết quả.

→ Chia 3 tầng như vậy giúp **3 người làm song song** ít bị đụng code của nhau.

### 3.2. Giao diện (`app/src/main/res/`)

| File | Loại | Dùng ở đâu | Nội dung |
|---|---|---|---|
| `layout/activity_login.xml` | Màn hình | LoginActivity | Tiêu đề, ô username, ô password, nút Đăng nhập, gợi ý tài khoản mẫu |
| `layout/activity_main.xml` | Màn hình | MainActivity | Lời chào, vai trò, 3 nút: Trái cây / Đơn hàng / Đăng xuất |
| `layout/activity_fruit.xml` | Màn hình | FruitActivity | Tiêu đề, ô tìm kiếm, nút "+ Thêm trái cây" (chỉ admin), danh sách |
| `layout/item_fruit.xml` | 1 dòng | FruitActivity | Tên + giá (cùng hàng), loại + số lượng, HSD + trạng thái hạn, mô tả |
| `layout/dialog_fruit.xml` | Hộp thoại | FruitActivity (admin) | Form 6 trường: tên, loại, giá, số lượng, HSD, mô tả |
| `layout/dialog_order.xml` | Hộp thoại | FruitActivity (khách) | Thông tin trái cây (mô tả, giá, còn lại, HSD), ô số lượng |
| `layout/activity_order.xml` | Màn hình | OrderActivity | Tiêu đề, danh sách đơn |
| `layout/item_order.xml` | 1 dòng | OrderActivity | Mã đơn + trạng thái (có màu), ID khách hàng, ngày đặt + tổng tiền |
| `drawable/bg_edittext.xml` | Nền | Các ô nhập | Trắng, viền xám, bo góc 8dp |
| `drawable/bg_card.xml` | Nền | Khung danh sách | Trắng, viền xám, bo góc 12dp |
| `values/colors.xml` | Màu | Toàn app | Xanh lá (chính), đỏ (hết hạn), cam (Pending), xanh dương (Confirmed) |
| `values/themes.xml` | Theme | Toàn app | Material Light, không ActionBar |
| `AndroidManifest.xml` | Khai báo | Hệ thống | 4 Activity, `MainActivity` là màn hình mở đầu |

**Quy ước màu** (giúp người dùng nhìn là hiểu):

| Màu | Ý nghĩa |
|---|---|
| 🟢 Xanh lá | Màu chính, giá tiền, "Còn hạn", trạng thái `Completed` |
| 🔴 Đỏ | "ĐÃ HẾT HẠN" |
| 🟠 Cam | Đơn `Pending` (đang chờ) |
| 🔵 Xanh dương | Đơn `Confirmed` (đã xác nhận) |

---

## 4. Cơ sở dữ liệu

### 4.1. Lược đồ

```
┌────────────── Account ──────────────┐
│ id (PK) │ username (UNIQUE) │ password │ role │
└─────▲───────────────────────────────┘
      │ accountId (FK)
┌─────┴─────────── Customer ────────────────┐
│ id (PK) │ fullname │ phone │ email │ accountId │
└─────▲─────────────────────────────────────┘
      │ customerId (FK)
┌─────┴─────────── "Order" ──────────────────────────┐
│ id (PK) │ customerId │ orderDate │ totalAmount │ status │
└────────────────────────────────────────────────────┘

┌──────────────────────── Fruit ─────────────────────────────────────┐
│ id (PK) │ name │ category │ price │ quantity │ expiryDate │ description │
└────────────────────────────────────────────────────────────────────┘
```

| Bảng | Cột | Kiểu SQLite | Ghi chú |
|---|---|---|---|
| Account | id | INTEGER PK AUTOINCREMENT | |
| | username | TEXT NOT NULL UNIQUE | Không trùng tên đăng nhập |
| | password | TEXT NOT NULL | |
| | role | TEXT NOT NULL | `ADMIN` hoặc `CUSTOMER` |
| Fruit | id | INTEGER PK AUTOINCREMENT | |
| | name, category | TEXT NOT NULL | |
| | price | REAL NOT NULL | Giá (đồng) |
| | quantity | INTEGER NOT NULL | Số lượng tồn kho |
| | expiryDate | TEXT NOT NULL | Dạng `yyyy-MM-dd` |
| | description | TEXT | Có thể trống |
| Customer | id | INTEGER PK AUTOINCREMENT | |
| | fullname | TEXT NOT NULL | |
| | phone, email | TEXT | |
| | accountId | INTEGER FK → Account.id | Biết khách nào đang đăng nhập |
| Order | id | INTEGER PK AUTOINCREMENT | |
| | customerId | INTEGER FK → Customer.id | Đơn của khách nào |
| | orderDate | TEXT NOT NULL | Dạng `yyyy-MM-dd HH:mm` |
| | totalAmount | REAL NOT NULL | Tổng tiền |
| | status | TEXT NOT NULL | `Pending` / `Confirmed` / `Completed` |

### 4.2. Ba điểm kỹ thuật quan trọng

**① Bảng `Order` phải đặt trong dấu nháy kép.**
`ORDER` là từ khóa SQL (`ORDER BY`), viết `CREATE TABLE Order` sẽ lỗi cú pháp. Vì vậy:
```java
public static final String TABLE_ORDER = "\"Order\"";   // → "Order" trong câu SQL
```
Tên bảng vẫn đúng là `Order` như đề yêu cầu.

**② Ngày lưu dạng `yyyy-MM-dd` để so sánh như chuỗi.**
Với định dạng năm–tháng–ngày, thứ tự chữ cái trùng với thứ tự thời gian:
`"2026-10-21" < "2026-10-23"` → đúng. Nhờ vậy kiểm tra hết hạn chỉ cần `compareTo`, không phải đổi sang `Date`. Khi **hiển thị** mới đổi sang `dd/MM/yyyy` cho dễ đọc.

**③ Bảng `Order` không có cột trái cây.**
Đề chỉ cho `Order(id, customerId, orderDate, totalAmount, status)` nên project **giữ đúng**, không thêm bảng chi tiết đơn hàng. Mỗi lần đặt hàng là đặt 1 loại trái cây; đơn lưu lại **tổng tiền** = `giá × số lượng`, đồng thời **trừ số lượng tồn kho** của trái cây đó để số liệu `quantity` luôn đúng.

### 4.3. Chọn SQLite hay Room? — Nhóm chọn SQLite và ảnh hưởng đến project

Đề cho phép chọn **SQLite** hoặc **Room Database**. Cả hai đều lưu dữ liệu vào **cùng một loại file SQLite** trong máy; khác nhau ở **cách viết code** để đọc/ghi file đó:

- **SQLite (`SQLiteOpenHelper`)**: tự viết câu SQL, tự chuyển từng dòng kết quả (`Cursor`) thành object Java.
- **Room**: thư viện của Google nằm **bên trên** SQLite. Mình khai báo bằng annotation (`@Entity`, `@Dao`, `@Query`), Room **tự sinh code** chuyển đổi khi build.

#### So sánh nhanh

| Tiêu chí | SQLite (nhóm chọn) | Room |
|---|---|---|
| Thư viện thêm | Không cần, có sẵn trong Android | Thêm `room-runtime` + `room-compiler` vào Gradle |
| Số file lớp dữ liệu | **1 file** `DBHelper.java` | `AppDatabase` + 1 `@Dao` cho mỗi bảng (≈ 5 file) |
| Viết SQL | Tự viết toàn bộ (`CREATE TABLE`, `INSERT`, …) | Chỉ viết `@Query`, còn `INSERT/UPDATE/DELETE` dùng annotation |
| Chuyển dòng → object | Tự viết `cursorToFruit()`, `ContentValues` | Room tự làm |
| Phát hiện lỗi SQL (sai tên cột…) | Khi **chạy app** (crash lúc gọi tới) | Khi **build** (báo lỗi ngay, chưa cần chạy) |
| Truy vấn trên luồng giao diện | Được phép | **Bị cấm** mặc định → phải dùng luồng phụ (`Executor`) hoặc `allowMainThreadQueries()` |
| Transaction | Tự viết `beginTransaction / setTransactionSuccessful / endTransaction` | Thêm `@Transaction` lên hàm |
| Nâng cấp cấu trúc bảng | `onUpgrade()`: xóa bảng, tạo lại | Viết lớp `Migration` |
| Tự cập nhật giao diện khi dữ liệu đổi | Không, phải tự gọi `loadFruits()` | Có, nếu dùng `LiveData` / `Flow` |

#### Ảnh hưởng cụ thể của việc chọn SQLite lên project này

| # | Ảnh hưởng | Thể hiện ở đâu trong code |
|---|---|---|
| 1 | **Cấu trúc project khớp mẫu của đề**: mẫu dùng `dal/DBHelper.java` cho bản SQLite, nên toàn bộ dữ liệu gom vào 1 lớp duy nhất | `dal/DBHelper.java` |
| 2 | **Models là lớp Java thuần**, không có annotation → dễ đọc, dễ giải thích | `models/*.java` |
| 3 | **Phải tự viết code chuyển đổi** dữ liệu: mỗi bảng có hàm `cursorTo...()` và `...Values()` → code dài hơn Room khoảng 100 dòng | `cursorToFruit`, `fruitValues`, `orderValues`… |
| 4 | **Phải tự xử lý tên bảng `Order`** (từ khóa SQL) bằng hằng `TABLE_ORDER = "\"Order\""`. Room cũng phải đặt tên này trong dấu nháy khi viết `@Query` | `DBHelper.TABLE_ORDER` |
| 5 | **Gọi DB trực tiếp trên luồng giao diện**, ví dụ `loadFruits()` gọi thẳng `dbHelper.searchFruits(...)`. Dữ liệu nhỏ (vài chục dòng) nên không gây giật. Nếu dùng Room, mọi chỗ gọi DB trong 3 Activity phải bọc trong luồng phụ rồi `runOnUiThread` → phức tạp hơn nhiều | `FruitActivity`, `OrderActivity`, `MainActivity` |
| 6 | **Transaction đặt hàng viết tay**: tự kiểm soát tạo đơn + trừ kho trong 1 khối | `DBHelper.placeOrder()` |
| 7 | **Lỗi SQL chỉ lộ ra khi chạy** → nhóm phải chạy thử từng chức năng (bảng kiểm thử mục 8.2) thay vì dựa vào trình biên dịch | Mục 8.2 |
| 8 | **Danh sách không tự cập nhật** → sau mỗi thêm / sửa / xóa / đặt hàng / đổi trạng thái phải gọi lại `loadFruits()` / `loadOrders()` | `FruitActivity`, `OrderActivity` |
| 9 | **Build nhẹ, nhanh hơn**: không có thư viện ngoài, không có bước sinh code | `app/build.gradle` (chỉ 3 thư viện giao diện) |
| 10 | **Dễ chia việc và dễ vấn đáp**: mọi câu SQL nằm trong 1 file, nhìn vào là thấy; mỗi thành viên phụ trách 1 nhóm hàm trong `DBHelper` | Mục 7 |

**Kết luận:** project nhỏ (4 bảng, dữ liệu ít, làm có giới hạn thời gian), nên **SQLite** là lựa chọn hợp lý: không thêm thư viện, đúng cấu trúc mẫu, ít khái niệm mới, dễ giải thích từng dòng. Đổi lại nhóm phải viết thêm code chuyển đổi và tự kiểm thử kỹ. Nếu project lớn hơn (nhiều bảng, nhiều màn hình cần tự cập nhật) thì **Room** sẽ đáng dùng hơn.

#### Nếu chuyển sang Room thì project thay đổi thế nào?

| Hiện tại (SQLite) | Khi dùng Room |
|---|---|
| `models/Fruit.java` (lớp thường) | Thêm `@Entity(tableName = "Fruit")`, `@PrimaryKey(autoGenerate = true)` trên `id` |
| `dal/DBHelper.java` | Thay bằng `dal/AppDatabase.java` (`extends RoomDatabase`) + `AccountDao`, `FruitDao`, `CustomerDao`, `OrderDao` |
| `searchFruits(keyword, onlyAvailable)` | `@Query("SELECT * FROM Fruit WHERE name LIKE :kw OR category LIKE :kw") List<Fruit> search(String kw);` |
| `addFruit / updateFruit / deleteFruit` | `@Insert`, `@Update`, `@Delete` (không viết SQL) |
| `placeOrder()` với `beginTransaction` | Hàm có `@Transaction` trong `OrderDao` |
| `onCreate` + `insertSampleData` | `RoomDatabase.Callback.onCreate()` |
| Gọi DB trực tiếp trong Activity | Bọc trong `Executors.newSingleThreadExecutor()` rồi cập nhật giao diện bằng `runOnUiThread` |
| Không thêm thư viện | `implementation "androidx.room:room-runtime:2.6.1"` + `annotationProcessor "androidx.room:room-compiler:2.6.1"` |

Các phần **không đổi**: toàn bộ layout XML, SharedPreferences, luồng đăng nhập, phân quyền, kiểm tra hạn sử dụng, quy trình GitHub.

---

## 5. Luồng hoạt động

### 5.1. Sơ đồ tổng quát

```
                         ┌──────────────┐
          Bấm icon app → │ MainActivity │
                         └──────┬───────┘
              SharedPreferences: isLoggedIn ?
                ┌───────────────┴────────────────┐
              false                             true
                │                                 │
                ▼                                 ▼
        ┌───────────────┐               Đọc Account theo accountId
        │ LoginActivity │                         │
        └───────┬───────┘                  role = ?
   Đúng user/pass → ghi SharedPreferences  ┌──────┴──────┐
                │                        ADMIN        CUSTOMER
                └──► MainActivity ◄────    │              │
                                           ▼              ▼
                              ┌──────────────────┐ ┌──────────────────────┐
                              │ Quản lý trái cây │ │ Xem & đặt trái cây   │──► FruitActivity
                              │ Quản lý đơn hàng │ │ Lịch sử đơn hàng     │──► OrderActivity
                              │ Đăng xuất        │ │ Đăng xuất            │
                              └──────────────────┘ └──────────────────────┘
```

Cùng một màn hình `FruitActivity` / `OrderActivity` nhưng **hiển thị và cho phép thao tác khác nhau** tùy `role` đọc từ SharedPreferences.

### 5.2. Luồng đăng nhập

```
1. Người dùng nhập username + password
2. Bấm ĐĂNG NHẬP
3. Kiểm tra ô trống → báo lỗi đỏ ngay tại ô
4. DBHelper.login() → SELECT * FROM Account WHERE username=? AND password=?
     ├─ Không có dòng nào → Toast "Sai tên đăng nhập hoặc mật khẩu"
     └─ Có → ghi SharedPreferences:
              isLoggedIn = true, accountId, username, role
5. Mở MainActivity, đóng LoginActivity (finish) để bấm Back không quay lại Login
```

### 5.3. Luồng đăng xuất

```
MainActivity → bấm "Đăng xuất"
  → xóa toàn bộ thông tin đăng nhập trong SharedPreferences (clear)
  → mở LoginActivity
```

### 5.4. Luồng Admin quản lý trái cây

```
FruitActivity (admin)
 ├─ Mở màn hình → searchFruits("", false) → hiện TẤT CẢ trái cây (kể cả hết hàng, hết hạn)
 │                mỗi dòng: tên, giá, loại, số lượng, HSD, mô tả
 │
 ├─ Gõ vào ô tìm kiếm → mỗi ký tự gõ → searchFruits(keyword) → tìm theo TÊN hoặc LOẠI
 │
 ├─ Bấm "+ Thêm trái cây" → mở dialog_fruit (trống)
 │     → nhập 6 trường, bấm ô HSD để chọn ngày từ lịch
 │     → bấm Lưu → kiểm tra hợp lệ → addFruit() → tải lại danh sách
 │
 └─ Bấm vào 1 trái cây → mở dialog_fruit (điền sẵn dữ liệu)
       ├─ Lưu → kiểm tra hợp lệ → updateFruit() → tải lại
       ├─ Xóa → deleteFruit() → tải lại
       └─ Hủy → đóng
```

Kiểm tra hợp lệ khi Lưu: tên, loại không trống; giá > 0; có số lượng; đã chọn HSD. Nếu sai → báo lỗi ngay tại ô **và không đóng hộp thoại** (để không mất dữ liệu đã nhập).

### 5.5. Luồng quản lý hạn sử dụng

```
Mỗi lần vẽ 1 dòng trái cây:
   fruit.isExpired()  ⇔  expiryDate < ngày hôm nay
     ├─ false → "HSD: 30/10/2026 • Còn hạn"       (chữ xanh)
     └─ true  → "HSD: 21/10/2026 • ĐÃ HẾT HẠN"    (chữ đỏ)

Khách bấm vào trái cây:
     ├─ Hết hạn → hộp thoại "Không thể đặt hàng — ... đã hết hạn sử dụng"  ⛔ DỪNG
     └─ Còn hạn → mở hộp thoại đặt hàng
```

- Trái cây có HSD **đúng hôm nay** vẫn được bán (chỉ hết hạn khi đã **qua** ngày đó).

### 5.6. Luồng khách hàng đặt hàng

```
FruitActivity (khách)
 ├─ Mở màn hình → searchFruits("", true) → chỉ trái cây CÒN HÀNG (quantity > 0)
 ├─ Gõ tìm kiếm → lọc theo tên/loại
 └─ Bấm 1 trái cây CÒN HẠN → dialog_order:
        Mô tả, Giá, Còn lại, HSD
        [Số lượng muốn đặt]
        Bấm "Đặt hàng":
          ├─ SL ≤ 0              → lỗi "Số lượng phải lớn hơn 0"
          ├─ SL > tồn kho        → lỗi "Chỉ còn N"
          └─ Hợp lệ → DBHelper.placeOrder() trong 1 TRANSACTION:
                 ① INSERT "Order"(customerId, orderDate = bây giờ,
                                  totalAmount = giá × SL, status = 'Pending')
                 ② UPDATE Fruit SET quantity = quantity - SL
               → Toast "Đặt hàng thành công" → tải lại danh sách (số lượng đã giảm)
```

**Transaction** đảm bảo ① và ② **cùng thành công hoặc cùng bị hủy** — không bao giờ có chuyện tạo đơn mà kho không trừ (hoặc ngược lại).

### 5.7. Luồng đơn hàng

```
OrderActivity
 ├─ Khách hàng: getOrdersByCustomerId(id của mình) → chỉ thấy đơn của mình (lịch sử)
 │              mỗi dòng: Đơn #id — trạng thái (màu) — ID khách hàng — ngày đặt — tổng tiền
 │
 └─ Admin: getAllOrders() → thấy đơn của mọi khách, biết đơn của ai qua "ID khách hàng" (cột customerId)
           Bấm 1 đơn → hộp thoại chọn 1 trong 3:
               ○ Pending   ○ Confirmed   ○ Completed
           → updateOrderStatus() → tải lại danh sách
```

Vòng đời một đơn hàng:
```
 Khách đặt ──► Pending ──(admin xác nhận)──► Confirmed ──(đã giao)──► Completed
```

---

## 6. Giải thích chi tiết từng file

### 6.1. `models/` — 4 lớp dữ liệu

Mỗi lớp gồm: thuộc tính `private` **trùng tên cột**, constructor rỗng, constructor đầy đủ, getter/setter.

**`Account.java`**
```java
public static final String ROLE_ADMIN = "ADMIN";
public static final String ROLE_CUSTOMER = "CUSTOMER";
public boolean isAdmin() { return ROLE_ADMIN.equals(role); }
```
Dùng hằng thay vì gõ chuỗi `"ADMIN"` khắp nơi → tránh gõ sai chính tả.

**`Fruit.java`**
```java
public static final String DATE_FORMAT = "yyyy-MM-dd";

public boolean isExpired() {
    String today = new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date());
    return expiryDate.compareTo(today) < 0;
}
```
- Lấy ngày hôm nay dạng `yyyy-MM-dd`, so sánh chuỗi với `expiryDate`.
- `compareTo < 0` nghĩa là `expiryDate` đứng **trước** hôm nay → đã hết hạn.

**`Customer.java`** — chỉ có thuộc tính + getter/setter.

**`Order.java`**
```java
public static final String STATUS_PENDING = "Pending";
public static final String STATUS_CONFIRMED = "Confirmed";
public static final String STATUS_COMPLETED = "Completed";
public static final String[] STATUSES = {STATUS_PENDING, STATUS_CONFIRMED, STATUS_COMPLETED};
```
Mảng `STATUSES` dùng trực tiếp làm danh sách lựa chọn trong hộp thoại cập nhật trạng thái.

### 6.2. `dal/DBHelper.java` — toàn bộ phần dữ liệu

```java
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "fruit_shop.db";
    private static final int DB_VERSION = 1;
```
- `SQLiteOpenHelper` là lớp có sẵn của Android, tự lo việc **tạo / mở / nâng cấp** file DB.
- File DB nằm ở `/data/data/com.example.fruitshop/databases/fruit_shop.db`.

| Hàm | Khi nào chạy / làm gì |
|---|---|
| `onCreate(db)` | **Chỉ 1 lần** khi file DB chưa tồn tại: `CREATE TABLE` 4 bảng + `insertSampleData()` |
| `onUpgrade(db, old, new)` | Khi tăng `DB_VERSION`: `DROP TABLE` rồi tạo lại |
| `insertSampleData(db)` | Chèn 3 tài khoản, 2 khách hàng, 6 trái cây, 2 đơn hàng |
| `dateAfter(days)` | Trả về ngày `hôm nay + days` → dữ liệu mẫu luôn có hàng còn hạn và hết hạn |

**Các hàm public (Activity gọi):**

| Nhóm | Hàm | SQL | Người dùng |
|---|---|---|---|
| Account | `login(u, p)` | `SELECT * FROM Account WHERE username=? AND password=?` | LoginActivity |
| | `getAccountById(id)` | `SELECT * FROM Account WHERE id=?` | MainActivity |
| Customer | `getCustomerByAccountId(id)` | `SELECT * FROM Customer WHERE accountId=?` | Main/Fruit/OrderActivity |
| Fruit | `searchFruits(keyword, onlyAvailable)` | `SELECT * FROM Fruit WHERE (name LIKE ? OR category LIKE ?) [AND quantity > 0] ORDER BY name` | FruitActivity |
| | `addFruit(f)` | `INSERT INTO Fruit ...` → id mới, `-1` nếu lỗi | Admin |
| | `updateFruit(f)` | `UPDATE Fruit SET ... WHERE id=?` | Admin |
| | `deleteFruit(id)` | `DELETE FROM Fruit WHERE id=?` | Admin |
| Order | `placeOrder(order, fruit, qty)` | Transaction: `INSERT "Order"` + `UPDATE Fruit SET quantity = quantity - ?` | Khách |
| | `getAllOrders()` | `SELECT * FROM "Order" ORDER BY id DESC` | Admin |
| | `getOrdersByCustomerId(id)` | `SELECT * FROM "Order" WHERE customerId=? ORDER BY id DESC` | Khách |
| | `updateOrderStatus(id, status)` | `UPDATE "Order" SET status=? WHERE id=?` | Admin |

**Giải thích kỹ thuật:**
- **Tham số `?`**: giá trị truyền qua mảng `String[]`, không cộng chuỗi → **chống SQL Injection**.
- **`LIKE '%keyword%'`**: tìm chuỗi con. Keyword rỗng → `'%%'` khớp tất cả → dùng chung 1 hàm cho cả "xem tất cả" và "tìm kiếm".
- **`Cursor`**: con trỏ duyệt kết quả. `moveToFirst()` lấy 1 dòng, `while (c.moveToNext())` lấy nhiều dòng; dùng xong phải `c.close()`.
- **`ContentValues`**: cặp *tên cột → giá trị* cho `insert` / `update`. Hàm `fruitValues()` và `orderValues()` gom lại để không lặp code.
- **Transaction** trong `placeOrder`:
```java
db.beginTransaction();
try {
    if (db.insert(TABLE_ORDER, null, orderValues(order)) == -1) return false;
    db.execSQL("UPDATE Fruit SET quantity = quantity - ? WHERE id = ?", new Object[]{quantity, fruit.getId()});
    db.setTransactionSuccessful();   // đánh dấu "lưu"
    return true;
} finally {
    db.endTransaction();             // chưa gọi setTransactionSuccessful → tự hoàn tác
}
```

### 6.3. `activities/LoginActivity.java`

**Các key SharedPreferences** (khai báo `public static` để các màn khác dùng chung):

| Key | Kiểu | Ý nghĩa |
|---|---|---|
| `isLoggedIn` | boolean | Đang đăng nhập hay không |
| `accountId` | int | id tài khoản đang đăng nhập |
| `username` | String | Tên đăng nhập |
| `role` | String | `ADMIN` / `CUSTOMER` |

SharedPreferences thực chất là file XML: `/data/data/com.example.fruitshop/shared_prefs/login_prefs.xml`, ví dụ sau khi đăng nhập:
```xml
<map>
    <boolean name="isLoggedIn" value="true" />
    <int name="accountId" value="2" />
    <string name="username">kh01</string>
    <string name="role">CUSTOMER</string>
</map>
```

**Code chính:**
```java
prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);      // mở file, chỉ app này đọc được
...
SharedPreferences.Editor editor = prefs.edit();             // muốn ghi phải lấy Editor
editor.putBoolean(KEY_LOGGED_IN, true);
editor.putInt(KEY_ACCOUNT_ID, account.getId());
editor.putString(KEY_ROLE, account.getRole());
...
editor.apply();                                             // ghi nền, không làm đứng giao diện
```

### 6.4. `MainActivity.java`

- Là màn **LAUNCHER** (khai báo trong Manifest) → mở đầu tiên.
- Kiểm tra `isLoggedIn` **trước** `setContentView` → chưa đăng nhập thì chuyển ngay sang Login, không vẽ giao diện thừa.
- Đọc `Account` theo `accountId` đã lưu; nếu không còn trong DB (bị xóa dữ liệu) → tự đăng xuất.
- **Phân quyền giao diện**:

| | Admin | Customer |
|---|---|---|
| Lời chào | "Xin chào, admin" (username) | "Xin chào, *tên khách*" (từ bảng Customer) |
| Nút 1 → FruitActivity | "Quản lý trái cây" | "Xem & đặt trái cây" |
| Nút 2 → OrderActivity | "Quản lý đơn hàng" | "Lịch sử đơn hàng" |

### 6.5. `activities/FruitActivity.java`

| Phần | Hàm | Giải thích |
|---|---|---|
| Khởi tạo | `onCreate` | Đọc `role`, `accountId` từ SharedPreferences. Admin → hiện nút Thêm, bấm dòng = sửa. Khách → ẩn nút Thêm, bấm dòng = đặt hàng |
| Danh sách | `ArrayAdapter` + `bindFruitRow()` | Ghi đè `getView()` để vẽ `item_fruit.xml` (đủ 6 thông tin) và **tô màu HSD** (xanh/đỏ). `convertView` được tái sử dụng cho mượt |
| Tìm kiếm | `TextWatcher.afterTextChanged` | Gõ tới đâu lọc tới đó, gọi `loadFruits()` |
| Tải dữ liệu | `loadFruits()` | `searchFruits(keyword, !isAdmin)` → admin thấy tất cả, khách chỉ thấy còn hàng |
| Thêm/Sửa/Xóa | `showFruitDialog(fruit)` | `fruit == null` → thêm; khác null → sửa, có thêm nút **Xóa** (`deleteFruit`). Ô HSD `focusable=false` + `DatePickerDialog` để chọn ngày, không gõ sai định dạng |
| Giữ dialog khi lỗi | `dialog.getButton(BUTTON_POSITIVE).setOnClickListener(...)` | Mặc định AlertDialog luôn đóng khi bấm nút; gán lại listener sau `show()` để dữ liệu sai thì **không đóng** |
| Đặt hàng | `showOrderDialog(fruit)` | **Chặn hết hạn** trước tiên; kiểm tra `0 < SL ≤ tồn kho`; tổng tiền = giá × SL; gọi `placeOrder` |
| Định dạng | `formatMoney()`, `formatDate()` | `120000` → `120.000 đ`; `2026-10-30` → `30/10/2026`. Để `public static` cho OrderActivity dùng lại |

### 6.6. `activities/OrderActivity.java`

| Phần | Giải thích |
|---|---|
| `onCreate` | Admin: bấm đơn → đổi trạng thái. Khách: lấy `customerId` của mình |
| `loadOrders()` | Admin `getAllOrders()`, khách `getOrdersByCustomerId()` |
| `bindOrderRow()` | Vẽ `item_order.xml`: hiện đủ 5 cột của bảng Order — mã đơn (`id`), ID khách hàng (`customerId`), ngày đặt, tổng tiền, trạng thái (tô màu theo `statusColor()`) |
| `showStatusDialog()` | `setSingleChoiceItems(Order.STATUSES, ...)` — danh sách 3 lựa chọn, đánh dấu sẵn trạng thái hiện tại; chọn là cập nhật luôn |

### 6.7. `AndroidManifest.xml`
- Khai báo đủ 4 Activity (thiếu sẽ crash `ActivityNotFoundException`).
- `MainActivity` có `intent-filter MAIN + LAUNCHER` → icon app mở màn này.
- 3 màn còn lại `exported="false"` → app khác không mở thẳng được, buộc đi qua MainActivity kiểm tra đăng nhập.

---

## 7. Phân công công việc 3 thành viên

> Điền họ tên, MSSV vào bảng. Chia việc **theo chức năng** (mỗi người làm trọn 1 mảng từ giao diện → xử lý → SQL) để mỗi người làm trên **nhánh Git riêng**, ít bị xung đột code.

### 7.1. Tổng quan

| | Thành viên 1 (Nhóm trưởng) | Thành viên 2 | Thành viên 3 |
|---|---|---|---|
| **Họ tên – MSSV** | ……………… | ……………… | ……………… |
| **Mảng phụ trách** | Nền tảng: Database, Đăng nhập, Phân quyền, GitHub | Admin quản lý trái cây + Hạn sử dụng | Khách hàng đặt hàng + Quản lý đơn hàng |
| **Yêu cầu đề đáp ứng** | #1, #2, #3, #4, #9 | #5, #6 | #6 (chặn đặt), #7, #8 |
| **Nhánh Git** | `feature/core-login` | `feature/admin-fruit` | `feature/order` |
| **Tỉ lệ đóng góp** | 34% | 33% | 33% |

### 7.2. Thành viên 1 — Nền tảng: Database, Đăng nhập, Phân quyền, GitHub

**Công việc:**
1. Tạo project Android, cấu hình Gradle, theme, màu (`themes.xml`, `colors.xml`, `bg_edittext.xml`, `bg_card.xml`).
2. Tạo repo GitHub, `.gitignore`, mời 2 thành viên, đặt quy tắc nhánh.
3. Viết **4 model**: `Account`, `Fruit`, `Customer`, `Order` (đúng tên cột của đề).
4. Viết **`DBHelper`** phần khung: `onCreate` (tạo 4 bảng, xử lý tên bảng `"Order"`), `onUpgrade`, dữ liệu mẫu, các hàm `login`, `getAccountById`, `getCustomerByAccountId`.
5. **`LoginActivity`** + `activity_login.xml`: kiểm tra ô trống, đăng nhập, **lưu SharedPreferences**.
6. **`MainActivity`** + `activity_main.xml`: kiểm tra phiên đăng nhập, **phân quyền** giao diện theo role, đăng xuất.
7. `AndroidManifest.xml`; review và **merge Pull Request** của 2 bạn; ghép báo cáo.

**Files phụ trách:** `models/*.java`, `dal/DBHelper.java` (phần khung + Account/Customer), `LoginActivity.java`, `MainActivity.java`, `activity_login.xml`, `activity_main.xml`, `AndroidManifest.xml`, `values/*`, `drawable/*`, `.gitignore`.

**Khi vấn đáp trình bày:** mục 4 (CSDL), mục 5.1–5.3 (luồng đăng nhập/đăng xuất), SharedPreferences, cách phân quyền 2 role.

### 7.3. Thành viên 2 — Admin quản lý trái cây + Hạn sử dụng

**Công việc:**
1. Viết các hàm SQL trái cây trong `DBHelper`: `searchFruits`, `addFruit`, `updateFruit`, `deleteFruit`, `fruitValues`, `cursorToFruit`.
2. Viết hàm `Fruit.isExpired()` (so sánh ngày dạng `yyyy-MM-dd`).
3. **`FruitActivity`** phần dùng chung + phần admin:
   - Danh sách trái cây với `item_fruit.xml` (tên, giá, loại, số lượng, HSD).
   - **Đánh dấu hết hạn** màu đỏ.
   - Ô **tìm kiếm** theo tên/loại.
   - **Thêm / Sửa** bằng `dialog_fruit.xml`, chọn ngày bằng `DatePickerDialog`, kiểm tra dữ liệu hợp lệ.
   - **Xóa** trái cây.
   - Hàm định dạng `formatMoney`, `formatDate`.

**Files phụ trách:** `FruitActivity.java` (phần danh sách, tìm kiếm, `showFruitDialog`, định dạng), `activity_fruit.xml`, `item_fruit.xml`, `dialog_fruit.xml`, các hàm Fruit trong `DBHelper.java`, `Fruit.isExpired()`.

**Khi vấn đáp trình bày:** mục 5.4–5.5 (luồng admin + hạn sử dụng), cách tìm kiếm bằng `LIKE`, vì sao lưu ngày dạng `yyyy-MM-dd`, vì sao dialog không bị đóng khi nhập sai.

### 7.4. Thành viên 3 — Khách hàng đặt hàng + Quản lý đơn hàng

**Công việc:**
1. Viết các hàm SQL đơn hàng trong `DBHelper`: `placeOrder` (**transaction**: tạo đơn + trừ kho), `getAllOrders`, `getOrdersByCustomerId`, `updateOrderStatus`, `orderValues`.
2. **`FruitActivity`** phần khách hàng: chỉ hiện trái cây còn hàng, **chặn đặt trái cây hết hạn**, hộp thoại đặt hàng `dialog_order.xml` (kiểm tra số lượng ≤ tồn kho, tổng tiền = giá × số lượng).
3. **`OrderActivity`** + `activity_order.xml` + `item_order.xml`:
   - Khách: **lịch sử đơn hàng** của mình.
   - Admin: xem tất cả đơn, **cập nhật trạng thái** Pending / Confirmed / Completed, tô màu trạng thái.

**Files phụ trách:** `OrderActivity.java`, `FruitActivity.java` (phần `showOrderDialog`), `activity_order.xml`, `item_order.xml`, `dialog_order.xml`, các hàm Order trong `DBHelper.java`.

**Khi vấn đáp trình bày:** mục 5.5–5.7 (chặn đặt hàng hết hạn, luồng đặt hàng, vòng đời đơn hàng), transaction là gì, vì sao phải trừ kho.

### 7.5. Quy trình làm việc nhóm trên GitHub

```
                     main  ●───────●──────────●──────────●──────────●
                           │  (1) khung    ▲ PR#1     ▲ PR#2     ▲ PR#3
feature/core-login         └──●──●──●──────┘          │          │
feature/admin-fruit            └──────●──●──●──────────┘          │
feature/order                  └──────────●──●──●──●──────────────┘
```

| Bước | Ai | Việc |
|---|---|---|
| 1 | TV1 | Tạo project + 4 model + khung `DBHelper` → push lên `main` (để 2 bạn có nền mà code) |
| 2 | Cả 3 | `git clone`, mỗi người tạo nhánh riêng: `git checkout -b feature/...` |
| 3 | Cả 3 | Code phần của mình, commit nhỏ, thường xuyên, message rõ ràng |
| 4 | Mỗi người | `git push origin feature/...` → tạo **Pull Request** vào `main` |
| 5 | TV1 | Review, test thử, **merge** PR. Có xung đột (thường ở `DBHelper.java`, `FruitActivity.java`) thì người tạo PR sửa |
| 6 | Cả 3 | `git pull origin main` để cập nhật code mới nhất của nhau |

**Lệnh mẫu:**
```bash
# TV1 — khởi tạo repo
git init
git add .
git commit -m "Khởi tạo project: 4 model, DBHelper tạo bảng, dữ liệu mẫu"
git branch -M main
git remote add origin https://github.com/<tai-khoan>/FruitShop.git
git push -u origin main

# TV2 / TV3 — làm việc trên nhánh riêng
git clone https://github.com/<tai-khoan>/FruitShop.git
git checkout -b feature/admin-fruit
git add .
git commit -m "Admin: thêm/sửa/xóa/tìm kiếm trái cây, đánh dấu hết hạn"
git push origin feature/admin-fruit
# → lên GitHub bấm "Compare & pull request"
```

**Commit mẫu từng người:**

| TV1 | TV2 | TV3 |
|---|---|---|
| Khởi tạo project, theme, màu | DBHelper: hàm search/add/update/delete Fruit | DBHelper: placeOrder (transaction), query Order |
| Thêm 4 model theo đề | Fruit.isExpired() | Chặn đặt trái cây hết hạn |
| DBHelper: tạo 4 bảng + dữ liệu mẫu | FruitActivity: danh sách + tìm kiếm | Dialog đặt hàng + kiểm tra số lượng |
| LoginActivity + SharedPreferences | Dialog thêm/sửa trái cây + DatePicker | OrderActivity: lịch sử đơn (khách) |
| MainActivity: phân quyền + đăng xuất | Xóa trái cây | OrderActivity: cập nhật trạng thái (admin) |
| Merge PR, viết báo cáo | Hiển thị màu hết hạn | Màu trạng thái đơn |

---

## 8. Hướng dẫn chạy và kiểm thử

### 8.1. Chạy app
1. Android Studio → **File → Open** → chọn thư mục `FruitShop`.
2. Đợi Gradle sync xong → chọn máy ảo/điện thoại → bấm **Run ▶**.
3. Muốn **reset dữ liệu mẫu**: gỡ app hoặc *Settings → Apps → Fruit Shop → Clear data*.

### 8.2. Bảng kiểm thử

| # | Tài khoản | Thao tác | Kết quả mong đợi |
|---|---|---|---|
| 1 | — | Bỏ trống username, bấm Đăng nhập | Báo lỗi đỏ tại ô username |
| 2 | — | `admin` / `sai` | Toast "Sai tên đăng nhập hoặc mật khẩu" |
| 3 | admin | Đăng nhập, tắt app, mở lại | Vào thẳng trang chủ (SharedPreferences còn lưu) |
| 4 | admin | Quản lý trái cây | Thấy 6 loại đủ tên, giá, loại, SL, HSD, mô tả; Dâu tây + Chuối có dòng "ĐÃ HẾT HẠN" màu đỏ |
| 5 | admin | Gõ "nhập" vào ô tìm kiếm | Chỉ còn Táo Envy, Nho xanh |
| 6 | admin | + Thêm, bỏ trống tên, bấm Lưu | Lỗi tại ô tên, dialog **không đóng** |
| 7 | admin | + Thêm đủ thông tin | Trái cây mới xuất hiện trong danh sách |
| 8 | admin | Bấm 1 trái cây → đổi giá → Lưu | Giá mới hiển thị |
| 9 | admin | Bấm 1 trái cây → Xóa | Trái cây biến mất |
| 10 | kh01 | Xem & đặt trái cây → bấm Dâu tây | Hộp thoại "Không thể đặt hàng... đã hết hạn" |
| 11 | kh01 | Bấm Cam sành → nhập 100 | Lỗi "Chỉ còn 80" |
| 12 | kh01 | Bấm Cam sành → nhập 3 → Đặt hàng | "Đặt hàng thành công", số lượng Cam còn 77 |
| 13 | kh01 | Lịch sử đơn hàng | Đơn mới nhất ở đầu: 105.000 đ, trạng thái `Pending` màu cam |
| 14 | kh02 | Lịch sử đơn hàng | Chỉ thấy đơn của kh02 (ban đầu trống) |
| 15 | admin | Quản lý đơn hàng → bấm đơn → Confirmed | Trạng thái đổi màu xanh dương |
| 16 | kh01 | Lịch sử đơn hàng | Thấy trạng thái đơn đã được admin cập nhật |
| 17 | bất kỳ | Đăng xuất, tắt app, mở lại | Về màn đăng nhập (SharedPreferences đã xóa) |

---

## 9. Câu hỏi vấn đáp thường gặp

| Câu hỏi | Trả lời |
|---|---|
| SharedPreferences khác SQLite? | SharedPreferences lưu dữ liệu nhỏ dạng key–value (phiên đăng nhập, cài đặt). SQLite lưu dữ liệu có cấu trúc dạng bảng, truy vấn bằng SQL. |
| `apply()` khác `commit()`? | `apply()` ghi nền, không trả kết quả; `commit()` ghi ngay, trả về `boolean`, có thể làm chậm giao diện. |
| Phân quyền 2 role làm thế nào? | Cột `role` trong Account → lưu vào SharedPreferences khi đăng nhập → mỗi màn hình đọc `role` để ẩn/hiện nút và gọi hàm SQL khác nhau. |
| Tại sao tên bảng `Order` phải có dấu `"`? | `ORDER` là từ khóa SQL, không đặt trong nháy sẽ lỗi cú pháp. |
| Kiểm tra hết hạn thế nào? | Lưu ngày dạng `yyyy-MM-dd`, so sánh chuỗi với ngày hôm nay; nhỏ hơn hôm nay là hết hạn. |
| Chặn khách đặt hàng hết hạn ở đâu? | Đầu hàm `showOrderDialog()`: `if (fruit.isExpired())` → hiện thông báo rồi `return`, không mở form đặt. |
| Transaction là gì, dùng ở đâu? | Nhóm nhiều câu lệnh thành 1 khối "tất cả hoặc không có gì". Dùng trong `placeOrder` để tạo đơn và trừ kho luôn đi cùng nhau. |
| Tại sao dùng `?` trong SQL? | Truyền tham số an toàn, chống SQL Injection. |
| `onCreate` của DBHelper chạy khi nào? | Lần đầu gọi `getReadable/WritableDatabase()` mà file DB chưa tồn tại. |
| Bảng Order không có cột trái cây, sao biết đặt gì? | Đề chỉ cho các cột đó nên nhóm giữ đúng đề: đơn lưu tổng tiền, trạng thái, ngày, khách hàng; số lượng tồn kho được trừ ngay khi đặt. |
| Nếu dùng Room thay SQLite? | Mỗi model thêm `@Entity`; tạo interface `@Dao` với `@Query/@Insert/@Update/@Delete`; lớp `AppDatabase extends RoomDatabase` thay cho `DBHelper`. |
