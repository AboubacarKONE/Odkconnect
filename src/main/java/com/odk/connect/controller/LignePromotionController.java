package com.odk.connect.controller;

import static org.springframework.http.HttpStatus.OK;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.odk.connect.exception.ExceptionHandling;
import com.odk.connect.exception.model.PromotionException;
import com.odk.connect.model.Alumni;
import com.odk.connect.model.HttpResponse;
import com.odk.connect.model.LignePromotion;
import com.odk.connect.model.Promotion;
import com.odk.connect.model.User;
import com.odk.connect.service.LignePromotionService;
import com.odk.connect.service.PromotionService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = { "/", "/odkConnect/lignePromo" })
@RequiredArgsConstructor
public class LignePromotionController extends ExceptionHandling{
	private final LignePromotionService lignePromoService;
	@PostMapping("/saveLignePromo")
	ResponseEntity<LignePromotion> save(@RequestParam("idUser") Long idUser, @RequestParam("idPromo") Long idPromo) throws PromotionException{
		LignePromotion ligPromo = lignePromoService.save(idUser, idPromo);
		return new ResponseEntity<LignePromotion>(ligPromo, OK);
	}
	@PutMapping("/updateLignePromo")
	ResponseEntity<LignePromotion> update(@RequestParam("idLignePromo") Long idLignePromo,@RequestParam("idPromo") Long idPromo,@RequestParam("idUser") Long idUser) throws PromotionException{
		LignePromotion ligPromo =lignePromoService.update(idLignePromo, idPromo, idUser);
		return new ResponseEntity<LignePromotion>(ligPromo, OK); 
	}
	@GetMapping("/lignePromotions/{idLignePromo}")
	ResponseEntity<LignePromotion> findByLignePromoById(@PathVariable("idLignePromo") Long idLignePromo) {
		return ResponseEntity.ok(lignePromoService.findByLignePromoById(idLignePromo));
	}
	@GetMapping("/lignePromos")
	ResponseEntity<List<LignePromotion>> findAllLignePromo(){
		return ResponseEntity.ok(lignePromoService.findAllLignePromo());
	}
	@GetMapping("/promotion/lignePromotions/{idUser}")
	ResponseEntity<List<Promotion>>findAllPromotionByUserId(@PathVariable("idUser") Long idUser) throws PromotionException {
		return ResponseEntity.ok(lignePromoService.findAllPromotionByUserId(idUser));
	}
	@GetMapping("/user/lignePromotions/{idPromo}")
	ResponseEntity<List<User>> findAllUserByPromotionId(@PathVariable("idPromo") Long idPromo) throws PromotionException {
		return ResponseEntity.ok(lignePromoService.findAllUserByPromotionId(idPromo));
	}
	@GetMapping("/Alumni/lignePromotions/{idPromo}")
	ResponseEntity<List<User>> findAllAlumniByPromotionId(@PathVariable("idPromo") Long idPromo) throws PromotionException {
		return ResponseEntity.ok(lignePromoService.findAllALumniByPromotionId(idPromo));
	}
	@GetMapping("/Formateur/lignePromotions/{idPromo}")
	ResponseEntity<List<LignePromotion>> findAllFormateurByPromotionId(@PathVariable("idPromo") Long idPromo) throws PromotionException {
		return ResponseEntity.ok(lignePromoService.findAllFormateurByPromotionId(idPromo));
	}
//	@GetMapping("/Formateur/{idPromo}")
//	ResponseEntity<List<User>> findAllFormateurByPromotionIdNot(@PathVariable("idPromo") Long idPromo) throws PromotionException {
//		return ResponseEntity.ok(lignePromoService.findAllFormateurByPromotionIdNot(idPromo));
//	}
	@DeleteMapping("deleteLignePromo")
	@PreAuthorize("hasAnyAuthority('promotion:delete')")
	ResponseEntity<HttpResponse> deleteLignePromo(Long id) {
		lignePromoService.deleteLignePromo(id);
		return response(HttpStatus.OK, "ligne promotion supprimé avec succès");
		
	}
	@PostMapping("ajouteruserpromo")
	LignePromotion ajouter(@RequestBody LignePromotion lignePromotion) {
		return lignePromoService.ajouterlignepromo(lignePromotion);
	}
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		HttpResponse body = new HttpResponse(new Date(), httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
		return new ResponseEntity<HttpResponse>(body, httpStatus);
	}



	

}
