package modelo;

/**
 * Docente responsable del banco de ejercicios de una o varias materias
 * de ciencias basicas. ES-UN {@link Usuario}: hereda identificador,
 * nombres y credenciales de acceso, y agrega su departamento academico.
 *
 * Justifica la HERENCIA junto con {@link Estudiante}: el sistema tiene
 * dos actores que se autentican y comparten credenciales, pero con
 * atributos y PERMISOS distintos. El metodo {@link #getRol()}
 * (polimorfismo) permite a la capa de negocio aplicar la regla de
 * autorizacion: solo un Docente puede registrar ejercicios en el banco.
 */
public class Docente extends Usuario {

    private String departamento;

    public Docente() {
        super();
    }

    public Docente(int idUsuario, String correoUdla, String contrasena,
                   String nombres, String departamento) {
        super(idUsuario, nombres, correoUdla, contrasena);
        this.departamento = departamento;
    }

    @Override
    public String getRol() {
        return "DOCENTE";
    }

    /**
     * Indica que este actor tiene permiso para administrar el banco de
     * ejercicios. Regla de autorizacion consultada por GestorEjercicios.
     *
     * @return true (los docentes gestionan el banco)
     */
    public boolean puedeGestionarBanco() {
        return true;
    }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
