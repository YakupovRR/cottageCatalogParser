package ru.brigader.cottageCatalogParser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.parser.*;
import ru.brigader.cottageCatalogParser.parser.Tooba.HousePageParserTooba;

import java.util.LinkedList;


@SpringBootApplication
@Slf4j
public class CottageCatalogParserApplication {

    private static HouseLinkExtractor houseLinkExtractor;
    private static ReadCSV readCSV;
    public static HousePageParser housePageParser = new HousePageParserTooba();

    @Autowired
    public CottageCatalogParserApplication(HouseLinkExtractor houseLinkExtracto) {
        this.houseLinkExtractor = houseLinkExtractor;
    }

    public static void main(String[] args) {
        SpringApplication.run(CottageCatalogParserApplication.class, args);

        //сохранение ссылок на проекты
        //houseLinkExtractor.saveLinksToFile();

        LinkedList<String> urls = readCSV.readFromFile("url.csv");
        LinkedList<String> titles = readCSV.readFromFile("title.csv");
        int id = 1;
        int fallenSaved = 0;

        for (int i = 0; i < urls.size(); i++) {
            House house = new House();
            house.setId(id);
            house.setTitle(titles.get(i));
            house.setUrlSource(urls.get(i));
            log.info(id + " Парсинг проекта " + house.getUrlSource());
            house = housePageParser.parse(house);
            if (house != null) {
                log.info(house.toString());
            //сохранние в БД
                id++;
            } else {
                fallenSaved ++;
            }
        }
        log.info("Не удалось сохранить проектов: " + fallenSaved);
    }
}
