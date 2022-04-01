package com.odk.connect.repository;

import com.odk.connect.model.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ReponseRepository extends JpaRepository<Reponse, Long> {
	List<Reponse>findAllReponseByQuizId(Long id);
	
}
