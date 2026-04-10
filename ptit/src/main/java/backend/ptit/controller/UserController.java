package backend.ptit.controller;

import backend.ptit.dto.request.ChangePasswordRequest;
import backend.ptit.dto.request.UpdateProfileRequest;
import backend.ptit.dto.response.UserProfileResponse;
import backend.ptit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600) // Cho phép Frontend (React) gọi API
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {



    private final UserService userService;




    // 1: chỉ admin mới được quyền gọi toàn bộ danh sách người dùng

    @GetMapping
    @PreAuthorize("hasRole('ADMIN)")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    // 2: Xem thông tin user (Cả 3 quyền đều xem được)
    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')") // SỬA ĐÚNG DÒNG NÀY (Có dấu phẩy và ngoặc tròn)
    public ResponseEntity<UserProfileResponse> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    // xoa dia chi nguoi dung qua id
    @DeleteMapping("/{id}")
    @PreAuthorize("hashRole('ADMIN')")

    public ResponseEntity<String>deleteUser(@PathVariable Long id ){
        userService.deleteUserById(id);
        return ResponseEntity.ok("đã xóa người dùng thành công !");
    }

    // api cap nhap mat khau
    @PutMapping("/{username}/password")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<String>changePassword(@PathVariable String username, @RequestBody ChangePasswordRequest request){


        userService.changePassword(username,request);

        return ResponseEntity.ok("đổi mật khẩu thành công");
    }

    // api đổi mật khẩu user đang đăng nhập (không phụ thuộc username từ client)
    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<String> changePasswordForCurrentUser(Authentication authentication, @RequestBody ChangePasswordRequest request) {
        String currentUsername = authentication.getName();
        userService.changePassword(currentUsername, request);
        return ResponseEntity.ok("đổi mật khẩu thành công");
    }

    //api update profile
    @PutMapping("/{username}/profile")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public ResponseEntity<String>updateProfile(@PathVariable String username, @RequestBody UpdateProfileRequest request){
        userService.UpdateProfile(username,request);
        return ResponseEntity.ok("cập nhật thông tin thành công ");
    }

//
//    //api quen mat khau
//    @PostMapping("/forgot-password")
//
//    public ResponseEntity<String>forgotPassword(@PathVariable String email,@RequestBody )
}
