package com.odk.connect.controller;

import com.odk.connect.constants.fileConstant;
import com.odk.connect.exception.ExceptionHandling;
import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.exception.model.UsernameExistException;
import com.odk.connect.model.CategoryForum;
import com.odk.connect.model.HttpResponse;
import com.odk.connect.model.User;
import com.odk.connect.service.CategoryService;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.odk.connect.constants.fileConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/odkConnect/forum/category")
@RequiredArgsConstructor
public class CategoryController extends ExceptionHandling {

	private final CategoryService categoryService;

	@PostMapping("/SaveCategoryFroum")
	public ResponseEntity<CategoryForum> ajouterCategory(@RequestParam("libelleCat") String description,
			@RequestParam("id") Long idUser,
			@RequestParam(value = "categoryImage", required = false) MultipartFile categoryImage)
			throws ForumException, IOException, NotAnImageFileException {
		CategoryForum catForum = categoryService.ajouterCategory(description, idUser, categoryImage);
		return new ResponseEntity<CategoryForum>(catForum, OK);
	}
	@GetMapping("/listCateForum")
	public ResponseEntity<List<CategoryForum>> getAllCateForum() {
		List<CategoryForum> catForum = categoryService.getAllCateforum();
		return new ResponseEntity<List<CategoryForum>>(catForum, OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getCategoryImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
			throws IOException {
		return Files.readAllBytes(Paths.get(CATEGORY_FOLDER + username + FORWARD_SLASH + fileName));
	}
	@DeleteMapping("/deleteCatForum/{id}")	
	public ResponseEntity<HttpResponse> deleteCatForum(@PathVariable("id") Long id) throws IOException, UsernameExistException, ForumException {
		categoryService.supprimerCategory(id);
		return response(OK, "Categorie de discussion supprimer avec succès");
	}
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		HttpResponse body = new HttpResponse(new Date(), httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
		return new ResponseEntity<HttpResponse>(body, httpStatus);
	}

}
