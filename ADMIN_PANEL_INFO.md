# 📱 ADMIN PANEL - Novel Reading App

## 🎯 Tổng quan

Admin Panel là một ứng dụng web được xây dựng bằng **Next.js 14** để quản lý users và novels cho Novel Reading App. Ứng dụng này chỉ dành cho **Admin** và không có trong app mobile.

## 📂 Vị trí

```
novel-reading-app/
├── admin-web/          <-- ADMIN PANEL MỚI TẠO
├── app/                (Android app)
├── backend/            (Spring Boot API)
└── ...
```

## ✨ Tính năng

### 🔐 Xác thực & Bảo mật
- ✅ Login với JWT authentication
- ✅ Chỉ user có role **ADMIN** mới được truy cập
- ✅ Auto logout khi token hết hạn
- ✅ Protected routes cho mọi trang

### 👥 Quản lý User
- ✅ Tìm kiếm user theo ID
- ✅ Tạo user mới với đầy đủ thông tin
- ✅ Chỉnh sửa thông tin user (username, email, password, roles, status, etc.)
- ✅ Xóa user
- ✅ Quản lý roles: USER, AUTHOR, MODERATOR, ADMIN
- ✅ Quản lý status: ACTIVE, INACTIVE, BANNED, SUSPENDED

### 📚 Quản lý Novel
- ✅ Xem danh sách novels với phân trang (10 novels/trang)
- ✅ Hiển thị cover image, thông tin chi tiết
- ✅ Chỉnh sửa thông tin novel (title, description, categories, status, etc.)
- ✅ Xóa novel
- ✅ Filter theo thể loại, trạng thái
- ✅ Hiển thị số liệu: views, follows, chapters

## 🛠️ Tech Stack

- **Framework:** Next.js 14 (App Router)
- **Language:** TypeScript
- **UI:** Custom CSS với CSS Variables
- **HTTP Client:** Axios
- **Icons:** React Icons
- **Authentication:** JWT tokens

## 🚀 Cách chạy

### 1. Cài đặt dependencies

```bash
cd admin-web
npm install
```

### 2. Chạy ứng dụng

**Development:**
```bash
npm run dev
```

**Production:**
```bash
npm run build
npm start
```

### 3. Truy cập

Mở trình duyệt và vào: **http://localhost:3000**

## 🔑 Đăng nhập

### Yêu cầu
- Tài khoản phải có role **ADMIN**
- Backend API phải đang chạy tại `http://localhost:8080/api`

### Tạo tài khoản Admin

Nếu chưa có tài khoản Admin, vào MongoDB và chạy:

```javascript
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

**Login với:**
- Username: `admin`
- Password: `Admin@123`

## 📖 Cấu trúc dự án

```
admin-web/
├── src/
│   ├── app/
│   │   ├── dashboard/
│   │   │   ├── layout.tsx        # Layout cho dashboard
│   │   │   ├── page.tsx          # Trang chủ dashboard
│   │   │   ├── users/
│   │   │   │   └── page.tsx      # Quản lý users
│   │   │   └── novels/
│   │   │       └── page.tsx      # Quản lý novels
│   │   ├── login/
│   │   │   └── page.tsx          # Trang login
│   │   ├── layout.tsx            # Root layout
│   │   ├── page.tsx              # Home (redirect)
│   │   └── globals.css           # Global styles
│   ├── components/
│   │   └── Sidebar.tsx           # Navigation sidebar
│   ├── lib/
│   │   ├── api.ts                # API client với Axios
│   │   └── auth.ts               # Authentication utilities
│   └── types/
│       └── index.ts              # TypeScript type definitions
├── public/                        # Static assets
├── package.json                   # Dependencies
├── tsconfig.json                  # TypeScript config
├── next.config.mjs                # Next.js config
├── README.md                      # Documentation đầy đủ
├── SETUP_GUIDE.md                 # Hướng dẫn setup chi tiết
└── QUICKSTART.md                  # Quick start guide
```

## 🔌 API Endpoints

Admin Panel kết nối với các API sau:

### Authentication
- `POST /users/login` - Đăng nhập

### Users
- `GET /users/{id}` - Lấy user theo ID
- `POST /users` - Tạo user mới
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Xóa user

### Novels
- `GET /novels?page={page}&size={size}` - Lấy danh sách novels
- `GET /novels/{id}` - Lấy novel theo ID
- `PUT /novels/{id}` - Update novel
- `DELETE /novels/{id}` - Xóa novel

## 🎨 UI/UX Features

- ✅ **Responsive Design** - Hoạt động tốt trên mọi kích thước màn hình
- ✅ **Modern UI** - Giao diện đẹp mắt với gradient và shadows
- ✅ **Loading States** - Hiển thị spinner khi đang load
- ✅ **Error Handling** - Thông báo lỗi rõ ràng
- ✅ **Confirmation Dialogs** - Xác nhận trước khi xóa
- ✅ **Form Validation** - Validate input trước khi submit
- ✅ **Toast Notifications** - Thông báo kết quả thành công/thất bại

## 📝 Hướng dẫn sử dụng

### Dashboard
- Hiển thị tổng quan: số lượng users, novels
- Quick access đến các chức năng chính
- Thông tin user đang đăng nhập

### Quản lý Users

**Tìm kiếm:**
1. Nhập User ID
2. Click "Tìm kiếm"
3. Xem thông tin user

**Tạo mới:**
1. Click "Tạo User"
2. Điền form (username, email, password, roles, status)
3. Click "Tạo"

**Sửa:**
1. Tìm user
2. Click "Sửa"
3. Chỉnh sửa thông tin
4. Click "Cập nhật"

**Xóa:**
1. Tìm user
2. Click "Xóa"
3. Xác nhận

### Quản lý Novels

**Xem danh sách:**
- Hiển thị 10 novels/trang
- Pagination với nút "Trước"/"Sau"
- Hiển thị: cover, title, author, categories, status, stats

**Sửa:**
1. Tìm novel trong danh sách
2. Click icon "Sửa"
3. Chỉnh sửa thông tin
4. Click "Cập nhật"

**Xóa:**
1. Tìm novel
2. Click icon "Xóa"
3. Xác nhận

## ⚙️ Configuration

File `.env.local` (đã tạo sẵn):
```
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Nếu backend chạy ở địa chỉ khác, thay đổi giá trị này.

