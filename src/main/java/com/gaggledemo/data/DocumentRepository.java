package com.gaggledemo.data;

import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DocumentRepository extends CrudRepository<Document, Integer> {

    @Override
    @NonNull
    Optional<Document> findById(Integer id);
}
