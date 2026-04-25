package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    List<Language> findByIdIn(List<Long> languageIds);
}
