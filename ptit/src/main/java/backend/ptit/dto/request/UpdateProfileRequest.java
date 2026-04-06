package backend.ptit.dto.request;


import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String Email;
    private String Phone;
}
