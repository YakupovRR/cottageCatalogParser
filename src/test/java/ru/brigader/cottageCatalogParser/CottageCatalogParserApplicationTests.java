package ru.brigader.cottageCatalogParser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.brigader.cottageCatalogParser.model.Parameters.Floors;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.model.Parameters.ArchitectureStyle;
import ru.brigader.cottageCatalogParser.model.Parameters.Garage;
import ru.brigader.cottageCatalogParser.model.Parameters.RoofType;
import ru.brigader.cottageCatalogParser.model.Parameters.Technology;
import ru.brigader.cottageCatalogParser.parser.HousePageParserTooba;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class CottageCatalogParserApplicationTests {

    private static House house;
    @BeforeAll
    static void init() {
        HousePageParserTooba housePageParser = new HousePageParserTooba();
        house = new House();
        house.setTitle("Тестовый");
        house.setId(-1);
        house.setUrlSource("https://www.tooba.pl/projekt-domu-BW-49-wariant-11-bez-garazu-TXF-289");
        house = housePageParser.parse(house);
    }
    @Test
    void parseLivingArea() {
        Double expected = 99.1;
        Double notExpected = 199.1;
        Double actualLivingArea = house.getLivingArea();
        assertEquals(expected, actualLivingArea, 0.01);
        assertNotEquals(notExpected, actualLivingArea, 0.01);
    }

    @Test
    void parseWidth() {
        Double expected = 10.55;
        Double notExpected = -10.55;
        Double checkedValue  = house.getWidth();
        assertEquals(expected, checkedValue, 0.01);
        assertNotEquals(notExpected, checkedValue, 0.01);
    }

    @Test
    void parseDepth() {
        Double expected = 8.2;
        Double notExpected = -8.2;
        Double checkedValue  = house.getDepth();
        assertEquals(expected, checkedValue, 0.01);
        assertNotEquals(notExpected, checkedValue, 0.01);
    }

    @Test
    void parseHeight() {
        Double expected = 8.23;
        Double notExpected = -8.23;
        Double checkedValue  = house.getHeight();
        assertEquals(expected, checkedValue, 0.01);
        assertNotEquals(notExpected, checkedValue, 0.01);
    }

    @Test
    void parseRoofAngle() {
        Integer expected = 42;
        Integer notExpected = -42;
        Integer checkedValue  = house.getRoofAngle();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseRoofType() {
        RoofType expected = RoofType.TWOSLOPES;
        RoofType notExpected = RoofType.UNDEFINEDROOFTYPE;
        RoofType checkedValue  = house.getRoofType();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseRoofArea() {
        Double expected = 158.7;
        Double notExpected = -158.7;
        Double checkedValue  = house.getRoofArea();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseFloors() {
        Floors expected = Floors.ONEPLUSMANSARD;
        Floors notExpected = Floors.TWO;
        Floors checkedValue  = house.getFloors();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseArchitectureStyle() {
        ArchitectureStyle expected = ArchitectureStyle.MODERN;
        ArchitectureStyle notExpected = ArchitectureStyle.UNDEFINEDSTYLE;
        ArchitectureStyle checkedValue  = house.getArchitectureStyle();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseTechnology() {
        Technology expected = Technology.STONE;
        Technology notExpected = Technology.UNDEFINEDTECHNOLOGY;
        Technology checkedValue  = house.getTechnology();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseRooms() {
        Integer expected = 5;
        Integer notExpected = -5;
        Integer checkedValue  = house.getRooms();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }


    @Test
    void parseBathrooms() {
        Integer expected = 2;
        Integer notExpected = -2;
        Integer checkedValue  = house.getBathrooms();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseHasGarage() {
        Boolean expected = false;
        Boolean notExpected = true;
        Boolean checkedValue  = house.getHasGarage();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseGarage() {
        Garage expected = Garage.NON;
        Garage notExpected = Garage.UNDEFINEDGARAGE;
        Garage checkedValue  = house.getGarage();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

    @Test
    void parseHasFireplace() {
        Boolean expected = true;
        Boolean notExpected = false;
        Boolean checkedValue  = house.getHasFireplace();
        assertEquals(expected, checkedValue);
        assertNotEquals(notExpected, checkedValue);
    }

}
