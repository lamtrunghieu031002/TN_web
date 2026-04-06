package backend.ptit.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.IOException;


@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {



    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
        // ghi ra cho backend dễ log bug
        System.err.println("truy cập bất thường:"+authException.getMessage());

        // trả về lỗi 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"bạn chưa đăng nhập hoặc token không hợp lệ");
    }
}
