package backend.ptit.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;



@Getter
@Setter
public class LoginRequest {

    private String username;

    private String email;
    private String password;

    private Set<String>roles; // mảng này chỉ cho admin và teacher login
}
