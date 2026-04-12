package backend.ptit.service.serviceImpl;

import backend.ptit.dto.request.SubmitRequest;
import backend.ptit.dto.response.LeaderboardResponse;
import backend.ptit.dto.response.SubmitResponse;
import backend.ptit.dto.response.UserStatsResponse;
import backend.ptit.entity.Problem;
import backend.ptit.entity.Submission;
import backend.ptit.entity.TestCase;
import backend.ptit.entity.User;
import backend.ptit.repository.ProblemRepository;
import backend.ptit.repository.SubmissionRepository;
import backend.ptit.repository.UserRepository;
import backend.ptit.service.SqlSandboxService;
import backend.ptit.service.SubmissionService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SqlSandboxService sandboxService;
    private final ProblemRepository problemRepo;
    private final SubmissionRepository submissionRepo;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SubmitResponse submit(Long userId, SubmitRequest request) {
        Problem problem = problemRepo.findById(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài: " + request.getProblemId()));

        List<TestCase> testCases = problem.getTestCases() != null ? problem.getTestCases() : List.of();
        if (testCases.isEmpty()) {
            return SubmitResponse.builder()
                    .status(Submission.SubmissionStatus.ERROR)
                    .message("Bài tập chưa có test case")
                    .testCaseResults(List.of())
                    .executionTimeMs(0)
                    .build();
        }

        List<SubmitResponse.TestCaseResult> testResults = new ArrayList<>();
        boolean allPassed = true;
        long totalTime = 0;

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);

            SqlSandboxServiceImpl.SandboxResult userResult = sandboxService.runInSandbox(
                    problem.getSchemaSetupSql(),
                    tc.getExtraSetupSql(),
                    request.getUserQuery()
            );

            if (userResult == null) {
                testResults.add(SubmitResponse.TestCaseResult.builder()
                        .testCaseIndex(i + 1)
                        .passed(false)
                        .hidden(tc.isHidden())
                        .errorMessage("Lỗi kết nối database")
                        .build());
                allPassed = false;
                continue;
            }

            totalTime += userResult.getExecutionTimeMs();

            if (!userResult.isSuccess()) {
                testResults.add(SubmitResponse.TestCaseResult.builder()
                        .testCaseIndex(i + 1)
                        .passed(false)
                        .hidden(tc.isHidden())
                        .errorMessage(userResult.getErrorMessage())
                        .build());
                allPassed = false;
                continue;
            }

            List<Map<String, Object>> expected = parseExpected(tc.getExpectedResultJson());
            boolean passed = compareResults(userResult.getRows(), expected);
            if (!passed) allPassed = false;

            testResults.add(SubmitResponse.TestCaseResult.builder()
                    .testCaseIndex(i + 1)
                    .passed(passed)
                    .hidden(tc.isHidden())
                    .userOutput(userResult.getRows())
                    .expectedOutput(tc.isHidden() ? null : expected)
                    .build());
        }

        boolean matchedSolution = false;
        if (allPassed) {
            matchedSolution = compareWithSolution(problem, request.getUserQuery());
        }

        Submission.SubmissionStatus status = allPassed
                ? Submission.SubmissionStatus.ACCEPTED
                : Submission.SubmissionStatus.WRONG_ANSWER;

        saveSubmission(userId, problem, request.getUserQuery(), status, totalTime, matchedSolution);

        return SubmitResponse.builder()
                .status(status)
                .message(allPassed ? "Accepted" : "Wrong answer")
                .testCaseResults(testResults)
                .executionTimeMs(totalTime)
                .solutionQuery(allPassed ? problem.getSolutionQuery() : null)
                .build();
    }

    @Override
    public List<Submission> getHistory(Long userId, Long problemId) {
        return submissionRepo.findByUser_IdAndProblem_Id(userId, problemId);
    }

    // -------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------

    private void saveSubmission(Long userId, Problem problem, String query,
                                Submission.SubmissionStatus status,
                                long timeMs, boolean matchedSolution) {
        submissionRepo.save(Submission.builder()
                .user(userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại: " + userId)))
                .problem(problem)
                .userQuery(query)
                .status(status)
                .executionTimeMs(timeMs)
                .matchedSolution(matchedSolution)
                .submittedAt(LocalDateTime.now())
                .build());
    }
    @Override
    public List<LeaderboardResponse> getLeaderboard() {
        List<Long> userIds = submissionRepo.findAllUserIds();

        List<LeaderboardResponse> leaderboard = new ArrayList<>();

        for (Long uid : userIds) {
            User user = userRepo.findById(uid).orElse(null);
            if (user == null) continue;

            int totalSubmissions = submissionRepo.countByUser_Id(uid);
            int totalAccepted = submissionRepo.countByUser_IdAndStatus(uid, Submission.SubmissionStatus.ACCEPTED);
            int totalSolved = submissionRepo.findSolvedProblemIds(uid).size();
            int totalEasy = submissionRepo.countSolvedByDifficulty(uid, Problem.Difficulty.EASY);
            int totalMedium = submissionRepo.countSolvedByDifficulty(uid, Problem.Difficulty.MEDIUM);
            int totalHard = submissionRepo.countSolvedByDifficulty(uid, Problem.Difficulty.HARD);
            double acceptanceRate = totalSubmissions == 0 ? 0 :
                    Math.round((double) totalAccepted / totalSubmissions * 1000.0) / 10.0;

            leaderboard.add(LeaderboardResponse.builder()
                    .userId(uid)
                    .username(user.getUsername())
                    .totalSolved(totalSolved)
                    .totalEasy(totalEasy)
                    .totalMedium(totalMedium)
                    .totalHard(totalHard)
                    .acceptanceRate(acceptanceRate)
                    .totalSubmissions(totalSubmissions)
                    .build());
        }

        // Sort theo totalSolved DESC, nếu bằng nhau thì sort theo acceptanceRate DESC
        leaderboard.sort(Comparator
                .comparingInt(LeaderboardResponse::getTotalSolved).reversed()
                .thenComparingDouble(LeaderboardResponse::getAcceptanceRate).reversed()
        );

        // Gán rank
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }

        return leaderboard;
    }
    @Override
    public UserStatsResponse getUserStats(Long userId){

        User user=userRepo.findById(userId)
                .orElseThrow(()->new RuntimeException("User khong ton tai"+userId));
        int totalSubmissions = submissionRepo.countByUser_Id(userId);
        int totalAccepted=submissionRepo.countByUser_IdAndStatus(userId, Submission.SubmissionStatus.ACCEPTED);
        int totalSolved = submissionRepo.findSolvedProblemIds(userId).size();
        int totalEasy = submissionRepo.countSolvedByDifficulty(userId, Problem.Difficulty.EASY);
        int totalMedium = submissionRepo.countSolvedByDifficulty(userId, Problem.Difficulty.MEDIUM);
        int totalHard = submissionRepo.countSolvedByDifficulty(userId, Problem.Difficulty.HARD);

        double acceptanceRate = totalSubmissions == 0 ? 0 :
                Math.round((double) totalAccepted / totalSubmissions * 1000.0) / 10.0;


        return UserStatsResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .totalSubmissions(totalSubmissions)
                .totalSolved(totalSolved)
                .totalEasy(totalEasy)
                .totalMedium(totalMedium)
                .totalHard(totalHard)
                .acceptanceRate(acceptanceRate)
                .build();

    }

    private boolean compareWithSolution(Problem problem, String userQuery) {
        if (problem.getTestCases() == null || problem.getTestCases().isEmpty()) return false;

        TestCase firstTc = problem.getTestCases().get(0);

        SqlSandboxServiceImpl.SandboxResult solutionResult = sandboxService.runInSandbox(
                problem.getSchemaSetupSql(),
                firstTc.getExtraSetupSql(),
                problem.getSolutionQuery()
        );

        SqlSandboxServiceImpl.SandboxResult userResult = sandboxService.runInSandbox(
                problem.getSchemaSetupSql(),
                firstTc.getExtraSetupSql(),
                userQuery
        );

        if (solutionResult == null || userResult == null) return false;
        if (!solutionResult.isSuccess() || !userResult.isSuccess()) return false;
        return compareResults(userResult.getRows(), solutionResult.getRows());
    }

    private boolean compareResults(List<Map<String, Object>> actual,
                                   List<Map<String, Object>> expected) {
        if (actual == null || expected == null) return false;
        if (actual.size() != expected.size()) return false;

        List<String> actualSorted = actual.stream()
                .map(this::toSortedJson)
                .sorted()
                .toList();

        List<String> expectedSorted = expected.stream()
                .map(this::toSortedJson)
                .sorted()
                .toList();

        return actualSorted.equals(expectedSorted);
    }
    private String toSortedJson(Map<String, Object> row) {
        try {
            Map<String, String> normalized = new TreeMap<>();
            row.forEach((k, v) -> {
                String value;
                if (v == null) {
                    value = "null";
                } else if (v instanceof Number) {
                    // Chuẩn hóa số: 800.00 → 800, 450.50 → 450.5
                    value = new BigDecimal(v.toString())
                            .stripTrailingZeros()
                            .toPlainString();
                } else {
                    value = v.toString();
                }
                normalized.put(k.toLowerCase(), value);
            });
            return objectMapper.writeValueAsString(normalized);
        } catch (Exception e) {
            log.error("Lỗi convert row sang JSON", e);
            return row.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseExpected(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            log.error("Lỗi parse expected JSON: {}", json, e);
            return Collections.emptyList();
        }
    }
}