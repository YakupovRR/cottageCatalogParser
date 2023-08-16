package ru.brigader.cottageCatalogParser.parser;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ReadCSV {
    public static LinkedList<String> readFromFile(String filePath) {
        LinkedList<String> s = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                s.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

}
