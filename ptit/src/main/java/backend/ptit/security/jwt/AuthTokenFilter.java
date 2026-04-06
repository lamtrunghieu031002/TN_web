package backend.ptit.security.jwt;

import backend.ptit.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Lấy token từ request gửi lên
            String jwt = parseJwt(request);

            // 2. Nếu có token và token hợp lệ (không bị hết hạn, không bị sai chữ ký)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // Lấy username từ token đó
                String username = jwtUtils.getAllUserFromJwtToken(jwt);

                // Lấy thông tin chi tiết của user từ database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 3. Tạo thẻ chứng nhận đăng nhập thành công (Authentication)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. CẤP THẺ: Lưu vào SecurityContext (Bảo vệ chính thức mở cửa)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Không thể thiết lập xác thực người dùng: {}", e.getMessage());
        }

        // Chuyển cho các bộ lọc khác tiếp tục xử lý
        filterChain.doFilter(request, response);
    }

    // Hàm phụ trợ: Cắt bỏ chữ "Bearer " để lấy đúng chuỗi mã hóa
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Số 7 là độ dài của chữ "Bearer "
        }

        return null;
    }
}