package ru.brigader.cottageCatalogParser.parser;

import lombok.extern.slf4j.Slf4j;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.model.ImageHouse;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.ImageType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class HouseImageDownloader {

    final String fileFormat = ".jpg";

    public House saveImage(House house) {
        for (int i = 0; i < house.getImageHouseList().size(); i++) {
            ImageHouse imageHouse = house.getImageHouseList().get(i);
            String fullPath = house.getDirSaveImages() + generateName(house.getId(), house.getTitleEng(),
                    imageHouse.getImageType(), i);
            downloadImage(fullPath, imageHouse.getUrlSource());
            house.getImageHouseList().get(i).setPath(fullPath);
        }
        return house;
    }

    private String generateName(int id, String titleEng, ImageType imageType, int i) {
        String name = imageType.toString().toLowerCase() + "/" + id + "_" + titleEng + "_"
                + imageType.toString().toLowerCase() + "_" + i + fileFormat;
        return name;
    }

    private void downloadImage(String fullPath, String url) {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(fullPath));
        } catch (IOException e) {
            log.info("Не удалось скачать картинку с url " + url);
            e.getMessage();
        }
    }

}
