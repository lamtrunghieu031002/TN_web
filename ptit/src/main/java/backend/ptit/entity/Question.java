package backend.ptit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "questions")
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // Nội dung câu hỏi

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctAnswer; // Đáp án đúng (Chỉ lưu chữ A, B, C, hoặc D)

    // Mối quan hệ Nhiều-1: Nhiều câu hỏi thuộc về 1 Đề thi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    @JsonIgnore // DÒNG NÀY CỰC KỲ QUAN TRỌNG: Giúp Postman không bị lỗi vòng lặp vô hạn khi in dữ liệu
    private Exam exam;
}
