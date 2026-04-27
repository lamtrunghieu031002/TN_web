package backend.ptit.controller;

import backend.ptit.dto.request.ChangePasswordRequest;
import backend.ptit.dto.request.UpdateProfileRequest;
import backend.ptit.dto.response.MessageResponse;
import backend.ptit.dto.response.UserProfileResponse;
import backend.ptit.entity.ERole;
import backend.ptit.entity.Role;
import backend.ptit.entity.User;
import backend.ptit.repository.RoleRepository;
import backend.ptit.repository.UserRepository;
import backend.ptit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Lấy tất cả user - chỉ ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")   // ← sửa lỗi thiếu dấu '
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    // Lấy tất cả user dạng raw - cho Admin Dashboard
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsersRaw() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Đổi role của user - chỉ ADMIN
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, List<String>> body) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<String> roleNames = body.get("roles");
        Set<Role> roles = new HashSet<>();
        roleNames.forEach(r -> {
            Role role = roleRepository.findByName(ERole.valueOf("ROLE_" + r.toUpperCase()))
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + r));
            roles.add(role);
        });

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Cập nhật role thành công"));
    }

    // Xem thông tin user
    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    // Xóa user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")   // ← sửa lỗi hashRole → hasRole
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Đã xóa người dùng thành công!");
    }

    // Đổi mật khẩu
    @PutMapping("/{username}/password")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<String> changePassword(
            @PathVariable String username,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(username, request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }

    // Đổi mật khẩu cho user đang đăng nhập
    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<String> changePasswordForCurrentUser(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }

    // Cập nhật profile
    @PutMapping("/{username}/profile")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<String> updateProfile(
            @PathVariable String username,
            @RequestBody UpdateProfileRequest request) {
        userService.UpdateProfile(username, request);
        return ResponseEntity.ok("Cập nhật thông tin thành công");
    }
}