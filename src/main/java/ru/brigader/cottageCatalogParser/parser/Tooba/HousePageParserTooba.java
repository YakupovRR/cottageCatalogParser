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

import java.util.concurrent.atomic.AtomicInteger;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;


@Slf4j
@Component
@Setter
public class HousePageParserTooba implements HousePageParser {

    protected CsvIO csvIO;
    protected JsonIO jsonIO = new JsonIO();

    @Override
    public LinkedList<House> startParse(int id, ExecutorService executorService) {
        LinkedList<House> houses = new LinkedList<>();
        LinkedList<String> urls = csvIO.outputCsv("url.csv");
        LinkedList<String> titles = csvIO.outputCsv("title.csv");
        AtomicInteger fallenSaved = new AtomicInteger(0);
        for (int i = 0; i < urls.size(); i++) {
            final int idCopy = id;
            int finalI = i;
            executorService.submit(() -> {
                House house = new House();
                house.setId(idCopy);
                house.setTitle(titles.get(finalI));
                house.setUrlSource(urls.get(finalI));
                house.setTitleEng(transliterate(house.getTitle()));
                log.info(idCopy + " Парсинг проекта " + house.getUrlSource());
                try {
                    house = parse(house);
                    if (house != null) {
                        houses.add(house);
                    } else {
                        fallenSaved.incrementAndGet();
                    }
                } catch (Exception e) {
                    log.warn("Не сохранён проект: " + house.getUrlSource(), e);
                    fallenSaved.incrementAndGet();
                }
            });
            id++;
        }
        return houses;
    }

    private House parse(House house) {
        try {
            Document document = Jsoup.connect(house.getUrlSource()).get();
            ParametersHouseTooba parametersHouseTooba = new ParametersHouseTooba();
            house = parametersHouseTooba.parseParametersHouse(document, house);
            if (!checkingRequiredParameters(house)) {
                log.warn("Проект " + house.getTitle() + " не сохранен - отсуствуют обязательные параметры");
                return null;
            }
            ParametersRoomsTooba parametersRoomsTooba = new ParametersRoomsTooba();
            ImagesParserTooba imagesParserTooba = new ImagesParserTooba();
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

