package ru.brigader.cottageCatalogParser.model.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageHouse {
    String path;
    ImageType imageType;
    String imageTypeOrg;
    String urlSource;
}
