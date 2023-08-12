package ru.brigader.cottageCatalogParser.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.brigader.cottageCatalogParser.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.brigader.cottageCatalogParser.model.Image.ImageDownload;
import ru.brigader.cottageCatalogParser.model.Image.ImageHouse;
import ru.brigader.cottageCatalogParser.model.Image.ImageType;
import com.ibm.icu.text.Transliterator;
import org.apache.commons.lang3.StringUtils;
import ru.brigader.cottageCatalogParser.model.houseParameters.*;


import java.io.IOException;
import java.util.*;


@Slf4j
@Component
public class HousePageParserTooba implements HousePageParser {

    ImageDownload imageDownload = new ImageDownload();

    @Override
    public House parse(House house) {

     //   House house = new House();
      //  house.setId(id);
      //  house.setTitle(title);
        house.setTitleEng(transliterate(house.getTitle()));
       String url = house.getUrlSource();


        try {
            Document document = Jsoup.connect(url).get();

            LinkedList<ImageHouse> imageHouseList = new LinkedList<>();

           /*
            imageHouseList = addAndSave(imageHouseList, parseFloorsAndModel3DImages(document), id, title);
            imageHouseList = addAndSave(imageHouseList, parseSituationalPlan(document), id, title);
            imageHouseList = addAndSave(imageHouseList, parseFacades(document), id, title);
            imageHouseList = addAndSave(imageHouseList, parseSection(document), id, title);
            house.setImageHouseList(imageHouseList);
*/
            //жилая площадь
            String cssQueryLivingArea = "span.label:contains(Pow. użytkowa) + span.val";
            house.setLivingArea(extractDoubleValue(parseParameterString(document, cssQueryLivingArea)));

            //этажность
            String cssQueryFullFloors = "span.label:contains(Kondygnacje) + span.val";
            String cssQueryHasMansard = "span.label:contains(Poddasze użytkowe) + span.val";
            String fullFloors = parseParameterString(document, cssQueryFullFloors);
            String hasMansardString = parseParameterString(document, cssQueryHasMansard);
            Boolean hasMansard = extractBooleanValue(hasMansardString);
            house.setFloors(findFloors(fullFloors, hasMansard));

            //высота
            String cssQueryHeight = "span.label:contains(Wysokość domu) + span.val";
            house.setHeight(extractDoubleValue(parseParameterString(document, cssQueryHeight)));

            //ширина
            String cssQueryWidth = "span.label:contains(Szerokość domu) + span.val";
            house.setWidth(extractDoubleValue(parseParameterString(document, cssQueryWidth)));

            //глубина
            String cssQueryDepth = "span.label:contains(Długość domu) + span.val";
            house.setDepth(extractDoubleValue(parseParameterString(document, cssQueryDepth)));

            //тип крыши
            String cssQueryRoofType = "span.label:contains(Rodzaj dachu) + span.val";
            String roofTypeOrg = parseParameterString(document, cssQueryRoofType);
            RoofType roofType = findRoofType(roofTypeOrg);
            house.setRoofType(roofType);
            if (roofType == RoofType.UNDEFINEDROOFTYPE) house.setRoofTypeOrg(roofTypeOrg);

            //угол наклона крыши
            String cssQueryRoofAngle = "span.label:contains(Nachylenie dachu) + span.val";
            house.setRoofAngle(extractIntegerValue(parseParameterString(document, cssQueryRoofAngle)));

            // площадь крыши
            String cssQueryRoofArea = "span.label:contains(Powierzchnia dachu) + span.val";
            house.setRoofArea(extractDoubleValue(parseParameterString(document, cssQueryRoofArea)));

            //технология (материал) строительства
            String cssTechnology = "span.label:contains(Technologia) + span.val";
            String technologyOrg = document.selectFirst(cssTechnology).text();
            Technology technology = findTechnology(technologyOrg);
            house.setTechnology(technology);
            if (technology == Technology.UNDEFINEDTECHNOLOGY) house.setTechnologyOrg(technologyOrg);


            //стиль дома
            String cssQueryArchitectureStyle = "span.label:contains(Styl) + span.val";
            String styleOrg = document.selectFirst(cssQueryArchitectureStyle).text();
            ArchitectureStyle architectureStyle = findArchitectureStyle(styleOrg);
            house.setArchitectureStyle(architectureStyle);
            if (architectureStyle == ArchitectureStyle.UNDEFINEDSTYLE) house.setArchitectureStyleOrg(styleOrg);

            // количество комнат
            String cssQueryRoomsNumber = "span.label:contains(Liczba pokoi) + span.val";
            house.setRooms(extractIntegerValue(parseParameterString(document, cssQueryRoomsNumber)));

            // количество ванных комнат
            String cssQueryBathroomsNumber = "span.label:contains(Liczba łazienek) + span.val";
            house.setBathrooms(extractIntegerValue(parseParameterString(document, cssQueryBathroomsNumber)));

            // камин
            String cssQueryFireplace = "span.label:contains(Kominek) + span.val";
            house.setHasFireplace(extractBooleanValue(parseParameterString(document, cssQueryFireplace)));

            // гараж
            String cssQueryGarage = "span.label:contains(Garaż) + span.val";
            Garage garage = findGarage(parseParameterString(document, cssQueryGarage));
            if (garage == Garage.NON || garage == Garage.UNDEFINEDGARAGE) house.setHasGarage(false);
            house.setGarage(garage);

            // подвал
            String cssQueryBasement = "span.label:contains(Piwnica) + span.val";
            house.setHasBasement(extractBooleanValue(parseParameterString(document, cssQueryBasement)));

            //эксплуатируемый чердак
            String cssQueryLoft = "span.label:contains(Poddasze do adaptacji) + span.val";
            house.setOperatedLoft(extractBooleanValue(parseParameterString(document, cssQueryLoft)));

            // рейтинг
            String cssQueryRating = ".ocena-value.average";
            house.setRating(extractDoubleValue(parseParameterString(document, cssQueryRating)));

            //проголосовавшие в рейтинге
            String cssQueryVoted = ".ocena-value.average";
            house.setVoted(extractIntegerValue(parseParameterString(document, cssQueryVoted)));

            //данные о планировке из подписей сбоку
            house.setSignatureLayoutList(parseSignatureRoomList(document));


        } catch (
                IOException e) {
            e.printStackTrace();
            // Обработка исключения при соединении с сайтом
        }

        return house;
    }
    private String parseParameterString(Document document, String cssQuery) {
        try {
            Element element = document.selectFirst(cssQuery);
            return element.text();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private LinkedList<ImageHouse> parseFloorsAndModel3DImages(Document document) {
        LinkedList<ImageHouse> images = new LinkedList<>();
        try {
            Elements imageWrappers = document.select("div.image-wrapper");
            for (Element imageWrapper : imageWrappers) {
                Element link = imageWrapper.selectFirst("a");
                Element caption = imageWrapper.selectFirst("div.highslide-caption");
                if (link != null) {
                    ImageHouse imageHouse = new ImageHouse();
                    imageHouse.setUrlSource(link.attr("href"));
                    if (caption != null) {
                        String imageTypeString = caption.text();
                        ImageType imageType = generateImageType(imageTypeString);
                        if (imageType.equals(ImageType.UNDEFINEDTYPE)) imageHouse.setImageTypeOrg(caption.text());
                        imageHouse.setImageType(imageType);
                    }
                    images.add(imageHouse);
                }
            }
            log.info("Картинок с планировкой этажей и 3d моделей " + images.size());
        } catch (NullPointerException e) {
            log.error("Ошибка при обработке элементов: " + e.getMessage());
        }
        return images;
    }

    private LinkedList<ImageHouse> parseSituationalPlan(Document document) {
        LinkedList<ImageHouse> imagesSituationalPlan = new LinkedList<>();
        String cssQuery = "h2.section-title:contains(Sytuacja) + div.rzut a";
        String attributeKey = "href";
        ImageType imageType = ImageType.SITUATIONALPLAN;
        log.info("Парсим картинки расположения на участке");

        return parseStandardImages(document, cssQuery, attributeKey, imageType);

    }

    private LinkedList<ImageHouse> parseFacades(Document document) {
        String cssQuery = "h2.section-title:contains(Elewacje) + div.elewacje a";
        String attributeKey = "href";
        ImageType imageType = ImageType.FACADE;
        log.info("Парсим фасады  на участке");
        return parseStandardImages(document, cssQuery, attributeKey, imageType);

    }

    private LinkedList<ImageHouse> parseSection(Document document) {
        String cssQuery = "h2.section-title:contains(Przekrój) + div.rzut a";
        String attributeKey = "href";
        ImageType imageType = ImageType.SECTION;
        log.info("Парсим разрезы на участке");

        return parseStandardImages(document, cssQuery, attributeKey, imageType);
    }
    private LinkedList<ImageHouse> parseStandardImages(Document document, String cssQuery,
                                                       String attributeKey, ImageType imageType) {
        LinkedList<ImageHouse> images = new LinkedList<>();
        log.info("запускаем стандартный парсер картинок");

        try {
            Elements selectedElement = document.select(cssQuery);
            for (Element e : selectedElement) {
                ImageHouse imageHouse = new ImageHouse();
                String url = e.attr(attributeKey);
                imageHouse.setUrlSource(url);
                imageHouse.setImageType(imageType);
                images.add(imageHouse);
            }
        } catch (NullPointerException e) {
            log.error("Ошибка при обработке элементов: " + e.getMessage());
        }
        return images;
    }
    private LinkedList<SignatureLayout> parseSignatureRoomList(Document document) {
        LinkedList<SignatureLayout> signatureLayoutList = new LinkedList<>();

        try {
            Elements roomRows = document.select("table.pomieszczenia tr");
            for (Element roomRow : roomRows) {
                String roomNumberString = roomRow.select(".pomLp").text();
                String roomTitle = roomRow.select(".pomNazwa").text();
                String roomAreaString = roomRow.select(".pomPow b").text();
                SignatureLayout signatureLayout = new SignatureLayout(roomTitle, parseRoomNumber(roomNumberString),
                        parseRoomArea(roomAreaString), roomNumberString, roomAreaString);
                signatureLayoutList.add(signatureLayout);
            }
            log.info("Лист параметров комнат " + signatureLayoutList.size());
        } catch (NumberFormatException e) {
            log.error("Ошибка при парсинге параметров комнат: " + e.getMessage());

        }
        return signatureLayoutList;
    }
    private Double parseRoomArea(String roomAreaString) {
        Double roomArea;
        try {
            roomAreaString = roomAreaString.replace(",", ".");
            roomArea = Double.parseDouble(roomAreaString);
        } catch (NumberFormatException e) {
            log.error("Ошибка при парсинге площади комнаты: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return roomArea;
    }
    private Integer parseRoomNumber(String roomNumberString) {
        Integer roomNumber;
        try {
            String cleanRoomNumberString = roomNumberString.substring(0, roomNumberString.length() - 1);
            roomNumber = Integer.parseInt(cleanRoomNumberString);
        } catch (NumberFormatException e) {
            log.error("Ошибка при парсинге номера комнаты: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return roomNumber;
    }
    private ImageType generateImageType(String type) {
        ImageType imageType = ImageType.UNDEFINEDTYPE;

        switch (type) {
            case ("Rzut parteru"):
                imageType = ImageType.LAYOUT;
                break;
            case ("Rzut poddasza"):
                imageType = ImageType.LAYOUT;
                break;
            case ("Rzut parteru 3D"):
                imageType = ImageType.MODEL3D;
                break;
            case ("Sytuacja"):
                imageType = ImageType.SITUATIONALPLAN;
                break;
            case ("Elewacje"):
                imageType = ImageType.FACADE;
                break;
        }
        return imageType;
    }

    private LinkedList<ImageHouse> addAndSave(LinkedList oldList, LinkedList savedList, Integer id, String title) {
        if (!savedList.isEmpty()) {
            oldList.addAll(imageDownload.saveImage(id, title, savedList));
        }
        return oldList;
    }
    private String transliterate(String rusText) {
        Transliterator transliterator = Transliterator.getInstance("Russian-Latin/BGN");
        String engText = transliterator.transliterate(rusText);
        return StringUtils.stripAccents(engText);
    }
    private Double extractDoubleValue(String input) {
        try {
            String[] parts = input.split(" ");
            String numberString = parts[0].replace(",", ".");
            return Double.parseDouble(numberString);
        } catch (NumberFormatException e) {
            log.error("Не удалось извлечь значение из строки " + input);
            return null;
        }
    }
    private Integer extractIntegerValue(String input) {
        try {
            String numericString = input.replaceAll("[^\\d]", "");
            return Integer.parseInt(numericString);
        } catch (NumberFormatException e) {
            log.error("Не удалось извлечь значение из строки " + input);
            return null;
        }
    }
    private Boolean extractBooleanValue(String input) {
        boolean b;
        switch (input) {
            case "tak":
                b = true;
                break;
            case "nie":
                b = false;
                break;
            default:
                log.error("Не удалось определить буллевое значение параметра " + input);
                b = false;
        }
        return b;
    }
    private Floors findFloors(String fullFloors, boolean hasMansard) {
        Floors floors;
        switch (fullFloors) {
            case "parterowy":
                if (hasMansard) {
                    floors = Floors.ONEPLUSMANSARD;
                } else floors = Floors.ONE;
                break;
            case "piętrowy":
                if (hasMansard) {
                    floors = Floors.TWOPLUSMANSARD;
                } else floors = Floors.TWO;
                break;
            default:
                floors = Floors.UNDEFINEDFLOOR;
        }
        return floors;
    }
    private RoofType findRoofType(String input) {
        RoofType roofType;
        switch (input) {
            case "dwuspadowy":
                roofType = RoofType.TWOSLOPES;
                break;
            case "kopertowy":
                roofType = RoofType.FOURSLOPES;
                break;
            case "wielospadowy":
                roofType = RoofType.COMPLICATED;
                break;
            case "płaski":
                roofType = RoofType.FLAT;
                break;
            default:
                roofType = RoofType.UNDEFINEDROOFTYPE;
        }
        return roofType;
    }
    private Technology findTechnology(String input) {
        Technology technology;
        switch (input) {
            case "murowany":
                technology = Technology.STONE;
                break;
            case "drewniany - szkielet":
                technology = Technology.FRAME;
                break;
            case "z bali":
                technology = Technology.WOODEN;
                break;
            default:
                technology = Technology.UNDEFINEDTECHNOLOGY;
        }
        return technology;
    }
    private ArchitectureStyle findArchitectureStyle(String input) {
        ArchitectureStyle architectureStyle;
        switch (input) {
            case "nowoczesna elewacja":
                architectureStyle = ArchitectureStyle.MODERN;
                break;
            case "tradycyjny":
                architectureStyle = ArchitectureStyle.CLASSIC;
                break;
            case "nowoczesny":
                architectureStyle = ArchitectureStyle.MODERN;
                break;
            case "górski":
                architectureStyle = ArchitectureStyle.MOUNTAIN;
                break;
            case "pałacyk":
                architectureStyle = ArchitectureStyle.WILLA;
                break;
            default:
                architectureStyle = ArchitectureStyle.UNDEFINEDSTYLE;
        }
        return architectureStyle;
    }
    private Garage findGarage(String input) {
        Garage garage;
        switch (input) {
            case "bez garażu":
                garage = Garage.NON;
                break;
            case "dwustanowiskowy":
                garage = Garage.DOUBLE;
                break;
            case "jednostanowiskowy":
                garage = Garage.SINGLE;
                break;
            case "więcej niż dwa auta":
                garage = Garage.MANY;
                break;
            default:
                garage = Garage.UNDEFINEDGARAGE;
        }
        return garage;
    }

}

