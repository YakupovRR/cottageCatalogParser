package ru.brigader.cottageCatalogParser.database;

import lombok.extern.slf4j.Slf4j;
import ru.brigader.cottageCatalogParser.model.House;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Slf4j
public class HouseDb {

    private DataSource dataSource;

    public void saveProjectDb(LinkedList<House> houses) throws SQLException {


        //здесь обозначить SQL запросы


        String sqlHouse = "INSERT INTO projects (idProject, title,titleEng,livingArea, width, depth, height," +
                " roofAngle, roofType, idRoofTypeOrg, roofArea, floors, architectureStyle, idArchitectureStyleOrg, " +
                "idArchitectureStyleOrg, idTechnologyOrg, rooms, bathrooms, hasGarage, garage," +
                "hasBasement, hasFireplace, operatedRoof, operatedLoft, rating, voted," +
                "urlSource, description, descriptionOrg, dirSaveImages) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlOriginalDesignation = "INSERT INTO originalDesignation (orgDesignation, ruDesignation) VALUES (?, ?)";

        String sqlImages = "INSERT INTO images (idProject, path, imageType, imageTag, idImageTypeOrg," +
                " urlSource) VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection connection = dataSource.getConnection();
             PreparedStatement statementHouse = connection.prepareStatement(sqlHouse);
             PreparedStatement statementOriginalDesignation = connection.prepareStatement(sqlOriginalDesignation);
             PreparedStatement statementImages = connection.prepareStatement(sqlImages);

        ) {

            for (House house : houses) {
                statementHouse.setInt(1, house.getId());
                statementHouse.setString(2, house.getTitle());
                statementHouse.setString(3, house.getTitleEng());
                statementHouse.setDouble(4, house.getDimensions().getLivingArea());
                statementHouse.setDouble(5, house.getDimensions().getWidth());
                statementHouse.setDouble(6, house.getDimensions().getDepth());
                statementHouse.setDouble(7, house.getDimensions().getHeight());


            }


            //Запись в БД самого проекта
            statementHouse.setInt(1, house.getId());
            statementHouse.setString(2, house.getTitle());
            statementHouse.setString(3, house.getTitleEng());
            statementHouse.setString(4, house.getDescription());
            statementHouse.setDouble(5, house.getSquare());
            statementHouse.setInt(6, house.getRooms());
            statementHouse.setDouble(7, house.getWidth());
            statementHouse.setDouble(8, house.getLength());
            statementHouse.setString(9, String.valueOf(house.getFloors()));
            statementHouse.setString(10, String.valueOf(house.getFeatures()));
            statementHouse.executeUpdate();

            // Запись в БД экстерьеров
            try {
                boolean isBaseExterior = true;   // предполагается, что основная картинка идет первой
                for (int i = 0; i < house.getExteriorPath().size(); i++) {
                    statementExterioir.setInt(1, house.getId());
                    statementExterioir.setString(2, house.getExteriorPath().get(i));
                    statementExterioir.setBoolean(3, isBaseExterior);
                    statementExterioir.executeUpdate();
                    isBaseExterior = false;
                }
            } catch (NullPointerException e) {
                log.warn("Проект id " + house.getId() + " Не удалось записать лист экстерьеров в БД, возможно он null");
            }


            // Запись в БД планировок
            try {
                int additionalImages = 0;
                if (house.isGroundFloor()) additionalImages = additionalImages++;
//эксплуатируемая кровля не учитывается, т.к. обычно сохранена криво на сайте-источнике
                if (isCorrectLengthListLayout(house, additionalImages)) {
                    for (int i = 0; i < (house.getLayoutPath().size() - additionalImages); i++) {
                        statementLayout.setInt(1, house.getId());
                        statementLayout.setString(2, house.getLayoutPath().get(i));
                        statementLayout.setString(3, getFloorByNumber(i, house.getFloors()));
                        statementLayout.executeUpdate();
                    }
                    //здесь может быть ошибка - эксплуатирумая кровля сохранится как подвал. Править потом вручную
                    if (house.isGroundFloor()) {
                        statementLayout.setInt(1, house.getId());
                        statementLayout.setString(2, house.getLayoutPath().get(
                                (house.getLayoutPath().size() - 1)));
                        statementLayout.setString(3, String.valueOf(Floors.BASEMENT));
                        statementLayout.executeUpdate();
                    }
                } else {
                    for (int i = 0; i < house.getLayoutPath().size(); i++) {
                        statementLayout.setInt(1, house.getId());
                        statementLayout.setString(2, house.getLayoutPath().get(i));
                        statementLayout.setString(3, "unknown");  //потом править только ручками
                        statementLayout.executeUpdate();
                    }
                }
            } catch (NullPointerException e) {
                log.warn("Проект id " + house.getId() + " - не удалось записать лист этажей в БД, возможно он null");
            }

            // запись в БД тэгов
            List<Integer> tagsId = new ArrayList<>();
            try {
                for (String i : house.getTags()) {
                    statementTags.setString(1, i);
                    ResultSet rs = statementTags.executeQuery();
                    if (rs.next()) {
                        int tagId = rs.getInt("tagid");
                        tagsId.add(tagId);
                    } else {
                        rs = statementTags.getGeneratedKeys();
                        if (rs.next()) {
                            int tagId = rs.getInt(1);
                            tagsId.add(tagId);
                        } else {
                            log.warn("Ошибка при поиске/сохранения тэга в БД проекта id " + house.getId());
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

// запись в БД пары проект/тэг
            try {
                for (int j : tagsId) {
                    statementProjectAndTag.clearParameters(); // очищаем параметры запроса перед повторным использованием
                    statementProjectAndTag.setInt(1, house.getId());
                    statementProjectAndTag.setInt(2, j);
                    statementProjectAndTag.executeUpdate();
                }
            } catch (NullPointerException e) {
                log.warn("Проект id " + house.getId() + " Не удалось записать связующий лист проект/тэги");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //получение последнего id проекта, сохраненного в таблице
    public Integer getLastProjectId() throws SQLException {
        Integer lastProjectId = 0;
        String sqlRequestId = "SELECT MAX(idProject) FROM projects";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statementId = connection.prepareStatement(sqlRequestId);
             ResultSet resultSet = statementId.executeQuery()) {
            if (resultSet.next()) {
                lastProjectId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastProjectId;
    }


    private boolean isCorrectLengthListLayout(House house, int additionalImages) { // что у двухэтажного дома будет 2 картинки и т.п.

        int expectedListLength = -1;

        try {
            switch (house.getFloors()) {
                case ONE:
                    expectedListLength = (1 + additionalImages);
                    break;
                case TWO:
                    expectedListLength = (2 + additionalImages);
                    break;
                case ONEPLUSMANSARD:
                    expectedListLength = (2 + additionalImages);
                    break;
                case THERE:
                    expectedListLength = (3 + additionalImages);
                    break;
                case TWOPLUSMANSARD:
                    expectedListLength = (3 + additionalImages);
                    break;
            }
        } catch (NullPointerException e) {
            log.warn("Не удалось определить ожидаемый размер листа");
        }
        return (expectedListLength == house.getLayoutPath().size());
    }

    private String getFloorByNumber(int i, Floors floor) {
        String floorString = null;
        switch (i) {
            case 0:
                floorString = String.valueOf(Floors.ONE);
                break;
            case 1:
                if (floor.equals(Floors.ONEPLUSMANSARD)) {
                    floorString = String.valueOf(Floors.ONEPLUSMANSARD);
                } else {
                    floorString = String.valueOf(Floors.TWO);
                }
                break;
            case 2:
                if (floor.equals(Floors.TWOPLUSMANSARD)) {
                    floorString = String.valueOf(Floors.TWOPLUSMANSARD);
                } else {
                    floorString = String.valueOf(Floors.THERE);
                }
                break;
        }
        return floorString;
    }  // получение название этажа по порядковому номеру картинки


}











