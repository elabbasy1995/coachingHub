package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.model.enums.MobilePlatform;
import com.elabbasy.coatchinghub.model.response.MobileVersionResponse;
import com.elabbasy.coatchinghub.service.MobileVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mobile/version")
public class MobileVersionController {

    private final MobileVersionService service;


    @GetMapping("/check")
    public MobileVersionResponse checkVersion(
            @RequestParam MobilePlatform platform,
            @RequestParam String version) {

        return service.checkVersion(platform, version);
    }
}