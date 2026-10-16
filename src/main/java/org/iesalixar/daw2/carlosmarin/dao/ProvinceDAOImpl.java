package org.iesalixar.daw2.carlosmarin.dao;

import org.iesalixar.daw2.carlosmarin.entity.Province;
import org.iesalixar.daw2.carlosmarin.entity.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link ProvinceDAO}.
 * Realiza las operaciones CRUD sobre la tabla {@code provinces}, haciendo un
 * {@code JOIN} con {@code regions} para cargar también la Comunidad Autónoma
 * de cada provincia.
 */
public class ProvinceDAOImpl implements ProvinceDAO {

    // Logger para registrar entradas, salidas y errores
    private static final Logger logger = LoggerFactory.getLogger(ProvinceDAOImpl.class);

    /** Consulta base: provincia + datos de su región. */
    private static final String SELECT_BASE =
            "SELECT p.id, p.code, p.name, " +
                    "r.id AS region_id, r.code AS region_code, r.name AS region_name " +
                    "FROM provinces p INNER JOIN regions r ON p.id_region = r.id";

    /**
     * Lista todas las provincias ordenadas por código.
     *
     * @return Lista de provincias con su región.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public List<Province> listAllProvinces() throws SQLException {
        logger.info("Entrando en el método listAllProvinces");
        List<Province> provinces = new ArrayList<>();
        String query = SELECT_BASE + " ORDER BY p.code";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                provinces.add(mapRow(resultSet));
            }
            logger.info("Se han recuperado {} provincias", provinces.size());
        } catch (SQLException e) {
            logger.error("Error al listar las provincias: {}", e.getMessage(), e);
            throw e;
        }

        logger.info("Saliendo del método listAllProvinces");
        return provinces;
    }

    /**
     * Inserta una nueva provincia.
     *
     * @param province Provincia a insertar.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public void insertProvince(Province province) throws SQLException {
        logger.info("Entrando en el método insertProvince (code={})", province.getCode());
        String query = "INSERT INTO provinces (code, name, id_region) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, province.getCode());
            preparedStatement.setString(2, province.getName());
            preparedStatement.setInt(3, province.getRegion().getId());
            preparedStatement.executeUpdate();
            logger.info("Provincia insertada correctamente (code={})", province.getCode());
        } catch (SQLException e) {
            logger.error("Error al insertar la provincia {}: {}", province.getCode(), e.getMessage(), e);
            throw e;
        }

        logger.info("Saliendo del método insertProvince");
    }

    /**
     * Actualiza una provincia existente.
     *
     * @param province Provincia con los nuevos datos.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public void updateProvince(Province province) throws SQLException {
        logger.info("Entrando en el método updateProvince (id={})", province.getId());
        String query = "UPDATE provinces SET code = ?, name = ?, id_region = ? WHERE id = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, province.getCode());
            preparedStatement.setString(2, province.getName());
            preparedStatement.setInt(3, province.getRegion().getId());
            preparedStatement.setInt(4, province.getId());
            int rows = preparedStatement.executeUpdate();
            logger.info("Provincia actualizada (id={}, filas afectadas={})", province.getId(), rows);
        } catch (SQLException e) {
            logger.error("Error al actualizar la provincia {}: {}", province.getId(), e.getMessage(), e);
            throw e;
        }

        logger.info("Saliendo del método updateProvince");
    }

    /**
     * Elimina una provincia por su identificador.
     *
     * @param id Identificador de la provincia.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public void deleteProvince(int id) throws SQLException {
        logger.info("Entrando en el método deleteProvince (id={})", id);
        String query = "DELETE FROM provinces WHERE id = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rows = preparedStatement.executeUpdate();
            logger.info("Provincia eliminada (id={}, filas afectadas={})", id, rows);
        } catch (SQLException e) {
            logger.error("Error al eliminar la provincia {}: {}", id, e.getMessage(), e);
            throw e;
        }

        logger.info("Saliendo del método deleteProvince");
    }

    /**
     * Obtiene una provincia por su identificador.
     *
     * @param id Identificador de la provincia.
     * @return La provincia, o {@code null} si no existe.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public Province getProvinceById(int id) throws SQLException {
        logger.info("Entrando en el método getProvinceById (id={})", id);
        String query = SELECT_BASE + " WHERE p.id = ?";
        Province province = null;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    province = mapRow(resultSet);
                } else {
                    logger.warn("No se ha encontrado ninguna provincia con id={}", id);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener la provincia {}: {}", id, e.getMessage(), e);
            throw e;
        }

        logger.info("Saliendo del método getProvinceById");
        return province;
    }

    /**
     * Comprueba si ya existe una provincia con el código indicado (ignorando mayúsculas).
     *
     * @param code Código a comprobar.
     * @return {@code true} si ya existe.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public boolean existsProvinceByCode(String code) throws SQLException {
        logger.info("Entrando en el método existsProvinceByCode (code={})", code);
        String query = "SELECT COUNT(*) FROM provinces WHERE UPPER(code) = ?";
        boolean exists;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code.toUpperCase());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error al comprobar el código de provincia {}: {}", code, e.getMessage(), e);
            throw e;
        }

        logger.info("Saliendo del método existsProvinceByCode (existe={})", exists);
        return exists;
    }

    /**
     * Comprueba si otra provincia (distinta de {@code id}) usa ya el código indicado.
     *
     * @param code Código a comprobar.
     * @param id   Identificador de la provincia excluida de la comprobación.
     * @return {@code true} si otra provincia ya usa ese código.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public boolean existsProvinceByCodeAndNotId(String code, int id) throws SQLException {
        logger.info("Entrando en el método existsProvinceByCodeAndNotId (code={}, id={})", code, id);
        String query = "SELECT COUNT(*) FROM provinces WHERE UPPER(code) = ? AND id != ?";
        boolean exists;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code.toUpperCase());
            preparedStatement.setInt(2, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error al comprobar el código {} excluyendo id={}: {}", code, id, e.getMessage(), e);
            throw e;
        }

        logger.info("Saliendo del método existsProvinceByCodeAndNotId (existe={})", exists);
        return exists;
    }

    /**
     * Convierte la fila actual del {@link ResultSet} en un objeto {@link Province}
     * con su {@link Region}. Espera las columnas de {@link #SELECT_BASE}.
     *
     * @param resultSet ResultSet posicionado en una fila válida.
     * @return Provincia con su región cargada.
     * @throws SQLException si falla la lectura de alguna columna.
     */
    private Province mapRow(ResultSet resultSet) throws SQLException {
        Region region = new Region(
                resultSet.getInt("region_id"),
                resultSet.getString("region_code"),
                resultSet.getString("region_name"));

        return new Province(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("name"),
                region);
    }
}