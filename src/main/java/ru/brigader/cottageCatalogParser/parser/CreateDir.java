package ru.brigader.cottageCatalogParser.parser;

import lombok.extern.slf4j.Slf4j;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.ImageType;

import java.io.File;
import java.util.HashSet;

@Slf4j
public class CreateDir {


    public String createAllDir(String imagesFolderPath, HashSet<ImageType> imageTypes) {

        String dirProject = "c:" + "/" + "Houses" + "/" + imagesFolderPath + "/";
        File dirBase = new File(dirProject);
        createThisDir(dirBase);
        for (ImageType i : imageTypes) {
            String dirSecondPart = dirProject + i.toString().toLowerCase();
            File dirImage = new File(dirSecondPart);
            createThisDir(dirImage);
        }
        return dirProject;
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
