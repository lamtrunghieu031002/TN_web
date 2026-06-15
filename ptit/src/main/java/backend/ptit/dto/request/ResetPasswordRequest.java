package backend.ptit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Email khong duoc trong")
    @Email(message = "Email khong hop le")
    private String email;

    @NotBlank(message = "OTP khong duoc trong")
    @Size(min = 6, max = 6, message = "OTP phai 6 ky tu")
    private String otp;

    @NotBlank(message = "Mat khau moi khong duoc trong")
    @Size(min = 6, max = 100, message = "Mat khau moi phai it nhat 6 ky tu")
    private String newPassword;
}
