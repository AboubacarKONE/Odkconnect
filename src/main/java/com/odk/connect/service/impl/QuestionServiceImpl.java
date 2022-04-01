package com.odk.connect.service.impl;

import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.model.CategoryForum;
import com.odk.connect.model.Question;
import com.odk.connect.model.Reponse;
import com.odk.connect.model.User;
import com.odk.connect.repository.CategoryRepository;
import com.odk.connect.repository.QuestionRepository;
import com.odk.connect.repository.ReponseRepository;
import com.odk.connect.repository.UserRepository;
import com.odk.connect.service.QuestionService;
import lombok.RequiredArgsConstructor;
import static com.odk.connect.constants.fileConstant.DIRECTORY_CREATED;
import static com.odk.connect.constants.fileConstant.DOT;
import static com.odk.connect.constants.fileConstant.FILE_SAVED_IN_FILE_SYSTEM;
import static com.odk.connect.constants.fileConstant.FORWARD_SLASH;
import static com.odk.connect.constants.fileConstant.JPG_EXTENSION;
import static com.odk.connect.constants.fileConstant.NOT_AN_IMAGE_FILE;
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
import static com.odk.connect.constants.fileConstant.*;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private final QuestionRepository quizRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final ReponseRepository responseRepository;

	@Override
	public Question ajouterQuestion(String description, Long idUser, Long idCategory, MultipartFile quizImage)
			throws ForumException, IOException, NotAnImageFileException {
		Optional<User> user = userRepository.findById(idUser);
		if (user.isEmpty()) {
			LOGGER.error("Aucun utilisateur n'a été trouvé avec l'id" + idUser);
			throw new ForumException("Aucun utilisateur n'a été trouvé avec l'id" + idUser);
		}
		Optional<CategoryForum> cat = categoryRepository.findById(idCategory);
		if (cat.isEmpty()) {
			LOGGER.error("Aucune category de discution n'a été trouvé avec l'id " + idCategory);
			throw new ForumException("impossible une question dans une categorie inexistante");
		}
		Question quiz = new Question();
		quiz.setDescription(description);
		quiz.setCategoryForum(cat.get());
		quiz.setUser(user.get());		
		quizRepository.save(quiz);
		saveQuestionImage(quiz, quizImage);
		return quiz;
	}
	@Override
	public Question updateQuiz(Long idQuiz, String description,MultipartFile quizImage)
			throws ForumException, IOException, NotAnImageFileException {
		Optional<Question> quizUpdate = quizRepository.findById(idQuiz);
		if(quizUpdate.isEmpty()) {
			LOGGER.error("Aucune question n'a été trouvé dans la BDD avec l'ID " + idQuiz);
			throw new ForumException("impossible de modifier une question non enregistée dans la base de données");
		}
		quizUpdate.get().setDescription(description);
		quizRepository.save(quizUpdate.get());
		saveQuestionImage(quizUpdate.get(), quizImage);
		return quizUpdate.get();
	}

	@Override
	public List<Question> getAllQuizForum() {
		return quizRepository.findAll();
	}
	

	@Override
	public void supprimerQuestion(Long id) throws ForumException {
		List<Reponse> response = responseRepository.findAllReponseByQuizId(id);
		if(!response.isEmpty()) {
			throw new ForumException("impossible de supprimer une question repondue");
		}
		quizRepository.deleteById(id);		
	}

	@Override
	public List<Question> findAllQuizByCategorie(Long id) throws ForumException {
		List<Question> quizCat = quizRepository.findAllByCategoryForumId(id);
		if(quizCat.isEmpty()) {
			throw new ForumException("Aucune question n'existe pour cette categorie");
		}
		return quizCat;
	}

	private void saveQuestionImage(Question quiz, MultipartFile quizImage) throws IOException, NotAnImageFileException {
		if (quizImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(quizImage.getContentType())) {
				throw new NotAnImageFileException(quizImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path userFolder = Paths.get(QUIZ_FOLDER + quiz.getUser().getLogin()).toAbsolutePath().normalize();
			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
				LOGGER.info(DIRECTORY_CREATED + userFolder);
			}			
			String fileNameWithOutExt = FilenameUtils.removeExtension(quizImage.getOriginalFilename());			
			Files.copy(quizImage.getInputStream(), userFolder.resolve(fileNameWithOutExt + DOT + JPG_EXTENSION));
			quiz.setPhotoUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path(QUIZ_IMAGE_PATH
					+ quiz.getUser().getLogin() + FORWARD_SLASH + fileNameWithOutExt + DOT + JPG_EXTENSION)
					.toUriString());
			quizRepository.save(quiz);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + quizImage.getOriginalFilename());
		}

	}		

}
