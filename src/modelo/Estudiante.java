package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Estudiante de Ingenieria cursando entre el 1er y 4to semestre (etapa
 * de ciencias basicas). ES-UN {@link Usuario}: hereda identificador,
 * nombres y credenciales, y agrega los atributos academicos del dominio
 * (cedula, semestre, indice de carga y materias cursadas).
 *
 * Principio POO: Herencia (extends Usuario) y reutilizacion del metodo
 * heredado iniciarSesion(). Redefine getRol() (polimorfismo).
 */
public class Estudiante extends Usuario {

    /** Semestre minimo aceptado (inicio de ciencias basicas). */
    public static final int SEMESTRE_MINIMO = 1;
    /** Semestre maximo aceptado (cierre de la etapa de ciencias basicas). */
    public static final int SEMESTRE_MAXIMO = 4;

    private String cedula;
    private int semestreActual;
    private int indiceCargaCiencias;
    private List<Materia> materiasCursadas;

    public Estudiante() {
        super();
        this.materiasCursadas = new ArrayList<>();
    }

    public Estudiante(int idUsuario, String correoUdla, String contrasena,
                      String cedula, String nombres, int semestreActual) {
        super(idUsuario, nombres, correoUdla, contrasena);
        this.cedula = cedula;
        this.semestreActual = semestreActual;
        this.indiceCargaCiencias = 0;
        this.materiasCursadas = new ArrayList<>();
    }

    @Override
    public String getRol() {
        return "ESTUDIANTE";
    }

    /**
     * Calcula el indice de carga academica en escala 1-10 a partir de
     * las materias inscritas. Las materias filtro suman peso adicional
     * porque historicamente concentran los indices mas altos de
     * reprobacion en ciencias basicas.
     *
     * Regla de negocio del dominio (comportamiento propio de la entidad):
     * cada materia aporta su nivel de semestre como peso base y las
     * materias filtro aportan +2. El resultado se normaliza a 1-10.
     *
     * @param materias lista de materias inscritas en el semestre
     * @return indice de carga en escala 0-10 (0 si no hay materias)
     */
    public int calcularIndiceCarga(List<Materia> materias) {
        if (materias == null || materias.isEmpty()) {
            this.indiceCargaCiencias = 0;
            return 0;
        }
        int suma = 0;
        for (Materia m : materias) {
            int peso = m.getNivelSemestre();
            if (m.isEsMateriaFiltro()) {
                peso += 2; // las materias filtro elevan la carga percibida
            }
            suma += peso;
        }
        int indice = Math.min(10, suma / Math.max(1, materias.size()) + materias.size());
        this.indiceCargaCiencias = indice;
        return indice;
    }

    public void agregarMateria(Materia m) {
        if (m != null && !this.materiasCursadas.contains(m)) {
            this.materiasCursadas.add(m);
        }
    }

    // Getters y setters propios de Estudiante
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public int getSemestreActual() { return semestreActual; }
    public void setSemestreActual(int semestreActual) { this.semestreActual = semestreActual; }

    public int getIndiceCargaCiencias() { return indiceCargaCiencias; }
    public void setIndiceCargaCiencias(int indiceCargaCiencias) { this.indiceCargaCiencias = indiceCargaCiencias; }

    public List<Materia> getMateriasCursadas() { return materiasCursadas; }
    public void setMateriasCursadas(List<Materia> materiasCursadas) { this.materiasCursadas = materiasCursadas; }
}
