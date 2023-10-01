package ru.brigader.cottageCatalogParser.parser;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.brigader.cottageCatalogParser.dataIO.CsvIO;


import java.io.IOException;
import java.util.LinkedList;

@Slf4j
@Component
public class HouseLinkExtractor {

    private String baseUrl = "https://www.tooba.pl/projekty-domow-z-aranzacjami-wnetrz";
    private CsvIO csvIO = new CsvIO();
    private String filePath ="url.csv";

    public void saveLinksToFile() {
        int startPage = 1;
        int lastPage = 2;
        String firstPartUrl = "https://www.tooba.pl";
        log.info("Сохраняем ссылки с toolba.pl со страниц " + startPage + " по " + lastPage);
        LinkedList<String> allLinks = new LinkedList<>();
        try {
            for (int page = startPage; page <= lastPage; page++) {
                log.info("Сохраняем ссылки со страницы " + page);
                String url = baseUrl + (page == 1 ? "" : "?page=" + page);
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("h3.projekt-name > a");

                for (Element link : links) {
                    String href = link.attr("href");
                    String fullHref = firstPartUrl + href;
                    allLinks.add(fullHref);
                }
                csvIO.inputCsv(allLinks, filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}