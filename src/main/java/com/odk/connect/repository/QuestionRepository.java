package com.odk.connect.repository;

import com.odk.connect.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface QuestionRepository extends JpaRepository<Question, Long> {
	List<Question>findAllByCategoryForumId(Long id);
}
