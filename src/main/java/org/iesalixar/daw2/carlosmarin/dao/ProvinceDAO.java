package org.iesalixar.daw2.carlosmarin.dao;

import org.iesalixar.daw2.carlosmarin.entity.Province;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz DAO (Data Access Object) para la entidad {@link Province}.
 * Define las operaciones de acceso a datos sobre la tabla {@code provinces}.
 */
public interface ProvinceDAO {

    /**
     * Lista todas las provincias, incluida su Comunidad Autónoma.
     *
     * @return Lista de provincias.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    List<Province> listAllProvinces() throws SQLException;

    /**
     * Inserta una nueva provincia.
     *
     * @param province Provincia a insertar (con su región asignada).
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    void insertProvince(Province province) throws SQLException;

    /**
     * Actualiza una provincia existente.
     *
     * @param province Provincia con los nuevos datos (el {@code id} identifica el registro).
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    void updateProvince(Province province) throws SQLException;

    /**
     * Elimina una provincia por su identificador.
     *
     * @param id Identificador de la provincia.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    void deleteProvince(int id) throws SQLException;

    /**
     * Obtiene una provincia por su identificador.
     *
     * @param id Identificador de la provincia.
     * @return La provincia, o {@code null} si no existe.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    Province getProvinceById(int id) throws SQLException;

    /**
     * Comprueba si ya existe una provincia con el código indicado (ignorando mayúsculas).
     *
     * @param code Código a comprobar.
     * @return {@code true} si ya existe.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    boolean existsProvinceByCode(String code) throws SQLException;

    /**
     * Comprueba si existe otra provincia, distinta de la indicada, con el mismo
     * código (ignorando mayúsculas). Se usa al editar.
     *
     * @param code Código a comprobar.
     * @param id   Identificador de la provincia que se excluye de la comprobación.
     * @return {@code true} si otra provincia ya usa ese código.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    boolean existsProvinceByCodeAndNotId(String code, int id) throws SQLException;
}
