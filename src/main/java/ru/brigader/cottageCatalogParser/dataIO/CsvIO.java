package ru.brigader.cottageCatalogParser.dataIO;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

@Slf4j
public class CsvIO {
    public static LinkedList<String> outputCsv(String filePath) {
        LinkedList<String> s = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),
                StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                s.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void inputCsv(LinkedList<String> links, String filePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String link : links) {
                writer.write(link);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

