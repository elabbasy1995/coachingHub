package com.elabbasy.coatchinghub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "certificates")
@Where(clause = "DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} certificates SET DELETED = true where id = ?")
public class Certificate extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_id")
    private Coach coach;
}
