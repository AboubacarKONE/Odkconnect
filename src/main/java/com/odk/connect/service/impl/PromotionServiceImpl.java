package com.odk.connect.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import static com.odk.connect.constants.UserImplConstant.*;
import com.odk.connect.exception.model.PromotionException;
import com.odk.connect.model.LignePromotion;
import com.odk.connect.model.Promotion;
import com.odk.connect.model.User;
import com.odk.connect.repository.LignePromoRepository;
import com.odk.connect.repository.PromotionRepository;
import com.odk.connect.repository.UserRepository;
import com.odk.connect.service.PromotionService;
import com.odk.connect.validator.PromotionValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
	private final PromotionRepository promotionRepository;
	private final UserRepository userRepository;
	private final LignePromoRepository lignePromoRepository;
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Override
	public Promotion save(Promotion promo) throws PromotionException {
		Optional<Promotion>promoOptional= promotionRepository.findByLibelle(promo.getLibelle());
		if(promoOptional.isPresent()) {
			throw new PromotionException("cette promo existe deja dans la BDD");
		}
		List<String> errors = PromotionValidator.validate(promo);
		if(!errors.isEmpty()) {
			throw new PromotionException("promotion invalide");
		}
		
//		if (promo.getLignePromotions() != null) {
//			promo.getLignePromotions().forEach(ligPromo -> {
//				if (ligPromo.getUser() != null) {
//					Optional<User> user = userRepository.findById(ligPromo.getUser().getId());
//					if (!user.isPresent()) {
//						throw new UsernameNotFoundException(NO_USER_FOUND_BY_ID + ligPromo.getUser().getId());
//					}
//				} else {
//					throw new UsernameNotFoundException(
//							"impossible d'enregistrer une promotion avec des personnes null");
//				}
//			});
//		}
//		if(promo.getLignePromotions() != null) {
//			promo.getLignePromotions().forEach(ligPromo->{
//				LignePromotion lignePromo = new LignePromotion();
//				lignePromo.setDate(ligPromo.getDate());
//				lignePromo.setPromotion(ligPromo.getPromotion());
//				lignePromo.setUser(ligPromo.getUser());
//				lignePromoRepository.save(lignePromo);
//			});		
//		}	
		return promotionRepository.save(promo);
	}

	@Override
	public Promotion update(Long id, Promotion promo) throws PromotionException {
		List<String> errors = PromotionValidator.validate(promo);
		if (!errors.isEmpty()) {
			throw new PromotionException("promotion invalide");
		}
		Promotion promodb = promotionRepository.findById(id).get();
		if (promodb == null) {
			throw new PromotionException(PROMO_NOT_FOUND_BY_ID + id);
		}
		promodb.setLibelle(promo.getLibelle());
		promodb.setDateDebut(promo.getDateDebut());
		promo.setDatefin(promo.getDatefin());
		return promotionRepository.save(promodb);
	}

	@Override
	public Promotion findPromotionById(Long id) throws PromotionException {
		Promotion promodb = promotionRepository.findById(id).get();
		if (promodb == null) {
			throw new PromotionException(PROMO_NOT_FOUND_BY_ID + id);
		}
		return promodb;
	}

	@Override
	public List<Promotion> getAllPromotions() {
		return promotionRepository.findAll();
	}

	@Override
	public void delete(Long id) throws PromotionException {
		List<LignePromotion>lignePromo = lignePromoRepository.findAllByPromotionId(id);
		if(!lignePromo.isEmpty()) {
			LOGGER.error("impossible de supprimer une promotion avec une ligne promotion");
			throw new PromotionException("impossible de supprimer une promotion avec une ligne promotion");
		}
		promotionRepository.deleteById(id);
	}

//	@Override
//	public Promotion updateUserByPromotion(Long idPromo, Long idLignePromo, Long idUser) throws PromotionException {
//		checkIdPromo(idPromo);
//		checkIdLignePromo(idLignePromo);
//		checkIdUser(idUser, "nouvel");
//		Promotion promo = promotionRepository.findById(idPromo).get();
//		if (promo == null) {
//			throw new UsernameNotFoundException("Aucune promotion n'a été  trouvé avec l'ID " + idPromo);
//		}
//		Optional<User> userOptional = userRepository.findById(idUser);
//		if (userOptional.isEmpty()) {
//			throw new UsernameNotFoundException("Aucun utilisateur n'a été  trouvé avec l'ID " + idUser);
//		}
//		Optional<LignePromotion> lignePromo = findLignePromotion(idLignePromo);
//		LignePromotion lignePromoToSaved = lignePromo.get();
//		lignePromoToSaved.setUser(userOptional.get());
//		lignePromoRepository.save(lignePromoToSaved);
//		return promo;
//	}
//	@Override
//	public Promotion deleteUserByPromotion(Long idPromo, Long idLignePromo) throws PromotionException {
//		checkIdPromo(idPromo);
//		checkIdLignePromo(idLignePromo);
//		Promotion promo = promotionRepository.findById(idPromo).get();
//		if (promo == null) {
//			throw new UsernameNotFoundException("Aucune promotion n'a été  trouvé avec l'ID " + idPromo);
//		}
//		findLignePromotion(idLignePromo);
//		lignePromoRepository.deleteById(idLignePromo);
//		return promo;
//	}

	private Optional<LignePromotion> findLignePromotion(Long idLignePromo) throws PromotionException {
		Optional<LignePromotion> LignePromoOptional = lignePromoRepository.findById(idLignePromo);
		if (LignePromoOptional.isEmpty()) {
			throw new PromotionException("Aucune ligne de promotion n'a été trouvé avec l'ID " + idLignePromo);
		}
		return LignePromoOptional;
	}

	private void checkIdUser(Long idUser, String msg) throws PromotionException {
		if (idUser == null) {
			LOGGER.error("L'ID de " + msg + " est nul");
			throw new PromotionException("impossible de modifier une promotion avec un " + msg + " id null");
		}

	}

	private void checkIdLignePromo(Long idLignePromo) throws PromotionException {
		if (idLignePromo == null) {
			LOGGER.error("impossible de modifier une promotion avec une ligne de promotion null");
			throw new PromotionException("impossible de modifier une promotion avec une ligne de promotion null");
		}

	}

	private void checkIdPromo(Long idPromo) throws PromotionException {
		if (idPromo == null) {
			LOGGER.error("impossible de modifier une promotion avec un id null");
			throw new PromotionException("impossible de modifier une promotion avec un id null");
		}

	}

	

}
