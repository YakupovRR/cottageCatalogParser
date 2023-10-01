package ru.brigader.cottageCatalogParser.parser;

import org.springframework.stereotype.Component;
import ru.brigader.cottageCatalogParser.model.House;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;


@Component
public interface HousePageParser {
    LinkedList<House> startParse(int id, ExecutorService executorService);

}
