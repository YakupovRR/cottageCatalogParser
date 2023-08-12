package ru.brigader.cottageCatalogParser.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.brigader.cottageCatalogParser.model.Image.ImageHouse;
import ru.brigader.cottageCatalogParser.model.houseParameters.*;

import java.util.List;

@Getter
@Setter
@ToString
public class House {

    int id;  ///
    String title; ///
    String titleEng;//перевод русского названия  ///
    List<ImageHouse> imageHouseList;
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
    List<SignatureLayout> signatureLayoutList; // размеры помещений из таблицы около картинок
    @Builder.Default
    Boolean hasGarage;
    Garage garage;
    @Builder.Default
    Boolean hasBasement;
    @Builder.Default
    Boolean hasFireplace;
    @Builder.Default
    boolean operatedRoof = false;
    @Builder.Default
    boolean operatedLoft = false; //эксплуатируемый чердак - как мансарда, но без комнат
    String urlSource;
    Double rating;
    Integer voted;
}
