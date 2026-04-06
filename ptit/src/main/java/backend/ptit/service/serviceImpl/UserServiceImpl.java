package backend.ptit.service.serviceImpl;

import backend.ptit.dto.request.ChangePasswordRequest;
import backend.ptit.dto.request.ForgotPasswordRequest;
import backend.ptit.dto.request.ResetPasswordRequest;
import backend.ptit.dto.request.UpdateProfileRequest;
import backend.ptit.dto.response.UserProfileResponse;
import backend.ptit.entity.User;
import backend.ptit.repository.UserRepository;
import backend.ptit.service.EmailService;
import backend.ptit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service // Bắt buộc đặt ở đây để Spring Boot nhận diện
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;






    @Override
    public List<UserProfileResponse>getAllUser(){
        List<User>users=userRepository.findAll();

        //chuyển đôi danh sách entity sang dto để trả về controller;



        return users.stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng với username: " + username));

        return mapToProfileResponse(user);
    }

    @Override
    public void deleteUserById(Long id ){
        if(!userRepository.existsById(id)){
            throw new RuntimeException("Lỗi: người dùng không tồn tại với Id"+id);
        }
        userRepository.deleteById(id);
    }
    @Override
    public void changePassword(String username, ChangePasswordRequest request){


        // 1 tim user
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->  new RuntimeException("Không tìm thấy người dùng!"));
        // 2 kiem tra mat khau xem co khop voi db cu hay khong
        boolean isMatch=passwordEncoder.matches(request.getOldPassword(),user.getPassword());

        if(!isMatch){
            throw  new RuntimeException("mật khẩu cũ không chính xác!");
        }

        // luuw lai mat khau da ma hoa
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    @Override
    public void UpdateProfile(String username, UpdateProfileRequest request){

        User user=userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("không tìm thấy người dùng"));
        user.setUsername(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        userRepository.save(user);
    }


    //tao va gui otp
    @Override
    public void ForgotPassword( ForgotPasswordRequest request){
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("không tìm thấy email"));


        //sinh otp 6 so
        String otp=String.format("%06d",new java.util.Random().nextInt(999999));
        user.setResetPasswordOtp(otp);
        user.setOtpGenerationTime(java.time.LocalDateTime.now());
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);

    }

    // kiem tra otp va dat lai mat khau
    @Override
    public void ResetPassword(ResetPasswordRequest request){

    }

    // Hàm phụ trợ (Helper method) để chuyển đổi User Entity -> UserProfileResponse DTO
    private UserProfileResponse mapToProfileResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }

}
