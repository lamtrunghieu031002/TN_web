package backend.ptit.repository;

import backend.ptit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {




    //Springboot sẽ tự động dịch câu này thành :SELECT * FROM users where username="?"
    Optional<User>findByUsername(String username);


    // Kiểm tra username này có tồn tại trong database chưa trả về (True or False)
    Boolean existsByUsername(String username);
// kiểm tra email này có tồn tại hay chưa nếu tồn rồi trả về true hoặc false
    Boolean existsByEmail(String email);
  Optional<User>findByEmail(String email);
}
