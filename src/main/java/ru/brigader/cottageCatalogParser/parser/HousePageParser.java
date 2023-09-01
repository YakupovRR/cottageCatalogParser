package ru.brigader.cottageCatalogParser.parser;

import org.springframework.stereotype.Component;
import ru.brigader.cottageCatalogParser.model.House;

import java.util.LinkedList;


@Component
public interface HousePageParser {
    LinkedList<House> startParse();

}
