package ru.brigader.cottageCatalogParser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.parser.HouseLinkExtractor;
import ru.brigader.cottageCatalogParser.parser.HousePageParser;
import ru.brigader.cottageCatalogParser.parser.HousePageParserTooba;
import ru.brigader.cottageCatalogParser.parser.ReadCSV;

import java.util.LinkedHashMap;
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
        LinkedList<String> title = readCSV.readFromFile("title.csv");



        House house = new House();
        house.setId(1);
        house.setTitle("Пробный");
        house.setUrlSource("https://www.tooba.pl/projekt-domu-BW-49-wariant-11-bez-garazu-TXF-289");
        house = housePageParser.parse(house);
        log.info(String.valueOf(house));
    }


}
