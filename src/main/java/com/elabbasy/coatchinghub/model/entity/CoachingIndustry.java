package com.elabbasy.coatchinghub.model.entity;

import jakarta.persistence.Entity;
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
@Table(name = "coaching_industries")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} coaching_industries SET DELETED = true where id = ?")
public class CoachingIndustry extends BaseEntity {

    private String nameEn;
    private String nameAr;

}
