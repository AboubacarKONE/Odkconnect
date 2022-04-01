package com.odk.connect.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.model.Reponse;
import org.springframework.stereotype.Service;


@Service
public interface ReponseService {

	Reponse ajouterReponse(String description, Long idUser, Long idQuiz, MultipartFile responseImage)
			throws ForumException, IOException, NotAnImageFileException;

	List<Reponse> findAllReponseByQuizId(Long id) throws ForumException;
	List<Reponse>findAllResponse();
	Reponse updateResponse(Long idResponse, String description, MultipartFile responseImage)
			throws ForumException, IOException, NotAnImageFileException;

	void supprimerReponse(Long id);
}
