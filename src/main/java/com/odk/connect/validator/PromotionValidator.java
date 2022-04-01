package com.odk.connect.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.odk.connect.model.Promotion;

public class PromotionValidator {
	public static List<String> validate(Promotion promo){
		List<String> errors = new ArrayList<String>();
		if(promo == null) {
			errors.add("Veuillez renseinger le libelle de la promotion");
			errors.add("Veuillez renseigner la date de debut de la promotion");
			errors.add("Veuillez renseigner la date de debut de la promotion");
			return errors;
		}
		if(!StringUtils.hasLength(promo.getLibelle())) {
			errors.add("Veuillez renseinger le libelle de la promotion");
		}
		if(promo.getDateDebut() == null) {
			errors.add("Veuillez renseigner la date de debut de la promotion");
		}
		if(promo.getDatefin() == null) {
			errors.add("Veuillez renseigner la date de debut de la promotion");
		}
		
		return errors;
	}

}
