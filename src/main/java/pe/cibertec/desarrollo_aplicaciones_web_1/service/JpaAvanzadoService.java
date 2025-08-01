package pe.cibertec.desarrollo_aplicaciones_web_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pe.cibertec.desarrollo_aplicaciones_web_1.entity.Alumno;
import pe.cibertec.desarrollo_aplicaciones_web_1.repository.AlumnoRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaAvanzadoService {

    private final AlumnoRepository alumnoRepository;

    public Page<Alumno> listarPaginado(String filtro, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("nombre").ascending());
        return alumnoRepository.findByNombreContaining(filtro, pageable);
    }
}
