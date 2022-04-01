package com.odk.connect.repository;

import com.odk.connect.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface MediaRepository extends JpaRepository<Media, Long> {
	@Query(value="select u from Media u where u.user.id = :id and  u.fileName = :fileName ")
	List<Media> findByFileName(@Param("id")Long id,@Param("fileName") String fileName);
	@Query(value="select u from Media u where u.user.id = :id and  u.user.role = 'ROLE_ALUM' ")
	List<Media>findAllByUserId(@Param("id") Long id);
	@Query(value="select u from Media u where u.titre <> '' ")
	List<Media>findAllByAdminAndFormateur();
//	@Query(value="select u from Media u where u.user.role = 'ROLE_FORMATEUR' ")
//	List<Media>findAllByFormateur();
	@Query(value="select u from Media u where u.titre = '' ") 
	List<Media>findAllByAlum();
	@Query(value="select u from Media u where u.date >= :dateDebut and u.date <= :datFin and u.titre = '' ")
	List<Media>findByDateGreaterThanEqualAndDateLessThanEqual(@Param("dateDebut") LocalDate dateDebut,@Param("datFin")  LocalDate datFin);
}
