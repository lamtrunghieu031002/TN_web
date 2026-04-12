package backend.ptit.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Table(name="exams")
@Data
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // Tiêu đề đề thi (VD: Bài thi Giữa kỳ Java)

    private String description; // Mô tả ngắn gọn

    private Integer timeLimit; // Thời gian làm bài (tính bằng phút)

    // Mối quan hệ 1-Nhiều: 1 Đề thi chứa nhiều Câu hỏi
    // cascade = CascadeType.ALL: Xóa đề thi là xóa sạch câu hỏi bên trong
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions;

}
