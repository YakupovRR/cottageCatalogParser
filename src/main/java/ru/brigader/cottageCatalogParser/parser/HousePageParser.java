package ru.brigader.cottageCatalogParser.parser;

import org.springframework.stereotype.Component;
import ru.brigader.cottageCatalogParser.model.House;


@Component
public interface HousePageParser {


    House parse(House house);


}
