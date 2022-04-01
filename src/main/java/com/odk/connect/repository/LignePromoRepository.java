package com.odk.connect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.odk.connect.model.LignePromotion;
import com.odk.connect.model.User;
@RepositoryRestResource
public interface LignePromoRepository extends JpaRepository<LignePromotion, Long> {
	List<LignePromotion>findAllByPromotionId(Long id);
	List<LignePromotion>findAllByUserId(Long id);
	@Query("select u from LignePromotion u where u.user.login = :login ")
	List<LignePromotion>findAllByUserLogin(@Param("login") String login);
	@Query(value="select u from LignePromotion u where u.promotion.id = :id and  u.user.role ='ROLE_ALUM' ")
	List<LignePromotion>findAllByAlumByPromotionId(@Param("id") Long id);
	@Query(value="select u from LignePromotion u where u.promotion.id = :id and  u.user.role ='ROLE_FORMATEUR' ")
	List<LignePromotion>findAllFormateurByPromotionId(@Param("id") Long id);
//	@Query(value="select u from LignePromotion u where u.promotion.id <> :id and  u.user.role ='ROLE_FORMATEUR' ")
//	List<LignePromotion>findAllFormateurByPromotionIdNot(@Param("id") Long id);

}
