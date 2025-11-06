# Firebase Cloud Messaging (FCM) Setup Guide

## Tổng quan
App đã được chuyển từ client-side polling sang server-side push notification sử dụng Firebase Cloud Messaging (FCM).

## Backend Setup

### 1. Tạo Firebase Project
1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Tạo project mới hoặc chọn project hiện có
3. Thêm Android app vào project
4. Tải file `google-services.json` và đặt vào `mobileapp/app/`

### 2. Tạo Service Account
1. Vào Firebase Console > Project Settings > Service Accounts
2. Click "Generate new private key"
3. Lưu file JSON vào server (ví dụ: `firebase-service-account.json`)
4. Set environment variable:
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS="/path/to/firebase-service-account.json"
   ```
   Hoặc đặt file vào thư mục root của backend project

### 3. Backend Dependencies
Đã được thêm vào `backend/build.gradle.kts`:
```kotlin
implementation("com.google.firebase:firebase-admin:9.2.0")
```

### 4. Cấu hình Backend
- `FcmService`: Service để gửi push notifications
- `DeviceTokenService`: Service để quản lý FCM tokens
- `DeviceTokenController`: API endpoints để lưu/xóa tokens
- `ChapterService`: Tự động gửi push khi có chapter mới

### 5. API Endpoints
- `POST /api/device-tokens` - Lưu FCM token
- `GET /api/device-tokens` - Lấy danh sách tokens của user
- `DELETE /api/device-tokens/{token}` - Xóa token

## Mobile App Setup

### 1. Thêm google-services.json
1. Tải file `google-services.json` từ Firebase Console
2. Đặt vào `mobileapp/app/`

### 2. Dependencies
Đã được thêm vào `mobileapp/app/build.gradle.kts`:
```kotlin
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

### 3. Cấu hình
- `NovelFirebaseMessagingService`: Service để nhận push notifications
- `FcmService`: Service để quản lý FCM token và gửi lên server
- `MainActivity`: Tự động gửi token lên server khi user đăng nhập

## Luồng hoạt động

### Khi có chapter mới:
1. Author tạo chapter mới
2. `ChapterService.createChapter()` được gọi
3. Backend lấy danh sách users đang follow novel và có `notify = true`
4. Backend lấy FCM tokens của những users đó
5. Backend gửi push notification qua FCM
6. Mobile app nhận push và hiển thị notification

### Khi user đăng nhập:
1. User đăng nhập thành công
2. `MainActivity` lấy FCM token
3. Token được gửi lên server qua API `/api/device-tokens`
4. Server lưu token vào database

### Khi user bật/tắt notification:
1. User thay đổi setting trong ProfileScreen
2. Nếu bật: Token sẽ được gửi lên server (nếu chưa có)
3. Nếu tắt: Token vẫn được giữ trên server (có thể xóa nếu cần)

## Kiểm tra

### Backend:
1. Kiểm tra Firebase đã được initialize:
   ```bash
   # Xem logs khi start server
   # Sẽ thấy "Firebase initialized successfully" hoặc warning
   ```

2. Test gửi notification:
   - Tạo chapter mới cho novel có người follow
   - Kiểm tra logs xem có gửi notification không

### Mobile:
1. Kiểm tra token đã được gửi lên server:
   - Đăng nhập
   - Xem logs: "FCM token saved to server successfully"

2. Test nhận notification:
   - Follow một novel
   - Bật notification setting
   - Tạo chapter mới cho novel đó
   - Kiểm tra notification có hiển thị không

## Troubleshooting

### Backend không gửi được notification:
1. Kiểm tra Firebase service account file có đúng không
2. Kiểm tra environment variable `GOOGLE_APPLICATION_CREDENTIALS`
3. Xem logs để biết lỗi cụ thể

### Mobile không nhận được notification:
1. Kiểm tra `google-services.json` có đúng không
2. Kiểm tra token đã được gửi lên server chưa
3. Kiểm tra user đã follow novel và bật notification chưa
4. Kiểm tra device có kết nối internet không

### Token không được lưu:
1. Kiểm tra user đã đăng nhập chưa
2. Kiểm tra API endpoint có hoạt động không
3. Xem logs để biết lỗi cụ thể

## Lưu ý

1. **Firebase Service Account**: File này rất quan trọng và nhạy cảm, không commit vào git
2. **google-services.json**: File này có thể commit nhưng nên kiểm tra kỹ
3. **Token Management**: Token sẽ tự động được refresh bởi FCM, không cần xử lý thủ công
4. **Notification Settings**: User có thể bật/tắt notification trong ProfileScreen

## Đã xóa
- `ChapterUpdateWorker`: Client-side polling worker
- `ChapterUpdateReminderManager`: Manager để schedule worker
- Logic schedule/cancel trong MainActivity và ProfileScreen

## Migration Notes
- App đã chuyển từ client-side polling (24h interval) sang server-side push (real-time)
- Không cần WorkManager nữa cho notification checking
- Token được quản lý tự động bởi FCM và backend

