package backend.ptit.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionStatusResponse {
    private Long id;
    private String submittedAt;
    private String username;
    private String problemTitle;
    private Long problemId;
    private String status;        // AC / WA
    private long executionTimeMs;
}
