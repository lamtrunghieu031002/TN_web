package backend.ptit.service;

import backend.ptit.dto.request.CreateExamRequest;
import backend.ptit.entity.Exam;

public interface ExamService {

    Exam createExam(CreateExamRequest request);
}
