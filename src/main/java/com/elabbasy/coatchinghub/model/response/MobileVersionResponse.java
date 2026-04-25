package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.entity.MobileAppVersion;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MobileVersionResponse {

    private boolean supported;
    private boolean updateAvailable;
    private boolean forceUpdate;
    private String latestVersion;
    private String storeUrl;

    public static MobileVersionResponse forceUpdate(MobileAppVersion v) {
        return MobileVersionResponse.builder()
                .supported(false)
                .forceUpdate(true)
                .updateAvailable(true)
                .latestVersion(v.getVersion())
                .storeUrl(v.getStoreUrl())
                .build();
    }

    public static MobileVersionResponse normal(
            boolean updateAvailable,
            String latestVersion,
            String storeUrl,
            boolean forceUpdate) {

        return MobileVersionResponse.builder()
                .supported(true)
                .updateAvailable(updateAvailable)
                .forceUpdate(forceUpdate)
                .latestVersion(latestVersion)
                .storeUrl(storeUrl)
                .build();
    }
}
