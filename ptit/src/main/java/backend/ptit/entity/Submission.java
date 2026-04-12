package backend.ptit.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Table(name = "submissions")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(columnDefinition = "TEXT")
    private String userQuery;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;
    private String errorMessage;

    private long executionTimeMs;
    private LocalDateTime submittedAt;
    // so sanh voi solution;
    private boolean matchedSolution;

    public enum SubmissionStatus{
        ACCEPTED,WRONG_ANSWER,ERROR,RUNTIME_ERROR
    }
}
