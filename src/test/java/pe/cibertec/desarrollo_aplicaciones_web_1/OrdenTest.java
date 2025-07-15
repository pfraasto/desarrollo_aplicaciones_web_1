package pe.cibertec.desarrollo_aplicaciones_web_1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Cliente;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Orden;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Producto;
import pe.cibertec.desarrollo_aplicaciones_web_1.repository.ClienteRepository;
import pe.cibertec.desarrollo_aplicaciones_web_1.repository.OrdenRepository;
import pe.cibertec.desarrollo_aplicaciones_web_1.repository.ProductoRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Slf4j
class OrdenTest {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    private Cliente cliente;
    private Producto producto;

    @BeforeEach
    void setUp() {
        // Crear cliente de prueba
        cliente = new Cliente();
        cliente.setNombre("Lucía Ramírez");
        cliente.setCorreo("lucia@example.com");
        clienteRepository.save(cliente);

        // Crear producto de prueba
        producto = new Producto();
        producto.setNombre("Laptop Lenovo");
        producto.setPrecio(3500.0);
        producto.setStock(5);
        productoRepository.save(producto);
    }

    @Test
    void testCrearOrdenConClienteYProducto() {
        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setProducto(producto);
        ordenRepository.save(orden);

        log.info("Orden creada: {}", orden);

        assertThat(orden.getId()).isNotNull();
        assertThat(orden.getCliente()).isEqualTo(cliente);
        assertThat(orden.getProducto()).isEqualTo(producto);
    }

    @Test
    void testListarOrdenes() {
        // Crear una orden de prueba
        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setProducto(producto);
        ordenRepository.save(orden);

        List<Orden> ordenes = ordenRepository.findAll();
        ordenes.forEach(o -> log.info("Orden: {}", o));

        assertThat(ordenes).isNotEmpty();
        assertThat(ordenes.get(0).getCliente()).isEqualTo(cliente);
        assertThat(ordenes.get(0).getProducto()).isEqualTo(producto);
    }
}
