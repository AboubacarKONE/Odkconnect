package com.odk.connect.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.model.Question;
import jdk.jfr.Category;
import org.springframework.stereotype.Service;


@Service
public interface QuestionService {

    Question ajouterQuestion(String description, Long idUser, Long idCategory, MultipartFile quizImage) throws ForumException, IOException, NotAnImageFileException;
    List<Question>getAllQuizForum();
    List<Question>findAllQuizByCategorie(Long id) throws ForumException;
    Question updateQuiz(Long idQuiz, String description, MultipartFile quizImage)throws ForumException, IOException, NotAnImageFileException;
    void supprimerQuestion(Long id) throws ForumException;
}
