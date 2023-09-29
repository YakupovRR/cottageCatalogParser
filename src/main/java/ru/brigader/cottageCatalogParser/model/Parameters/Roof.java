package ru.brigader.cottageCatalogParser.model.Parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.RoofType;

@Getter
@Setter
@ToString
public class Roof {
    Integer roofAngle;
    RoofType roofType;
    String roofTypeOrg;
    Double roofArea;
}
