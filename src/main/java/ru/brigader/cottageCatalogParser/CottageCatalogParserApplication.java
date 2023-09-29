package ru.brigader.cottageCatalogParser;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.brigader.cottageCatalogParser.database.HouseRepository;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.parser.*;
import ru.brigader.cottageCatalogParser.parser.Tooba.HousePageParserTooba;
import java.util.List;


// TODO: 22.08.2023 Проверить как работает JSON если уже есть сохраненные проекты
// TODO: 22.08.2023 Создание таблицы из программы
// TODO: 22.08.2023 Разобраться с boolean в house 

@SpringBootApplication
@Slf4j
public class CottageCatalogParserApplication {

    private static HouseLinkExtractor houseLinkExtractor;
    public static HousePageParser housePageParser = new HousePageParserTooba();

    @Autowired
    public CottageCatalogParserApplication(HouseLinkExtractor houseLinkExtracto) {
        this.houseLinkExtractor = houseLinkExtractor;
    }

    public static void main(String[] args) {
        SpringApplication.run(CottageCatalogParserApplication.class, args);




        //сохранение ссылок на проекты
       // houseLinkExtractor.saveLinksToFile();
       housePageParser.startParse();
    }
}