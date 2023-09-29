package ru.brigader.cottageCatalogParser.model.Parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.ArchitectureStyle;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.Technology;

@Getter
@Setter
@ToString
public class Architecture {

    ArchitectureStyle architectureStyle;
    String ArchitectureStyleOrg;
    Technology technology;
    String technologyOrg;
    Integer rooms;
    Integer bathrooms;
}
