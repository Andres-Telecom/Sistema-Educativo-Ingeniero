package modelo;

/**
 * Superclase ABSTRACTA que representa a un usuario del sistema.
 *
 * Define el estado y el comportamiento COMUN a todos los actores que
 * pueden autenticarse (identificador, nombres, credenciales
 * institucionales) y obliga a cada subclase a declarar su rol mediante
 * el metodo abstracto {@link #getRol()} (polimorfismo).
 *
 * Se declara abstract porque un "Usuario generico" no existe en el
 * mundo del problema: siempre es un Estudiante o un Docente. Esto evita
 * instanciaciones sin sentido y justifica la herencia (dos subclases
 * concretas: Estudiante y Docente).
 *
 * Principios POO: Encapsulamiento (atributos privados con accesores),
 * Herencia (base comun) y Polimorfismo (getRol()).
 *
 * Nota de arquitectura (MVC): esta clase del MODELO no imprime ni lee
 * de consola ni conoce la interfaz. Solo guarda datos y expone
 * comportamiento propio de la entidad.
 */
public abstract class Usuario {

    private int idUsuario;
    private String nombres;
    private String correoUdla;
    private String contrasena;

    protected Usuario() {
    }

    protected Usuario(int idUsuario, String nombres, String correoUdla, String contrasena) {
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.correoUdla = correoUdla;
        this.contrasena = contrasena;
    }

    /**
     * Verifica las credenciales contra los datos almacenados del usuario.
     * Comparacion insensible a mayusculas en el correo y exacta en la clave.
     *
     * @param correo correo institucional ingresado
     * @param clave  contrasena ingresada
     * @return true si las credenciales coinciden
     */
    public boolean iniciarSesion(String correo, String clave) {
        if (correo == null || clave == null) {
            return false;
        }
        return correo.trim().equalsIgnoreCase(this.correoUdla)
                && clave.equals(this.contrasena);
    }

    /**
     * Rol del usuario dentro del sistema. Cada subclase concreta lo
     * define (polimorfismo). Usado por la capa de negocio para aplicar
     * reglas de autorizacion.
     *
     * @return identificador del rol ("ESTUDIANTE", "DOCENTE", ...)
     */
    public abstract String getRol();

    // Getters y setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getCorreoUdla() { return correoUdla; }
    public void setCorreoUdla(String correoUdla) { this.correoUdla = correoUdla; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
