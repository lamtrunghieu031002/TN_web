package backend.ptit.repository;

import backend.ptit.entity.Problem;
import backend.ptit.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {




    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.problem LEFT JOIN FETCH s.user " +
            "WHERE s.user.id = :userId AND s.problem.id = :problemId " +
            "ORDER BY s.submittedAt DESC")
    List<Submission> findByUser_IdAndProblem_Id(@Param("userId") Long userId,
                                                @Param("problemId") Long problemId);



    // dem tong so lan nop cua user
    int countByUser_Id(Long userId);

    // dem so lan accepted cua 1 user
    int countByUser_IdAndStatus(Long userId, Submission.SubmissionStatus status);

    // lay cac problem_id da accepted khong bi trung lap

    @Query("SELECT DISTINCT s.problem.id FROM Submission s WHERE s.user.id=:userId and s.status='ACCEPTED'")
    List<Long>findSolvedProblemIds(@Param("userId")Long userId);

    //dem cac bai accepted theo do kho
    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM Submission s WHERE s.user.id = :userId AND s.status = 'ACCEPTED' AND s.problem.difficulty = :difficulty")
    int countSolvedByDifficulty(@Param("userId") Long userId, @Param("difficulty") Problem.Difficulty difficulty);


    // Lấy tất cả userId đã từng submit
    @Query("SELECT DISTINCT s.user.id FROM Submission s")
    List<Long> findAllUserIds();


    //lay lich su cua 1 nguoi dung


    // Lấy tất cả lịch sử (dành cho tab Trạng thái chung của hệ thống)
    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.problem LEFT JOIN FETCH s.user ORDER BY s.submittedAt DESC")
    List<Submission> findAllSubmissionsWithDetails();
    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.problem LEFT JOIN FETCH s.user " +
            "WHERE s.user.id = :userId ORDER BY s.submittedAt DESC")
    List<Submission> findByUserIdOrderBySubmittedAtDesc(@Param("userId") Long userId);
    int countByStatus(Submission.SubmissionStatus status);

    @Query("SELECT FUNCTION('DATE', s.submittedAt) AS day, COUNT(s) AS total " +
            "FROM Submission s WHERE s.submittedAt >= :since " +
            "GROUP BY FUNCTION('DATE', s.submittedAt) ORDER BY day")
    List<Object[]> countSubmissionsByDay(@Param("since") LocalDate since);







}
