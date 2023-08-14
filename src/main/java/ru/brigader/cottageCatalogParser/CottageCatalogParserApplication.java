package ru.brigader.cottageCatalogParser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.parser.HouseImageDownloader;
import ru.brigader.cottageCatalogParser.parser.*;

// TODO: 13.08.2023 Подумать, может создавать директории только для найденных типов картинок 

@SpringBootApplication
@Slf4j
public class CottageCatalogParserApplication {

    private static HouseLinkExtractor houseLinkExtractor;
    private static ReadCSV readCSV;
    public static HousePageParser housePageParser = new HousePageParserTooba();
    public static HouseImageDownloader houseImageDownloader = new HouseImageDownloader();
    public static CreateDir createDir = new CreateDir();

    @Autowired
    public CottageCatalogParserApplication(HouseLinkExtractor houseLinkExtracto) {
        this.houseLinkExtractor = houseLinkExtractor;
    }

    public static void main(String[] args) {
        SpringApplication.run(CottageCatalogParserApplication.class, args);

        //сохранение ссылок на проекты
        //houseLinkExtractor.saveLinksToFile();
        // LinkedList<String> urls = readCSV.readFromFile("url.csv");
        // LinkedList<String> title = readCSV.readFromFile("title.csv");

        int id = 1;


        House house = new House();
        house.setId(id);
        house.setTitle("Пробный");
        house.setUrlSource("https://www.tooba.pl/projekt-domu-BW-49-wariant-11-bez-garazu-TXF-289");
        house = housePageParser.parse(house);
        house.setDirSaveImages(createDir.createAllDir(house.getId(), house.getTitleEng()));
        houseImageDownloader.saveImage(house);
//        log.info(String.valueOf(house));


    }


}
