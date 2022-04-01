package com.odk.connect.controller;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.odk.connect.exception.ExceptionHandling;
import com.odk.connect.exception.model.PromotionException;
import com.odk.connect.model.HttpResponse;
import com.odk.connect.model.LignePromotion;
import com.odk.connect.model.Promotion;
import com.odk.connect.service.PromotionService;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = { "/", "/odkConnect/promotion" })
@RequiredArgsConstructor
public class PromotionController extends ExceptionHandling {
	private final PromotionService promoService;
	@PostMapping("/savePromo")
	ResponseEntity<Promotion> save(@RequestBody Promotion promo) throws PromotionException {
		return ResponseEntity.ok(promoService.save(promo));

	}

	@PutMapping("/updatePromo/{idPromo}")
	ResponseEntity<Promotion> update(@PathVariable("idPromo") Long idPromo,@RequestBody Promotion promo) throws PromotionException {
		return ResponseEntity.ok(promoService.update(idPromo, promo));

	}

	@GetMapping("/promoById/{id}")
	ResponseEntity<Promotion> findById(@PathVariable("id") Long id) throws PromotionException {
		return ResponseEntity.ok(promoService.findPromotionById(id));

	}

	@GetMapping("/promotions")
	ResponseEntity<List<Promotion>> getAllPromotions() {
		return ResponseEntity.ok(promoService.getAllPromotions());

	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('promotion:delete')")
	ResponseEntity<HttpResponse> delete(@RequestParam("id") Long id) throws PromotionException {
		promoService.delete(id);
		return response(HttpStatus.OK, "promotion supprimé avec succès");

	}
//	@PatchMapping("/promotions/update/user/{idPromo}/{idLingePromo}/{idUser}")
//	ResponseEntity<Promotion> updateUserByPromotion(@PathVariable("idPromo") Long idPromo,@PathVariable("idLignePromo") Long idLignePromo,@PathVariable("idUser") Long idUser) throws PromotionException {
//		return ResponseEntity.ok(promoService.updateUserByPromotion(idPromo, idLignePromo, idUser));
//
//	}
//	@DeleteMapping("/promotions/delete/user/{idPromo}/{idLignePromo}")
//	ResponseEntity<Promotion> deleteUserByPromotion(Long idPromo, Long idLignePromo) throws PromotionException {		
//		return ResponseEntity.ok(promoService.deleteUserByPromotion(idPromo, idLignePromo));
//	}
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		HttpResponse body = new HttpResponse(new Date(), httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
		return new ResponseEntity<HttpResponse>(body, httpStatus);
	}

}
