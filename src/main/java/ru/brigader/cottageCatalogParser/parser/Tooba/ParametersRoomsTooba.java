package ru.brigader.cottageCatalogParser.parser.Tooba;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.brigader.cottageCatalogParser.exception.ParseException;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.model.SignatureLayout;

import java.util.LinkedList;

@Slf4j
public class ParametersRoomsTooba {

    protected House parseParametersRooms(Document document, House house) {
        try {
            house.setSignatureLayoutList(parseSignatureRoomList(document));
        } catch (ParseException e) {
            log.error("Ошибка при парсинге картинок дома");
            e.printStackTrace();
        }
        return house;
    }

    //описание комнат
    private LinkedList<SignatureLayout> parseSignatureRoomList(Document document) {
        LinkedList<SignatureLayout> signatureLayoutList = new LinkedList<>();
        try {
            Elements roomRows = document.select("table.pomieszczenia tr");
            for (Element roomRow : roomRows) {
                String roomNameOrg = roomRow.select(".pomNazwa").text();
                String roomNumberString = roomRow.select(".pomLp").text();
                String roomAreaString = roomRow.select(".pomPow b").text();
                Double roomArea = parseRoomArea(roomAreaString);
                Integer roomNumber = parseRoomNumber(roomNumberString);
                String roomName = translateRoomName(roomNameOrg);
                if (roomName != null) {
                    roomNameOrg = null;
                } else {
                    log.warn(roomNameOrg);
                }
                SignatureLayout signatureLayout = new SignatureLayout(roomNameOrg, roomName, roomNumber,
                        roomArea);
                signatureLayoutList.add(signatureLayout);
            }
        } catch (NumberFormatException | NullPointerException e) {
            log.error("Ошибка при парсинге параметров комнат: " + e.getMessage());

        }
        return signatureLayoutList;
    }

    private Double parseRoomArea(String roomAreaString) {
        Double roomArea;
        try {
            if (roomAreaString == null || roomAreaString.length() == 0) {
                log.info("Нет площади комнаты: Значение равно null");
                return null;
            }
            String firstNumberString = roomAreaString.split(" ")[0];
            firstNumberString = firstNumberString.replace(",", ".");
            roomArea = Double.parseDouble(firstNumberString);
        } catch (NumberFormatException | NullPointerException e) {
            log.warn("Ошибка при парсинге площади комнаты: " + e.getMessage());
            return null;
        }
        return roomArea;
    }

    private Integer parseRoomNumber(String roomNumberString) {
        Integer roomNumber;
        try {
            if (roomNumberString == null || roomNumberString.length() < 1) {
                log.info("Нет номера комнаты: Значение равно null");
                return null;
            }
            String[] parts = roomNumberString.split("[/\\.]");
            String cleanRoomNumberString = parts[parts.length - 1];
            roomNumber = Integer.parseInt(cleanRoomNumberString);
        } catch (NumberFormatException | NullPointerException e) {
            log.error("Ошибка при парсинге номера комнаты: " + e.getMessage());
            return null;
        }
        return roomNumber;
    }

