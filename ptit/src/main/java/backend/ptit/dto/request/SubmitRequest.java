package backend.ptit.dto.request;


import lombok.Data;

@Data
public class SubmitRequest {
    private long problemId;

    private String userQuery;

}
