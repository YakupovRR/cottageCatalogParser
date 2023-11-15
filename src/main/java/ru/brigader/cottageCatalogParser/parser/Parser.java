package ru.brigader.cottageCatalogParser.parser;

import lombok.extern.slf4j.Slf4j;
import ru.brigader.cottageCatalogParser.database.HouseDb;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.parser.Tooba.ParserFromSiteTooba;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Parser {

    public static ParserFromSite parserFromSite = new ParserFromSiteTooba();
    private static HouseDb houseDb = new HouseDb();
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);

    public void startParse () throws SQLException {

        Integer id = houseDb.getLastProjectId() + 1;
        log.info("Номер стартового проекта " + id);
        LinkedList<House> houses = parserFromSite.parseProjects(id, executorService);
        houseDb.saveProjectDb(houses);
        executorService.shutdown();
    }
}
