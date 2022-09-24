package com.tuan03.SpringbootJWT.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tuan03.SpringbootJWT.common.ERole;
import com.tuan03.SpringbootJWT.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

        Optional<Role> findByName(ERole name);
}
