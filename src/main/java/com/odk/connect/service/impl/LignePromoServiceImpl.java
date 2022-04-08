package com.odk.connect.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.xmlbeans.impl.xb.xsdschema.Attribute.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.odk.connect.exception.model.PromotionException;
import com.odk.connect.exception.model.UsernameExistException;
import com.odk.connect.model.Alumni;
import com.odk.connect.model.LignePromotion;
import com.odk.connect.model.Promotion;
import com.odk.connect.model.User;
import com.odk.connect.repository.LignePromoRepository;
import com.odk.connect.repository.PromotionRepository;
import com.odk.connect.repository.UserRepository;
import com.odk.connect.service.LignePromotionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LignePromoServiceImpl implements LignePromotionService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private final LignePromoRepository lignePromoRepository;
	private final PromotionRepository promotionRepository;
	private final UserRepository userRepository;
	private LignePromotion lignePromoSaved;
	int counter = 0;

	@Override
	public LignePromotion save(Long idUser, Long idPromo) throws PromotionException {
		Optional<Promotion> promotion = promotionRepository.findById(idPromo);
		Optional<User> user = userRepository.findById(idUser);
		if (promotion.isEmpty()) {
			LOGGER.error("impossible d'enregister une ligne de promotion avec une promotion vide");
			throw new PromotionException("impossible d'enregister une ligne de promotion avec une promotion vide");
		}		
		if (user.isEmpty()) {
			LOGGER.error("impossible d'enregistrer une ligne promotion avec un utilisateur vide");
			throw new PromotionException("impossible d'enregistrer une ligne promotion avec un utilisateur vide");
		}
		LignePromotion lignePromo = new LignePromotion();
		lignePromo.setUser(user.get());
		lignePromo.setPromotion(promotion.get());
		List<LignePromotion> lignePromoAllUser = lignePromoRepository.findAllByUserId(lignePromo.getUser().getId());
		if (!lignePromoAllUser.isEmpty()) {
			lignePromoAllUser.stream().forEach(lig -> {
//				System.out.println(lig.getPromotion().getId());
//				System.out.println(lignePromo.getPromotion().getId());
				if (lig.getPromotion().getId().equals(lignePromo.getPromotion().getId())) {
					counter+=1;						
				}
			});
		}
		if (counter == 0) {
			lignePromoSaved = lignePromo;
		} else {
			counter = 0;
			throw new PromotionException("cette promotion existe deja pour cet utilisateur");
		}
		return lignePromoRepository.save(lignePromoSaved);
	}

	@Override
	public LignePromotion update(Long idLignePromo, Long idPromo, Long idUser) throws PromotionException {
		checkIdPromo(idPromo);
		checkIdLignePromo(idLignePromo);
		checkIdUser(idUser);
		Optional<Promotion> promo = promotionRepository.findById(idPromo);
		if (promo.isEmpty()) {
			throw new UsernameNotFoundException("Aucune promotion n'a été  trouvé avec l'ID " + idPromo);
		}
		Optional<User> userOptional = userRepository.findById(idUser);
		
		if (userOptional.isEmpty()) {
			throw new UsernameNotFoundException("Aucun utilisateur n'a été  trouvé avec l'ID " + idUser);
		}
		List<LignePromotion> lignePromoAllUser = lignePromoRepository.findAllByUserId(userOptional.get().getId());
		if (!lignePromoAllUser.isEmpty()) {
			lignePromoAllUser.stream().forEach(lig -> {
//				System.out.println(lig.getPromotion().getId().equals(promo.get().getId()));
				if (lig.getPromotion().getId().equals(promo.get().getId())) {
					counter+=1;					
				}
				
			});
		}
		if (counter == 0) {
			Optional<LignePromotion> lignePromo = findLignePromotion(idLignePromo);
			LignePromotion lignePromoToSaved = lignePromo.get();
			lignePromoToSaved.setUser(userOptional.get());
			lignePromoToSaved.setPromotion(promo.get());
			lignePromoSaved = lignePromoToSaved;
		} else {
			counter = 0;
			throw new PromotionException("cette promotion existe deja pour cet utilisateur");
			
		}
		
		return lignePromoRepository.save(lignePromoSaved);
	}

	@Override
	public LignePromotion findByLignePromoById(Long idLignePromo) {
		Optional<LignePromotion> lignePromo = lignePromoRepository.findById(idLignePromo);
		if (lignePromo.isEmpty()) {
			LOGGER.error("aucun promotion du ligne promotion n'a été trouvé avec utilisateur l'ID " + idLignePromo);
		}
		return lignePromo.get();
	}

	@Override
	public List<LignePromotion> findAllLignePromo() {
		return lignePromoRepository.findAll();
	}

	@Override
	public void deleteLignePromo(Long id) {
		lignePromoRepository.deleteById(id);

	}
	@Override
	public List<Promotion> findAllPromotionByUserId(Long id) throws PromotionException {
		List<LignePromotion> ligneUserByPromoId = lignePromoRepository.findAllByUserId(id);
		if(ligneUserByPromoId.isEmpty()) {
			throw new PromotionException("aucun utilisateur du ligne promotion n'a été trouvé avec promotion l'ID " +id);
		}
		List<Promotion>promo = new ArrayList<Promotion>();
		ligneUserByPromoId.stream().forEach(l->{
			promo.add(l.getPromotion());
		});
		return promo;
	}
	
	@Override
	public List<User> findAllUserByPromotionId(Long id) throws PromotionException {
		List<LignePromotion> lignePromoByUserId = lignePromoRepository.findAllByPromotionId(id);
		if(lignePromoByUserId.isEmpty()) {
			throw new PromotionException("aucun ligne promotion n'a été trouvé avec l'ID " +id);
		}
		List<User>user = new ArrayList<User>();
		lignePromoByUserId.stream().forEach(l->{
			user.add(l.getUser());
		});
		return user;
	}
	public  LignePromotion ajouterlignepromo(LignePromotion lignePromotion){
//		LignePromotion lignePromo = new LignePromotion();
//		lignePromo.setUser(user.get());
//		lignePromo.setPromotion(promotion.get());
		List<LignePromotion> lignePromoAllUser = lignePromoRepository.findAllByUserId(lignePromotion.getUser().getId());
		if (!lignePromoAllUser.isEmpty()) {
			lignePromoAllUser.stream().forEach(lig -> {
//				System.out.println(lig.getPromotion().getId());
//				System.out.println(lignePromo.getPromotion().getId());
				if (lig.getPromotion().getId().equals(lignePromotion.getPromotion().getId())) {
					counter+=1;						
				}
			});
		}
		if (counter == 0) {
			lignePromoSaved = lignePromotion;
		} else {
			counter = 0;
//			throw new PromotionException("cette promotion existe deja pour cet utilisateur");
		}
		return lignePromoRepository.save(lignePromoSaved);
	}

	@Override
	public List<User> findAllALumniByPromotionId(Long id) throws PromotionException {
		List<LignePromotion> lignePromoByUserId = lignePromoRepository.findAllByAlumByPromotionId(id);
		if(lignePromoByUserId.isEmpty()) {
			throw new PromotionException("aucun alumni n'est associé à cette promotion");
		}
		List<User>user = new ArrayList<User>();
		lignePromoByUserId.stream().forEach(l->{
			user.add(l.getUser());
		});
		return user;
	}
	@Override
	public List<LignePromotion> findAllFormateurByPromotionId(Long id) throws PromotionException {
		List<LignePromotion> lignePromoByUserId = lignePromoRepository.findAllFormateurByPromotionId(id);
//		if(lignePromoByUserId.isEmpty()) {
//			throw new PromotionException("aucun formateur n'est associé à cette promotion");
//		}
//		List<User>user = new ArrayList<User>();
//		lignePromoByUserId.stream().forEach(l->{
//			user.add(l.getUser());
//		});
		return lignePromoByUserId;
	}
