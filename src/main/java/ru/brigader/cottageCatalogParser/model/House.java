package ru.brigader.cottageCatalogParser.model;

import lombok.*;

import javax.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.brigader.cottageCatalogParser.model.Parameters.*;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.*;

import javax.persistence.Transient;
import java.util.List;

@Repository
@Getter
@Setter
@ToString
@Entity
@Table(name = "projects")
public class House {

    @Autowired
    public House() {
        this.dimensions = new Dimensions();
        this.roof = new Roof();
        this.options = new Options();
        this.projectRating = new ProjectRating();
        this.architecture = new Architecture();
    }

    @Id
    @Column(name = "idProject")
    int id;
    String title;
    String titleEng;
    String urlSource;
    String descriptionOrg;
    String dirSaveImages;

    Dimensions dimensions;
    Roof roof;
    Options options;
    ProjectRating projectRating;
    Architecture architecture;

    @Transient
    List<SignatureLayout> signatureLayoutList; // размеры помещений из таблицы около картинок
    @Transient
    List<ImageHouse> imageHouseList;

}
