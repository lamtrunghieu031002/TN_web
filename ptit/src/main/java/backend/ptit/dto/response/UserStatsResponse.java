package backend.ptit.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatsResponse {
    private Long userId;
    private String username;
    private int totalSubmissions;
    private int totalSolved;
    private int totalEasy;
    private int totalMedium;
    private int totalHard;
    private double acceptanceRate;

}
