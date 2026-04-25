package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.mapper.TaskAssignmentMapper;
import com.elabbasy.coatchinghub.mapper.TaskTemplateMapper;
import com.elabbasy.coatchinghub.model.dto.TaskAssignmentDto;
import com.elabbasy.coatchinghub.model.entity.*;
import com.elabbasy.coatchinghub.model.enums.QuestionType;
import com.elabbasy.coatchinghub.model.enums.TaskAssignmentStatus;
import com.elabbasy.coatchinghub.model.request.AssignTaskRequest;
import com.elabbasy.coatchinghub.model.request.CreateTaskWithQuestionsRequest;
import com.elabbasy.coatchinghub.model.request.SubmitTaskAnswersRequest;
import com.elabbasy.coatchinghub.model.response.*;
import com.elabbasy.coatchinghub.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskTemplateService {

    private final TaskTemplateRepository templateRepository;
    private final TaskQuestionRepository questionRepository;
    private final TaskQuestionOptionRepository optionRepository;
    private final CoachRepository coachRepository;
    private final TaskTemplateMapper taskTemplateMapper;
    private final TaskAssignmentMapper taskAssignmentMapper;
    private final TaskAssignmentRepository assignmentRepository;
    private final BookingRepository bookingRepository;
    private final CoacheeRepository coacheeRepository;
    private final TaskAnswerRepository answerRepository;

    public TaskTemplateResponse createTaskWithQuestions(Long coachId, CreateTaskWithQuestionsRequest request) {
        Coach coach = coachRepository.findById(coachId).orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));

        // 1️⃣ Create Template
        TaskTemplate template = new TaskTemplate();
        template.setCoach(coach);
        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setActive(true);

        template = templateRepository.save(template);

        // 2️⃣ Create Questions
        if (request.getQuestions() != null) {
            for (CreateTaskWithQuestionsRequest.TaskQuestionItem qItem : request.getQuestions()) {

                TaskQuestion question = new TaskQuestion();
                question.setTaskTemplate(template);
                question.setQuestionText(qItem.getQuestionText());
                question.setType(qItem.getType());
                question.setRequired(Boolean.TRUE.equals(qItem.getRequired()));
                question.setOrderIndex(qItem.getOrderIndex() != null ? qItem.getOrderIndex() : 0);

                question = questionRepository.save(question);

                // 3️⃣ Create options if MULTIPLE_CHOICE
                if (qItem.getType() == QuestionType.MULTIPLE_CHOICE && qItem.getOptions() != null) {
                    for (CreateTaskWithQuestionsRequest.TaskQuestionOptionItem oItem : qItem.getOptions()) {
                        TaskQuestionOption option = new TaskQuestionOption();
                        option.setQuestion(question);
                        option.setOptionText(oItem.getOptionText());

                        option = optionRepository.save(option);
                    }
                } else if (qItem.getType() == QuestionType.MULTIPLE_CHOICE) {
                    throw new BusinessException(ErrorMessage.OPTIONS_IS_REQUIRED_FOR_MULTI_CHOSIE);
                }
            }
        }

        return taskTemplateMapper.toTaskTemplateResponse(template);
    }

    public ApiResponse<List<TaskTemplateResponse>> getTaskTemplates(Long coachId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<TaskTemplate> page = templateRepository.findByCoachIdOrderByCreatedDateDesc(coachId, pageable);

        if (Objects.nonNull(page) && Objects.nonNull(page.getContent()) && !page.getContent().isEmpty()) {
            return new ApiResponse<>(taskTemplateMapper.toTaskTemplateResponseList(page.getContent()), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());

    }

    public TaskAssignmentDto assignTaskToBooking(Long coachId, AssignTaskRequest request) {
        Coach coach = coachRepository.findById(coachId).orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));


        // 1️⃣ Validate template ownership
        TaskTemplate template = templateRepository.findById(request.getTaskTemplateId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.TASK_NOT_FOUND));

        if (!template.getCoach().getId().equals(coach.getId())) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION);
        }

        // 2️⃣ Validate booking existence
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.BOOKING_NOT_FOUND));

        // 3️⃣ Create TaskAssignment
        TaskAssignment assignment = new TaskAssignment();
        assignment.setTaskTemplate(template);
        assignment.setBooking(booking);
        assignment.setCoachee(booking.getCoachee());
        assignment.setDueDate(request.getDueDate());
        assignment.setStatus(TaskAssignmentStatus.ASSIGNED);

        assignment = assignmentRepository.save(assignment);

        // 4️⃣ Map to DTO
        return taskAssignmentMapper.toDto(assignment);
    }

    public TaskSubmissionResponse submitAnswers(Long coacheeId, SubmitTaskAnswersRequest request) {
        Coachee coachee = coacheeRepository.findById(coacheeId).orElseThrow(() -> new BusinessException(ErrorMessage.COACHEE_NOT_FOUND));

        TaskAssignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.TASK_ASSIGNMENT_NOT_FOUND));

        if (!assignment.getCoachee().getId().equals(coachee.getId())) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION);
        }

        if (assignment.getStatus() == TaskAssignmentStatus.COMPLETED) {
            throw new BusinessException(ErrorMessage.TASK_ALREADY_SUBMITED);
        }
        Map<Long, SubmitTaskAnswersRequest.QuestionAnswerRequest> answerMap =
                request.getAnswers()
                        .stream()
                        .collect(Collectors.toMap(
                                SubmitTaskAnswersRequest.QuestionAnswerRequest::getQuestionId,
                                a -> a
                        ));
        for (TaskQuestion question : assignment.getTaskTemplate().getQuestions()) {

            if (!question.getRequired()) {
                continue;
            }

            SubmitTaskAnswersRequest.QuestionAnswerRequest provided =
                    answerMap.get(question.getId());

            if (provided == null) {
                throw new BusinessException(ErrorMessage.REQUIRED_QUESTION_NOT_ANSWERED);
            }

            if (QuestionType.TEXT.equals(question.getType())) {

                if (provided.getAnswerText() == null ||
                        provided.getAnswerText().isBlank()) {

                    throw new BusinessException(ErrorMessage.REQUIRED_QUESTION_NOT_ANSWERED);
                }

            } else if (QuestionType.MULTIPLE_CHOICE.equals(question.getType())) {

                if (provided.getSelectedOptionId() == null) {
                    throw new BusinessException(ErrorMessage.REQUIRED_QUESTION_NOT_ANSWERED);
                }

            }
        }

        Map<Long, TaskQuestion> questionMap =
                assignment.getTaskTemplate().getQuestions()
                        .stream()
                        .collect(Collectors.toMap(TaskQuestion::getId, q -> q));

        for (SubmitTaskAnswersRequest.QuestionAnswerRequest req : request.getAnswers()) {

            TaskQuestion question = questionMap.get(req.getQuestionId());

            if (question == null) {
                throw new BusinessException(ErrorMessage.INVALID_QUESTION_ID);
            }

            if (QuestionType.TEXT.equals(question.getType())) {
                TaskAnswer answer = new TaskAnswer();
                answer.setAssignment(assignment);
                answer.setQuestion(question);
                answer.setAnswerText(req.getAnswerText());

                answerRepository.save(answer);
            } else {
                TaskQuestionOption option = optionRepository.findById(req.getSelectedOptionId()).orElseThrow(() -> new BusinessException(ErrorMessage.OPTIONS_IS_REQUIRED_FOR_MULTI_CHOSIE));
                TaskAnswer answer = new TaskAnswer();
                answer.setAssignment(assignment);
                answer.setQuestion(question);
                answer.setSelectedOption(option);

                answerRepository.save(answer);
            }

        }

        assignment.setStatus(TaskAssignmentStatus.COMPLETED);
        assignment.setCompletedAt(LocalDateTime.now());

        return new TaskSubmissionResponse(
                assignment.getId(),
                assignment.getStatus().name(),
                assignment.getCompletedAt()
        );
    }

    public ApiResponse<List<TaskAssignmentSummaryResponse>> getByCoachee(Long coacheeId, Integer pageSize, Integer pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<TaskAssignment> page = assignmentRepository.findByCoacheeId(coacheeId, pageable);
        if (Objects.nonNull(page) && Objects.nonNull(page.getContent()) && !page.getContent().isEmpty()) {
            return new ApiResponse<>(taskAssignmentMapper.toSummaryList(page.getContent()), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());

    }

    public ApiResponse<List<TaskAssignmentSummaryResponse>> getByBooking(Long bookingId, Integer pageSize, Integer pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<TaskAssignment> page = assignmentRepository.findByBookingId(bookingId, pageable);
        if (Objects.nonNull(page) && Objects.nonNull(page.getContent()) && !page.getContent().isEmpty()) {
            return new ApiResponse<>(taskAssignmentMapper.toSummaryList(page.getContent()), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());

    }

    public TaskAssignmentDetailsResponse getDetails(Long assignmentId) {

        TaskAssignment assignment = assignmentRepository.findDetailsBase(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.TASK_ASSIGNMENT_NOT_FOUND));

        TaskTemplate template = assignment.getTaskTemplate();

        // 1️⃣ Load questions
        List<TaskQuestion> questions = questionRepository.findByTemplateId(template.getId());

        List<Long> questionIds = questions.stream()
                .map(TaskQuestion::getId)
                .toList();

        // 2️⃣ Load options in batch
        List<TaskQuestionOption> options = optionRepository.findByQuestionIdIn(questionIds);

        Map<Long, List<TaskQuestionOption>> optionsMap =
                options.stream().collect(Collectors.groupingBy(o -> o.getQuestion().getId()));

        // 3️⃣ Load answers
        List<TaskAnswer> answers = answerRepository.findByAssignmentId(assignmentId);

        Map<Long, TaskAnswer> answersMap =
                answers.stream().collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        // 4️⃣ Map questions
        List<TaskAssignmentDetailsResponse.TaskQuestionDetails> questionDtos = questions.stream()
                .map(q -> {

                    TaskAssignmentDetailsResponse.TaskQuestionDetails dto = taskTemplateMapper.toTaskQuestionDetails(q);

                    // attach options
                    dto.setOptions(
                            optionsMap.getOrDefault(q.getId(), List.of())
                                    .stream()
                                    .map(opt -> new TaskAssignmentDetailsResponse.TaskQuestionOptionDetails(
                                            opt.getId(),
                                            opt.getOptionText()
                                    ))
                                    .toList()
                    );

                    // attach answers ONLY if completed
                    if (assignment.getStatus() == TaskAssignmentStatus.COMPLETED) {

                        TaskAnswer answer = answersMap.get(q.getId());

                        if (answer != null) {
                            dto.setAnswerText(answer.getAnswerText());

                            if (answer.getSelectedOption() != null) {
                                dto.setSelectedOptionId(answer.getSelectedOption().getId());
                            }
                        }
                    }

                    return dto;
                })
                .toList();

        // 5️⃣ Build response
        TaskAssignmentDetailsResponse response = new TaskAssignmentDetailsResponse();
        response.setAssignmentId(assignment.getId());
        response.setStatus(assignment.getStatus().name());
        response.setDueDate(assignment.getDueDate());

        response.setTemplateId(template.getId());
        response.setTitle(template.getTitle());
        response.setDescription(template.getDescription());

        response.setQuestions(questionDtos);

        return response;
    }
}