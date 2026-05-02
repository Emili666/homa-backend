package poo.uniquindio.edu.co.Homa.config;

import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.repository.ReservaRepository;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoReserva;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoAlojamiento;

/**
 * HOMA — Métricas de negocio para Prometheus/Grafana
 *
 * Métricas registradas:
 * - homa_logins_total → Conteo de logins (exitosos / fallidos)
 * - homa_registros_total → Conteo de registros de usuarios
 * - homa_reservas_creadas_total → Reservas creadas
 * - homa_reservas_canceladas_total → Reservas canceladas
 * - homa_reservas_confirmadas_total → Reservas confirmadas
 * - homa_alojamientos_creados_total → Alojamientos publicados
 * - homa_usuarios_activos → Gauge: usuarios activos en BD
 * - homa_reservas_pendientes → Gauge: reservas pendientes en BD
 * - homa_alojamientos_activos → Gauge: alojamientos activos en BD
 * ── NUEVAS MÉTRICAS DE RESERVAS (ISO/IEC 25010) ──
 * - homa_reservas_rechazadas_total → Reservas rechazadas por anfitrión
 * - homa_reservas_completadas_gauge → Gauge: reservas completadas en BD
 * - homa_reservas_canceladas_gauge → Gauge: reservas canceladas en BD
 * - homa_reserva_duracion_noches → Histogram: distribución de noches por reserva
 * - homa_reserva_precio_total → Histogram: distribución de precios de reserva
 * - homa_reserva_creacion_duration_seconds → Timer: tiempo de creación de reserva
 * - homa_disponibilidad_consultas_total → Conteo de consultas de disponibilidad
 * - homa_disponibilidad_no_disponible_total → Conteo de alojamientos no disponibles
 */
@Slf4j
@Configuration
public class MetricsConfig {

    /**
     * Bean que registra los Gauges (valores en tiempo real) basados en la BD.
     * Los Counters y Timers se usan directamente con @Autowired en los servicios.
     */
    @Bean
    public HomaBusinessMetrics homaBusinessMetrics(
            MeterRegistry registry,
            UsuarioRepository usuarioRepository,
            ReservaRepository reservaRepository,
            AlojamientoRepository alojamientoRepository) {

        // ── GAUGES (valores que van y vienen) ─────────────────────────────────────
        // Se actualizan automáticamente cada vez que Prometheus scrapea

        Gauge.builder("homa_usuarios_activos", usuarioRepository,
                repo -> repo.countByEstado(EstadoUsuario.ACTIVO))
                .description("Número de usuarios con estado ACTIVO en la base de datos")
                .tag("entidad", "usuario")
                .register(registry);

        Gauge.builder("homa_reservas_pendientes", reservaRepository,
                repo -> repo.countByEstado(EstadoReserva.PENDIENTE_CONFIRMACION))
                .description("Número de reservas en estado PENDIENTE_CONFIRMACION")
                .tag("entidad", "reserva")
                .register(registry);

        Gauge.builder("homa_reservas_confirmadas_gauge", reservaRepository,
                repo -> repo.countByEstado(EstadoReserva.CONFIRMADA))
                .description("Número de reservas en estado CONFIRMADA")
                .tag("entidad", "reserva")
                .register(registry);

        Gauge.builder("homa_alojamientos_activos", alojamientoRepository,
                repo -> repo.countByEstado(EstadoAlojamiento.ACTIVO))
                .description("Número de alojamientos con estado ACTIVO")
                .tag("entidad", "alojamiento")
                .register(registry);

        Gauge.builder("homa_alojamientos_pendientes_revision", alojamientoRepository,
                repo -> repo.countByEstado(EstadoAlojamiento.PENDIENTE))
                .description("Número de alojamientos pendientes de revisión admin")
                .tag("entidad", "alojamiento")
                .register(registry);

        // ── NUEVOS GAUGES DE RESERVAS ──────────────────────────────────────────────
        Gauge.builder("homa_reservas_completadas_gauge", reservaRepository,
                repo -> repo.countByEstado(EstadoReserva.COMPLETADA))
                .description("Número de reservas en estado COMPLETADA")
                .tag("entidad", "reserva")
                .register(registry);

        Gauge.builder("homa_reservas_canceladas_gauge", reservaRepository,
                repo -> repo.countByEstado(EstadoReserva.CANCELADA))
                .description("Número de reservas en estado CANCELADA")
                .tag("entidad", "reserva")
                .register(registry);

        log.info("[Métricas HOMA] Gauges de negocio registrados en Micrometer.");
        return new HomaBusinessMetrics(registry);
    }

    /**
     * Clase wrapper que proporciona los Counters y Timers para uso en los
     * servicios.
     * Se inyecta con @Autowired donde sea necesario.
     */
    public static class HomaBusinessMetrics {

