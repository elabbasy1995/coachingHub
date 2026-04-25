package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.mapper.CoacheeMapper;
import com.elabbasy.coatchinghub.model.dto.CoacheeDto;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.entity.Role;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.model.enums.RoleName;
import com.elabbasy.coatchinghub.model.request.CreateCoacheeRequest;
import com.elabbasy.coatchinghub.model.request.UpdateCoacheeProfileRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.CoachCoacheeResponse;
import com.elabbasy.coatchinghub.repository.CoacheeRepository;
import com.elabbasy.coatchinghub.repository.RoleRepository;
import com.elabbasy.coatchinghub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Transactional
public class CoacheeService {

    private final CoacheeMapper coacheeMapper;
    private final UserRepository userRepository;
    private final CoacheeRepository coacheeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailOtpService emailOtpService;

    public CoacheeDto createCoachee(CreateCoacheeRequest request) {

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorMessage.EMAIL_ALREADY_EXIST);
        }

        // Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setLanguage(request.getLanguage());

        // Assign COACHEE role
        Role coachRole = roleRepository.findByName(RoleName.COACHEE.name())
                .orElseThrow(() -> new RuntimeException("COACHEE role not found"));
        user.getRoles().add(coachRole);

        user = userRepository.save(user);

        // Create coachee
        Coachee coachee = new Coachee();
        coachee.setFullName(request.getFullName());
        coachee.setBirthDate(request.getBirthDate());
        coachee.setPhoneNumber(request.getPhoneNumber());
        coachee.setActive(false);
        coachee.setUser(user);

        coachee = coacheeRepository.save(coachee);

        emailOtpService.sendOtpAfterRegistration(user.getEmail());
        // Map to DTO
        return coacheeMapper.toDto(coachee);
    }

    public ApiResponse<List<CoachCoacheeResponse>> findByCoach(Long coachId, Integer pageIndex, Integer pageSize, String name) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CoachCoacheeResponse> page = coacheeRepository.findCoacheesForCoach(coachId, pageable, name);
        if (Objects.nonNull(page)) {
            return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public CoacheeDto details(Long id) {
        Coachee coachee = coacheeRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorMessage.COACHEE_NOT_FOUND));

        return coacheeMapper.toDto(coachee);
    }

    public CoacheeDto updateCoacheeProfile(Long id, UpdateCoacheeProfileRequest updateCoacheeProfileRequest) {
        Coachee coachee = coacheeRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorMessage.COACHEE_NOT_FOUND));
        coachee.setPhoneNumber(updateCoacheeProfileRequest.getPhoneNumber());
        coachee.setFullName(updateCoacheeProfileRequest.getFullName());

        Coachee save = coacheeRepository.save(coachee);

        return coacheeMapper.toDto(save);
    }
}
