package ru.brigader.cottageCatalogParser.parser;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ReadCSV {
    public static LinkedList<String> readFromFile(String filePath) {
        LinkedList<String> s = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                s.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Размер листа  " + s.size());
        return s;
    }
}
