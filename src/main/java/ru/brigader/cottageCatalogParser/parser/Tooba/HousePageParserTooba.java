package ru.brigader.cottageCatalogParser.parser.Tooba;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Component;
import ru.brigader.cottageCatalogParser.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.ibm.icu.text.Transliterator;
import org.apache.commons.lang3.StringUtils;
import ru.brigader.cottageCatalogParser.model.Parameters.*;
import ru.brigader.cottageCatalogParser.parser.HousePageParser;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;


@Slf4j
@Component
@Setter
public class HousePageParserTooba implements HousePageParser {
    protected ParametersHouseTooba parametersHouseTooba = new ParametersHouseTooba();
    protected ParametersRoomsTooba parametersRoomsTooba = new ParametersRoomsTooba();

    protected ImagesParserTooba imagesParserTooba = new ImagesParserTooba();



    @Override
    public House parse(House house) {

        house.setTitleEng(transliterate(house.getTitle()));

        try {
            Document document = Jsoup.connect(house.getUrlSource()).get();
            house = parametersHouseTooba.parseParametersHouse(document, house);
           if (!checkingRequiredParameters(house)) {
               log.warn("Проект " +house.getTitle() + "не сохранен - отсуствуют обязательные параметры");
               return null;
           }
           house = parametersRoomsTooba.parseParametersRooms(document, house);
            //    house = imagesParserTooba.parseImages(document, house);

        } catch (IOException e) {
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
        if (house.getWidth() == null || house.getDepth() == null || house.getFloors().equals(Floors.UNDEFINEDFLOOR))
            checkPassed = false;
        return checkPassed;
    }
}

