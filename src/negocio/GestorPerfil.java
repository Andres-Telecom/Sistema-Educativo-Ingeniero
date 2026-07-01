package negocio;

import modelo.Docente;
import modelo.Estudiante;
import modelo.Materia;
import modelo.Usuario;
import negocio.excepciones.AutenticacionException;
import negocio.excepciones.DatosInvalidosException;
import negocio.excepciones.RegistroDuplicadoException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * MODULO 1 - Gestion de Perfil Academico (Maikel Cachimuel).
 *
 * Rol arquitectonico (CONTROLADOR en el patron MVC): es el intermediario
 * entre la interfaz y el modelo. Aplica TODAS las reglas de negocio de
 * identidad y acceso, y traduce cualquier violacion en una excepcion
 * verificada con mensaje claro. La interfaz nunca valida por su cuenta:
 * solo captura estas excepciones y las muestra.
 *
 * Reglas de negocio implementadas (contra el "facilismo"):
 *   R1. Cedula ecuatoriana valida: 10 digitos, codigo de provincia
 *       (01-24) y DIGITO VERIFICADOR (algoritmo modulo 10).
 *   R2. Correo estrictamente institucional (formato + dominio @udla.edu.ec).
 *   R3. Contrasena robusta: minimo 6 caracteres con al menos una letra
 *       y un numero.
 *   R4. Nombres no vacios, solo letras y espacios, minimo 3 caracteres.
 *   R5. Semestre dentro del alcance del sistema (1-4, ciencias basicas).
 *   R6. Unicidad: no se admiten cedulas ni correos duplicados.
 */
public class GestorPerfil {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@udla\\.edu\\.ec$");

    private final List<Usuario> usuarios;
    private Usuario sesionActiva;
    private int contadorId;

    public GestorPerfil() {
        this.usuarios = new ArrayList<>();
        this.sesionActiva = null;
        this.contadorId = 100;
    }

    /**
     * Registra un nuevo estudiante aplicando todas las validaciones.
     *
     * @return el estudiante creado
     * @throws DatosInvalidosException   si algun dato no cumple una regla
     * @throws RegistroDuplicadoException si la cedula o el correo ya existen
     */
    public Estudiante registrarEstudiante(String cedula, String nombres, String correo,
                                          String clave, int semestre)
            throws DatosInvalidosException, RegistroDuplicadoException {

        validarCedula(cedula);
        validarNombres(nombres);
        validarCorreo(correo);
        validarClave(clave);
        validarSemestre(semestre);

        if (cedulaYaRegistrada(cedula)) {
            throw new RegistroDuplicadoException(
                    "Ya existe un usuario registrado con la cedula " + cedula + ".");
        }
        if (correoYaRegistrado(correo)) {
            throw new RegistroDuplicadoException(
                    "Ya existe un usuario registrado con el correo " + correo + ".");
        }

        Estudiante e = new Estudiante(contadorId++, correo.trim(), clave,
                cedula.trim(), nombres.trim(), semestre);
        usuarios.add(e);
        return e;
    }

    /**
     * Registra un docente (usado para precargar el actor que administra
     * el banco). Valida correo, nombres y contrasena.
     */
    public Docente registrarDocente(String nombres, String correo, String clave,
                                    String departamento)
            throws DatosInvalidosException, RegistroDuplicadoException {
        validarNombres(nombres);
        validarCorreo(correo);
        validarClave(clave);
        if (correoYaRegistrado(correo)) {
            throw new RegistroDuplicadoException(
                    "Ya existe un usuario registrado con el correo " + correo + ".");
        }
        Docente d = new Docente(contadorId++, correo.trim(), clave, nombres.trim(), departamento);
        usuarios.add(d);
        return d;
    }

    /**
     * Autentica a un usuario (estudiante o docente) por correo y clave.
     *
     * @return el usuario autenticado (polimorfico)
     * @throws AutenticacionException si faltan datos o las credenciales fallan
     */
    public Usuario autenticar(String correo, String clave) throws AutenticacionException {
        if (correo == null || correo.trim().isEmpty()) {
            throw new AutenticacionException("Debe ingresar el correo institucional.");
        }
        if (clave == null || clave.isEmpty()) {
            throw new AutenticacionException("Debe ingresar la contrasena.");
        }
        for (Usuario u : usuarios) {
            if (u.iniciarSesion(correo, clave)) {
                this.sesionActiva = u;
                return u;
            }
        }
        throw new AutenticacionException("Credenciales incorrectas. Verifique correo y contrasena.");
    }

    /** Cierra la sesion activa (responsabilidad del controlador, no del modelo). */
    public void cerrarSesion() {
        this.sesionActiva = null;
    }

