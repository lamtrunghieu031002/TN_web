package backend.ptit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    @JsonBackReference  // ← tránh vòng lặp JSON khi serialize
    private Problem problem;

    @Column(columnDefinition = "TEXT")
    private String extraSetupSql;

    @Column(columnDefinition = "TEXT")
    private String expectedResultJson;

    private boolean hidden;
}