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
                title = "Тамбур";
                break;
            case "przedpokój":
                title = "Прихожая";
                break;
            case "pokój dzienny":
            case "pokoj dzienny":
            case "salon":
                title = "Гостиная";
                break;
            case "kuchnia":
                title = "Кухня";
                break;
            case "spiżarnia":
            case "schowek":
                title = "Кладовая";
                break;
            case "pokój":
                title = "Комната";
                break;
            case "łazienka":
                title = "Ванная комната";
                break;
            case "pom. gospodarcze":
                title = "Котел";
                break;
            case "sypialnia":
                title = "Подсобное помещение";
                break;
            case "toaleta":
            case "wc":
                title = "Туалет";
                break;
            case "garaż":
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
            case "salon + kuchnia":
            case "salon + aneks kuchenny":
            case "salon + jadalnia + kuchnia":
                title = "Кухня-гостиная";
                break;
            case "śluza":
                title = "Тамбур";
                break;
            case "garderoba":
            case "szatnia":
                title = "Гардероб";
                break;
            case "kotłownia":
            case "pom. gospodarcze - kotłownia":
                title = "Котельная";
                break;
            case "hol":
            case "holl":
            case " hol wejściowy":
                title = "Холл";
                break;
            case "pom. tech. z co":
            case "pom. techniczne":
                title = "Тех.  помещение";
                break;
            case "schody":
                title = "Лестница";
                break;
            case "korytarz":
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
                title = "Прачечная";
                break;
            case "strych":
                title = "Чердак";
                break;
            case "pokój / gabinet":
            case "pokój gościnny - gabinet":
            case "pokój - gabinet":
                title = "Комната / кабинет";
                break;
            case "jadalnia":
                title = "Столовая";
                break;
            case "spiżarka":
                title = "Столовая";
                break;
            case "kotłownia + spiżarnia":
                title = "Котельная - кладовая";
                break;
            case "kotłownia + pralnia":
                title = "Котельная - прачечная";
                break;
            case "powierzchnia":
                title = "Площадь";
                break;
            case "antresola":
                title = "Антресоль";
                break;

        }
        return title;
    }
}