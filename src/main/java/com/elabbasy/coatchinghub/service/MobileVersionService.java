package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.model.entity.MobileAppVersion;
import com.elabbasy.coatchinghub.model.enums.MobilePlatform;
import com.elabbasy.coatchinghub.model.response.MobileVersionResponse;
import com.elabbasy.coatchinghub.repository.MobileAppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MobileVersionService {

    private final MobileAppVersionRepository repository;

    public MobileVersionResponse checkVersion(
            MobilePlatform platform,
            String clientVersion) {

        MobileAppVersion current = repository
                .findByPlatformAndVersion(platform, clientVersion)
                .orElseThrow(() ->
                        new RuntimeException("Version not registered"));

        if (!current.getSupported()) {
            return MobileVersionResponse.forceUpdate(current);
        }

        MobileAppVersion latest =
                repository.findTopByPlatformOrderByReleaseDateDesc(platform)
                        .orElse(current);

        boolean updateAvailable =
                compareVersions(latest.getVersion(), clientVersion) > 0;

        return MobileVersionResponse.normal(
                updateAvailable,
                latest.getVersion(),
                latest.getStoreUrl(),
                latest.getForceUpdate()
        );
    }

    private int compareVersions(String v1, String v2) {
        String[] a1 = v1.split("\\.");
        String[] a2 = v2.split("\\.");
        for (int i = 0; i < Math.max(a1.length, a2.length); i++) {
            int n1 = i < a1.length ? Integer.parseInt(a1[i]) : 0;
            int n2 = i < a2.length ? Integer.parseInt(a2[i]) : 0;
            if (n1 != n2) return Integer.compare(n1, n2);
        }
        return 0;
    }
}

