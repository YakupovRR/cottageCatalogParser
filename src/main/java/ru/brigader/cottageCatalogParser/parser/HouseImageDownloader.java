package ru.brigader.cottageCatalogParser.parser;

import lombok.extern.slf4j.Slf4j;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.model.Parameters.ImageType;

@Slf4j
public class HouseImageDownloader {



    public void saveImage(House house) {
        final String fileFormat = ".jpg";




    }

    String generateName(int id, String titleEng, ImageType imageType, int i) {
        String name = id + "_" + titleEng + "_" + imageType.toString().toLowerCase() + "_" + i;
        log.info(name);
        return name;

    }


}
