package backend.ptit.controller;

import backend.ptit.entity.Problem;
import backend.ptit.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // ← filter và topics phải đặt TRƯỚC /{id}
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<List<Problem>> filter(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String topic) {
        return ResponseEntity.ok(problemService.filterProblems(difficulty, topic));
    }

    @GetMapping("/topics")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<List<String>> getTopics() {
        return ResponseEntity.ok(problemService.getAllTopics());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<List<Problem>> getAll() {
        return ResponseEntity.ok(problemService.getAllProblems());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<Problem> getById(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getProblemById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Problem> create(@RequestBody Problem problem) {
        return ResponseEntity.ok(problemService.createProblem(problem));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Problem> update(@PathVariable Long id,
                                          @RequestBody Problem problem) {
        return ResponseEntity.ok(problemService.updateProblem(id, problem));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {  // ← Void
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}