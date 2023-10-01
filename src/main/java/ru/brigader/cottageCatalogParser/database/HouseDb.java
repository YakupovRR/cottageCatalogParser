package ru.brigader.cottageCatalogParser.database;

import lombok.extern.slf4j.Slf4j;
import ru.brigader.cottageCatalogParser.model.House;
import ru.brigader.cottageCatalogParser.model.ImageHouse;
import ru.brigader.cottageCatalogParser.model.SignatureLayout;

import java.sql.*;
import java.util.LinkedList;


@Slf4j
public class HouseDb {

    private DataSource dataSource;
    private final String sqlHouse = "INSERT INTO projects (idProject, title, titleEng, livingArea, width, " + //по 5 шт в строке
            "depth, height, floors, roofAngle, roofType, " +
            "roofTypeOrg, roofArea, architectureStyle, ArchitectureStyleOrg, technology, " +
            "technologyOrg, rooms, bathrooms, hasGarage, garage, " +
            "hasBasement, hasFireplace, operatedRoof, operatedLoft, rating, " +
            "voted, urlSource, descriptionOrg, dirSaveImages )" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +   // по 10 шт в строке
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String sqlImages = "INSERT INTO images (idProject, path, imageType, imageTag, imageTypeOrg," +
            " urlSource) VALUES (?, ?, ?, ?, ?, ?)";
    private final String sqlSignatureLayout = "INSERT INTO signatureLayout (idProject, name, nameOrg, number, roomArea) " +
            "VALUES (?, ?, ?, ?, ?)";


    public void saveProjectDb(LinkedList<House> houses) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statementHouse = connection.prepareStatement(sqlHouse);
                 PreparedStatement statementImages = connection.prepareStatement(sqlImages);
                 PreparedStatement statementSignatureLayout = connection.prepareStatement(sqlSignatureLayout);
            ) {
                for (House house : houses) {
                    log.info("Сохраняем в БД проект id " + house.getId());
                    saveMainParametersHouseDb(house, statementHouse);
                    saveImagesDb(house, statementImages);
                    saveSignatureLayoutDb(house, statementSignatureLayout);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Откатываем транзакцию в случае ошибки
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Включаем автоматический commit обратно
                connection.close(); // Закрываем соединение
            }
        }
    }

    private void saveSignatureLayoutDb(House house, PreparedStatement statementSignatureLayout) throws SQLException {

        for (SignatureLayout signatureLayout : house.getSignatureLayoutList()) {

            setParameter(statementSignatureLayout, 1, house.getId(), "int");
            setParameter(statementSignatureLayout, 2, signatureLayout.getName(), "string");
            setParameter(statementSignatureLayout, 3, signatureLayout.getNameOrg(), "string");
            setParameter(statementSignatureLayout, 4, signatureLayout.getNumber(), "int");
            setParameter(statementSignatureLayout, 5, signatureLayout.getRoomArea(), "double");
            statementSignatureLayout.executeUpdate();
        }
    }

    private void saveImagesDb(House house, PreparedStatement statementImages) throws SQLException {
        for (ImageHouse imageHouse : house.getImageHouseList()) {
            setParameter(statementImages, 1, house.getId(), "int");
            setParameter(statementImages, 2, imageHouse.getPath(), "string");
            setParameter(statementImages, 3, String.valueOf(imageHouse.getImageType()), "string");
            setParameter(statementImages, 4, String.valueOf(imageHouse.getImageTag()), "string");
            setParameter(statementImages, 5, imageHouse.getImageTypeOrg(), "string");
            setParameter(statementImages, 6, imageHouse.getUrlSource(), "string");
            statementImages.executeUpdate();
        }
    }

    private void saveMainParametersHouseDb(House house, PreparedStatement statementHouse) throws SQLException {

        setParameter(statementHouse, 1, house.getId(), "int");
        setParameter(statementHouse, 2, house.getTitle(), "string");
        setParameter(statementHouse, 3, house.getTitleEng(), "string");
        setParameter(statementHouse, 4, house.getDimensions().getLivingArea(), "double");
        setParameter(statementHouse, 5, house.getDimensions().getWidth(), "double");
        setParameter(statementHouse, 6, house.getDimensions().getDepth(), "double");
        setParameter(statementHouse, 7, house.getDimensions().getHeight(), "double");
        setParameter(statementHouse, 8, String.valueOf(house.getDimensions().getFloors()), "string");
        setParameter(statementHouse, 9, house.getRoof().getRoofAngle(), "int");
        setParameter(statementHouse, 10, String.valueOf(house.getRoof().getRoofType()), "string");
        setParameter(statementHouse, 11, house.getRoof().getRoofTypeOrg(), "string");
        setParameter(statementHouse, 12, house.getRoof().getRoofArea(), "double");
        setParameter(statementHouse, 13, String.valueOf(house.getArchitecture()
                .getArchitectureStyle()), "string");
        setParameter(statementHouse, 14, house.getArchitecture().getArchitectureStyleOrg(), "string");
        setParameter(statementHouse, 15, String.valueOf(house.getArchitecture().getTechnology()),
                "string");
        setParameter(statementHouse, 16, house.getArchitecture().getTechnologyOrg(), "string");
        setParameter(statementHouse, 17, house.getArchitecture().getRooms(), "int");
        setParameter(statementHouse, 18, house.getArchitecture().getBathrooms(), "int");
        statementHouse.setBoolean(19, house.getOptions().isHasGarage());
        setParameter(statementHouse, 20, String.valueOf(house.getOptions().getGarage()), "string");
        statementHouse.setBoolean(21, house.getOptions().isHasBasement());
        statementHouse.setBoolean(22, house.getOptions().isHasFireplace());
        statementHouse.setBoolean(23, house.getOptions().isOperatedRoof());
        statementHouse.setBoolean(24, house.getOptions().isOperatedLoft());
        setParameter(statementHouse, 25, house.getProjectRating().getRating(), "double");
        setParameter(statementHouse, 26, house.getProjectRating().getVoted(), "int");
        setParameter(statementHouse, 27, house.getUrlSource(), "string");
        setParameter(statementHouse, 28, house.getDescriptionOrg(), "string");
        setParameter(statementHouse, 29, house.getDirSaveImages(), "string");
        statementHouse.executeUpdate();
    }

    private static void setParameter(PreparedStatement preparedStatement, int parameterIndex,
                                     Object value, String type) throws SQLException {
        switch (type.toLowerCase()) {
            case "int":
                if (value instanceof Integer) {
                    preparedStatement.setInt(parameterIndex, (Integer) value);
                } else {
                    preparedStatement.setNull(parameterIndex, Types.INTEGER);
                }
                break;
            case "double":
                if (value instanceof Double) {
                    preparedStatement.setDouble(parameterIndex, (Double) value);
                } else {
                    preparedStatement.setNull(parameterIndex, Types.DOUBLE);
                }
                break;
            case "string":
                if (value instanceof String) {
                    preparedStatement.setString(parameterIndex, (String) value);
                } else {
                    preparedStatement.setNull(parameterIndex, Types.VARCHAR);
                }
                break;
            default:
                preparedStatement.setObject(parameterIndex, value);
        }
    }

    public Integer getLastProjectId() {
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

}











