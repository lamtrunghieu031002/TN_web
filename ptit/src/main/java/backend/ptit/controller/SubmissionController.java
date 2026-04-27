package backend.ptit.controller;


import backend.ptit.dto.request.SubmitRequest;
import backend.ptit.dto.response.LeaderboardResponse;
import backend.ptit.dto.response.SubmissionStatusResponse;
import backend.ptit.dto.response.SubmitResponse;
import backend.ptit.dto.response.UserStatsResponse;
import backend.ptit.entity.Submission;
import backend.ptit.entity.User;
import backend.ptit.security.CustomUserDetail;
import backend.ptit.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;


    // user nop bai
    @PostMapping("/submit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<SubmitResponse> submit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SubmitRequest request) {

        Long userId = ((CustomUserDetail) userDetails).getId();
        return ResponseEntity.ok(submissionService.submit(userId, request));
    }
    // Xem lịch sử nộp bài
    @GetMapping("/history/{problemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<Submission>> history(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long problemId) {

        Long userId = ((CustomUserDetail) userDetails).getId();
        return ResponseEntity.ok(submissionService.getHistory(userId, problemId));
    }
    // Xem thống kê của bản thân
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<UserStatsResponse> getMyStats(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((CustomUserDetail) userDetails).getId();
        return ResponseEntity.ok(submissionService.getUserStats(userId));
    }

    // Admin xem thống kê của user bất kỳ
    @GetMapping("/stats/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(submissionService.getUserStats(userId));
    }
    // Leaderboard - ai cũng xem được
    @GetMapping("/leaderboard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard() {
        return ResponseEntity.ok(submissionService.getLeaderboard());
    }

    @GetMapping("/my-status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<Map<Long, String>> getMyStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetail) userDetails).getId();
        return ResponseEntity.ok(submissionService.getUserProblemStatus(userId));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<List<Submission>> getMyHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetail) userDetails).getId();
        return ResponseEntity.ok(submissionService.getUserHistory(userId));
    }

    // Tất cả submissions - trang trạng thái public
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<List<SubmissionStatusResponse>> getPublicStatus() {
        return ResponseEntity.ok(submissionService.getPublicStatus());
    }

}
