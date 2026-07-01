package negocio;

import modelo.Estudiante;
import modelo.Materia;
import modelo.ReporteProgreso;
import negocio.excepciones.DatosInvalidosException;

/**
 * MODULO 4 - Panel de Progreso y Reportes (David Galarza).
 * CONTROLADOR que procesa el avance del estudiante para calcular
 * porcentajes y disparar alertas de riesgo academico.
 *
 * Reglas de negocio:
 *   - ESTADO: DOMINIO (>= 80%), EN PROGRESO (>= 60%), EN RIESGO (< 60%).
 *   - ALERTA (RF4.2): riesgo si hay mas de 5 dias sin practicar O el
 *     rendimiento global es menor a 60%.
 */
public class GestorDashboard {

    public static final int DIAS_RIESGO_INACTIVIDAD = 5;
    public static final double UMBRAL_DOMINIO = 80.0;
    public static final double UMBRAL_RIESGO = 60.0;

    /** Crea el reporte vinculado al estudiante (composicion). */
    public ReporteProgreso crearReporte(Estudiante estudiante) {
        return new ReporteProgreso(1, estudiante);
    }

    /**
     * Registra el avance del estudiante en una materia (0-100).
     *
     * @throws DatosInvalidosException si el reporte/materia son nulos o el
     *                                 porcentaje esta fuera de rango
     */
    public void registrarAvance(ReporteProgreso reporte, Materia m, double porcentaje)
            throws DatosInvalidosException {
        if (reporte == null || m == null) {
            throw new DatosInvalidosException("No hay reporte o materia valida para registrar el avance.");
        }
        if (porcentaje < 0.0 || porcentaje > 100.0) {
            throw new DatosInvalidosException("El porcentaje de avance debe estar entre 0 y 100.");
        }
        reporte.registrarAvance(m, porcentaje);
    }

    /** Actualiza el contador de dias sin practicar. */
    public void actualizarInactividad(ReporteProgreso reporte, int dias) throws DatosInvalidosException {
        if (reporte == null) {
            throw new DatosInvalidosException("No hay un reporte activo.");
        }
        if (dias < 0) {
            throw new DatosInvalidosException("Los dias sin practicar no pueden ser negativos.");
        }
        reporte.setDiasSinPracticar(dias);
    }

    /** Clasifica el estado academico segun el rendimiento global. */
    public String clasificarEstado(ReporteProgreso reporte) {
        if (reporte == null) return "SIN DATOS";
        double r = reporte.getPorcentajeRendimiento();
        if (r >= UMBRAL_DOMINIO) return "DOMINIO";
        if (r >= UMBRAL_RIESGO) return "EN PROGRESO";
        return "EN RIESGO";
    }

    /** Genera el resumen ejecutivo del reporte con estado y alertas. */
    public String generarResumen(ReporteProgreso reporte) {
        if (reporte == null) return "Sin datos de progreso.";
        StringBuilder sb = new StringBuilder();
        sb.append("Rendimiento global: ")
          .append(String.format("%.1f", reporte.getPorcentajeRendimiento())).append("%\n");
        sb.append("Dias sin practicar: ").append(reporte.getDiasSinPracticar()).append("\n");
        sb.append("Estado: ").append(clasificarEstado(reporte));
        String alerta = reporte.generarAlertaRiesgo();
        if (alerta != null && !alerta.isEmpty()) {
            sb.append("\n\nAlertas:\n").append(alerta);
        }
        return sb.toString();
    }

    /** Determina si el estudiante esta en riesgo academico (RF4.2). */
    public boolean estaEnRiesgo(ReporteProgreso reporte) {
        if (reporte == null) return false;
        if (reporte.getDiasSinPracticar() > DIAS_RIESGO_INACTIVIDAD) return true;
        return reporte.getPorcentajeRendimiento() > 0
                && reporte.getPorcentajeRendimiento() < UMBRAL_RIESGO;
    }
}
