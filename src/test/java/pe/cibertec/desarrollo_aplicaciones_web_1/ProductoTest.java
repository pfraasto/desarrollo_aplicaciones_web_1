package pe.cibertec.desarrollo_aplicaciones_web_1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Producto;
import pe.cibertec.desarrollo_aplicaciones_web_1.repository.ProductoRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Slf4j
class ProductoTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void testInsertarProducto() {
        Producto p = new Producto();
        p.setNombre("Laptop");
        p.setPrecio(3200.0);
        p.setStock(5);

        productoRepository.save(p);

        log.info("Producto insertado: {}", p);
        assertThat(p.getId()).isNotNull();
    }

    @Test
    void testActualizarStock() {
        Producto p = new Producto();
        p.setNombre("Mouse");
        p.setPrecio(50.0);
        p.setStock(10);

        productoRepository.save(p);

        p.setStock(15);
        productoRepository.save(p);

        System.out.println("Producto actualizado: " + p);
        assertThat(p.getStock()).isEqualTo(15);
    }

    @Test
    void testListarProductos() {
        List<Producto> productos = productoRepository.findAll();
        productos.forEach(prod -> log.info("Producto: {}", prod));
        assertThat(productos).isNotNull();
    }
}
