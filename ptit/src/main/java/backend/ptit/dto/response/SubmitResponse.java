package backend.ptit.dto.response;

import backend.ptit.entity.Submission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitResponse {
    private Submission.SubmissionStatus status;
    private String message;
    private List<TestCaseResult> testCaseResults;
    private long executionTimeMs;

    /** Chỉ trả khi accepted hoặc user xem solution */
    private String solutionQuery;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseResult {
        private int testCaseIndex;
        private boolean passed;
        private boolean hidden;
        private List<Map<String, Object>> userOutput;
        private List<Map<String, Object>> expectedOutput;
        private String errorMessage;
    }
}
