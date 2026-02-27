package com.gaggledemo.data;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Integer> {

    @Override
    Optional<AppUser> findById(Integer id);
}
