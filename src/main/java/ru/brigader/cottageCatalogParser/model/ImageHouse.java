package ru.brigader.cottageCatalogParser.model;

import lombok.Getter;
import lombok.Setter;
import ru.brigader.cottageCatalogParser.model.Parameters.ImageType;

@Getter
@Setter
public class ImageHouse {
    String path;
    ImageType imageType;
    String imageTypeOrg;
    String urlSource;
}