    private String translateRoomName(String titleOrg) {
        String title = null;

        switch (titleOrg.toLowerCase()) {
            case "wiatrołap":
            case "przedsionek":
                title = "Тамбур";
                break;
            case "przedpokój":
                title = "Прихожая";
                break;
            case "pokój dzienny":
            case "pokoj dzienny":
            case "salon":
            case "aneks wypoczynkowy":
                title = "Гостиная";
                break;
            case "salon/jadalnia":
            case "salon+aneks jadalny":
                title = "Гостиная - столовая";
                break;
            case "pokój dzienny + hol":
                title = "Гостиная + холл";
                break;
            case "kuchnia":
                title = "Кухня";
                break;
            case "spiżarnia":
            case "schowek":
            case "magazyn":
            case "spiżarka":
            case "spizarnia":
                title = "Кладовая";
                break;
            case "pokój":
            case "pokoj":
            case "pokój 1":
            case "pokój 2":
            case "pokój 3":
            case "sypialnia + antresola":
                title = "Комната";
                break;
            case "łazienka":
            case "lazienka":
                title = "Ванная комната";
                break;
            case "pom. gospodarcze":
                title = "Котел";
                break;
            case "sypialnia":
            case "pom.gospodarcze":
            case "pomieszczenie gospodarcze":
            case "pom. gospod.*":
            case "pom. gospodarcze / szatnia":
            case "pom. pomocnicze":
            case "pom. pomocnicze - pralnia":
                title = "Подсобное помещение";
                break;
            case "toaleta":
            case "wc":
                title = "Туалет";
                break;
            case "garaż":
            case "garaż*":
            case "garaż dwustanowiskowy":
                title = "Гараж";
                break;
            case "komunikacja":
                title = "Прихожая";
                break;
            case "pokój dzienny z jadalnią":
            case "pokój dzienny + aneks kuchenny":
            case "salon + jadalnia":
            case "kuchnia + jadalnia":
            case "salon z kuchnią":
            case "pokój dzienny + jadalnia":
            case "pokój dzienny + jadalni":
            case "salon + kuchnia":
            case "salon + aneks kuchenny":
            case "salon + jadalnia + kuchnia":
            case "salon / jadalnia":
            case "salon z jadalnią":
                title = "Кухня-гостиная";
                break;
            case "śluza":
                title = "Тамбур";
                break;
            case "garderoba":
            case "szatnia":
            case "wiatrołap - garderoba":
            case "gardeorba":
            case "garderoba (strych)":
            case "garderoba (strych)*":
            case "komunikacja + garderoba":
                title = "Гардероб";
                break;
            case "kotłownia":
            case "pom. gospodarcze - kotłownia":
                title = "Котельная";
                break;
            case "hol":
            case "holl":
            case "hol wejściowy":
            case "holl wejściowy":
            case "holl - antresola":
            case "hol - antresola":
            case "hall - antresola":
            case "hall":
            case "hal":
            case "hol + komunikacja":
            case "hol - komunikacja":
            case "holl / antresola":
            case "holl + korytarz":
                title = "Холл";
                break;
            case "hall + schody":
            case "hol + schody":
            case "hol +schody":
                title = "Холл + лестница";
                break;
            case "hol i garderoba":
                title = "Холл и гардероб";
                break;
            case "pom. tech. z co":
            case "pom. techniczne":
            case "pom. techniczne z C.O.":
                title = "Тех.  помещение";
                break;
            case "schody":
            case "podest":
            case "komunikacja + schody":
                title = "Лестница";
                break;
            case "korytarz":
            case "korytarz/komunikacja":
                title = "Коридор";
                break;
            case "taras zadaszony":
                title = "Крытая терраса";
                break;
            case "taras":
            case "taras*":
                title = "Терраса";
                break;
            case "aneks kuchenny":
                title = "Мини-кухня";
                break;
            case "sień":
                title = "Сени";
                break;
            case "sypialnia + garderoba":
                title = "Спальня + гардероб";
                break;
            case "wiatrołap + garderoba":
                title = "Тамбур + гардероб";
                break;
            case "pralnia":
            case "pom. gospodarcze - pralnia":
            case "pralnia-suszarnia":
            case "pralnia / suszarnia":
            case "pralnia - suszarnia":
            case "pralnia*":
            case "pralnia + suszarnia":
                title = "Прачечная";
                break;
            case "strych":
            case "strych / antresola":
            case "strych / poddasze":
            case "strych - antresola nad salonem":
            case "strych + antresola nad salonem":
            case "pom. dodatkowe (strych)":
            case "strych - antresola":
            case "strych/poddasze":
            case "poddasze do adptacji":
            case "poddasze do adaptacji":
            case "poddasze / antresola":
                title = "Чердак";
                break;
            case "pokój / gabinet":
            case "pokój gościnny - gabinet":
            case "pokój - gabinet":
            case "pokój / biuro":
            case "pokój gościnyy - gabinet":
            case "strych / pokój":
                title = "Комната / кабинет";
                break;
            case "gabinet":
                title = "Кабинет";
                break;
            case "jadalnia":
                title = "Столовая";
                break;
            case "jadalnia + schody":
                title = "Столовая + лестница";
                break;
            case "kotłownia + spiżarnia":
                title = "Котельная - кладовая";
                break;
            case "kotłownia + pralnia":
            case "kotłownia+pralnia":
            case "kotłownia / pralnia":
                title = "Котельная - прачечная";
                break;
            case "powierzchnia":
                title = "Площадь";
                break;
            case "antresola":
                title = "Антресоль";
                break;
            case "pom. gospodarcze - garderoba":
            case "aneks do pracy + garderoba":
            case "pom. gospodarcze / garderoba":
                title = "Подсобное помещение - гардероб";
                break;
            case "powierzchnia po podłodze":
                title = "Площадь по полу";
                break;
            case "sauna":
                title = "Сауна";
                break;
            case "pustka nad salonem":
                title = "Второй свет";
                break;
            case "strefa wypocz. dzieci":
                title = "Детская";
                break;
            case "komunikacja gospodarcza":
            case "komunikacja - antresola":
            case "komunikacja + aneks":
                title = "Коммуникации";
                break;
        }
        return title;
    }
}