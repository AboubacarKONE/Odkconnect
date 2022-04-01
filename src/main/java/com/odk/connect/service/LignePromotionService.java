package com.odk.connect.service;

import java.util.List;

import com.odk.connect.exception.model.PromotionException;
import com.odk.connect.model.Alumni;
import com.odk.connect.model.LignePromotion;
import com.odk.connect.model.Promotion;
import com.odk.connect.model.User;

public interface LignePromotionService {
	LignePromotion save(Long idUser, Long idPromo) throws PromotionException;

	LignePromotion update(Long idLignePromo, Long idPromo, Long idUser) throws PromotionException;

	LignePromotion findByLignePromoById(Long idLignePromo);

	List<LignePromotion> findAllLignePromo();

	List<Promotion> findAllPromotionByUserId(Long id) throws PromotionException;

	List<User> findAllUserByPromotionId(Long id) throws PromotionException;

	List<User> findAllALumniByPromotionId(Long id) throws PromotionException;

	List<LignePromotion> findAllFormateurByPromotionId(Long id) throws PromotionException;
//	List<User> findAllFormateurByPromotionIdNot(Long id) throws PromotionException;

	void deleteLignePromo(Long id);

	public LignePromotion ajouterlignepromo(LignePromotion lignePromotion);
}