        // ── COUNTERS (solo suben) ──────────────────────────────────────────────────
        private final Counter loginExitoso;
        private final Counter loginFallido;
        private final Counter registroUsuario;
        private final Counter reservaCreada;
        private final Counter reservaCancelada;
        private final Counter reservaConfirmada;
        private final Counter reservaCompletada;
        private final Counter reservaRechazada;
        private final Counter alojamientoCreado;
        private final Counter alojamientoEliminado;
        private final Counter disponibilidadConsulta;
        private final Counter disponibilidadNoDisponible;

        // ── TIMERS (duración de operaciones clave) ─────────────────────────────────
        private final Timer timerLogin;
        private final Timer timerReservaCreacion;

        // ── COUNTERS ACUMULADOS DE NEGOCIO ─────────────────────────────────────────
        private final Counter nochesReservadas;
        private final Counter ingresosReservados;

        public HomaBusinessMetrics(MeterRegistry registry) {
            this.loginExitoso = Counter.builder("homa_logins_total")
                    .description("Total de inicios de sesión")
                    .tag("resultado", "exitoso")
                    .register(registry);

            this.loginFallido = Counter.builder("homa_logins_total")
                    .description("Total de inicios de sesión")
                    .tag("resultado", "fallido")
                    .register(registry);

            this.registroUsuario = Counter.builder("homa_registros_total")
                    .description("Total de registros de nuevos usuarios")
                    .register(registry);

            this.reservaCreada = Counter.builder("homa_reservas_total")
                    .description("Total de reservas según acción")
                    .tag("accion", "creada")
                    .register(registry);

            this.reservaCancelada = Counter.builder("homa_reservas_total")
                    .description("Total de reservas según acción")
                    .tag("accion", "cancelada")
                    .register(registry);

            this.reservaConfirmada = Counter.builder("homa_reservas_total")
                    .description("Total de reservas según acción")
                    .tag("accion", "confirmada")
                    .register(registry);

            this.reservaCompletada = Counter.builder("homa_reservas_total")
                    .description("Total de reservas según acción")
                    .tag("accion", "completada")
                    .register(registry);

            this.reservaRechazada = Counter.builder("homa_reservas_total")
                    .description("Total de reservas según acción")
                    .tag("accion", "rechazada")
                    .register(registry);

            this.alojamientoCreado = Counter.builder("homa_alojamientos_total")
                    .description("Total de alojamientos según acción")
                    .tag("accion", "creado")
                    .register(registry);

            this.alojamientoEliminado = Counter.builder("homa_alojamientos_total")
                    .description("Total de alojamientos según acción")
                    .tag("accion", "eliminado")
                    .register(registry);

            this.timerLogin = Timer.builder("homa_login_duration_seconds")
                    .description("Tiempo de respuesta del proceso de login")
                    .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                    .register(registry);

            this.timerReservaCreacion = Timer.builder("homa_reserva_creacion_duration_seconds")
                    .description("Tiempo de creación de una reserva (incluyendo validaciones)")
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .register(registry);

            this.disponibilidadConsulta = Counter.builder("homa_disponibilidad_consultas_total")
                    .description("Total de consultas de disponibilidad de alojamientos")
                    .register(registry);

            this.disponibilidadNoDisponible = Counter.builder("homa_disponibilidad_no_disponible_total")
                    .description("Total de consultas donde el alojamiento NO estaba disponible")
                    .register(registry);

            this.nochesReservadas = Counter.builder("homa_reserva_noches_total")
                    .description("Total acumulado de noches reservadas en el sistema")
                    .baseUnit("noches")
                    .register(registry);

            this.ingresosReservados = Counter.builder("homa_reserva_ingresos_total")
                    .description("Total acumulado de ingresos generados por reservas en COP")
                    .baseUnit("COP")
                    .register(registry);
        }

        // ── Métodos de acceso ──────────────────────────────────────────────────────

        public void incrementLoginExitoso() {
            loginExitoso.increment();
        }

        public void incrementLoginFallido() {
            loginFallido.increment();
        }

        public void incrementRegistro() {
            registroUsuario.increment();
        }

        public void incrementReservaCreada() {
            reservaCreada.increment();
        }

        public void incrementReservaCancelada() {
            reservaCancelada.increment();
        }

        public void incrementReservaConfirmada() {
            reservaConfirmada.increment();
        }

        public void incrementReservaCompletada() {
            reservaCompletada.increment();
        }

        public void incrementAlojamientoCreado() {
            alojamientoCreado.increment();
        }

        public void incrementAlojamientoEliminado() {
            alojamientoEliminado.increment();
        }

        public Timer getTimerLogin() {
            return timerLogin;
        }

        public Timer getTimerReservaCreacion() {
            return timerReservaCreacion;
        }

        public void incrementReservaRechazada() {
            reservaRechazada.increment();
        }

        public void incrementDisponibilidadConsulta() {
            disponibilidadConsulta.increment();
        }

        public void incrementDisponibilidadNoDisponible() {
            disponibilidadNoDisponible.increment();
        }

        public void registrarDuracionReserva(long noches) {
            nochesReservadas.increment(noches);
        }

        public void registrarPrecioReserva(double precio) {
            ingresosReservados.increment(precio);
        }
    }
}
