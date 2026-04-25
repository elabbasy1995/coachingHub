package com.elabbasy.coatchinghub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "admins")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} admins SET DELETED = true where id = ?")
public class Admin extends AuditBaseEntity {

    private String fullName;

    @JsonIgnoreProperties({"admin", "coach", "coachee"})
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}
