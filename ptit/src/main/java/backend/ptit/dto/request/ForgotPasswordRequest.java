package backend.ptit.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Cực kỳ quan trọng
@AllArgsConstructor
public class ForgotPasswordRequest {
    private String email;
}
