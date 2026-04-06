package backend.ptit.service;

import backend.ptit.dto.request.ChangePasswordRequest;
import backend.ptit.dto.request.ForgotPasswordRequest;
import backend.ptit.dto.request.ResetPasswordRequest;
import backend.ptit.dto.request.UpdateProfileRequest;
import backend.ptit.dto.response.UserProfileResponse;
import backend.ptit.repository.UserRepository;

import java.util.List;

public interface UserService {
    List<UserProfileResponse>getAllUser(); // lay tat ca nguoi dung

    UserProfileResponse getUserByUsername(String username);
    void deleteUserById(Long id);
    void changePassword(String username, ChangePasswordRequest request);
    void UpdateProfile(String username, UpdateProfileRequest request);
    void ForgotPassword(ForgotPasswordRequest request);
    void ResetPassword(ResetPasswordRequest request);
}
