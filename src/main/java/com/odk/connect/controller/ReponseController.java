package com.odk.connect.controller;

import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.exception.model.UsernameExistException;
import com.odk.connect.model.HttpResponse;
import com.odk.connect.model.Reponse;
import com.odk.connect.service.ReponseService;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import static com.odk.connect.constants.fileConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/odkConnect/forum/response")
public class ReponseController {
	private final ReponseService responseService;

	@PostMapping("/saveResponseForum")
	public ResponseEntity<Reponse> ajouterReponse(@RequestParam("description") String description,
			@RequestParam("idUser") Long idUser, @RequestParam("idQuiz") Long idQuiz,
			@RequestParam(value = "responseImage", required = false) MultipartFile responseImage)
			throws ForumException, IOException, NotAnImageFileException {
		Reponse response = responseService.ajouterReponse(description, idUser, idQuiz, responseImage);
		return new ResponseEntity<Reponse>(response, OK);
	}
	@PostMapping("/updateResponse")
	ResponseEntity<Reponse> updateResponse(@RequestParam("idResponse") Long idResponse,@RequestParam("description") String description,@RequestParam(value = "responseImage", required = false) MultipartFile responseImage)
			throws ForumException, IOException, NotAnImageFileException {
		Reponse respUdate = responseService.updateResponse(idResponse, description, responseImage);
		return new ResponseEntity<Reponse>(respUdate,OK);
	}

	@GetMapping("/listResponseForum/{idQuiz}")
	public ResponseEntity<List<Reponse>> findAllReponseByQuiz(@PathVariable("idQuiz") Long idQuiz)
			throws ForumException {
		List<Reponse> response = responseService.findAllReponseByQuizId(idQuiz);
		return new ResponseEntity<List<Reponse>>(response, OK);
	}
	@GetMapping("/listResponse")
	public ResponseEntity<List<Reponse>> findAllReponse() {
		List<Reponse> response = responseService.findAllResponse();
		return new ResponseEntity<List<Reponse>>(response, OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getResponseImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
			throws IOException {
		return Files.readAllBytes(Paths.get(RESPONSE_FOLDER + username + FORWARD_SLASH + fileName));
	}
	@DeleteMapping("/deleteResponseForum/{id}")	
	public ResponseEntity<HttpResponse> deleteResponseForum(@PathVariable("id") Long id) throws IOException, UsernameExistException, ForumException {
		responseService.supprimerReponse(id);
		return response(OK, "Reponse de la question supprimer avec succès");
	}
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		HttpResponse body = new HttpResponse(new Date(), httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
		return new ResponseEntity<HttpResponse>(body, httpStatus);
	}

}
