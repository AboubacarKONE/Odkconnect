package com.odk.connect.repository;

import com.odk.connect.model.CategoryForum;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CategoryRepository extends JpaRepository<CategoryForum, Long> {
	Optional<CategoryForum> findByLibelleCat(String libelle);

}
