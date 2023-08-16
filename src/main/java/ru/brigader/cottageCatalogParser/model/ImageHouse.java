package ru.brigader.cottageCatalogParser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.brigader.cottageCatalogParser.model.Parameters.ImageType;

@Getter
@Setter
@ToString
public class ImageHouse {
    String path;
    ImageType imageType;
    ImageType imageTag;
    String imageTypeOrg;
    String urlSource;
}
