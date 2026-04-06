package backend.ptit.dto.request;


import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String otp;
    private String newPassword;
    private String email;
}
