package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.mapper.CoachMapper;
import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.entity.*;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.enums.RoleName;
import com.elabbasy.coatchinghub.model.request.*;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.CertificateResponse;
import com.elabbasy.coatchinghub.model.response.CoachSearchDto;
import com.elabbasy.coatchinghub.model.response.PortalCoachDetailsResponse;
import com.elabbasy.coatchinghub.model.response.PortalCoachLookupProjection;
import com.elabbasy.coatchinghub.model.response.PortalCoachListResponse;
import com.elabbasy.coatchinghub.repository.*;
import com.elabbasy.coatchinghub.util.PasswordGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
public class CoachService {

    private final CoachRepository coachRepository;
    private final CoachMapper coachMapper;
    private final CoachingIndustryRepository industryRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final FileStorageService fileStorageService;
    private final CertificateRepository certificateRepository;
    private final CountryRepository countryRepository;
    private final NationalityRepository nationalityRepository;
    private final EmailOtpService emailOtpService;
    private final BookingRepository bookingRepository;

    public CoachDto step1(CreateCoachStep1 request) {

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorMessage.EMAIL_ALREADY_EXIST);
        }
        Country country = countryRepository.findById(request.getCountryId()).orElseThrow(() -> new BusinessException(ErrorMessage.COUNTRY_REQUIRED));
        Nationality nationality = nationalityRepository.findById(request.getNationalityId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.NATIONALITY_REQUIRED));

        Coach coach = new Coach();
        coach.setFullNameEn(request.getFullNameEn());
        coach.setFullNameAr(request.getFullNameAr());
        coach.setGender(request.getGender());
        coach.setBirthDate(request.getBirthDate());
        coach.setCountry(country);
        coach.setNationality(nationality);
        coach.setEmail(request.getEmail());
        coach.setWhatsAppNumber(request.getWhatsAppNumber());

        List<Language> languages = languageRepository.findByIdIn(request.getLanguageIds());

        coach.setLanguages(languages);

        coach.setYearsOfExperience(request.getYearsOfExperience());
        coach.setAvailableEveryWeek(request.getAvailableEveryWeek());
        coach.setJobTitle(request.getJobTitle());

        // set coaching industries
        List<CoachingIndustry> industries = industryRepository.findAllById(request.getCoachingIndustriesIds());
        coach.setCoachingIndustries(industries);

        coach.setUsername(request.getUsername());
        String profileImage = fileStorageService.saveProfileImage(request.getProfileImage());
        coach.setProfileImageUrl(profileImage);
        coach.setStatus(CoachStatus.PENDING_APPROVAL);


        Coach save = coachRepository.save(coach);
        // 2️⃣ Save certificates (if any)
        if (request.getCertificates() != null && !request.getCertificates().isEmpty()) {

            for (CreateCoachStep1.Attachment attachment : request.getCertificates()) {

                String certificateUrl =
                        fileStorageService.saveCertificate(attachment);

                Certificate certificate = new Certificate();
                certificate.setName(attachment.getAttachmentName());
                certificate.setFileUrl(certificateUrl);
                certificate.setContentType(attachment.getContentType());
                certificate.setCoach(save);

                certificateRepository.save(certificate);
            }
        }

        User user = new User();
        user.setEmail(coach.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setLanguage(request.getLanguage());
        Role coachRole = roleRepository.findByName(RoleName.COACH.name())
                .orElseThrow(() -> new RuntimeException("COACH role not found"));
        user.getRoles().add(coachRole);
        userRepository.save(user);
        save.setUser(user);

        coachRepository.save(save);
        emailOtpService.sendOtpAfterRegistration(user.getEmail());

        return coachMapper.toDto(save);
    }

    // Admin approves coach request
    public CoachDto approveCoach(Long coachId, ApproveCoachRequest request) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));

        coach.setStatus(CoachStatus.APPROVED);
        coach.setHourlyPrice(request.getHourlyPrice());
        coach.setTwoHoursPrice(request.getTwoHoursPrice());
        coach.setHalfHourPrice(request.getHalfHourPrice());
        coach.setOneAndHalfHourPrice(request.getOneAndHalfHourPrice());

        Coach save = coachRepository.save(coach);

        return coachMapper.toDto(save);
    }

    public CoachDto rejectCoach(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));

        coach.setStatus(CoachStatus.REJECTED);
        Coach save = coachRepository.save(coach);

        return coachMapper.toDto(save);
    }

    public ApiResponse<List<CoachSearchDto>> search(ApiRequest<CoachSearchCriteria> request) {
        Page<CoachSearchDto> page = coachRepository.search(request.getFilter(), request.buildPagination());

        if (Objects.nonNull(page) && Objects.nonNull(page.getContent()) && page.getContent().size() > 0) {
            return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else {
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        }
    }

    public CoachDto getDetails(Long id) {
        Coach coach = coachRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));
        if (!CoachStatus.APPROVED.equals(coach.getStatus()))
            throw new BusinessException(ErrorMessage.COACH_NOT_FOUND);

        Integer countByCoachIdAndPaymentStatus = bookingRepository.countByCoachIdAndPaymentStatus(coach.getId(), PaymentStatus.PAID);

        CoachDto dto = coachMapper.toDto(coach);
        dto.setBookingCount(countByCoachIdAndPaymentStatus);

        return dto;
    }

    public List<CoachDto> findAll() {
        List<Coach> coachList = coachRepository.findAll();

        return coachMapper.toDtoList(coachList);
    }

    public ApiResponse<List<PortalCoachListResponse>> findAllForAdmin(String name,
                                                                      Integer pageIndex,
                                                                      Integer pageSize,
                                                                      String sortBy,
                                                                      String sortDir) {
        ApiRequest<Void> apiRequest = ApiRequest.<Void>builder()
                .pageIndex(pageIndex == null ? 0 : pageIndex)
                .pageSize(pageSize == null ? Integer.MAX_VALUE : pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        Page<PortalCoachListResponse> page = coachRepository.searchAdminCoaches(name, apiRequest.buildPagination());
        return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public ApiResponse<List<PortalCoachLookupProjection>> findApprovedCoachLookup() {
        return new ApiResponse<>(coachRepository.findByStatusOrderByFullNameEnAsc(CoachStatus.APPROVED));
    }

    public PortalCoachDetailsResponse updateCoachForAdmin(Long coachId, UpdatePortalCoachRequest request) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.COUNTRY_REQUIRED));
        Nationality nationality = nationalityRepository.findById(request.getNationalityId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.NATIONALITY_REQUIRED));

        if (!coach.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorMessage.EMAIL_ALREADY_EXIST);
        }

        List<Language> languages = languageRepository.findByIdIn(request.getLanguageIds());
        List<CoachingIndustry> industries = industryRepository.findAllById(request.getCoachingIndustriesIds());

        coach.setFullNameEn(request.getFullNameEn());
        coach.setFullNameAr(request.getFullNameAr());
        coach.setGender(request.getGender());
        coach.setBirthDate(request.getBirthDate());
        coach.setCountry(country);
        coach.setNationality(nationality);
        coach.setEmail(request.getEmail());
        coach.setWhatsAppNumber(request.getWhatsAppNumber());
        coach.setYearsOfExperience(request.getYearsOfExperience());
        coach.setLanguages(languages);
        coach.setAvailableEveryWeek(request.getAvailableEveryWeek());
        coach.setJobTitle(request.getJobTitle());
        coach.setCoachingIndustries(industries);
        coach.setUsername(request.getUsername());
        coach.setHalfHourPrice(request.getHalfHourPrice());
        coach.setHourlyPrice(request.getHourlyPrice());
        coach.setOneAndHalfHourPrice(request.getOneAndHalfHourPrice());
        coach.setTwoHoursPrice(request.getTwoHoursPrice());

        replaceCoachFiles(coach, request);

        if (coach.getUser() != null) {
            coach.getUser().setEmail(request.getEmail());
        }

        coachRepository.save(coach);
        return getDetailsForAdmin(coachId);
    }

    public PortalCoachDetailsResponse getDetailsForAdmin(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));

        Integer bookingCount = bookingRepository.countByCoachIdAndPaymentStatus(coach.getId(), PaymentStatus.PAID);
        CoachDto coachDto = coachMapper.toDto(coach);

        PortalCoachDetailsResponse response = new PortalCoachDetailsResponse();
        response.setId(coachDto.getId());
        response.setCreatedDate(coachDto.getCreatedDate());
        response.setCreatedBy(coachDto.getCreatedBy());
        response.setUpdatedDate(coachDto.getUpdatedDate());
        response.setUpdatedBy(coachDto.getUpdatedBy());
        response.setFullNameEn(coachDto.getFullNameEn());
        response.setFullNameAr(coachDto.getFullNameAr());
        response.setGender(coachDto.getGender());
        response.setBirthDate(coachDto.getBirthDate());
        response.setEmail(coachDto.getEmail());
        response.setWhatsAppNumber(coachDto.getWhatsAppNumber());
        response.setYearsOfExperience(coachDto.getYearsOfExperience());
        response.setAvailableEveryWeek(coachDto.getAvailableEveryWeek());
        response.setJobTitle(coachDto.getJobTitle());
        response.setUsername(coachDto.getUsername());
        response.setProfileImageUrl(coachDto.getProfileImageUrl());
        response.setStatus(coachDto.getStatus());
        response.setHalfHourPrice(coach.getHalfHourPrice());
        response.setHourlyPrice(coach.getHourlyPrice());
        response.setOneAndHalfHourPrice(coach.getOneAndHalfHourPrice());
        response.setTwoHoursPrice(coach.getTwoHoursPrice());
        response.setCoachingIndustries(coachDto.getCoachingIndustries());
        response.setLanguages(coachDto.getLanguages());
        response.setUser(coachDto.getUser());
        response.setCountry(coachDto.getCountry());
        response.setNationality(coachDto.getNationality());
        response.setEnabled(coach.getUser() != null ? coach.getUser().isEnabled() : null);
        response.setBookingCount(bookingCount);
        response.setCertificates(mapCertificates(coach.getId()));

        return response;
    }

    private void replaceCoachFiles(Coach coach, UpdatePortalCoachRequest request) {
        replaceProfileImage(coach, request.getProfileImage());
        replaceCertificates(coach, request.getCertificates());
    }

    private List<CertificateResponse> mapCertificates(Long coachId) {
        return certificateRepository.findByCoachId(coachId).stream()
                .map(certificate -> new CertificateResponse(
                        certificate.getId(),
                        certificate.getName(),
                        certificate.getFileUrl(),
                        certificate.getContentType()
                ))
                .toList();
    }

    private void replaceProfileImage(Coach coach, UpdatePortalCoachRequest.Attachment profileImage) {
        if (profileImage == null) {
            return;
        }

        if (profileImage.hasContent()) {
            String oldProfileImageUrl = coach.getProfileImageUrl();
            String newProfileImageUrl = fileStorageService.saveProfileImage(toCreateAttachment(profileImage));
            coach.setProfileImageUrl(newProfileImageUrl);
            fileStorageService.deleteFile(oldProfileImageUrl);
            return;
        }

        if (profileImage.hasFileUrl() && Objects.equals(coach.getProfileImageUrl(), profileImage.getFileUrl())) {
            return;
        }

        throw new BusinessException(ErrorMessage.INVALID_ATTACHMENT_REFERENCE);
    }

    private void replaceCertificates(Coach coach, List<UpdatePortalCoachRequest.Attachment> requestedCertificates) {
        if (requestedCertificates == null) {
            return;
        }

        List<Certificate> existingCertificates = certificateRepository.findByCoachId(coach.getId());
        Map<Long, Certificate> existingCertificatesById = new HashMap<>();
        Map<String, Certificate> existingCertificatesByFileUrl = new HashMap<>();

        for (Certificate existingCertificate : existingCertificates) {
            existingCertificatesById.put(existingCertificate.getId(), existingCertificate);
            existingCertificatesByFileUrl.put(existingCertificate.getFileUrl(), existingCertificate);
        }

        Set<Long> keptCertificateIds = new HashSet<>();
        List<Certificate> newCertificates = new ArrayList<>();

        for (UpdatePortalCoachRequest.Attachment attachment : requestedCertificates) {
            if (attachment == null) {
                throw new BusinessException(ErrorMessage.INVALID_ATTACHMENT_REFERENCE);
            }

            if (attachment.hasContent()) {
                newCertificates.add(buildCertificateEntity(coach, attachment));
                continue;
            }

            Certificate existingCertificate = resolveExistingCertificateReference(
                    attachment,
                    existingCertificatesById,
                    existingCertificatesByFileUrl
            );
            keptCertificateIds.add(existingCertificate.getId());
        }

        List<Certificate> certificatesToDelete = existingCertificates.stream()
                .filter(certificate -> !keptCertificateIds.contains(certificate.getId()))
                .toList();

        for (Certificate certificate : certificatesToDelete) {
            fileStorageService.deleteFile(certificate.getFileUrl());
        }
        certificateRepository.deleteAll(certificatesToDelete);

        if (!newCertificates.isEmpty()) {
            certificateRepository.saveAll(newCertificates);
        }
    }

    private Certificate buildCertificateEntity(Coach coach, UpdatePortalCoachRequest.Attachment attachment) {
        CreateCoachStep1.Attachment newAttachment = toCreateAttachment(attachment);
        String certificateUrl = fileStorageService.saveCertificate(newAttachment);

        Certificate certificate = new Certificate();
        certificate.setName(newAttachment.getAttachmentName());
        certificate.setFileUrl(certificateUrl);
        certificate.setContentType(newAttachment.getContentType());
        certificate.setCoach(coach);
        return certificate;
    }

    private Certificate resolveExistingCertificateReference(UpdatePortalCoachRequest.Attachment attachment,
                                                            Map<Long, Certificate> existingCertificatesById,
                                                            Map<String, Certificate> existingCertificatesByFileUrl) {
        Certificate existingCertificate = null;

        if (attachment.getId() != null) {
            existingCertificate = existingCertificatesById.get(attachment.getId());
        }

        if (existingCertificate == null && attachment.hasFileUrl()) {
            existingCertificate = existingCertificatesByFileUrl.get(attachment.getFileUrl());
        }

        if (existingCertificate == null) {
            throw new BusinessException(ErrorMessage.INVALID_ATTACHMENT_REFERENCE);
        }

        if (attachment.getId() != null && !Objects.equals(existingCertificate.getId(), attachment.getId())) {
            throw new BusinessException(ErrorMessage.INVALID_ATTACHMENT_REFERENCE);
        }

        if (attachment.hasFileUrl() && !Objects.equals(existingCertificate.getFileUrl(), attachment.getFileUrl())) {
            throw new BusinessException(ErrorMessage.INVALID_ATTACHMENT_REFERENCE);
        }

        return existingCertificate;
    }

    private CreateCoachStep1.Attachment toCreateAttachment(UpdatePortalCoachRequest.Attachment attachment) {
        if (!hasText(attachment.getContentType())) {
            throw new BusinessException(ErrorMessage.CONTENT_TYPE_REQUIRED);
        }

        if (!hasText(attachment.getContent())) {
            throw new BusinessException(ErrorMessage.CONTENT_REQUIRED);
        }

        if (!hasText(attachment.getName())) {
            throw new BusinessException(ErrorMessage.ATTACHMENT_NAME_REQUIRED);
        }

        return new CreateCoachStep1.Attachment(
                attachment.getContentType(),
                attachment.getContent(),
                attachment.getName()
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
    public PortalCoachDetailsResponse enableCoach(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));

        if (coach.getUser() == null) {
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);
        }

        coach.getUser().setEnabled(true);
        coachRepository.save(coach);
        return getDetailsForAdmin(coachId);
    }

    public PortalCoachDetailsResponse disableCoach(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));

        if (coach.getUser() == null) {
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);
        }

        coach.getUser().setEnabled(false);
        coachRepository.save(coach);
        return getDetailsForAdmin(coachId);
    }

    public String resetCoachPassword(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));

        if (coach.getUser() == null) {
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);
        }

        String newPassword = PasswordGenerator.generateRandomPassword(12);
        coach.getUser().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(coach.getUser());

        emailOtpService.sendCoachPasswordReset(
                coach.getEmail(),
                coach.getFullNameEn(),
                coach.getUsername() != null && !coach.getUsername().isBlank() ? coach.getUsername() : coach.getEmail(),
                newPassword
        );

        return "Coach password has been reset successfully";
    }
}