    /**
     * Calcula y actualiza el indice de carga academica del estudiante.
     *
     * @throws DatosInvalidosException si no se selecciona ninguna materia
     */
    public int calcularCarga(Estudiante e, List<Materia> materias) throws DatosInvalidosException {
        if (e == null) {
            throw new DatosInvalidosException("No hay un estudiante activo.");
        }
        if (materias == null || materias.isEmpty()) {
            throw new DatosInvalidosException("Debe seleccionar al menos una materia inscrita.");
        }
        for (Materia m : materias) {
            e.agregarMateria(m);
        }
        return e.calcularIndiceCarga(materias);
    }

    // ---------------------- Reglas de validacion ----------------------

    /** R1: cedula ecuatoriana valida (provincia + digito verificador modulo 10). */
    private void validarCedula(String cedula) throws DatosInvalidosException {
        if (cedula == null || !cedula.trim().matches("\\d{10}")) {
            throw new DatosInvalidosException("La cedula debe tener exactamente 10 digitos numericos.");
        }
        String c = cedula.trim();
        int provincia = Integer.parseInt(c.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            throw new DatosInvalidosException("La cedula no es valida: el codigo de provincia (01-24) es incorrecto.");
        }
        int tercerDigito = Character.getNumericValue(c.charAt(2));
        if (tercerDigito >= 6) {
            throw new DatosInvalidosException("La cedula no es valida: el tercer digito debe ser menor a 6 para personas naturales.");
        }
        int[] coef = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int val = Character.getNumericValue(c.charAt(i)) * coef[i];
            if (val > 9) val -= 9;
            suma += val;
        }
        int verificadorEsperado = (suma % 10 == 0) ? 0 : 10 - (suma % 10);
        int verificadorReal = Character.getNumericValue(c.charAt(9));
        if (verificadorEsperado != verificadorReal) {
            throw new DatosInvalidosException("La cedula ingresada no es valida (no cumple el digito verificador de la cedula ecuatoriana).");
        }
    }

    /** R2: correo con formato valido y dominio institucional @udla.edu.ec. */
    private void validarCorreo(String correo) throws DatosInvalidosException {
        if (correo == null || correo.trim().isEmpty()) {
            throw new DatosInvalidosException("El correo no puede estar vacio.");
        }
        if (!PATRON_CORREO.matcher(correo.trim()).matches()) {
            throw new DatosInvalidosException("El correo debe ser institucional y valido (ejemplo: nombre@udla.edu.ec).");
        }
    }

    /** R3: contrasena de minimo 6 caracteres con al menos una letra y un numero. */
    private void validarClave(String clave) throws DatosInvalidosException {
        if (clave == null || clave.length() < 6) {
            throw new DatosInvalidosException("La contrasena debe tener al menos 6 caracteres.");
        }
        if (!clave.matches(".*[A-Za-z].*") || !clave.matches(".*\\d.*")) {
            throw new DatosInvalidosException("La contrasena debe incluir al menos una letra y un numero.");
        }
    }

    /** R4: nombres no vacios, solo letras y espacios, minimo 3 caracteres. */
    private void validarNombres(String nombres) throws DatosInvalidosException {
        if (nombres == null || nombres.trim().length() < 3) {
            throw new DatosInvalidosException("El nombre debe tener al menos 3 caracteres.");
        }
        if (!nombres.trim().matches("[A-Za-zAEIOUaeiouNnUu\\s]+")) {
            throw new DatosInvalidosException("El nombre solo puede contener letras y espacios.");
        }
    }

    /** R5: semestre dentro del alcance de ciencias basicas (1-4). */
    private void validarSemestre(int semestre) throws DatosInvalidosException {
        if (semestre < Estudiante.SEMESTRE_MINIMO || semestre > Estudiante.SEMESTRE_MAXIMO) {
            throw new DatosInvalidosException("El semestre debe estar entre " + Estudiante.SEMESTRE_MINIMO
                    + " y " + Estudiante.SEMESTRE_MAXIMO + " (etapa de ciencias basicas).");
        }
    }

    private boolean cedulaYaRegistrada(String cedula) {
        for (Usuario u : usuarios) {
            if (u instanceof Estudiante) {
                if (((Estudiante) u).getCedula().equals(cedula.trim())) return true;
            }
        }
        return false;
    }

    private boolean correoYaRegistrado(String correo) {
        for (Usuario u : usuarios) {
            if (u.getCorreoUdla() != null && u.getCorreoUdla().equalsIgnoreCase(correo.trim())) {
                return true;
            }
        }
        return false;
    }

    public List<Usuario> getUsuarios() { return usuarios; }
    public Usuario getSesionActiva() { return sesionActiva; }
}
