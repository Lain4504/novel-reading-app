# Novel Reading App - Admin Panel

Admin panel web application để quản lý users và novels cho Novel Reading App.

## 🚀 Yêu cầu

- Node.js 18+ hoặc Node.js 20+
- npm hoặc yarn
- Backend API đã được chạy (mặc định: http://localhost:8080)

## 📦 Cài đặt

1. **Di chuyển vào thư mục admin-web:**
```bash
cd admin-web
```

2. **Cài đặt dependencies:**
```bash
npm install
```

3. **Cấu hình môi trường (nếu cần):**

File `.env.local` đã được tạo sẵn với cấu hình mặc định:
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

Nếu backend API của bạn chạy ở địa chỉ khác, hãy thay đổi giá trị này.

## ▶️ Chạy ứng dụng

### Development Mode

```bash
npm run dev
```

Ứng dụng sẽ chạy tại: **http://localhost:3000**

### Production Build

```bash
npm run build
npm start
```

## 🔐 Đăng nhập

### Yêu cầu
- **Chỉ tài khoản có role ADMIN mới có thể đăng nhập**
- Nếu bạn chưa có tài khoản Admin, hãy tạo user trong database với role `ADMIN`

### Cách đăng nhập
1. Truy cập: http://localhost:3000
2. Nhập username hoặc email
3. Nhập password
4. Click "Đăng nhập"

Nếu tài khoản không có role Admin, hệ thống sẽ từ chối đăng nhập với thông báo lỗi.

## 📚 Hướng dẫn sử dụng

### Dashboard
- Trang chủ hiển thị tổng quan hệ thống
- Số lượng users, novels
- Truy cập nhanh các chức năng chính

### Quản lý User

#### Tìm kiếm User
1. Vào menu "Quản lý User"
2. Nhập User ID vào ô tìm kiếm
3. Click "Tìm kiếm" hoặc nhấn Enter
4. Thông tin user sẽ hiển thị nếu tìm thấy

#### Tạo User mới
1. Click nút "Tạo User"
2. Điền thông tin:
   - **Username** (bắt buộc): Tên đăng nhập
   - **Email** (bắt buộc): Email của user
   - **Password** (bắt buộc): Mật khẩu
   - **Display Name**: Tên hiển thị
   - **Bio**: Mô tả về user
   - **Roles**: Chọn các quyền (USER, AUTHOR, MODERATOR, ADMIN)
   - **Status**: Trạng thái tài khoản (ACTIVE, INACTIVE, BANNED, SUSPENDED)
3. Click "Tạo"

#### Chỉnh sửa User
1. Tìm user cần chỉnh sửa
2. Click nút "Sửa"
3. Thay đổi thông tin cần thiết
4. Click "Cập nhật"

**Lưu ý:** Nếu không muốn đổi password, để trống trường Password

#### Xóa User
1. Tìm user cần xóa
2. Click nút "Xóa"
3. Xác nhận xóa trong dialog

### Quản lý Novel

#### Xem danh sách Novel
1. Vào menu "Quản lý Novel"
2. Danh sách novels hiển thị dạng bảng với:
   - Cover image
   - Tiêu đề
   - Tác giả
   - Thể loại
   - Trạng thái
   - Lượt xem, theo dõi
   - Số chương
3. Sử dụng nút "Trước" / "Sau" để phân trang

#### Chỉnh sửa Novel
1. Tìm novel cần chỉnh sửa trong danh sách
2. Click nút "Sửa" (icon bút)
3. Thay đổi thông tin:
   - **Tiêu đề**: Tên novel
   - **Mô tả**: Mô tả nội dung
   - **Tên tác giả**: Tên tác giả
   - **Thể loại**: Các thể loại cách nhau bởi dấu phẩy (VD: ACTION,ROMANCE,FANTASY)
   - **Số từ**: Tổng số từ
   - **Số chương**: Tổng số chương
   - **Trạng thái**: DRAFT, PUBLISHED, COMPLETED, HIATUS, DROPPED
   - **R18**: Check nếu novel có nội dung 18+
4. Click "Cập nhật"

#### Xóa Novel
1. Tìm novel cần xóa
2. Click nút "Xóa" (icon thùng rác)
3. Xác nhận xóa trong dialog

## 🎨 Tính năng

### Bảo mật
- ✅ JWT authentication
- ✅ Role-based access control (chỉ Admin)
- ✅ Auto logout khi token hết hạn
- ✅ Protected routes

### Quản lý User
- ✅ Tìm kiếm user theo ID
- ✅ Tạo user mới với đầy đủ thông tin
- ✅ Chỉnh sửa thông tin user
- ✅ Xóa user
- ✅ Quản lý roles và status

### Quản lý Novel
- ✅ Xem danh sách novels với phân trang
- ✅ Chỉnh sửa thông tin novel
- ✅ Xóa novel
- ✅ Hiển thị cover image
- ✅ Quản lý thể loại, trạng thái

### UI/UX
- ✅ Giao diện đẹp, hiện đại
- ✅ Responsive design
- ✅ Loading states
- ✅ Error handling
- ✅ Confirmation dialogs
- ✅ Toast notifications

## 📝 Các thể loại Novel có sẵn

- ACTION
- ADVENTURE
- COMEDY
- DRAMA
- FANTASY
- HORROR
- MYSTERY
- ROMANCE
- SCIFI
- SLICE_OF_LIFE
- SUPERNATURAL
- THRILLER
- TRAGEDY
- MARTIAL_ARTS
- XUANHUAN
- XIANXIA
- WUXIA
- HISTORICAL
- SCHOOL_LIFE
- PSYCHOLOGICAL

## 🔧 Troubleshooting

### Không đăng nhập được
- Kiểm tra backend API đã chạy chưa
- Kiểm tra user có role ADMIN không
- Kiểm tra username/email và password
- Kiểm tra CORS đã được config đúng ở backend

### Không load được danh sách novels
- Kiểm tra backend API đang hoạt động
- Kiểm tra token còn hạn không
- Kiểm tra Console log để xem lỗi chi tiết

### API errors
- Mở Developer Tools (F12)
- Vào tab Network để xem request/response
- Vào tab Console để xem error logs

## 🛠️ Tech Stack

- **Framework:** Next.js 14 (App Router)
- **Language:** TypeScript
- **HTTP Client:** Axios
- **Icons:** React Icons
- **Styling:** Custom CSS (CSS Variables)

## 📁 Cấu trúc thư mục

```
admin-web/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── dashboard/          # Dashboard pages
│   │   │   ├── layout.tsx      # Dashboard layout
│   │   │   ├── page.tsx        # Dashboard home
│   │   │   ├── users/          # User management
│   │   │   └── novels/         # Novel management
│   │   ├── login/              # Login page
│   │   ├── layout.tsx          # Root layout
│   │   ├── page.tsx            # Home redirect
│   │   └── globals.css         # Global styles
│   ├── components/             # Reusable components
│   │   └── Sidebar.tsx         # Navigation sidebar
│   ├── lib/                    # Utilities
│   │   ├── api.ts              # API client
│   │   └── auth.ts             # Auth helpers
│   └── types/                  # TypeScript types
│       └── index.ts            # Type definitions
├── public/                     # Static files
├── package.json                # Dependencies
├── tsconfig.json               # TypeScript config
├── next.config.mjs             # Next.js config
└── README.md                   # Documentation
```

## 🔗 API Endpoints được sử dụng

### Auth
- `POST /users/login` - Đăng nhập

### Users
- `GET /users/{id}` - Lấy thông tin user
- `POST /users` - Tạo user mới
- `PUT /users/{id}` - Cập nhật user
- `DELETE /users/{id}` - Xóa user

### Novels
- `GET /novels?page={page}&size={size}` - Lấy danh sách novels
- `GET /novels/{id}` - Lấy thông tin novel
- `PUT /novels/{id}` - Cập nhật novel
- `DELETE /novels/{id}` - Xóa novel

## 📞 Liên hệ & Hỗ trợ

Nếu có vấn đề hoặc câu hỏi, vui lòng tạo issue trên repository.

---

**Chúc bạn sử dụng thành công! 🎉**

