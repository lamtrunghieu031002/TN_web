package backend.ptit.dto.request;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupRequest {

    private String username;
    private String password;
    private String email;
    // SỬA Ở ĐÂY: Phải là Set<String> thay vì String
    private Set<String> roles;
}