## 🐛 Troubleshooting

### Không đăng nhập được
- ✅ Kiểm tra backend đang chạy
- ✅ Kiểm tra user có role ADMIN
- ✅ Kiểm tra CORS đã config đúng

### Không load được data
- ✅ Kiểm tra Network tab (F12)
- ✅ Kiểm tra Console logs
- ✅ Kiểm tra token còn hạn

### Build errors
```bash
rm -rf .next node_modules
npm install
npm run build
```

## 📚 Documentation

- **README.md** - Documentation chính, đầy đủ nhất
- **SETUP_GUIDE.md** - Hướng dẫn setup từng bước chi tiết
- **QUICKSTART.md** - Quick start trong 3 bước

## 🔒 Security

- ✅ JWT authentication
- ✅ Role-based access control
- ✅ Password hashing (BCrypt)
- ✅ Auto logout on token expiry
- ✅ CORS protection

## 🚀 Deployment

### Development
```bash
npm run dev
```

### Production
```bash
npm run build
npm start
```

### Docker (optional)
```dockerfile
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
CMD ["npm", "start"]
```

## 📊 Features Summary

| Feature | Status | Description |
|---------|--------|-------------|
| Login | ✅ | JWT authentication với admin check |
| Dashboard | ✅ | Tổng quan hệ thống |
| User CRUD | ✅ | Create, Read, Update, Delete users |
| Novel CRUD | ✅ | Read, Update, Delete novels |
| Role Management | ✅ | Quản lý roles: USER, AUTHOR, MODERATOR, ADMIN |
| Status Management | ✅ | Quản lý status: ACTIVE, INACTIVE, BANNED, SUSPENDED |
| Pagination | ✅ | Phân trang cho novels |
| Search | ✅ | Tìm kiếm user theo ID |
| Responsive | ✅ | Mobile-friendly UI |
| Error Handling | ✅ | Xử lý lỗi và hiển thị thông báo |

## 💡 Tips

1. **Backup database** trước khi xóa data
2. Sử dụng **status** thay vì xóa user
3. **Logout** sau khi làm xong
4. Kiểm tra **Console logs** khi có lỗi
5. Đọc **error messages** cẩn thận

## 🎓 Next Steps

1. ✅ Setup và chạy admin panel
2. ✅ Tạo tài khoản admin
3. ✅ Login và test các chức năng
4. 📝 Tùy chỉnh UI nếu cần
5. 🚀 Deploy lên server (optional)

## 📞 Support

Nếu gặp vấn đề:
1. Đọc error message trong Console
2. Kiểm tra Network tab
3. Đọc SETUP_GUIDE.md
4. Kiểm tra backend logs

---

**Chúc bạn sử dụng thành công Admin Panel! 🎉**

Project được tạo với ❤️ bằng Next.js 14 + TypeScript

