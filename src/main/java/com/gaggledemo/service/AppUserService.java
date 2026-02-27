package com.gaggledemo.service;

import com.gaggledemo.controllers.request.AppUserRequestDto;
import com.gaggledemo.data.AppUser;
import com.gaggledemo.data.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AppUserService {
    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AppUserRepository repo;

    @Autowired
    public AppUserService(AppUserRepository repo) {
        this.repo = repo;
    }

    public AppUser createAppUser(AppUserRequestDto dto) {
        //TODO: need a check here just to make sure this email doesn't already exist
        AppUser user = AppUser.builder()
                .name(dto.name)
                .email(dto.email)
                .schoolAccount(dto.schoolAccount).build();
        user = repo.save(user);
        logger.info("Saved new user under id {}", user.getId());
        return user;
    }

    public List<AppUser> listAppUsers() {
        return StreamSupport.stream(repo.findAll().spliterator(), false).toList();
    }

    public Optional<AppUser> findAppUserById(Integer id) {
        return repo.findById(id);
    }
}
