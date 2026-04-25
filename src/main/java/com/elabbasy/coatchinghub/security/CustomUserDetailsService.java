package com.elabbasy.coatchinghub.security;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.repository.UserRepository;
import com.elabbasy.coatchinghub.service.EmailOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmailOtpService emailOtpService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findDetailedByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(ErrorMessage.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}
