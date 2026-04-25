package com.elabbasy.coatchinghub.model.entity;

import com.elabbasy.coatchinghub.model.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_questions")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} task_questions SET DELETED = true WHERE id = ?")
public class TaskQuestion extends AuditBaseEntity {

    @Column(nullable = false, length = 2000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Column(nullable = false)
    private Boolean required = false;

    @Column(nullable = false)
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "task_template_id", nullable = false)
    private TaskTemplate taskTemplate;

    @OneToMany(mappedBy = "question")
    private Set<TaskQuestionOption> options;
}
