package ru.brigader.cottageCatalogParser.model.Parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.brigader.cottageCatalogParser.model.Parameters.Enums.Garage;

@Getter
@Setter
@ToString
public class Options {
    @Builder.Default
    boolean hasGarage;
    Garage garage;
    @Builder.Default
    boolean hasBasement;
    @Builder.Default
    boolean hasFireplace;
    @Builder.Default
    boolean operatedRoof = false;
    @Builder.Default
    boolean operatedLoft = false; //эксплуатируемый чердак - как мансарда, но без комнат
}
