package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.MobileAppVersion;
import com.elabbasy.coatchinghub.model.enums.MobilePlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileAppVersionRepository  extends JpaRepository<MobileAppVersion, Long> {

    Optional<MobileAppVersion>
    findByPlatformAndVersion(MobilePlatform platform, String version);

    Optional<MobileAppVersion>
    findTopByPlatformOrderByReleaseDateDesc(MobilePlatform platform);
}