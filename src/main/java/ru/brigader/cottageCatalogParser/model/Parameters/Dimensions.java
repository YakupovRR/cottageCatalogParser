package ru.brigader.cottageCatalogParser.model.Parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.Floors;

@Getter
@Setter
@ToString
public class Dimensions {
    Double livingArea;
    Double width;
    Double depth;
    Double height;
    Floors floors;


}
