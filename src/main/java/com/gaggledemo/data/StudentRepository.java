package com.gaggledemo.data;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Integer> {

    @Override
    Optional<Student> findById(Integer id);
}
