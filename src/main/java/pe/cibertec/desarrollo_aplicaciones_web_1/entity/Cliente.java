package pe.cibertec.desarrollo_aplicaciones_web_1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad JPA que representa la tabla 'cliente' en la base de datos.
 * Esta clase define los atributos de un cliente y será utilizada por JPA/Hibernate
 * para mapear registros de la tabla a objetos Java.
 */
@Entity
@Getter     // Lombok: genera automáticamente los métodos get
@Setter     // Lombok: genera automáticamente los métodos set
@ToString   // Lombok: genera automáticamente el metodo toString
public class Cliente {

    /**
     * Identificador único del cliente.
     * Se genera automáticamente con estrategia de incremento (auto-increment en base de datos).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String correo;
}
