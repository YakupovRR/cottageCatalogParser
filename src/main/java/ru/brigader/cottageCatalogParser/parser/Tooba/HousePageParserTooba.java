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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
        int[] ids = generateIdsArray(id, urls.size());

        AtomicInteger fallenSaved = new AtomicInteger(0);

        List<House> parsedHouses = parseAll(urls, executorService, titles, ids);

        for (House house : parsedHouses) {
            if (house != null) {
                house.setId(id);
                houses.add(house);
                id++;
            } else {
                fallenSaved.incrementAndGet();
            }
        }
        return houses;
    }


    public List<House> parseAll(List<String> urls, ExecutorService executorService, List<String> titles, int[] ids) {
        List<House> result = new ArrayList<>();
        List<Future<House>> futures = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            final String url = urls.get(i);
            final String title = titles.get(i);
            final int id = ids[i];

            Future<House> future = executorService.submit(() -> parse(url, title, id));
            futures.add(future);
        }

        for (Future<House> future : futures) {
            try {
                House house = future.get();
                if (house != null) {
                    result.add(house);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Ошибка в методе parseAll");
            }
        }

        executorService.shutdown();

        return result;
    }

    private House parse(String url, String title, int id) {
        House house = new House();
        house.setId(id);
        house.setTitle(title);
        house.setTitleEng(transliterate(title));
        try {
            Document document = Jsoup.connect(url).get();
            ParametersHouseTooba parametersHouseTooba = new ParametersHouseTooba();
            house = parametersHouseTooba.parseParametersHouse(document, house);
            if (!checkingRequiredParameters(house)) {
                log.warn("Проект " + house.getTitle() + " не сохранен - отсутствуют обязательные параметры");
                return null;
            }
            ParametersRoomsTooba parametersRoomsTooba = new ParametersRoomsTooba();
            ImagesParserTooba imagesParserTooba = new ImagesParserTooba();
            house = parametersRoomsTooba.parseParametersRooms(document, house);
            house = imagesParserTooba.parseImages(document, house);
        } catch (IOException e) {
            log.error("Ошибка при парсинге проекта", e);
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


    private int[] generateIdsArray(int id, int length) {
        int[] ids = new int[length];
        ids[0] = id;

        for (int i = 1; i < length; i++) {
            ids[i] = id + i;
        }

        return ids;
    }


}

