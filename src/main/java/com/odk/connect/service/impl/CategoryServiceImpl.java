package com.odk.connect.service.impl;

import static com.odk.connect.constants.fileConstant.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.model.CategoryForum;
import com.odk.connect.model.Question;
import com.odk.connect.model.User;
import com.odk.connect.repository.CategoryRepository;
import com.odk.connect.repository.QuestionRepository;
import com.odk.connect.repository.UserRepository;
import com.odk.connect.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.commons.io.FilenameUtils;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final QuestionRepository quizRepository;

//	public CategoryForum ajouterCategory(CategoryForum category) throws ForumException {
//		if(category == null) {
//			throw new ForumException("impossible d'ajouter une category null");
//		}
//		Optional<CategoryForum> catForum = categoryRepository.findByLibelle(category.getLibelle());
//		if(!catForum.isEmpty()) {
//			throw new ForumException("cette categorie de discution  exite deja dans le forum");
//		}
//		return categoryRepository.save(category);
//	}

	@Override
	public CategoryForum ajouterCategory(String libelleCat, Long idUser, MultipartFile categoryImage)
			throws ForumException, IOException, NotAnImageFileException {
		Optional<CategoryForum> cat = categoryRepository.findByLibelleCat(libelleCat);
		if (cat.isPresent()) {
			throw new ForumException("cette categorie de discussion existe deja");
		}
		CategoryForum cateForum = new CategoryForum();
		Optional<User> user = userRepository.findById(idUser);
		if (user.isEmpty()) {
			throw new ForumException("Aucun utilisateur n'a été trouvé avec l'id" + idUser);
		}
		cateForum.setLibelleCat(libelleCat);
		cateForum.setUser(user.get());
		categoryRepository.save(cateForum);
		saveCategoryImage(cateForum, categoryImage);
		return cateForum;
	}

	private void saveCategoryImage(CategoryForum cateForum, MultipartFile categoryImage)
			throws IOException, NotAnImageFileException {
		if (categoryImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(categoryImage.getContentType())) {
				throw new NotAnImageFileException(categoryImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path userFolder = Paths.get(CATEGORY_FOLDER + cateForum.getUser().getLogin()).toAbsolutePath().normalize();
			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
				LOGGER.info(DIRECTORY_CREATED + userFolder);
			}
			String fileNameWithOutExt = FilenameUtils.removeExtension(categoryImage.getOriginalFilename());
			Files.copy(categoryImage.getInputStream(), userFolder.resolve(fileNameWithOutExt + DOT + JPG_EXTENSION));
			cateForum.setPhotoUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path(CATEGORY_IMAGE_PATH
					+ cateForum.getUser().getLogin() + FORWARD_SLASH + fileNameWithOutExt + DOT + JPG_EXTENSION)
					.toUriString());
			categoryRepository.save(cateForum);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + categoryImage.getOriginalFilename());
		}

	}

	@Override
	public List<CategoryForum> getAllCateforum() {
		return categoryRepository.findAll();
	}

	@Override
	public void supprimerCategory(Long id) throws ForumException {
		List<Question>quiz = quizRepository.findAllByCategoryForumId(id);
		if(!quiz.isEmpty()) {
			throw new ForumException("impossible de supprimer une categorie avec des questions");
		}
		categoryRepository.deleteById(id);
	}

//	private String getTempraryProfileIamgeUrl(String login) {
//		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_CATEGORY_IMAGE_PATH + login).toUriString();
//	}

}
