package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Asignatura de ciencias basicas de la carrera de Ingenieria, dictada
 * entre el 1er y 4to semestre (Calculo, Algebra Lineal, Fisica, etc.).
 * Contiene su nivel (semestre), si es materia filtro y la lista de
 * prerrequisitos (auto-asociacion Materia-Materia).
 *
 * Es la entidad transversal compartida por todos los modulos del
 * sistema, asociada a Estudiante, Ejercicio y ReporteProgreso.
 */
public class Materia {

    private int idMateria;
    private String nombre;
    private int nivelSemestre;
    private boolean esMateriaFiltro;
    private List<Materia> prerrequisitos;

    public Materia() {
        this.prerrequisitos = new ArrayList<>();
    }

    public Materia(int idMateria, String nombre, int nivelSemestre, boolean esMateriaFiltro) {
        this.idMateria = idMateria;
        this.nombre = nombre;
        this.nivelSemestre = nivelSemestre;
        this.esMateriaFiltro = esMateriaFiltro;
        this.prerrequisitos = new ArrayList<>();
    }

    /** Retorna la lista de materias que deben aprobarse antes de esta. */
    public List<Materia> obtenerPrerrequisitos() {
        return prerrequisitos;
    }

    /** Agrega una materia como prerrequisito de la actual. */
    public void agregarPrerrequisito(Materia m) {
        if (m != null && !this.prerrequisitos.contains(m)) {
            this.prerrequisitos.add(m);
        }
    }

    @Override
    public String toString() {
        String filtro = esMateriaFiltro ? " [Filtro]" : "";
        return nombre + " (Semestre " + nivelSemestre + ")" + filtro;
    }

    // Getters y setters
    public int getIdMateria() { return idMateria; }
    public void setIdMateria(int idMateria) { this.idMateria = idMateria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getNivelSemestre() { return nivelSemestre; }
    public void setNivelSemestre(int nivelSemestre) { this.nivelSemestre = nivelSemestre; }

    public boolean isEsMateriaFiltro() { return esMateriaFiltro; }
    public void setEsMateriaFiltro(boolean esMateriaFiltro) { this.esMateriaFiltro = esMateriaFiltro; }

    public void setPrerrequisitos(List<Materia> prerrequisitos) { this.prerrequisitos = prerrequisitos; }
}