//	@Override
//	public List<User> findAllFormateurByPromotionIdNot(Long id) throws PromotionException {
//		List<LignePromotion> lignePromoByUserId = lignePromoRepository.findAllFormateurByPromotionIdNot(id);
////		if(lignePromoByUserId.isEmpty()) {
////			throw new PromotionException("aucun formateur n'est associé à cette promotion");
////		}
//		List<User>user = new ArrayList<User>();
//		lignePromoByUserId.stream().forEach(l->{
//			user.add(l.getUser());
//		});
//		return user;
//	}
	private Optional<LignePromotion> findLignePromotion(Long idLignePromo) throws PromotionException {
		Optional<LignePromotion> LignePromoOptional = lignePromoRepository.findById(idLignePromo);
		if (LignePromoOptional.isEmpty()) {
			throw new PromotionException("Aucune ligne de promotion n'a été trouvé avec l'ID " + idLignePromo);
		}
		return LignePromoOptional;
	}

	private void checkIdUser(Long idUser) throws PromotionException {
		if (idUser == null) {
			LOGGER.error("L'ID d'utilisateur est nul");
			throw new PromotionException("impossible de modifier une ligne promotion avec un id utilisateur  null");
		}

	}

	private void checkIdLignePromo(Long idLignePromo) throws PromotionException {
		if (idLignePromo == null) {
			LOGGER.error("impossible de modifier une promotion avec une ligne de promotion null");
			throw new PromotionException("impossible de modifier une ligne promotion avec une linge promotion null");
		}

	}

	private void checkIdPromo(Long idPromo) throws PromotionException {
		if (idPromo == null) {
			LOGGER.error("impossible de modifier une promotion avec un id null");
			throw new PromotionException("impossible de modifier une ligne promotion avec un id promotion null");
		}

	}
	
}
