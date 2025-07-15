package pe.cibertec.desarrollo_aplicaciones_web_1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Cliente;
import pe.cibertec.desarrollo_aplicaciones_web_1.repository.ClienteRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
class ClienteTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void testInsertarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setCorreo("juan@example.com");

        clienteRepository.save(cliente);

        log.info("Cliente insertado: {}", cliente);
        assertThat(cliente.getId()).isNotNull();
    }

    @Test
    void testListarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        clientes.forEach(c -> log.info("Cliente: {}", c));
        assertThat(clientes).isNotNull();
    }

}
