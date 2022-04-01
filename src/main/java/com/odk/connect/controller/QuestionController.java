package com.odk.connect.controller;

import com.odk.connect.exception.ExceptionHandling;
import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.exception.model.UsernameExistException;
import com.odk.connect.model.HttpResponse;
import com.odk.connect.model.Question;
import com.odk.connect.service.QuestionService;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import static com.odk.connect.constants.fileConstant.*;
import static com.odk.connect.constants.fileConstant.FORWARD_SLASH;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/odkConnect/forum/quiz")
@RequiredArgsConstructor
public class QuestionController extends ExceptionHandling {
	private final QuestionService quizService;

	@PostMapping("/saveQuizForum")
	ResponseEntity<Question> ajouterQuestion(@RequestParam("description") String description,
			@RequestParam("idUser") Long idUser, @RequestParam("idCat") Long idCategory,
			@RequestParam(value = "quizImage", required = false) MultipartFile quizImage)
			throws ForumException, IOException, NotAnImageFileException {
		Question quiz = quizService.ajouterQuestion(description, idUser, idCategory, quizImage);
		return new ResponseEntity<Question>(quiz, OK);
	}

	@PostMapping("/updateQuiz")
	ResponseEntity<Question> updateQuiz(@RequestParam("idQuiz") Long idQuiz, @RequestParam("description") String description,
			@RequestParam(value = "quizImage", required = false) MultipartFile quizImage)
			throws ForumException, IOException, NotAnImageFileException {
		Question quizUpdate = quizService.updateQuiz(idQuiz, description, quizImage);
		return new ResponseEntity<Question>(quizUpdate,OK);
	}

	@GetMapping("/listQuizForum")
	public ResponseEntity<List<Question>> getAllCateForum() {
		List<Question> quizForum = quizService.getAllQuizForum();
		return new ResponseEntity<List<Question>>(quizForum, OK);
	}

	@GetMapping("/listQuizForum/{idCat}")
	public ResponseEntity<List<Question>> getAllQuizByCatForum(@PathVariable("idCat") Long id) throws ForumException {
		List<Question> quizByCatForum = quizService.findAllQuizByCategorie(id);
		return new ResponseEntity<List<Question>>(quizByCatForum, OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getQuizImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
			throws IOException {
		return Files.readAllBytes(Paths.get(QUIZ_FOLDER + username + FORWARD_SLASH + fileName));
	}
	@DeleteMapping("/deleteQuizForum/{id}")	
	public ResponseEntity<HttpResponse> deleteQuizForum(@PathVariable("id") Long id) throws IOException, UsernameExistException, ForumException {
		quizService.supprimerQuestion(id);
		return response(OK, "Question pour la discussion supprimer avec succès");
	}
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		HttpResponse body = new HttpResponse(new Date(), httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
		return new ResponseEntity<HttpResponse>(body, httpStatus);
	}

}
