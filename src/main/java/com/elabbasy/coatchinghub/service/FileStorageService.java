package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.model.request.CreateCoachStep1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.storage.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    public String saveProfileImage(CreateCoachStep1.Attachment attachment) {

        validateAttachment(attachment);

        byte[] fileBytes;
        try {
            fileBytes = Base64.getDecoder().decode(
                    attachment.getContent().replaceAll("\\s+", "")
            );
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorMessage.INVALID_BASE64_CONTENT);
        }

        // Max size: 2MB
        if (fileBytes.length > 2_000_000) {
            throw new BusinessException(ErrorMessage.FILE_SIZE_EXCEEDED);
        }

        String extension = resolveExtension(attachment.getContentType());

        String safeName = sanitizeFilename(attachment.getAttachmentName());
        String storedName = UUID.randomUUID() + "_" + safeName + "." + extension;

        Path target = Paths.get(uploadDir, "profiles", storedName);

        try {
            Files.createDirectories(target.getParent());
            Files.write(target, fileBytes);
        } catch (IOException e) {
            throw new BusinessException(ErrorMessage.FILE_SAVE_FAILED);
        }

        return "/uploads/profiles/" + storedName;
    }

    public String saveCertificate(CreateCoachStep1.Attachment attachment) {

        validateAttachment(attachment);

        byte[] bytes;
        try {
            bytes = Base64.getDecoder()
                    .decode(attachment.getContent().replaceAll("\\s+", ""));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorMessage.INVALID_BASE64_CONTENT);
        }

        // 5MB max
        if (bytes.length > 5_000_000) {
            throw new BusinessException(ErrorMessage.FILE_SIZE_EXCEEDED);
        }

        String extension = resolveExtension(attachment.getContentType());
        String safeName = sanitizeFilename(attachment.getAttachmentName());

        String storedName = UUID.randomUUID() + "_" + safeName + "." + extension;

        Path target = Paths.get(uploadDir, "certificates", storedName);

        try {
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
        } catch (IOException e) {
            throw new BusinessException(ErrorMessage.FILE_SAVE_FAILED);
        }

        // 👈 this is what you save in DB
        return "/uploads/certificates/" + storedName;
    }

    // ----------------- helpers -----------------

    private void validateAttachment(CreateCoachStep1.Attachment attachment) {
        if (!ALLOWED_IMAGE_TYPES.contains(attachment.getContentType())) {
            throw new IllegalArgumentException("UNSUPPORTED_CONTENT_TYPE");
        }
    }

    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/jpeg", "image/jpg" -> "jpg";
            default -> throw new IllegalArgumentException("INVALID_CONTENT_TYPE");
        };
    }

    private String sanitizeFilename(String filename) {
        return filename
                .replaceAll("[^a-zA-Z0-9-_]", "")
                .toLowerCase();
    }
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        String relativePath = fileUrl.replace("/uploads/", "");
        Path basePath = Paths.get(uploadDir).normalize();
        Path filePath = basePath.resolve(relativePath).normalize();

        if (!filePath.startsWith(basePath)) {
            throw new BusinessException(ErrorMessage.FILE_SAVE_FAILED);
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new BusinessException(ErrorMessage.FILE_SAVE_FAILED);
        }
    }
}
