package backend.ptit.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitRequest {

    @NotNull(message = "problemId khong duoc trong")
    @Min(value = 1, message = "problemId phai lon hon 0")
    private Long problemId;

    @NotBlank(message = "Query khong duoc trong")
    private String userQuery;
}
