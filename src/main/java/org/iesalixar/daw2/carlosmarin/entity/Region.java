package org.iesalixar.daw2.carlosmarin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La clase `Region` representa una entidad que modela una región dentro de la
 * base de datos.
 * Contiene tres campos: `id`, `code` y `name`, donde `id` es el identificador
 * único de la región, `code` es un código asociado a la región, y `name` es el
 * nombre de la región.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar
 * automáticamente métodos comunes como getters, setters, constructores, y otros
 * métodos estándar de los objetos.
 */
@Data // Genera getters, setters, equals(), hashCode(), toString() y canEqual().
@NoArgsConstructor // Genera un constructor sin argumentos (necesario para Hibernate/JPA).
@AllArgsConstructor // Genera un constructor con todos los campos (id, code, name).
// Ejemplo: new Region(1, "01", "Andalucía");
public class Region {

    // Identificador único de la región, normalmente autogenerado por la base de datos.
    private int id;

    // Código de la región, una cadena corta. Ejemplo: "01" para Andalucía.
    private String code;

    // Nombre completo de la región, como "Andalucía" o "Cataluña".
    private String name;

    /**
     * Constructor personalizado que no incluye el campo `id`.
     * Se usa para crear una `Region` antes de insertarla en la base de datos,
     * cuando el `id` aún no se conoce porque es autogenerado.
     *
     * @param code Código de la región.
     * @param name Nombre de la región.
     */
    public Region(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
