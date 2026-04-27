package backend.ptit.service;

import backend.ptit.entity.Problem;

import java.util.List;

public interface ProblemService {
    List<Problem> getAllProblems();
    Problem getProblemById(Long id);
    Problem createProblem(Problem problem);
    Problem updateProblem(Long id, Problem problem);
    void deleteProblem(Long id);
    List<Problem> filterProblems(String difficulty, String topic); // ← thêm
    List<String> getAllTopics();

}
