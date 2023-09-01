package ru.brigader.cottageCatalogParser.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import org.springframework.stereotype.Repository;
import ru.brigader.cottageCatalogParser.model.Parameters.*;

import javax.persistence.Transient;
import java.util.List;

@Repository
@Getter
@Setter
@ToString
@Entity
@Table(name = "projects")
public class House {

    @Id
    @Column(name = "idProject")
    int id;
    String title;
    String titleEng;
    Double livingArea;
    Double width;
    Double depth;
    Double height;
    Integer roofAngle;
    RoofType roofType;
    String roofTypeOrg;
    Double roofArea;
    Floors floors;
    ArchitectureStyle architectureStyle;
    String ArchitectureStyleOrg;
    Technology technology;
    String technologyOrg;
    Integer rooms;
    Integer bathrooms;
    @Builder.Default
    boolean hasGarage;
    Garage garage;
    @Builder.Default
    boolean hasBasement;
    @Builder.Default
    boolean hasFireplace;
    @Builder.Default
    boolean operatedRoof = false;
    @Builder.Default
    boolean operatedLoft = false; //эксплуатируемый чердак - как мансарда, но без комнат
    Double rating;
    Integer voted;
    String urlSource;
    String descriptionOrg;
    String dirSaveImages;
    @Transient
    List<SignatureLayout> signatureLayoutList; // размеры помещений из таблицы около картинок
    @Transient
    List<ImageHouse> imageHouseList;

    //List <ImageType> foundedImageTypes;

}
