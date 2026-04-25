package com.elabbasy.coatchinghub.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_answers")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} task_answers SET DELETED = true WHERE id = ?")
public class TaskAnswer extends AuditBaseEntity {

    private String answerText;

    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    private TaskQuestionOption selectedOption;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private TaskQuestion question;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private TaskAssignment assignment;
}