package com.odk.connect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.odk.connect.model.Promotion;
@RepositoryRestResource
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
	Optional<Promotion>findByLibelle(String libelle);
}
