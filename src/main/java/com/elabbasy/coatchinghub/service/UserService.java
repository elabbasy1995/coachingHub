package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.model.request.UpdateLanguageRequest;
import com.elabbasy.coatchinghub.model.request.UpdatePasswordRequest;
import com.elabbasy.coatchinghub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.USER_NOT_FOUND));

        // 1️⃣ Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorMessage.INVALID_CURRENT_PASSWORD);
        }

        // 2️⃣ Validate new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorMessage.PASSWORDS_DO_NOT_MATCH);
        }

        // 4️⃣ Encode & save
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void updateLanguage(Long userId, UpdateLanguageRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.USER_NOT_FOUND));

        if (request.getLanguage() == null) {
            throw new BusinessException(ErrorMessage.INVALID_LANGUAGE);
        }

        user.setLanguage(request.getLanguage());
    }
}