package poo.uniquindio.edu.co.Homa.ServiceTest;

class AlojamientoServiceTest {
    /* 
    @Mock
    private AlojamientoRepository alojamientoRepository;

    @InjectMocks
    private AlojamientoServiceImpl alojamientoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        // intentar eliminar con anfitrionId distinto
        assertThrows(RuntimeException.class, () -> alojamientoService.eliminar(1L, 999L));
    }

    @Test
    @DisplayName("Eliminar exitoso")
    void eliminar_Exitoso() {
        Usuario anfitrion = new Usuario();
        anfitrion.setId(1L);
        Alojamiento a = new Alojamiento();
        a.setId(1L);
        a.setAnfitrion(anfitrion);
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(a));

        assertDoesNotThrow(() -> alojamientoService.eliminar(1L, 1L));
        verify(alojamientoRepository, times(1)).delete(a);
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
        // when(alojamientoRepository.tieneReservasFuturas(1L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> alojamientoService.eliminar(1L, 1L));
        assertNotNull(ex.getMessage());
    }
        */

}