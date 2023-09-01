package ru.brigader.cottageCatalogParser.dataIO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.brigader.cottageCatalogParser.model.House;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class JsonIO {

    private final String fileJson = "houses.json";

    public void inputJson(LinkedList<House> houses) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(fileJson), houses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<House> outputJson() {
        LinkedList<House> houses = new LinkedList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            houses = objectMapper.readValue(new File(fileJson),
                    new TypeReference<LinkedList<House>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return houses;
    }
}