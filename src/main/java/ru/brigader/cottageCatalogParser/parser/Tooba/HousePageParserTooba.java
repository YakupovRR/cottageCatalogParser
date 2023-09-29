package ru.brigader.cottageCatalogParser.parser.Tooba;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.brigader.cottageCatalogParser.dataIO.JsonIO;
import ru.brigader.cottageCatalogParser.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.ibm.icu.text.Transliterator;
import org.apache.commons.lang3.StringUtils;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.Floors;
import ru.brigader.cottageCatalogParser.parser.HousePageParser;
import ru.brigader.cottageCatalogParser.dataIO.CsvIO;


import java.io.IOException;
import java.util.*;


@Slf4j
@Component
@Setter
public class HousePageParserTooba implements HousePageParser {
    protected ParametersHouseTooba parametersHouseTooba = new ParametersHouseTooba();
    protected ParametersRoomsTooba parametersRoomsTooba = new ParametersRoomsTooba();

    protected ImagesParserTooba imagesParserTooba = new ImagesParserTooba();

    protected CsvIO csvIO;
    protected JsonIO jsonIO = new JsonIO();


    @Override
    public LinkedList<House> startParse() {
        LinkedList<House> houses = new LinkedList<>();

        LinkedList<String> urls = csvIO.outputCsv("url.csv");
        LinkedList<String> titles = csvIO.outputCsv("title.csv");

        int id = 1;
        int fallenSaved = 0;
        for (int i = 0; i < urls.size(); i++) {
            House house = new House();
            house.setId(id);
            house.setTitle(titles.get(i));
            house.setUrlSource(urls.get(i));
            house.setTitleEng(transliterate(house.getTitle()));
            log.info(id + " Парсинг проекта " + house.getUrlSource());
            house = parse(house);
            if (house != null) {
                houses.add(house);
                id++;
            } else {
                fallenSaved++;
            }
        }
        log.info("Не удалось сохранить проектов: " + fallenSaved);
        log.info(houses.toString());
        jsonIO.inputJson(houses);

        return houses;
    }


    public House parse(House house) {

        try {
            Document document = Jsoup.connect(house.getUrlSource()).get();
            house = parametersHouseTooba.parseParametersHouse(document, house);
            if (!checkingRequiredParameters(house)) {
                log.warn("Проект " + house.getTitle() + "не сохранен - отсуствуют обязательные параметры");
                return null;
            }
            house = parametersRoomsTooba.parseParametersRooms(document, house);
            house = imagesParserTooba.parseImages(document, house);

        } catch (
                IOException e) {
            log.error("Ошибка при парсинге проекта");
            return null;
        }
        return house;
    }

    private String transliterate(String rusText) {
        try {
            Transliterator transliterator = Transliterator.getInstance("Russian-Latin/BGN");
            String engText = transliterator.transliterate(rusText);
            return StringUtils.stripAccents(engText);
        } catch (Exception e) {
            log.error("Не удалось получить английское название дома");
            return null;
        }
    }

    private boolean checkingRequiredParameters(House house) {
        boolean checkPassed = true;
        if (house.getDimensions().getWidth() == null ||
                house.getDimensions().getDepth() == null ||
                house.getDimensions().getFloors().equals(Floors.UNDEFINEDFLOOR))
            checkPassed = false;
        return checkPassed;
    }
}

