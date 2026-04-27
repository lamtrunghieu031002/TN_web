package backend.ptit.service.serviceImpl;

import backend.ptit.entity.Problem;
import backend.ptit.repository.ProblemRepository;
import backend.ptit.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepo;

    @Override
    public List<Problem> getAllProblems() {
        return problemRepo.findAll();
    }

    @Override
    public Problem getProblemById(Long id) {
        return problemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài: " + id));
    }

    @Override
    public Problem createProblem(Problem problem) {
        // Set problem vào từng testCase trước khi save
        if (problem.getTestCases() != null) {
            problem.getTestCases().forEach(tc -> tc.setProblem(problem));
        }
        return problemRepo.save(problem);
    }

    @Override
    public Problem updateProblem(Long id, Problem problem) {
        Problem existing = getProblemById(id);
        existing.setTitle(problem.getTitle());
        existing.setDescription(problem.getDescription());
        existing.setDifficulty(problem.getDifficulty());
        existing.setTopic(problem.getTopic());
        existing.setSchemaSetupSql(problem.getSchemaSetupSql());
        existing.setSolutionQuery(problem.getSolutionQuery());
        existing.setTestCases(problem.getTestCases());
        return problemRepo.save(existing);
    }

    @Override
    public void deleteProblem(Long id) {
        problemRepo.deleteById(id);
    }
    @Override
    public List<Problem> filterProblems(String difficulty, String topic) {

        // 1. Chuẩn hóa dữ liệu: Nếu là chuỗi trống hoặc "ALL" thì coi như null để lấy tất cả

        boolean hasDifficulty=difficulty!=null&&!difficulty.trim().isEmpty()&&!"ALL".equalsIgnoreCase(difficulty);
        boolean hasTopic=topic!=null&&!topic.trim().isEmpty()&&!"ALL".equalsIgnoreCase(topic);


        // loc thoe ca do kho va chu de
        if(hasDifficulty&&hasTopic){
            return problemRepo.findByDifficultyAndTopicIgnoreCase(
                    Problem.Difficulty.valueOf(difficulty.toUpperCase())
                    ,topic);
        }
        // truong hop loc theo do kho
        if(hasDifficulty){
            return problemRepo.findByDifficulty(
                    Problem.Difficulty.valueOf(difficulty.toUpperCase())
            );
        }
        // truong hop loc theo chu de
        if(hasTopic){
            return problemRepo.findByTopicIgnoreCase(topic);
        }

        // mac dinh tra ve tat ca
        return problemRepo.findAll();
    }

    @Override
    public List<String> getAllTopics() {
        return problemRepo.findAllTopics();
    }
}