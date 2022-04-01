package com.odk.connect.service;

import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.model.Media;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Service

public interface MediaService {
	 Media addMedia(String titre,Long idUser,MultipartFile mediaImage) throws ForumException, IOException, NotAnImageFileException;
	 List<Media>findAllMediaByUserId(Long id) throws ForumException;
	 List<Media>findAllByAdminAndFormateur();
	 List<Media>findAllByAlum();
	 List<Media>findAllMedia();
	 List<Media>getMediaByWeek() throws ForumException;
	 List<Media>getMediaByMonth() throws ForumException;
	 List<Media>getMediaBetweenDate(LocalDate dateDebut, LocalDate dateFin);
}
