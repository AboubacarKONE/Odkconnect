package com.odk.connect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.odk.connect.model.Log;
@RepositoryRestResource
public interface LogRepository extends JpaRepository<Log, Long> {
	List<Log>findAllByOrderByCreatedDateDesc();

}
