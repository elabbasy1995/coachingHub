package com.elabbasy.coatchinghub.model.entity;

import com.elabbasy.coatchinghub.model.enums.MobilePlatform;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mobile_app_version")
@Where(clause = "DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} mobile_app_version SET DELETED = true where id = ?")
public class MobileAppVersion extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MobilePlatform platform;

    @Column(nullable = false)
    private String version; // e.g. 1.2.0

    @Column(nullable = false)
    private Boolean supported; // false = blocked version

    @Column(nullable = false)
    private Boolean forceUpdate;

    private String storeUrl;

    private LocalDateTime releaseDate;
}
