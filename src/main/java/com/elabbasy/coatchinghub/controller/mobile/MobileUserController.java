package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.request.UpdateLanguageRequest;
import com.elabbasy.coatchinghub.model.request.UpdatePasswordRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mobile/api/user")
@RequiredArgsConstructor
public class MobileUserController {

    private final UserService userService;

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestAttribute(name = Constants.USER_ID_ATTRIBUTE) Long userId,
                                              @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        userService.updatePassword(userId, updatePasswordRequest);

        return new ApiResponse<>("success");
    }

    @PutMapping("/update-language")
    public ApiResponse<String> updateLanguage(@RequestAttribute(name = Constants.USER_ID_ATTRIBUTE) Long userId,
                                              @RequestBody UpdateLanguageRequest updateLanguageRequest) {
        userService.updateLanguage(userId, updateLanguageRequest);

        return new ApiResponse<>("success");
    }
}
