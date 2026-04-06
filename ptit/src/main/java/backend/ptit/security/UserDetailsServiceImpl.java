package backend.ptit.security;

import backend.ptit.entity.User;
import backend.ptit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service; // Import thư viện này

@Service // BƯỚC QUAN TRỌNG NHẤT LÀ THÊM CHỮ NÀY
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));

        return CustomUserDetail.build(user); // Trả về CustomUserDetail mà chúng ta đã sửa ban nãy
    }
}