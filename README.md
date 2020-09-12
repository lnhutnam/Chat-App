# Chat-App
Desktop chat app using Java Swing, Networking

# Project for Topics in Java Application Course

Bài tập ứng dụng chat: 
- Sinh viên viết một chương trình chat (giao diện đồ họa) có các chức năng sau:
   - Đăng ký chat user (đăng ký từ ứng dụng client)
   - Chương trình cho phép một user có thể chat với nhiều user khác (đang online) cùng lúc.
   - Cho phép gởi file trong khi chat.

(các chức năng không bắt buộc: chat group, voice chat, webcam)

Thời hạn nộp bài
 - Ngày 26/07/2020
 - Nội dung nộp: MSSV.zip 
	+ Hướng dẫn sử dụng
	+ File jar thực thi
	+ File ant đóng gói chương trình
	+ Source code
  
  # Đồ án đã sử dụng
  
  - IDE: Apache Netbeans 12 LTS
  - Java Development Kits 11 LTS
  - Giao diện đồ hoạ: Java Swing
  - Hỗ trợ được: Multithreading, Network (Local network), File transfer
 
 Chat-App
 	|<br>
	+------> Jar: Chứa File Jar thực thi<br>
	|<br>
	+------> Libs: Chứa các file jar là các thư viện hỗ trợ cho chương trình, import vào hai project server và client<br>
	|<br>
	+------> Report: 18120061.pdf file báo cáo cũng như giải thích chi tiết hơn những gì video có thể chưa kịp nói.<br>
	|<br>
	+------> ScriptDatabase: Chứa script init database<br>
	|<br>
	+------> SourceCode: Chứa toàn bộ source code của đồ án<br>
	|<br>
	+------> LTUDJava-CQ2017-2018-NVKhiet-Chat App-Self-Evaluation.xlsx: File excel sinh viên tự đánh giá<br>
	|<br>
	+------> File txt Readme: file hướng dẫn sử dụng<br>

Đồ án có sử dụng Database với hệ quản trị CSDL MySQL
Tài khoản dùng: root và không có mật khẩu

- Cài đặt XAMPP và khởi động Apache, MySQL
- Chạy Script init database
- Vào thư mục Jar, khởi động file server.jar trong thư mục server rồi sau đó mở client.jar trong thư mục client
  # Status
  
| STT  | Tiêu chí chấm điểm  | Thang điểm  | Điểm  |
|---|---|---|---|
| 1  | Chức năng đăng kí tài khoản  | 1  | 1  |
| 2  | Chức năng đăng nhập với tài khoản đã đăng kí  | 1  | 1  |
| 3  | Chat  | 2  | 2  |
| 4  | Chat có emoji  | 0  | 0  |
| 5  | Chat cùng lúc với nhiều người (mở nhiều cửa sổ)  | 2  | 2  |
| 6  | Gửi file (bên nhận download và xem được, ko bị lỗi)  | 3  | 3 |
| 7  | Chat group  | 1  | 1  |
| 8  | Voice chat  | 0.5  | 0  |
| 9  | Webcam  | 0.5  | 0  |
| 10  | Khác  | Quên mật khẩu, Chat Kaomoji  |   |
|   | Tổng  | Max: 12  | 10/12  |
