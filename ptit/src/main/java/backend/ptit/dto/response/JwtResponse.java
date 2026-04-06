package backend.ptit.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {


    private String token;
    private String username;
    private String email;
    private String type="Bearer";
    private  Long id;


    private List<String> roles;

    public JwtResponse(String token,Long id ,String username,String email,List<String>roles) {
        this.token=token;
        this.username=username;
        this.email=email;
        this.id=id;
        this.roles=roles;

    }
}
