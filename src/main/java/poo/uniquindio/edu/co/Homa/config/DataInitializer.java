package poo.uniquindio.edu.co.Homa.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poo.uniquindio.edu.co.Homa.model.entity.*;
import poo.uniquindio.edu.co.Homa.model.enums.*;
import poo.uniquindio.edu.co.Homa.repository.*;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final ResenaRepository resenaRepository;
    private final FavoritoRepository favoritoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Iniciando carga de datos de prueba...");

        // 1. Crear usuarios
        List<Usuario> usuarios = crearUsuarios();
        
        // 2. Crear alojamientos
        List<Alojamiento> alojamientos = crearAlojamientos(usuarios);
        
        // 3. Crear reservas
        List<Reserva> reservas = crearReservas(alojamientos, usuarios);
        
        // 4. Crear reseñas (solo para reservas completadas)
        crearResenas(reservas, usuarios);
        
        // 5. Crear favoritos
        crearFavoritos(alojamientos, usuarios);

        log.info("✅ Datos de prueba cargados exitosamente!");
        log.info("📊 Resumen:");
        log.info("   - Usuarios creados: {}", usuarios.size());
        log.info("   - Alojamientos creados: {}", alojamientos.size());
        log.info("   - Reservas creadas: {}", reservas.size());
    }

    private List<Usuario> crearUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        // Admin
        if (usuarioRepository.findByEmail("admin@homa.com").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .email("admin@homa.com")
                    .contrasena(passwordEncoder.encode("admin123"))
                    .rol(RolUsuario.Anfitrion)
                    .estado(EstadoUsuario.ACTIVO)
                    .esAnfitrion(true)
                    .codigoActivacion(UUID.randomUUID().toString())
                    .telefono("3001234567")
                    .build();
            usuarios.add(usuarioRepository.save(admin));
            log.info("👤 Usuario admin creado: admin@homa.com / admin123");
        }

        // Huésped 1
        if (usuarioRepository.findByEmail("huesped1@homa.com").isEmpty()) {
            Usuario huesped1 = Usuario.builder()
                    .nombre("Carlos Pérez")
                    .email("huesped1@homa.com")
                    .contrasena(passwordEncoder.encode("huesped123"))
                    .rol(RolUsuario.Huesped)
                    .estado(EstadoUsuario.ACTIVO)
                    .esAnfitrion(false)
                    .codigoActivacion(UUID.randomUUID().toString())
                    .telefono("3001111111")
                    .fechaNacimiento(LocalDate.of(1990, 5, 15))
                    .build();
            usuarios.add(usuarioRepository.save(huesped1));
            log.info("👤 Huésped 1 creado: huesped1@homa.com / huesped123");
        }

        // Huésped 2
        if (usuarioRepository.findByEmail("huesped2@homa.com").isEmpty()) {
            Usuario huesped2 = Usuario.builder()
                    .nombre("María García")
                    .email("huesped2@homa.com")
                    .contrasena(passwordEncoder.encode("huesped123"))
                    .rol(RolUsuario.Huesped)
                    .estado(EstadoUsuario.ACTIVO)
                    .esAnfitrion(false)
                    .codigoActivacion(UUID.randomUUID().toString())
                    .telefono("3002222222")
                    .fechaNacimiento(LocalDate.of(1985, 8, 22))
                    .build();
            usuarios.add(usuarioRepository.save(huesped2));
            log.info("👤 Huésped 2 creado: huesped2@homa.com / huesped123");
        }

        // Anfitrión 1
        if (usuarioRepository.findByEmail("anfitrion1@homa.com").isEmpty()) {
            Usuario anfitrion1 = Usuario.builder()
                    .nombre("Juan Rodríguez")
                    .email("anfitrion1@homa.com")
                    .contrasena(passwordEncoder.encode("anfitrion123"))
                    .rol(RolUsuario.Anfitrion)
                    .estado(EstadoUsuario.ACTIVO)
                    .esAnfitrion(true)
                    .codigoActivacion(UUID.randomUUID().toString())
                    .telefono("3003333333")
                    .build();
            usuarios.add(usuarioRepository.save(anfitrion1));
            log.info("👤 Anfitrión 1 creado: anfitrion1@homa.com / anfitrion123");
        }

        // Anfitrión 2
        if (usuarioRepository.findByEmail("anfitrion2@homa.com").isEmpty()) {
            Usuario anfitrion2 = Usuario.builder()
                    .nombre("Ana Martínez")
                    .email("anfitrion2@homa.com")
                    .contrasena(passwordEncoder.encode("anfitrion123"))
                    .rol(RolUsuario.Anfitrion)
                    .estado(EstadoUsuario.ACTIVO)
                    .esAnfitrion(true)
                    .codigoActivacion(UUID.randomUUID().toString())
                    .telefono("3004444444")
                    .build();
            usuarios.add(usuarioRepository.save(anfitrion2));
            log.info("👤 Anfitrión 2 creado: anfitrion2@homa.com / anfitrion123");
        }

        return usuarios;
    }

    private List<Alojamiento> crearAlojamientos(List<Usuario> usuarios) {
        List<Alojamiento> alojamientos = new ArrayList<>();
        
        // Buscar anfitriones en la lista o en la BD
        Usuario anfitrion1 = usuarios.stream()
                .filter(u -> u.getEmail().equals("anfitrion1@homa.com"))
                .findFirst()
                .orElseGet(() -> usuarioRepository.findByEmail("anfitrion1@homa.com")
                        .orElseThrow(() -> new RuntimeException("Anfitrion1 no encontrado")));
        
        Usuario anfitrion2 = usuarios.stream()
                .filter(u -> u.getEmail().equals("anfitrion2@homa.com"))
                .findFirst()
                .orElseGet(() -> usuarioRepository.findByEmail("anfitrion2@homa.com")
                        .orElseThrow(() -> new RuntimeException("Anfitrion2 no encontrado")));

        // Alojamiento 1: Apartamento céntrico en Bogotá
        if (alojamientoRepository.findByTitulo("Apartamento céntrico con vista panorámica").isEmpty()) {
            Alojamiento apto1 = Alojamiento.builder()
                    .titulo("Apartamento céntrico con vista panorámica")
                    .descripcion("Hermoso apartamento ubicado en el corazón de Bogotá, cerca de zonas turísticas, restaurantes y centros comerciales. Cuenta con acabados modernos, cocina totalmente equipada y una vista espectacular de la ciudad. Perfecto para viajes de negocios o turismo.")
                    .ciudad("Bogotá")
                    .direccion("Carrera 7 # 72-41, Chapinero")
                    .precioPorNoche(180000f)
                    .maxHuespedes(4)
                    .estado(EstadoAlojamiento.ACTIVO)
                    .anfitrion(anfitrion1)
                    .imagenes(Arrays.asList(
                            "https://placehold.co/800x600/4A90E2/FFFFFF?text=Apartamento+Bogotá+1",
                            "https://placehold.co/800x600/4A90E2/FFFFFF?text=Apartamento+Bogotá+2",
                            "https://placehold.co/800x600/4A90E2/FFFFFF?text=Apartamento+Bogotá+3"
                    ))
                    .servicios(Arrays.asList(Servicio.WIFI, Servicio.COCINA, Servicio.TV, 
                            Servicio.AIRE_ACONDICIONADO, Servicio.CALEFACCION, Servicio.ZONA_TRABAJO))
                    .latitud(4.6568f)
                    .longitud(-74.0595f)
                    .build();
            alojamientos.add(alojamientoRepository.save(apto1));
            log.info("🏠 Alojamiento creado: {}", apto1.getTitulo());
        }

        // Alojamiento 2: Casa de campo en Villa de Leyva
        if (alojamientoRepository.findByTitulo("Casa de campo colonial en Villa de Leyva").isEmpty()) {
            Alojamiento casa1 = Alojamiento.builder()
                    .titulo("Casa de campo colonial en Villa de Leyva")
                    .descripcion("Encantadora casa colonial restaurada con patio interior, jardín privado y vistas a las montañas. Disfruta de la tranquilidad de Villa de Leyva en esta casa tradicional con todas las comodidades modernas. Ideal para escapadas románticas o familiares.")
                    .ciudad("Villa de Leyva")
                    .direccion("Calle 12 # 8-23, Centro Histórico")
                    .precioPorNoche(250000f)
                    .maxHuespedes(6)
                    .estado(EstadoAlojamiento.ACTIVO)
                    .anfitrion(anfitrion1)
                    .imagenes(Arrays.asList(
                            "https://placehold.co/800x600/E74C3C/FFFFFF?text=Casa+Villa+de+Leyva+1",
                            "https://placehold.co/800x600/E74C3C/FFFFFF?text=Casa+Villa+de+Leyva+2",
                            "https://placehold.co/800x600/E74C3C/FFFFFF?text=Casa+Villa+de+Leyva+3"
                    ))
                    .servicios(Arrays.asList(Servicio.WIFI, Servicio.COCINA, Servicio.JARDIN, 
                            Servicio.PARRILLA, Servicio.CHIMENEA, Servicio.ESTACIONAMIENTO))
                    .latitud(5.6366f)
                    .longitud(-73.5266f)
                    .build();
            alojamientos.add(alojamientoRepository.save(casa1));
            log.info("🏠 Alojamiento creado: {}", casa1.getTitulo());
        }

        // Alojamiento 3: Loft moderno en Medellín
        if (alojamientoRepository.findByTitulo("Loft moderno en el Poblado").isEmpty()) {
            Alojamiento loft1 = Alojamiento.builder()
                    .titulo("Loft moderno en el Poblado")
                    .descripcion("Espectacular loft de diseño contemporáneo en el exclusivo sector de El Poblado. Espacios abiertos, iluminación natural, terraza privada con jacuzzi y vista a la ciudad. Cerca de los mejores restaurantes, bares y centros comerciales de Medellín.")
                    .ciudad("Medellín")
                    .direccion("Carrera 43A # 1A Sur-50, El Poblado")
                    .precioPorNoche(320000f)
                    .maxHuespedes(2)
                    .estado(EstadoAlojamiento.ACTIVO)
                    .anfitrion(anfitrion2)
                    .imagenes(Arrays.asList(
                            "https://placehold.co/800x600/2ECC71/FFFFFF?text=Loft+Medellín+1",
                            "https://placehold.co/800x600/2ECC71/FFFFFF?text=Loft+Medellín+2",
                            "https://placehold.co/800x600/2ECC71/FFFFFF?text=Loft+Medellín+3"
                    ))
                    .servicios(Arrays.asList(Servicio.WIFI, Servicio.COCINA, Servicio.JACUZZI, 
                            Servicio.AIRE_ACONDICIONADO, Servicio.TV, Servicio.GIMNASIO))
                    .latitud(6.2086f)
                    .longitud(-75.5701f)
                    .build();
            alojamientos.add(alojamientoRepository.save(loft1));
            log.info("🏠 Alojamiento creado: {}", loft1.getTitulo());
        }

        // Alojamiento 4: Casa familiar en Cali
        if (alojamientoRepository.findByTitulo("Casa familiar con piscina en Cali").isEmpty()) {
            Alojamiento casa2 = Alojamiento.builder()
                    .titulo("Casa familiar con piscina en Cali")
                    .descripcion("Espaciosa casa familiar perfecta para vacaciones en la capital de la salsa. Cuenta con piscina privada, jardín amplio, zona de BBQ y espacios abiertos para disfrutar del clima cálido de Cali. Cerca del centro y zonas turísticas.")
                    .ciudad("Cali")
                    .direccion("Avenida 9N # 12N-45, Granada")
                    .precioPorNoche(280000f)
                    .maxHuespedes(8)
                    .estado(EstadoAlojamiento.ACTIVO)
                    .anfitrion(anfitrion2)
                    .imagenes(Arrays.asList(
                            "https://placehold.co/800x600/F39C12/FFFFFF?text=Casa+Cali+1",
                            "https://placehold.co/800x600/F39C12/FFFFFF?text=Casa+Cali+2",
                            "https://placehold.co/800x600/F39C12/FFFFFF?text=Casa+Cali+3"
                    ))
                    .servicios(Arrays.asList(Servicio.WIFI, Servicio.COCINA, Servicio.PISCINA, 
                            Servicio.JARDIN, Servicio.PARRILLA, Servicio.ESTACIONAMIENTO, Servicio.TV))
                    .latitud(3.4516f)
                    .longitud(-76.5320f)
                    .build();
            alojamientos.add(alojamientoRepository.save(casa2));
            log.info("🏠 Alojamiento creado: {}", casa2.getTitulo());
        }

        // Alojamiento 5: Habitación privada en Cartagena
        if (alojamientoRepository.findByTitulo("Habitación privada en casa colonial").isEmpty()) {
            Alojamiento hab1 = Alojamiento.builder()
                    .titulo("Habitación privada en casa colonial")
                    .descripcion("Acogedora habitación en una hermosa casa colonial restaurada en el centro histórico de Cartagena. Baño privado, aire acondicionado y acceso a terraza con vista a la ciudad amurallada. Desayuno incluido.")
                    .ciudad("Cartagena")
                    .direccion("Calle del Curato # 38-99, Centro Histórico")
                    .precioPorNoche(120000f)
                    .maxHuespedes(2)
                    .estado(EstadoAlojamiento.ACTIVO)
                    .anfitrion(anfitrion1)
                    .imagenes(Arrays.asList(
                            "https://placehold.co/800x600/9B59B6/FFFFFF?text=Habitación+Cartagena+1",
                            "https://placehold.co/800x600/9B59B6/FFFFFF?text=Habitación+Cartagena+2"
                    ))
                    .servicios(Arrays.asList(Servicio.WIFI, Servicio.AIRE_ACONDICIONADO, 
                            Servicio.BALCON, Servicio.TV))
                    .latitud(10.4236f)
                    .longitud(-75.5510f)
                    .build();
            alojamientos.add(alojamientoRepository.save(hab1));
            log.info("🏠 Alojamiento creado: {}", hab1.getTitulo());
        }

        // Alojamiento 6: Penthouse de lujo en Bogotá
        if (alojamientoRepository.findByTitulo("Penthouse de lujo con terraza").isEmpty()) {
            Alojamiento ph1 = Alojamiento.builder()
                    .titulo("Penthouse de lujo con terraza")
                    .descripcion("Exclusivo penthouse con acabados de lujo, terraza panorámica de 100m², jacuzzi privado y vistas 360° de Bogotá. Ubicado en la zona más exclusiva de la ciudad, cerca de embajadas, restaurantes de alta cocina y centros de negocios.")
                    .ciudad("Bogotá")
                    .direccion("Carrera 9 # 74-08, Rosales")
                    .precioPorNoche(550000f)
                    .maxHuespedes(4)
                    .estado(EstadoAlojamiento.ACTIVO)
                    .anfitrion(anfitrion2)
                    .imagenes(Arrays.asList(
                            "https://placehold.co/800x600/1ABC9C/FFFFFF?text=Penthouse+Bogotá+1",
                            "https://placehold.co/800x600/1ABC9C/FFFFFF?text=Penthouse+Bogotá+2",
                            "https://placehold.co/800x600/1ABC9C/FFFFFF?text=Penthouse+Bogotá+3"
                    ))
                    .servicios(Arrays.asList(Servicio.WIFI, Servicio.COCINA, Servicio.JACUZZI, 
                            Servicio.GIMNASIO, Servicio.AIRE_ACONDICIONADO, Servicio.CALEFACCION, 
                            Servicio.TV, Servicio.ZONA_TRABAJO, Servicio.ALARMA))
                    .latitud(4.6584f)
                    .longitud(-74.0589f)
                    .build();
            alojamientos.add(alojamientoRepository.save(ph1));
            log.info("🏠 Alojamiento creado: {}", ph1.getTitulo());
        }

        return alojamientos;
    }

    private List<Reserva> crearReservas(List<Alojamiento> alojamientos, List<Usuario> usuarios) {
        List<Reserva> reservas = new ArrayList<>();
        
        // Buscar huéspedes en la lista o en la BD
        Usuario huesped1 = usuarios.stream()
                .filter(u -> u.getEmail().equals("huesped1@homa.com"))
                .findFirst()
                .orElseGet(() -> usuarioRepository.findByEmail("huesped1@homa.com")
                        .orElseThrow(() -> new RuntimeException("Huesped1 no encontrado")));
        
        Usuario huesped2 = usuarios.stream()
                .filter(u -> u.getEmail().equals("huesped2@homa.com"))
                .findFirst()
                .orElseGet(() -> usuarioRepository.findByEmail("huesped2@homa.com")
                        .orElseThrow(() -> new RuntimeException("Huesped2 no encontrado")));

        // Solo crear reservas si hay alojamientos
        if (alojamientos.isEmpty()) {
            return reservas;
        }

        // Reserva 1: Completada (para reseña)
        Alojamiento aptoBogota = alojamientos.stream()
                .filter(a -> a.getTitulo().contains("Apartamento céntrico"))
                .findFirst()
                .orElse(alojamientos.get(0));
        
        Reserva reserva1 = Reserva.builder()
                .alojamiento(aptoBogota)
                .huesped(huesped1)
                .cantidadHuespedes(2)
                .fechaEntrada(LocalDate.now().minusDays(30))
                .fechaSalida(LocalDate.now().minusDays(25))
                .precio(Double.valueOf(aptoBogota.getPrecioPorNoche() * 5))
                .estado(EstadoReserva.COMPLETADA)
                .build();
        reservas.add(reservaRepository.save(reserva1));

        // Reserva 2: Completada (para reseña)
        Alojamiento casaVilla = alojamientos.stream()
                .filter(a -> a.getTitulo().contains("Villa de Leyva"))
                .findFirst()
                .orElse(alojamientos.get(0));
        
        Reserva reserva2 = Reserva.builder()
                .alojamiento(casaVilla)
                .huesped(huesped2)
                .cantidadHuespedes(4)
                .fechaEntrada(LocalDate.now().minusDays(20))
                .fechaSalida(LocalDate.now().minusDays(17))
                .precio(Double.valueOf(casaVilla.getPrecioPorNoche() * 3))
                .estado(EstadoReserva.COMPLETADA)
                .build();
        reservas.add(reservaRepository.save(reserva2));

        // Reserva 3: Completada (para reseña)
        Alojamiento loftMedellin = alojamientos.stream()
                .filter(a -> a.getTitulo().contains("Loft moderno"))
                .findFirst()
                .orElse(alojamientos.get(0));
        
        Reserva reserva3 = Reserva.builder()
                .alojamiento(loftMedellin)
                .huesped(huesped1)
                .cantidadHuespedes(2)
                .fechaEntrada(LocalDate.now().minusDays(15))
                .fechaSalida(LocalDate.now().minusDays(13))
                .precio(Double.valueOf(loftMedellin.getPrecioPorNoche() * 2))
                .estado(EstadoReserva.COMPLETADA)
                .build();
        reservas.add(reservaRepository.save(reserva3));

        // Reserva 4: Pendiente
        Alojamiento casaCali = alojamientos.stream()
                .filter(a -> a.getTitulo().contains("Cali"))
                .findFirst()
                .orElse(alojamientos.get(0));
        
        Reserva reserva4 = Reserva.builder()
                .alojamiento(casaCali)
                .huesped(huesped2)
                .cantidadHuespedes(6)
                .fechaEntrada(LocalDate.now().plusDays(10))
                .fechaSalida(LocalDate.now().plusDays(15))
                .precio(Double.valueOf(casaCali.getPrecioPorNoche() * 5))
                .estado(EstadoReserva.PENDIENTE)
                .build();
        reservas.add(reservaRepository.save(reserva4));

        // Reserva 5: Confirmada
        Alojamiento habCartagena = alojamientos.stream()
                .filter(a -> a.getTitulo().contains("Cartagena"))
                .findFirst()
                .orElse(alojamientos.get(0));
        
        Reserva reserva5 = Reserva.builder()
                .alojamiento(habCartagena)
                .huesped(huesped1)
                .cantidadHuespedes(2)
                .fechaEntrada(LocalDate.now().plusDays(5))
                .fechaSalida(LocalDate.now().plusDays(8))
                .precio(Double.valueOf(habCartagena.getPrecioPorNoche() * 3))
                .estado(EstadoReserva.CONFIRMADA)
                .build();
        reservas.add(reservaRepository.save(reserva5));

        // Reserva 6: Cancelada
        Alojamiento phBogota = alojamientos.stream()
                .filter(a -> a.getTitulo().contains("Penthouse"))
                .findFirst()
                .orElse(alojamientos.get(0));
        
        Reserva reserva6 = Reserva.builder()
                .alojamiento(phBogota)
                .huesped(huesped2)
                .cantidadHuespedes(2)
                .fechaEntrada(LocalDate.now().plusDays(20))
                .fechaSalida(LocalDate.now().plusDays(25))
                .precio(Double.valueOf(phBogota.getPrecioPorNoche() * 5))
                .estado(EstadoReserva.CANCELADA)
                .build();
        reservas.add(reservaRepository.save(reserva6));

        log.info("📅 Reservas creadas: {} (3 completadas, 1 confirmada, 1 pendiente, 1 cancelada)", reservas.size());
        return reservas;
    }

    private void crearResenas(List<Reserva> reservas, List<Usuario> usuarios) {
        // Solo crear reseñas para reservas completadas
        List<Reserva> completadas = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.COMPLETADA)
                .toList();

        if (completadas.isEmpty()) {
            return;
        }

        // Reseña 1
        Reserva r1 = completadas.get(0);
        if (resenaRepository.findByAlojamientoAndUsuario(r1.getAlojamiento(), r1.getHuesped()).isEmpty()) {
            Resena resena1 = Resena.builder()
                    .alojamiento(r1.getAlojamiento())
                    .usuario(r1.getHuesped())
                    .calificacion(5)
                    .comentario("¡Excelente experiencia! El apartamento estaba impecable, muy bien ubicado y con todas las comodidades. El anfitrión fue muy atento y respondió rápidamente a todas nuestras preguntas. Definitivamente volveremos.")
                    .esDestacado(true)
                    .build();
            resenaRepository.save(resena1);
            log.info("⭐ Reseña creada: 5 estrellas para {}", r1.getAlojamiento().getTitulo());
        }

        // Reseña 2
        if (completadas.size() > 1) {
            Reserva r2 = completadas.get(1);
            if (resenaRepository.findByAlojamientoAndUsuario(r2.getAlojamiento(), r2.getHuesped()).isEmpty()) {
                Resena resena2 = Resena.builder()
                        .alojamiento(r2.getAlojamiento())
                        .usuario(r2.getHuesped())
                        .calificacion(4)
                        .comentario("Lugar muy bonito y acogedor. La casa colonial tiene mucho encanto y el jardín es perfecto para relajarse. Un poco frío en las noches pero la chimenea lo compensa. Recomendado para una escapada tranquila.")
                        .esDestacado(false)
                        .build();
                resenaRepository.save(resena2);
                log.info("⭐ Reseña creada: 4 estrellas para {}", r2.getAlojamiento().getTitulo());
            }
        }

        // Reseña 3
        if (completadas.size() > 2) {
            Reserva r3 = completadas.get(2);
            if (resenaRepository.findByAlojamientoAndUsuario(r3.getAlojamiento(), r3.getHuesped()).isEmpty()) {
                Resena resena3 = Resena.builder()
                        .alojamiento(r3.getAlojamiento())
                        .usuario(r3.getHuesped())
                        .calificacion(5)
                        .comentario("El loft es espectacular, las fotos no le hacen justicia. Diseño moderno, jacuzzi en la terraza con vista increíble a Medellín. Perfecto para una escapada romántica. Zona muy segura y cerca de todo.")
                        .esDestacado(true)
                        .build();
                resenaRepository.save(resena3);
                log.info("⭐ Reseña creada: 5 estrellas para {}", r3.getAlojamiento().getTitulo());
            }
        }

        log.info("📝 Total reseñas creadas");
    }

    private void crearFavoritos(List<Alojamiento> alojamientos, List<Usuario> usuarios) {
        if (alojamientos.size() < 3) {
            return;
        }

        Usuario huesped1 = usuarios.stream()
                .filter(u -> u.getEmail().equals("huesped1@homa.com"))
                .findFirst()
                .orElse(null);
        
        Usuario huesped2 = usuarios.stream()
                .filter(u -> u.getEmail().equals("huesped2@homa.com"))
                .findFirst()
                .orElse(null);

        if (huesped1 != null) {
            // Favoritos de huesped1
            for (int i = 0; i < Math.min(3, alojamientos.size()); i++) {
                Alojamiento alojamiento = alojamientos.get(i);
                if (favoritoRepository.findByUsuarioAndAlojamiento(huesped1, alojamiento).isEmpty()) {
                    Favorito favorito = Favorito.builder()
                            .usuario(huesped1)
                            .alojamiento(alojamiento)
                            .build();
                    favoritoRepository.save(favorito);
                }
            }
        }

        if (huesped2 != null && alojamientos.size() >= 4) {
            // Favoritos de huesped2 (diferentes)
            for (int i = 2; i < Math.min(5, alojamientos.size()); i++) {
                Alojamiento alojamiento = alojamientos.get(i);
                if (favoritoRepository.findByUsuarioAndAlojamiento(huesped2, alojamiento).isEmpty()) {
                    Favorito favorito = Favorito.builder()
                            .usuario(huesped2)
                            .alojamiento(alojamiento)
                            .build();
                    favoritoRepository.save(favorito);
                }
            }
        }

        log.info("❤️ Favoritos creados para huéspedes");
    }
}
