package ru.brigader.cottageCatalogParser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.brigader.cottageCatalogParser.parser.HouseLinkExtractor;
import ru.brigader.cottageCatalogParser.parser.Parser;

import java.sql.SQLException;

@RestController
@Slf4j
public class Controller {

    private static HouseLinkExtractor houseLinkExtractor = new HouseLinkExtractor();
    private static Parser parser = new Parser();


    @GetMapping("/links")
    public String saveLinksToFile(
            @RequestParam(defaultValue = "1") int startPage,
            @RequestParam(defaultValue = "3") int lastPage
    ) {
        log.info("Получен запрос к контроллеру /links с параметрами startPage={}, lastPage={}", startPage, lastPage);
        houseLinkExtractor.saveLinksToFile(startPage, lastPage);
        return ("Ссылки на проекты сохранены");
    }

    @GetMapping("/start")
    public String startParse() throws SQLException {
        log.info("Получен запрос к контроллеру  /start");
        parser.startParse();
        return ("Парсинг проектов завершен");
    }


    @GetMapping("/test")
    public String test() {
        log.info("Тестовое подключение");
        return ("Тестовое подключение");
    }

}
