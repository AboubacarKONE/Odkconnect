package com.odk.connect.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
//@EqualsAndHashCode(callSuper = true)
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String fileName;
    private LocalDate  date = LocalDate.now();
    private String photoUrl;
    @ManyToOne
    private User user;
}
