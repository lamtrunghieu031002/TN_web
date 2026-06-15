package backend.ptit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Mat khau cu khong duoc trong")
    private String oldPassword;

    @NotBlank(message = "Mat khau moi khong duoc trong")
    @Size(min = 6, max = 100, message = "Mat khau moi phai it nhat 6 ky tu")
    private String newPassword;
}
