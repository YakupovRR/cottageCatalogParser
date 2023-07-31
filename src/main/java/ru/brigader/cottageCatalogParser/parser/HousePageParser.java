package ru.brigader.cottageCatalogParser.parser;

import ru.brigader.cottageCatalogParser.model.House;

public interface HousePageParser {
    House parse(Integer id, String url, String title);

}
