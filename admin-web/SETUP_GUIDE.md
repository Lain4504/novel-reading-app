# Hướng dẫn Setup và Sử dụng Admin Panel

## 📋 Checklist trước khi bắt đầu

- [ ] Backend API đã được cài đặt và chạy ở `http://localhost:8080`
- [ ] MongoDB đã được cài đặt và chạy
- [ ] Node.js 18+ đã được cài đặt
- [ ] Đã có tài khoản Admin trong database

## 🚀 Hướng dẫn Setup từng bước

### Bước 1: Kiểm tra Backend

Đảm bảo backend đang chạy:

```bash
# Di chuyển đến thư mục backend
cd backend

# Chạy backend (nếu chưa chạy)
./gradlew bootRun
# hoặc
./run-api.sh
```

Backend phải chạy tại: `http://localhost:8080` (API endpoint: `http://localhost:8080/api`)

### Bước 2: Tạo tài khoản Admin

Nếu chưa có tài khoản Admin, bạn có 2 cách:

#### Cách 1: Sử dụng API để tạo user rồi update role

```bash
# Tạo user mới
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "Admin@123"
  }'
```

Sau đó vào MongoDB và update role:

```javascript
// Kết nối MongoDB
use your_database_name

// Update user thành admin
db.users.updateOne(
  { username: "admin" },
  { $set: { roles: ["ADMIN"] } }
)
```

#### Cách 2: Tạo trực tiếp trong MongoDB

```javascript
// Kết nối MongoDB
use your_database_name

// Insert user admin (password đã hash = Admin@123)
db.users.insertOne({
  username: "admin",
  email: "admin@example.com",
  password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi",
  roles: ["ADMIN"],
  status: "ACTIVE",
  createdAt: new Date(),
  updatedAt: new Date()
})
```

### Bước 3: Cài đặt Admin Panel

```bash
# Di chuyển đến thư mục admin-web
cd admin-web

# Cài đặt dependencies
npm install
```

### Bước 4: Chạy Admin Panel

```bash
# Development mode
npm run dev
```

Hoặc production mode:

```bash
# Build
npm run build

# Start
npm start
```

### Bước 5: Đăng nhập

1. Mở trình duyệt và truy cập: `http://localhost:3000`
2. Đăng nhập với tài khoản admin vừa tạo:
   - Username: `admin`
   - Password: `Admin@123` (hoặc password bạn đã đặt)

## 🎯 Hướng dẫn sử dụng chi tiết

### Quản lý Users

#### Tìm User theo ID

1. Click "Quản lý User" trên sidebar
2. Nhập User ID vào ô tìm kiếm (lấy từ MongoDB hoặc app)
3. Click "Tìm kiếm"

**Lưu ý:** Backend hiện tại chỉ hỗ trợ tìm kiếm theo ID chính xác.

#### Tạo User mới

1. Click nút "Tạo User"
2. Điền đầy đủ thông tin:

**Required fields:**
- Username: Tên đăng nhập duy nhất (3-50 ký tự)
- Email: Email hợp lệ và duy nhất
- Password: Mật khẩu phải có:
  - Ít nhất 6 ký tự
  - Ít nhất 1 chữ thường
  - Ít nhất 1 chữ hoa
  - Ít nhất 1 ký tự đặc biệt

**Optional fields:**
- Display Name: Tên hiển thị
- Bio: Mô tả ngắn về user
- Roles: Chọn quyền (có thể chọn nhiều):
  - USER: Người dùng thông thường
  - AUTHOR: Tác giả (có thể tạo novel)
  - MODERATOR: Người kiểm duyệt
  - ADMIN: Quản trị viên
- Status: Trạng thái tài khoản
  - ACTIVE: Hoạt động bình thường
  - INACTIVE: Không hoạt động
  - BANNED: Bị cấm vĩnh viễn
  - SUSPENDED: Bị đình chỉ tạm thời

3. Click "Tạo"

#### Sửa User

1. Tìm user cần sửa
2. Click nút "Sửa"
3. Chỉnh sửa thông tin
4. **Lưu ý về Password:**
   - Để trống nếu KHÔNG muốn đổi password
   - Nhập password mới nếu muốn đổi
5. Click "Cập nhật"

#### Xóa User

1. Tìm user cần xóa
2. Click nút "Xóa"
3. Xác nhận trong dialog popup
4. User sẽ bị xóa vĩnh viễn khỏi database

**⚠️ CẢNH BÁO:** Không thể hoàn tác sau khi xóa!

### Quản lý Novels

#### Xem danh sách

