package backend.ptit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    @NotBlank(message = "Username khong duoc trong")
    @Size(min = 3, max = 30, message = "Username phai tu 3-30 ky tu")
    private String username;

    @NotBlank(message = "Password khong duoc trong")
    @Size(min = 6, max = 100, message = "Password phai it nhat 6 ky tu")
    private String password;

    @NotBlank(message = "Email khong duoc trong")
    @Email(message = "Email khong hop le")
    private String email;


}
