package backend.ptit.service;


import backend.ptit.dto.request.SubmitRequest;
import backend.ptit.dto.response.LeaderboardResponse;
import backend.ptit.dto.response.SubmissionStatusResponse;
import backend.ptit.dto.response.SubmitResponse;
import backend.ptit.dto.response.UserStatsResponse;
import backend.ptit.entity.Submission;

import java.util.List;
import java.util.Map;

public interface SubmissionService {
    SubmitResponse submit(Long userId, SubmitRequest request);
    List<Submission> getHistory(Long userId, Long problemId);
    UserStatsResponse getUserStats(Long userId);
    List<LeaderboardResponse> getLeaderboard();
    List<Submission> getUserHistory(Long userId);
    List<SubmissionStatusResponse> getPublicStatus();
    Map<Long, String> getUserProblemStatus(Long userId);  // ← trạng thái bài của user

}