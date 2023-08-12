package ru.brigader.cottageCatalogParser.parser;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HouseLinkExtractor {

    private String baseUrl = "https://www.tooba.pl/projekty-domow-z-aranzacjami-wnetrz";

    public void saveLinksToFile() {
        int startPage = 1;
        int lastPage = 10;
        String firstPartUrl = "https://www.tooba.pl";
        log.info("Сохраняем ссылки с toolba.pl со страниц " + startPage + " по " + lastPage);
        List<String> allLinks = new ArrayList<>();
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
            }

            // Сохранение ссылок в файл url.csv
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("url.csv", true))) {
                for (String link : allLinks) {
                    writer.write(link);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}