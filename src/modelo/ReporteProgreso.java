package modelo;

import java.util.HashMap;
import java.util.Map;

/**
 * Reporte de progreso academico del estudiante. Relacion de COMPOSICION
 * con Estudiante: si el registro del estudiante se elimina, su reporte
 * deja de existir. Almacena los dias sin practicar, el rendimiento
 * porcentual global y el avance por materia.
 *
 * Nota de arquitectura (MVC): esta clase del MODELO NO imprime en
 * consola. Devuelve datos (double, String, Map) para que la capa de
 * interfaz decida como mostrarlos (barras graficas, etiquetas, etc.).
 */
public class ReporteProgreso {

    private int idReporte;
    private int diasSinPracticar;
    private double porcentajeRendimiento;
    private Map<Materia, Double> avancePorMateria;
    private Estudiante estudiante;

    public ReporteProgreso() {
        this.avancePorMateria = new HashMap<>();
    }

    public ReporteProgreso(int idReporte, Estudiante estudiante) {
        this.idReporte = idReporte;
        this.estudiante = estudiante;
        this.diasSinPracticar = 0;
        this.porcentajeRendimiento = 0.0;
        this.avancePorMateria = new HashMap<>();
    }

    /**
     * Retorna el porcentaje de avance registrado en una materia.
     * (Antes imprimia una barra en consola; ahora solo devuelve el dato
     * y la interfaz grafica dibuja la barra con un JProgressBar).
     *
     * @param m materia consultada
     * @return porcentaje de avance 0-100 (0 si no hay registro)
     */
    public double obtenerAvance(Materia m) {
        if (m == null) {
            return 0.0;
        }
        return avancePorMateria.getOrDefault(m, 0.0);
    }

    /**
     * Genera un mensaje de alerta de riesgo academico cuando el
     * estudiante lleva muchos dias sin practicar o su rendimiento
     * global es bajo. Devuelve texto (no imprime).
     *
     * @return mensaje de alerta; cadena vacia si no hay riesgo
     */
    public String generarAlertaRiesgo() {
        StringBuilder alerta = new StringBuilder();
        if (diasSinPracticar > 5) {
            alerta.append("Llevas ").append(diasSinPracticar)
                  .append(" dias sin practicar. Retoma tu ruta de estudio.\n");
        }
        if (porcentajeRendimiento < 60.0 && porcentajeRendimiento > 0.0) {
            alerta.append("Tu rendimiento global es de ")
                  .append(String.format("%.1f", porcentajeRendimiento))
                  .append("%. Estas en riesgo academico.\n");
        }
        return alerta.toString();
    }

    public void registrarAvance(Materia m, double porcentaje) {
        if (m != null) {
            this.avancePorMateria.put(m, porcentaje);
            recalcularPromedio();
        }
    }

    private void recalcularPromedio() {
        if (avancePorMateria.isEmpty()) {
            this.porcentajeRendimiento = 0.0;
            return;
        }
        double suma = 0.0;
        for (Double v : avancePorMateria.values()) {
            suma += v;
        }
        this.porcentajeRendimiento = suma / avancePorMateria.size();
    }

    // Getters y setters
    public int getIdReporte() { return idReporte; }
    public void setIdReporte(int idReporte) { this.idReporte = idReporte; }

    public int getDiasSinPracticar() { return diasSinPracticar; }
    public void setDiasSinPracticar(int diasSinPracticar) { this.diasSinPracticar = diasSinPracticar; }

    public double getPorcentajeRendimiento() { return porcentajeRendimiento; }
    public void setPorcentajeRendimiento(double porcentajeRendimiento) { this.porcentajeRendimiento = porcentajeRendimiento; }

    public Map<Materia, Double> getAvancePorMateria() { return avancePorMateria; }
    public void setAvancePorMateria(Map<Materia, Double> avancePorMateria) { this.avancePorMateria = avancePorMateria; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
}
