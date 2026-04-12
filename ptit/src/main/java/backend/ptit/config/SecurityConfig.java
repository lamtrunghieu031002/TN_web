package backend.ptit.config;

import backend.ptit.security.UserDetailsServiceImpl;
import backend.ptit.security.jwt.AuthEntryPointJwt;
import backend.ptit.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    // 1. Khai báo "Máy quét vé" (AuthTokenFilter)
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // 2. Xây dựng "Cửa bảo vệ" (Security Filter Chain)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        // Cho phép tự do vào cửa Đăng nhập/Đăng ký
                        auth.requestMatchers("/api/auth/**").permitAll()
                                //Problems -Get cho user,student,admin
                                .requestMatchers(HttpMethod.GET,"/api/problems/**")
                                .hasAnyRole("USER","STUDENT","ADMIN")
                                //Problems -put post delete chi admin
                                .requestMatchers(HttpMethod.POST,"/api/problems/**")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,"/api/problems/**")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"/api/problems/**")
                                .hasRole("ADMIN")
                                // Submission cho ca 3
                                .requestMatchers("/api/submissions/**")
                                .hasAnyRole("USER","STUDENT","ADMIN")

                                .anyRequest().authenticated() // Tất cả các cửa khác (như /api/users) đều phải quét vé
                );

        // BƯỚC QUAN TRỌNG NHẤT: Đặt máy quét vé vào ngay trước cửa ra vào
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 3. Cung cấp người quản lý xác thực (Authentication Manager)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 4. Cung cấp thuật toán mã hóa mật khẩu cực mạnh (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}