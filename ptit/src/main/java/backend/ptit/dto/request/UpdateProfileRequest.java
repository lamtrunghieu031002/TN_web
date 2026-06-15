package backend.ptit.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;

    @Email(message = "Email khong hop le")
    private String email;

    private String phone;
}
