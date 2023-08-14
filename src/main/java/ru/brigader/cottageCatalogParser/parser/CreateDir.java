package ru.brigader.cottageCatalogParser.parser;

import lombok.extern.slf4j.Slf4j;
import ru.brigader.cottageCatalogParser.model.Parameters.ImageType;

import java.io.File;

@Slf4j
public class CreateDir {


    public String createAllDir(int id, String titleEng) {

        String dirFirstPart = "c:" + "/" + "Houses" + "/" + id + "_" + titleEng + "/";
        File dirBase = new File(dirFirstPart);
        createThisDir(dirBase);
        for (ImageType i : ImageType.values()) {
            String dirSecondPart = dirFirstPart + i.toString().toLowerCase();
            File dirImage = new File(dirSecondPart);
            createThisDir(dirImage);
        }
        return dirFirstPart;
    }

    private void createThisDir(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.warn("Kaтaлoг " + dir.getAbsolutePath()
                        + " coздать нe yдaлocь.");
            }
        } else {
            log.info("Kaтaлoг " + dir.getAbsolutePath()
                    + " yжe cyщecтвyeт.");
        }
    }

}
