package com.odk.connect.service;

import java.util.List;

import com.odk.connect.exception.model.PromotionException;
import com.odk.connect.model.LignePromotion;
import com.odk.connect.model.Promotion;

public interface PromotionService {
 Promotion save(Promotion promo) throws PromotionException;
 Promotion update(Long id,Promotion promo) throws PromotionException;
 Promotion findPromotionById(Long id) throws PromotionException;
 List<Promotion>getAllPromotions();
// Promotion updateUserByPromotion(Long idPromo, Long idLignePromo, Long idUser) throws PromotionException;
 // Delete a ==> delete LignePromotion
// Promotion deleteUserByPromotion(Long idPromo, Long idLignePromo) throws PromotionException;
 void delete(Long id) throws PromotionException;
}
