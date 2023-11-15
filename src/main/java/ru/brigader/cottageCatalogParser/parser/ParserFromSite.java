package ru.brigader.cottageCatalogParser.parser;

import org.springframework.stereotype.Component;
import ru.brigader.cottageCatalogParser.model.House;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;


@Component
public interface ParserFromSite {
    LinkedList<House> parseProjects(int id, ExecutorService executorService);

}
