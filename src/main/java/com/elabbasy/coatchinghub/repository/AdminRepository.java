package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("""
            select a
            from Admin a
            join a.user u
            where lower(a.fullName) like lower(concat('%', ?1, '%'))
               or lower(u.email) like lower(concat('%', ?1, '%'))
            """)
    Page<Admin> searchAdmins(String search, Pageable pageable);

    @Modifying
    @Query("update Admin a set a.deleted = true where a.id = :adminId")
    int softDeleteById(@Param("adminId") Long adminId);
}
