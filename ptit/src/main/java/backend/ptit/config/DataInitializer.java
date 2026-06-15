package backend.ptit.config;

import backend.ptit.entity.ERole;
import backend.ptit.entity.Problem;
import backend.ptit.entity.Role;
import backend.ptit.entity.TestCase;
import backend.ptit.entity.User;
import backend.ptit.repository.ProblemRepository;
import backend.ptit.repository.RoleRepository;
import backend.ptit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.defaultUsername:admin}")
    private String adminUsername;

    @Value("${app.admin.defaultPassword:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        seedRoles();
        seedDefaultAdmin();
        seedSampleProblems();
    }

    private void seedRoles() {
        Arrays.stream(ERole.values()).forEach(eRole -> {
            if (roleRepository.findByName(eRole).isEmpty()) {
                Role role = new Role();
                role.setName(eRole);
                roleRepository.save(role);
                log.info("Da seed role: {}", eRole);
            }
        });
    }

    private void seedDefaultAdmin() {
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN khong ton tai"));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setEmail("admin@ptit.local");
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRoles(roles);

        userRepository.save(admin);
        log.info("Da seed user admin mac dinh: username={}", adminUsername);
    }

    private void seedSampleProblems() {
        List<Problem> samples = new ArrayList<>();

        samples.add(buildProblem(
                "Liệt kê tất cả sinh viên",
                "Cho bảng `students(id, name, age)`. Viết câu lệnh trả về toàn bộ sinh viên.",
                "SELECT", Problem.Difficulty.EASY,
                """
                CREATE TABLE students (id INT PRIMARY KEY, name VARCHAR(50), age INT);
                INSERT INTO students VALUES (1, 'Hieu', 21), (2, 'Nam', 22), (3, 'Anh', 20);
                """,
                "SELECT * FROM students",
                "[{\"id\":1,\"name\":\"Hieu\",\"age\":21},{\"id\":2,\"name\":\"Nam\",\"age\":22},{\"id\":3,\"name\":\"Anh\",\"age\":20}]"
        ));

        samples.add(buildProblem(
                "Đếm số sản phẩm",
                "Cho bảng `products(id, name)`. Đếm tổng số sản phẩm, đặt cột kết quả tên là `total`.",
                "COUNT", Problem.Difficulty.EASY,
                """
                CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(50));
                INSERT INTO products VALUES (1, 'A'), (2, 'B'), (3, 'C'), (4, 'D'), (5, 'E');
                """,
                "SELECT COUNT(*) AS total FROM products",
                "[{\"total\":5}]"
        ));

        samples.add(buildProblem(
                "Sản phẩm giá trên 100k",
                "Cho bảng `products(id, name, price)`. Lấy `name` của các sản phẩm có giá lớn hơn 100000.",
                "WHERE", Problem.Difficulty.EASY,
                """
                CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(50), price INT);
                INSERT INTO products VALUES (1, 'Banh mi', 30000), (2, 'Ao', 150000), (3, 'Sach', 80000), (4, 'Giay', 500000);
                """,
                "SELECT name FROM products WHERE price > 100000",
                "[{\"name\":\"Ao\"},{\"name\":\"Giay\"}]"
        ));

        samples.add(buildProblem(
                "Tổng tiền đơn hàng theo khách",
                "Cho hai bảng `customers(id, name)` và `orders(id, customer_id, amount)`. " +
                        "Trả về `name` và `total` (tổng amount) cho mỗi khách hàng đã từng đặt đơn, sắp xếp giảm dần theo total.",
                "JOIN + GROUP BY", Problem.Difficulty.MEDIUM,
                """
                CREATE TABLE customers (id INT PRIMARY KEY, name VARCHAR(50));
                CREATE TABLE orders (id INT PRIMARY KEY, customer_id INT, amount INT);
                INSERT INTO customers VALUES (1, 'Hieu'), (2, 'Nam'), (3, 'Anh');
                INSERT INTO orders VALUES (1, 1, 100), (2, 1, 200), (3, 2, 150), (4, 1, 50);
                """,
                "SELECT c.name, SUM(o.amount) AS total FROM customers c JOIN orders o ON c.id = o.customer_id GROUP BY c.id, c.name ORDER BY total DESC",
                "[{\"name\":\"Hieu\",\"total\":350},{\"name\":\"Nam\",\"total\":150}]"
        ));

        samples.add(buildProblem(
                "Top 3 sản phẩm đắt nhất",
                "Cho bảng `products(id, name, price)`. Lấy `name` và `price` của 3 sản phẩm có giá cao nhất.",
                "ORDER BY + LIMIT", Problem.Difficulty.MEDIUM,
                """
                CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(50), price INT);
                INSERT INTO products VALUES (1, 'A', 100), (2, 'B', 500), (3, 'C', 250), (4, 'D', 900), (5, 'E', 300);
                """,
                "SELECT name, price FROM products ORDER BY price DESC LIMIT 3",
                "[{\"name\":\"D\",\"price\":900},{\"name\":\"B\",\"price\":500},{\"name\":\"E\",\"price\":300}]"
        ));

        samples.add(buildProblem(
                "Khách hàng chưa từng đặt đơn",
                "Cho hai bảng `customers(id, name)` và `orders(id, customer_id, amount)`. " +
                        "Liệt kê `name` của các khách hàng chưa từng có đơn nào.",
                "LEFT JOIN", Problem.Difficulty.HARD,
                """
                CREATE TABLE customers (id INT PRIMARY KEY, name VARCHAR(50));
                CREATE TABLE orders (id INT PRIMARY KEY, customer_id INT, amount INT);
                INSERT INTO customers VALUES (1, 'Hieu'), (2, 'Nam'), (3, 'Anh'), (4, 'Linh');
                INSERT INTO orders VALUES (1, 1, 100), (2, 2, 200);
                """,
                "SELECT c.name FROM customers c LEFT JOIN orders o ON c.id = o.customer_id WHERE o.id IS NULL",
                "[{\"name\":\"Anh\"},{\"name\":\"Linh\"}]"
        ));

        Set<String> existingTitles = problemRepository.findAll().stream()
                .map(Problem::getTitle)
                .collect(java.util.stream.Collectors.toSet());

        List<Problem> toInsert = samples.stream()
                .filter(p -> !existingTitles.contains(p.getTitle()))
                .toList();

        if (toInsert.isEmpty()) {
            log.info("Sample problems da co day du, bo qua seed");
            return;
        }

        problemRepository.saveAll(toInsert);
        log.info("Da seed {} problem mau moi", toInsert.size());
    }

    private Problem buildProblem(String title, String description, String topic,
                                  Problem.Difficulty difficulty,
                                  String schemaSetupSql, String solutionQuery,
                                  String expectedResultJson) {
        Problem problem = Problem.builder()
                .title(title)
                .description(description)
                .topic(topic)
                .difficulty(difficulty)
                .schemaSetupSql(schemaSetupSql)
                .solutionQuery(solutionQuery)
                .testCases(new ArrayList<>())
                .build();

        TestCase tc = TestCase.builder()
                .problem(problem)
                .extraSetupSql("")
                .expectedResultJson(expectedResultJson)
                .hidden(false)
                .build();
        problem.getTestCases().add(tc);
        return problem;
    }
}
