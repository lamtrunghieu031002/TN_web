package backend.ptit.controller;

import backend.ptit.entity.Submission;
import backend.ptit.repository.ProblemRepository;
import backend.ptit.repository.SubmissionRepository;
import backend.ptit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    private  final UserRepository userRepository;
    private final ProblemRepository problemRepository;


    private final SubmissionRepository submissionRepository;



    @GetMapping("/overview")
    public ResponseEntity<Map<String,Object>>overview(){
        Map<String,Object>data=new HashMap<>();
        data.put("totalUsers",userRepository.count());
        data.put("totalProblems",problemRepository.count());
        data.put("totalSubmissions",submissionRepository.count());
        data.put("totalAccepted",     submissionRepository.countByStatus(Submission.SubmissionStatus.ACCEPTED));
        data.put("submissionsByDay",  submissionRepository.countSubmissionsByDay(LocalDate.now().minusDays(6)));
        return ResponseEntity.ok(data);
    }
}
