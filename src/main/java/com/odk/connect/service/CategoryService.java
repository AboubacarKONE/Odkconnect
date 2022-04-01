package com.odk.connect.service;


import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.odk.connect.exception.model.ForumException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.model.CategoryForum;

public interface CategoryService {
    CategoryForum ajouterCategory(String libelleCat,Long idUser,MultipartFile categoryImage) throws ForumException, IOException, NotAnImageFileException;
    List<CategoryForum>getAllCateforum();
    void supprimerCategory(Long id) throws ForumException;
}
