package ru.brigader.cottageCatalogParser.model.Parameters;

public enum Floors {
    ONE,
    TWO,
    THERE,
    // под мансардой подразумевается любой утепленный чердак, к которому есть нормальная НЕвертикальная лестница
    ONEPLUSMANSARD, // для Layout подразумевается мансарда после первого этажа
    TWOPLUSMANSARD, // для Layout подразумевается мансарда после второго этажа
    BASEMENT, // цокольный этаж, подвал, подпол и т.д.
    OPERATEDROOF, // плоская эксплуатируемая кровля
    UNDEFINEDFLOOR
}
