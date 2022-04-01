package com.odk.connect.controller;

import com.odk.connect.exception.ExceptionHandling;
import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.model.Media;
import com.odk.connect.service.MediaService;
import lombok.RequiredArgsConstructor;
import static com.odk.connect.constants.fileConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/odkConnect/media/")
public class MediaController extends ExceptionHandling {

	private final MediaService mediaService;

	@PostMapping("/saveMedia")
	ResponseEntity<Media> addMedia(@RequestParam(value = "titre", required = false) String titre,
			@RequestParam("idUser") Long idUser, @RequestParam(value = "mediaImage") MultipartFile mediaImage)
			throws ForumException, IOException, NotAnImageFileException {
		Media media = mediaService.addMedia(titre, idUser, mediaImage);
		return new ResponseEntity<Media>(media, OK);
	}

	@GetMapping("/listMediaByUser/{id}")
	ResponseEntity<List<Media>> findAllMediaByUserId(@PathVariable("id") Long id) throws ForumException {
		List<Media> media = mediaService.findAllMediaByUserId(id);
		return new ResponseEntity<List<Media>>(media, OK);
	}

	@GetMapping("/listMediaByAdminOrFormateur")
	ResponseEntity<List<Media>> findAllByAdminAndFormateur() {
		List<Media> media = mediaService.findAllByAdminAndFormateur();
		return new ResponseEntity<List<Media>>(media, OK);
	}

	@GetMapping("/listMediaByAlum")
	ResponseEntity<List<Media>> findAllByAlum() {
		List<Media> media = mediaService.findAllByAlum();
		return new ResponseEntity<List<Media>>(media, OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getMediaImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
			throws IOException {
		return Files.readAllBytes(Paths.get(MEDIA_FOLDER + username + FORWARD_SLASH + fileName));
	}

	@GetMapping("/lastWeek")
	ResponseEntity<List<Media>> getMediaByWeek() throws ForumException {
		return ResponseEntity.ok(mediaService.getMediaByWeek());
	}

	@GetMapping("/lastMonth")
	ResponseEntity<List<Media>> getMediaByMonth() throws ForumException {
		return ResponseEntity.ok(mediaService.getMediaByMonth());
	}
	@GetMapping("/listAll")
	ResponseEntity<List<Media>> findAllMedia() throws ForumException {
		return ResponseEntity.ok(mediaService.findAllMedia());
	}
	@GetMapping("/MediaBeetween/{dateDebut}&{dateFin}")
	ResponseEntity<List<Media>> getMediaBetweenDate(
			@PathVariable("dateDebut") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
			@PathVariable("dateFin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin) {
		return ResponseEntity.ok(mediaService.getMediaBetweenDate(dateDebut, dateFin));
	}
//    @PostMapping("ajouter")
//    public Media ajouterMedia(Media media, @RequestParam("photo") MultipartFile photo) throws  IOException{
//        return mediaService.ajoutMedia(media, photo);
//    }
//    @GetMapping(value = "affichermedia/{id}",produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
//    byte[] affichermedia(@PathVariable("id") Long id) throws IOException{
//        return mediaService.getmedia(id);
//    }
//
//    @DeleteMapping("supprimer/{id}")
//    Void supprimer(@PathVariable("id") Long id){
//        return mediaService.supprimerMedia(id);
//    }
//
//    @GetMapping("listemedia")
//    List<Media> afficher(){
//        return mediaService.afficherListemedia();
//    }

}