- Click "Quản lý Novel" trên sidebar
- Danh sách hiển thị 10 novels/trang
- Thông tin hiển thị:
  - Cover image (nếu có)
  - Tiêu đề + badge R18 (nếu có)
  - Tên tác giả
  - Thể loại (hiển thị 2 đầu + số còn lại)
  - Trạng thái
  - Lượt xem
  - Lượt theo dõi
  - Số chương

#### Phân trang

- Sử dụng nút "Trước" / "Sau" ở cuối bảng
- Hiển thị số trang hiện tại / tổng số trang

#### Sửa Novel

1. Tìm novel trong danh sách
2. Click nút "Sửa" (icon bút)
3. Chỉnh sửa thông tin:

**Các trường có thể sửa:**
- **Tiêu đề:** Tên của novel
- **Mô tả:** Mô tả chi tiết nội dung
- **Tên tác giả:** Tên hiển thị của tác giả
- **Thể loại:** Danh sách thể loại, cách nhau bởi dấu phẩy
  - VD: `ACTION,ROMANCE,FANTASY`
  - Viết hoa tất cả
  - Tham khảo danh sách đầy đủ trong README
- **Số từ:** Tổng số từ trong novel
- **Số chương:** Tổng số chương
- **Trạng thái:**
  - DRAFT: Bản nháp
  - PUBLISHED: Đã xuất bản
  - COMPLETED: Đã hoàn thành
  - HIATUS: Tạm ngưng
  - DROPPED: Đã bỏ dở
- **R18:** Check nếu novel có nội dung 18+

4. Click "Cập nhật"

#### Xóa Novel

1. Tìm novel cần xóa
2. Click nút "Xóa" (icon thùng rác)
3. Xác nhận trong dialog popup
4. Novel và toàn bộ dữ liệu liên quan sẽ bị xóa

**⚠️ CẢNH BÁO:** Không thể hoàn tác! Chapters cũng có thể bị ảnh hưởng.

## 🔍 Tips & Tricks

### Lấy User ID

Có nhiều cách để lấy User ID:

1. **Từ MongoDB:**
```javascript
db.users.find({}, {_id: 1, username: 1, email: 1})
```

2. **Từ Backend logs:** Khi user đăng nhập, userId có trong response

3. **Từ App:** User ID được lưu trong JWT token

### Debug Issues

#### Không đăng nhập được

1. Kiểm tra Console (F12) để xem error
2. Kiểm tra Network tab để xem API response
3. Kiểm tra:
   - Backend có chạy không?
   - User có role ADMIN không?
   - Password đúng không?

#### API errors

```bash
# Kiểm tra CORS config trong backend
# File: backend/src/main/kotlin/.../controller/UserController.kt
@CrossOrigin(origins = ["http://localhost:3000", ...])
```

Đảm bảo `http://localhost:3000` có trong CORS origins.

#### Lỗi "Cannot read properties of null"

- Xóa localStorage: `localStorage.clear()`
- Logout và login lại
- Clear browser cache

## 📊 Database Schema

### Users Collection
```javascript
{
  _id: ObjectId,
  username: String,
  email: String,
  password: String (hashed),
  roles: [String],  // ["USER", "AUTHOR", "ADMIN", ...]
  status: String,   // "ACTIVE", "BANNED", ...
  displayName: String,
  bio: String,
  avatarUrl: String,
  backgroundUrl: String,
  authorName: String,
  createdAt: Date,
  updatedAt: Date
}
```

### Novels Collection
```javascript
{
  _id: ObjectId,
  title: String,
  description: String,
  authorName: String,
  authorId: String,
  categories: [String],
  coverImage: String,
  rating: Number,
  viewCount: Number,
  followCount: Number,
  wordCount: Number,
  chapterCount: Number,
  status: String,
  isR18: Boolean,
  createdAt: Date,
  updatedAt: Date
}
```

## 🎓 Best Practices

1. **Backup database** trước khi xóa user hoặc novel
2. **Sử dụng status** thay vì xóa user (đặt status = BANNED hoặc INACTIVE)
3. **Kiểm tra kỹ** trước khi xóa vì không thể hoàn tác
4. **Logout** sau khi làm việc xong để bảo mật
5. **Không share** tài khoản admin

## 🆘 Troubleshooting

### Port 3000 đã được sử dụng

```bash
# Chạy trên port khác
npm run dev -- -p 3001
```

### Node modules lỗi

```bash
# Xóa và cài lại
rm -rf node_modules package-lock.json
npm install
```

### Build lỗi

```bash
# Clear Next.js cache
rm -rf .next
npm run build
```

## 📞 Hỗ trợ

Nếu gặp vấn đề:
1. Kiểm tra logs trong Console (F12)
2. Kiểm tra Network tab để xem API requests
3. Kiểm tra backend logs
4. Đọc kỹ error message

---

**Chúc bạn sử dụng thành công! 🎉**

