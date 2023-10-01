package ru.brigader.cottageCatalogParser;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.brigader.cottageCatalogParser.database.HouseDb;
import ru.brigader.cottageCatalogParser.database.HouseRepository;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.parser.*;
import ru.brigader.cottageCatalogParser.parser.Tooba.HousePageParserTooba;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// TODO: 22.08.2023 Проверить как работает JSON если уже есть сохраненные проекты
// TODO: 22.08.2023 Создание таблицы из программы

@SpringBootApplication
@Slf4j
public class CottageCatalogParserApplication {

    private static HouseLinkExtractor houseLinkExtractor = new HouseLinkExtractor();
    public static HousePageParser housePageParser = new HousePageParserTooba();
    private static HouseDb houseDb = new HouseDb();
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);


    public static void main(String[] args) throws SQLException {
        SpringApplication.run(CottageCatalogParserApplication.class, args);


        //     houseLinkExtractor.saveLinksToFile();

        Integer id = houseDb.getLastProjectId() + 1;
        log.info("Номер нового проекта " + id);
        LinkedList<House> houses = housePageParser.startParse(id, executorService);
        houseDb.saveProjectDb(houses);
        executorService.shutdown();

    }
}