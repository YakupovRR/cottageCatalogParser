package ru.brigader.cottageCatalogParser.parser.Tooba;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.brigader.cottageCatalogParser.exception.ParseException;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.*;

@Slf4j
public class ParametersHouseTooba {

    protected House parseParametersHouse(Document document, House house) {

        try {

            //жилая площадь
            String cssQueryLivingArea = "span.label:contains(Pow. użytkowa) + span.val";
            house.getDimensions().setLivingArea(extractDoubleValue(parseParameterString(document, cssQueryLivingArea)));

            //этажность
            String cssQueryFullFloors = "span.label:contains(Kondygnacje) + span.val";
            String cssQueryHasMansard = "span.label:contains(Poddasze użytkowe) + span.val";
            String fullFloors = parseParameterString(document, cssQueryFullFloors);
            String hasMansardString = parseParameterString(document, cssQueryHasMansard);
            Boolean hasMansard = extractBooleanValue(hasMansardString);
            house.getDimensions().setFloors(findFloors(fullFloors, hasMansard));

            //высота
            String cssQueryHeight = "span.label:contains(Wysokość domu) + span.val";
            house.getDimensions().setHeight(extractDoubleValue(parseParameterString(document, cssQueryHeight)));

            //ширина
            String cssQueryWidth = "span.label:contains(Szerokość domu) + span.val";
            house.getDimensions().setWidth(extractDoubleValue(parseParameterString(document, cssQueryWidth)));

            //глубина
            String cssQueryDepth = "span.label:contains(Długość domu) + span.val";
            house.getDimensions().setDepth(extractDoubleValue(parseParameterString(document, cssQueryDepth)));

            //тип крыши
            String cssQueryRoofType = "span.label:contains(Rodzaj dachu) + span.val";
            String roofTypeOrg = parseParameterString(document, cssQueryRoofType);
            RoofType roofType = findRoofType(roofTypeOrg);
            house.getRoof().setRoofType(roofType);
            if (roofType == RoofType.UNDEFINEDROOFTYPE) house.getRoof().setRoofTypeOrg(roofTypeOrg);

            //угол наклона крыши
            String cssQueryRoofAngle = "span.label:contains(Nachylenie dachu) + span.val";
            house.getRoof().setRoofAngle(extractIntegerValue(parseParameterString(document, cssQueryRoofAngle)));

            // площадь крыши
            String cssQueryRoofArea = "span.label:contains(Powierzchnia dachu) + span.val";
            house.getRoof().setRoofArea(extractDoubleValue(parseParameterString(document, cssQueryRoofArea)));

            //технология (материал) строительства
            String cssTechnology = "span.label:contains(Technologia) + span.val";
            Element elementTechnology = document.selectFirst(cssTechnology);
            if (elementTechnology != null) {
                String technologyOrg = elementTechnology.text();
                Technology technology = findTechnology(technologyOrg);
                house.getArchitecture().setTechnology(technology);
                if (technology == Technology.UNDEFINEDTECHNOLOGY) house.getArchitecture().setTechnologyOrg(technologyOrg);
            }
            //стиль дома
            String cssQueryArchitectureStyle = "span.label:contains(Styl) + span.val";
            Element elementArchitectureStyle = document.selectFirst(cssQueryArchitectureStyle);
            if (elementArchitectureStyle != null) {
                String styleOrg = elementArchitectureStyle.text();
                ArchitectureStyle architectureStyle = findArchitectureStyle(styleOrg);
                house.getArchitecture().setArchitectureStyle(architectureStyle);
                if (architectureStyle == ArchitectureStyle.UNDEFINEDSTYLE) house.getArchitecture().setArchitectureStyleOrg(styleOrg);
            }

            // количество комнат
            String cssQueryRoomsNumber = "span.label:contains(Liczba pokoi) + span.val";
            house.getArchitecture().setRooms(extractIntegerValue(parseParameterString(document, cssQueryRoomsNumber)));

            // количество ванных комнат
            String cssQueryBathroomsNumber = "span.label:contains(Liczba łazienek) + span.val";
            house.getArchitecture().setBathrooms(extractIntegerValue(parseParameterString(document, cssQueryBathroomsNumber)));

            // камин
            String cssQueryFireplace = "span.label:contains(Kominek) + span.val";
            house.getOptions().setHasFireplace(extractBooleanValue(parseParameterString(document, cssQueryFireplace)));

            // гараж
            String cssQueryGarage = "span.label:contains(Garaż) + span.val";
            Garage garage = findGarage(parseParameterString(document, cssQueryGarage));
            if (garage == Garage.NON || garage == Garage.UNDEFINEDGARAGE) house.getOptions().setHasGarage(false);
            house.getOptions().setGarage(garage);

            // подвал
            String cssQueryBasement = "span.label:contains(Piwnica) + span.val";
            house.getOptions().setHasBasement(extractBooleanValue(parseParameterString(document, cssQueryBasement)));

            //эксплуатируемый чердак
            String cssQueryLoft = "span.label:contains(Poddasze do adaptacji) + span.val";
            house.getOptions().setOperatedLoft(extractBooleanValue(parseParameterString(document, cssQueryLoft)));

            // рейтинг
            String cssQueryRating = ".ocena-value.average";
            house.getProjectRating().setRating(extractDoubleValue(parseParameterString(document, cssQueryRating)));

            //проголосовавшие в рейтинге
            String cssQueryVoted = ".ocena-value.average";
            house.getProjectRating().setVoted(extractIntegerValue(parseParameterString(document, cssQueryVoted)));

            //описание дома
            house.setDescriptionOrg(extractDescriptionFromPage(document));
        } catch (ParseException e) {
            log.error("Ошибка при парсинге параметров дома");
            e.printStackTrace();
        }
        return house;
    }

    private Double extractDoubleValue(String input) {
        try {
            String[] parts = input.split(" ");
            String numberString = parts[0].replace(",", ".");
            return Double.parseDouble(numberString);
        } catch (NumberFormatException | NullPointerException e) {
            log.error("Не удалось извлечь значение из строки " + input);
            return null;
        }
    }

    private Integer extractIntegerValue(String input) {
        try {
            String numericString = input.replaceAll("\\D", "");
            return Integer.parseInt(numericString);
        } catch (NumberFormatException | NullPointerException e) {
            log.error("Не удалось извлечь значение из строки " + input);
            return null;
        }
    }

    private Boolean extractBooleanValue(String input) {

        try {
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
        } catch (NullPointerException e) {
            log.error("Не удалось извлечь значение из строки " + input);
            return false;
        }
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
        RoofType roofType = RoofType.UNDEFINEDROOFTYPE;
        if (input != null) {
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
            case "nowoczesny":
                architectureStyle = ArchitectureStyle.MODERN;
                break;
            case "tradycyjny":
                architectureStyle = ArchitectureStyle.CLASSIC;
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

    private String parseParameterString(Document document, String cssQuery) {
        try {
            Element element = document.selectFirst(cssQuery);
            String parameter = null;
            if (element != null) parameter = element.text();
            return parameter;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private String extractDescriptionFromPage(Document document) {
        String description = "";
        try {
            Element descriptionDiv = document.selectFirst("div#descOpisTooba.description");
            if (descriptionDiv != null) {
                description = descriptionDiv.html();
            }
        } catch (ParseException e) {
            log.warn("Не удалось получить описание проекта");
            e.printStackTrace();
        }
        return description;
    }


}
