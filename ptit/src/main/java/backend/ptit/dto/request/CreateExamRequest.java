package backend.ptit.dto.request;


import lombok.Data;

@Data
public class CreateExamRequest {
    private String title;
    private String description;
    private Integer timeLimit;
}
