package com.odk.connect.service.impl;

import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.model.Question;
import com.odk.connect.model.Reponse;
import com.odk.connect.model.User;
import com.odk.connect.repository.QuestionRepository;
import com.odk.connect.repository.ReponseRepository;
import com.odk.connect.repository.UserRepository;
import com.odk.connect.service.ReponseService;

import lombok.RequiredArgsConstructor;

import static com.odk.connect.constants.fileConstant.*;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ReponseServiceImpl implements ReponseService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private final ReponseRepository responseRepository;
	private final QuestionRepository quizRepository;
	private final UserRepository userRepository;

	@Override
	public Reponse ajouterReponse(String description, Long idUser, Long idQuiz, MultipartFile responseImage)
			throws ForumException, IOException, NotAnImageFileException {
		Optional<Question> quiz = quizRepository.findById(idQuiz);
		if (quiz.isEmpty()) {
			LOGGER.error("Aucune question de discution n'a été trouvé avec l'id " + idQuiz);
			throw new ForumException("impossible d'enregistrer une reponse pour une question enexistante");
		}
		Optional<User> user = userRepository.findById(idUser);
		if (user.isEmpty()) {
			LOGGER.error("Aucun utilisateur n'a été trouvé avec l'id " + idUser);
			throw new ForumException("impossible d'enregister une response pour un utilisateur inconnue");
		}
		Reponse response = new Reponse();
		response.setDescription(description);
		response.setUser(user.get());
		response.setQuiz(quiz.get());
		responseRepository.save(response);
		saveResponseImage(response, responseImage);
		return response;
	}

	@Override
	public Reponse updateResponse(Long idResponse, String description, MultipartFile responseImage)
			throws ForumException, IOException, NotAnImageFileException {
		Optional<Reponse> respUpdate = responseRepository.findById(idResponse);
		if (respUpdate.isEmpty()) {
			LOGGER.error("Aucune reponse n'a été trouvée avec l'ID " + idResponse);
			throw new ForumException("impossible de mettre à jour une reponse non enregistrée");
		}
		respUpdate.get().setDescription(description);
		saveResponseImage(respUpdate.get(), responseImage);
		return respUpdate.get();
	}

	@Override
	public List<Reponse> findAllReponseByQuizId(Long id) throws ForumException {
		return responseRepository.findAllReponseByQuizId(id);
	}
	@Override
	public List<Reponse> findAllResponse() {
		return responseRepository.findAll();
	}

	@Override
	public void supprimerReponse(Long id) {
		responseRepository.deleteById(id);
		;

	}

	private void saveResponseImage(Reponse response, MultipartFile responseImage)
			throws IOException, NotAnImageFileException {
		if (responseImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(responseImage.getContentType())) {
				throw new NotAnImageFileException(responseImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path userFolder = Paths.get(RESPONSE_FOLDER + response.getUser().getLogin()).toAbsolutePath().normalize();
			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
				LOGGER.info(DIRECTORY_CREATED + userFolder);
			}
			String fileNameWithOutExt = FilenameUtils.removeExtension(responseImage.getOriginalFilename());
			Files.copy(responseImage.getInputStream(), userFolder.resolve(fileNameWithOutExt + DOT + JPG_EXTENSION));
			response.setPhotoUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path(RESPONSE_IMAGE_PATH
					+ response.getUser().getLogin() + FORWARD_SLASH + fileNameWithOutExt + DOT + JPG_EXTENSION)
					.toUriString());
			responseRepository.save(response);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + responseImage.getOriginalFilename());
		}

	}

	

}
