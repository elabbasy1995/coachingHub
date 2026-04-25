package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByNameIn(Collection<String> names);
}
