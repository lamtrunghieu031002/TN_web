package backend.ptit.repository;

import backend.ptit.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem,Long> {

    // 1. Tìm theo độ khó (Vì là Enum nên không cần IgnoreCase,
    // nhưng Service phải convert String thành Enum chuẩn trước)
    List<Problem> findByDifficulty(Problem.Difficulty difficulty);

    // 2. Tìm theo chủ đề (Thêm IgnoreCase để "SQL" hay "sql" đều ra kết quả)
    List<Problem> findByTopicIgnoreCase(String topic);

    // 3. Tìm kết hợp (Chủ đề dùng IgnoreCase để tăng tính linh hoạt)
    List<Problem> findByDifficultyAndTopicIgnoreCase(Problem.Difficulty difficulty, String topic);

    // 4. Lấy danh sách các chủ đề duy nhất để hiển thị lên dropdown
    @Query("SELECT DISTINCT p.topic FROM Problem p WHERE p.topic IS NOT NULL ORDER BY p.topic ASC")
    List<String> findAllTopics();
}
