package pe.cibertec.desarrollo_aplicaciones_web_1.entity.herencia;

import jakarta.persistence.Entity;

@Entity
public class Administrador extends Usuario {
    private String rol;
}
