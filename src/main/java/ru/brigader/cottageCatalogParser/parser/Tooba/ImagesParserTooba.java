package ru.brigader.cottageCatalogParser.parser.Tooba;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.brigader.cottageCatalogParser.exception.ParseException;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.model.ImageHouse;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.ImageType;
import ru.brigader.cottageCatalogParser.parser.CreateDir;
import ru.brigader.cottageCatalogParser.parser.HouseImageDownloader;

import java.util.HashSet;
import java.util.LinkedList;

@Slf4j
public class ImagesParserTooba {
    private CreateDir createDir = new CreateDir();
    private LinkedList<ImageHouse> imageHouseList = new LinkedList<>();
    private HashSet<ImageType> allFoundImageTypes = new HashSet<>();
    public static HouseImageDownloader houseImageDownloader = new HouseImageDownloader();

    protected House parseImages(Document document, House house) {

        try {
            LinkedList<ImageHouse> floorsAndModel3DImages = parseFloorsAndModel3DImages(document);
            LinkedList<ImageHouse> situationalPlanImages = parseSituationalPlan(document);
            LinkedList<ImageHouse> facadesImages = parseFacades(document);
            LinkedList<ImageHouse> sectionImages = parseSection(document);
            LinkedList<ImageHouse> interiorImages = parseInterior(document);
            LinkedList<ImageHouse> exteriorImages = parseExterior(document);
            imageHouseList.addAll(floorsAndModel3DImages);
            imageHouseList.addAll(facadesImages);
            imageHouseList.addAll(sectionImages);
            imageHouseList.addAll(situationalPlanImages);
            imageHouseList.addAll(interiorImages);
            imageHouseList.addAll(exteriorImages);
            house.setImageHouseList(imageHouseList);
            house.setDirSaveImages(createDir.createAllDir(house.getId(), house.getTitleEng(), allFoundImageTypes));
            house = houseImageDownloader.saveImage(house);
        } catch (ParseException e) {
            log.error("Ошибка при парсинге картинок дома");
            e.printStackTrace();
        }
        return house;

    }

    //планировка комнат и 3d модели
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
                        int lastIndex = caption.text().lastIndexOf("-");
                        String imageTypeString = caption.text().substring(lastIndex + 1).trim();
                        imageHouse = generateImageType(imageHouse, imageTypeString);
                    }
                    images.add(imageHouse);
                }
            }
        } catch (NullPointerException e) {
            log.error("Ошибка при обработке элементов: " + e.getMessage());
        }
        return images;
    }

    // план расположения на участке
    private LinkedList<ImageHouse> parseSituationalPlan(Document document) {
        String cssQuery = "h2.section-title:contains(Sytuacja) + div.rzut a";
        String attributeKey = "href";
        ImageType imageType = ImageType.SITUATIONALPLAN;
        return parseStandardImages(document, cssQuery, attributeKey, imageType);

    }

    //фасады
    private LinkedList<ImageHouse> parseFacades(Document document) {
        String cssQuery = "h2.section-title:contains(Elewacje) + div.elewacje a";
        String attributeKey = "href";
        ImageType imageType = ImageType.FACADE;
        return parseStandardImages(document, cssQuery, attributeKey, imageType);

    }

    //разрезы
    private LinkedList<ImageHouse> parseSection(Document document) {
        String cssQuery = "h2.section-title:contains(Przekrój) + div.rzut a";
        String attributeKey = "href";
        ImageType imageType = ImageType.SECTION;
        return parseStandardImages(document, cssQuery, attributeKey, imageType);
    }

    //интерьеры
    private LinkedList<ImageHouse> parseInterior(Document document) {
        String cssQuery = "h2.section-title:contains(Aranżacje wnętrz) + div.aranzacje-list div.aranzacja a";
        String attributeKey = "href";
        ImageType imageType = ImageType.INTERIOR;
        return parseStandardImages(document, cssQuery, attributeKey, imageType);
    }

    //внешний вид
    private LinkedList<ImageHouse> parseExterior(Document document) {
        String cssQuery = "div#imgViewPane a[href]";
        String attributeKey = "href";
        ImageType imageType = ImageType.EXTERIOR;
        return parseStandardImages(document, cssQuery, attributeKey, imageType);
    }

    private LinkedList<ImageHouse> parseStandardImages(Document document, String cssQuery,
                                                       String attributeKey, ImageType imageType) {
        LinkedList<ImageHouse> images = new LinkedList<>();

        try {
            Elements selectedElement = document.select(cssQuery);
            for (Element e : selectedElement) {
                ImageHouse imageHouse = new ImageHouse();
                String url = e.attr(attributeKey);
                imageHouse.setUrlSource(url);
                imageHouse.setImageType(imageType);
                images.add(imageHouse);
                allFoundImageTypes.add(imageType);
            }
        } catch (NullPointerException e) {
            log.error("Ошибка при обработке элементов: " + e.getMessage());
        }
        return images;
    }

    private ImageHouse generateImageType(ImageHouse imageHouse, String type) {
        try {
            if (type.contains("Rzut parteru 3D")) {
                imageHouse.setImageType(ImageType.MODEL3D);
                imageHouse.setImageTag(ImageType.TAGFLOOR);
                allFoundImageTypes.add(ImageType.MODEL3D);
            } else if (type.contains("Rzut poddasza 3D")) {
                imageHouse.setImageType(ImageType.MODEL3D);
                imageHouse.setImageTag(ImageType.TAGMANSARD);
                allFoundImageTypes.add(ImageType.MODEL3D);
            } else if (type.contains("Rzut antresoli 3D")) {
                imageHouse.setImageType(ImageType.MODEL3D);
                imageHouse.setImageTag(ImageType.TAGENTRESOL);
                allFoundImageTypes.add(ImageType.MODEL3D);
            } else if (type.contains("Rzut parteru")) {
                imageHouse.setImageType(ImageType.LAYOUT);
                imageHouse.setImageTag(ImageType.TAGFLOOR);
                allFoundImageTypes.add(ImageType.LAYOUT);
            } else if (type.contains("Rzut poddasza")) {
                imageHouse.setImageType(ImageType.LAYOUT);
                imageHouse.setImageTag(ImageType.TAGMANSARD);
                allFoundImageTypes.add(ImageType.LAYOUT);
            } else if (type.contains("Rzut antresoli")) {
                imageHouse.setImageType(ImageType.LAYOUT);
                imageHouse.setImageTag(ImageType.TAGENTRESOL);
                allFoundImageTypes.add(ImageType.LAYOUT);
            } else if (type.contains("Sytuacja")) {
                imageHouse.setImageType(ImageType.SITUATIONALPLAN);
                allFoundImageTypes.add(ImageType.SITUATIONALPLAN);
            } else if (type.contains("Elewacje")) {
                imageHouse.setImageType(ImageType.FACADE);
                allFoundImageTypes.add(ImageType.FACADE);
            } else {
                imageHouse.setImageTypeOrg(type);
                imageHouse.setImageType(ImageType.UNDEFINEDTYPE);
                allFoundImageTypes.add(ImageType.UNDEFINEDTYPE);
            }
            return imageHouse;
        } catch (Exception e) {
            log.error("Не удалось получить тип картинки из " + type);
            return null;
        }
    }
}


