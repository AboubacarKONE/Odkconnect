package com.odk.connect.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
//@EqualsAndHashCode(callSuper = true)
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String photoUrl;
    @ManyToOne
    private CategoryForum categoryForum;
    @ManyToOne
    private User user;
//    @OneToMany(mappedBy = "quiz")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private List<Reponse>reponses;
}
