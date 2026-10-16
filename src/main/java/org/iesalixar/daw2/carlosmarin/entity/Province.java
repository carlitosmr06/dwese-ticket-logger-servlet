package org.iesalixar.daw2.carlosmarin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La clase {@code Province} representa una provincia española almacenada en la
 * tabla {@code provinces} de la base de datos.
 * <p>
 * Cada provincia pertenece a una Comunidad Autónoma, que se modela como un
 * objeto {@link Region} (relación muchos a uno). En la base de datos esta
 * relación se guarda en la columna {@code id_region}.
 * </p>
 */
@Data // Genera getters, setters, equals(), hashCode() y toString().
@NoArgsConstructor // Constructor vacío.
@AllArgsConstructor // Constructor con todos los campos (id, code, name, region).
public class Province {

    /** Identificador único de la provincia (autogenerado por la base de datos). */
    private int id;

    /** Código único de la provincia. Ejemplo: "41" para Sevilla. */
    private String code;

    /** Nombre de la provincia. Ejemplo: "Sevilla". */
    private String name;

    /** Comunidad Autónoma a la que pertenece la provincia. */
    private Region region;

    /**
     * Constructor sin {@code id}, para crear una provincia antes de insertarla
     * en la base de datos (el {@code id} lo genera la base de datos).
     *
     * @param code   Código de la provincia.
     * @param name   Nombre de la provincia.
     * @param region Comunidad Autónoma a la que pertenece.
     */
    public Province(String code, String name, Region region) {
        this.code = code;
        this.name = name;
        this.region = region;
    }
}