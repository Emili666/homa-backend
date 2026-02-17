package poo.uniquindio.edu.co.Homa.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import poo.uniquindio.edu.co.Homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoAlojamiento;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.service.impl.AlojamientoServiceImpl;

@ExtendWith(MockitoExtension.class)
class AlojamientoServiceTest {

    @Mock
    private AlojamientoRepository alojamientoRepository;

    @InjectMocks
    private AlojamientoServiceImpl alojamientoService;

    @BeforeEach
    void setUp() {
        // No necesitas abrir mocks manualmente si usas
        // @ExtendWith(MockitoExtension.class)
    }

    @Test
    @DisplayName("No debe eliminar alojamiento si no existe")
    void eliminar_NoExisteDebeLanzar() {
        when(alojamientoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> alojamientoService.eliminar(99L, 1L));
    }

    @Test
    @DisplayName("No debe eliminar alojamiento si no es del anfitrión")
    void eliminar_NoPropietarioDebeLanzar() {
        Usuario anfitrion = new Usuario();
        anfitrion.setId(2L);
        Alojamiento a = new Alojamiento();
        a.setId(1L);
        a.setAnfitrion(anfitrion);
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThrows(RuntimeException.class, () -> alojamientoService.eliminar(1L, 999L));
    }

    @Test
    @DisplayName("Eliminar exitoso (marca como eliminado)")
    void eliminar_Exitoso() {
        Usuario anfitrion = new Usuario();
        anfitrion.setId(1L);

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setId(1L);
        alojamiento.setAnfitrion(anfitrion);
        alojamiento.setEstado(EstadoAlojamiento.ACTIVO);

        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));

        assertDoesNotThrow(() -> alojamientoService.eliminar(1L, 1L));

        // Verifica que el estado se cambió
        assertEquals(EstadoAlojamiento.ELIMINADO, alojamiento.getEstado());

        // Verifica que se haya guardado de nuevo
        verify(alojamientoRepository).save(alojamiento);
    }

    @Test
    @DisplayName("No eliminar si tiene reservas futuras (simular negocio)")
    void eliminar_ConReservasFuturasDebeLanzar() {
        Usuario anfitrion = new Usuario();
        anfitrion.setId(1L);
        Alojamiento a = new Alojamiento();
        a.setId(1L);
        a.setAnfitrion(anfitrion);
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(a));
        when(alojamientoRepository.tieneReservasFuturas(1L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> alojamientoService.eliminar(1L, 1L));
        assertNotNull(ex.getMessage());
    }
}
