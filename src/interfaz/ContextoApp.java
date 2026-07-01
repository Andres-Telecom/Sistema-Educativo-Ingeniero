package interfaz;

import modelo.Docente;
import modelo.Ejercicio;
import modelo.Materia;
import negocio.GestorDashboard;
import negocio.GestorEjercicios;
import negocio.GestorPerfil;
import negocio.GestorRutas;
import negocio.excepciones.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contexto de la aplicacion: crea y comparte los cuatro gestores de la
 * capa de negocio, el catalogo de materias y el docente que administra
 * el banco. Centraliza la carga de datos iniciales.
 *
 * Es el "cableado" (composition root) entre la interfaz y el negocio:
 * las ventanas reciben este contexto y solo invocan a los gestores.
 */
public class ContextoApp {

    public final GestorPerfil gestorPerfil = new GestorPerfil();
    public final GestorRutas gestorRutas = new GestorRutas();
    public final GestorEjercicios gestorEjercicios = new GestorEjercicios();
    public final GestorDashboard gestorDashboard = new GestorDashboard();
    public final List<Materia> catalogoMaterias = new ArrayList<>();
    public Docente docenteSistema;

    /**
     * Carga materias de ciencias basicas (con prerrequisitos), el docente
     * del sistema, el banco de ejercicios (registrado por el docente, lo
     * que ejercita la regla de autorizacion) y dos usuarios de prueba.
     * Todo pasa por las validaciones reales de la capa de negocio.
     */
    public void cargarDatosIniciales() throws ReglaNegocioException {
        Materia algebra = new Materia(1, "Algebra Lineal", 1, true);
        Materia calculo1 = new Materia(2, "Calculo Diferencial", 2, true);
        Materia calculo2 = new Materia(3, "Calculo Integral", 3, true);
        Materia fisica1 = new Materia(4, "Fisica I", 2, false);
        Materia ecuaciones = new Materia(5, "Ecuaciones Diferenciales", 4, true);

        calculo1.agregarPrerrequisito(algebra);
        calculo2.agregarPrerrequisito(calculo1);
        fisica1.agregarPrerrequisito(calculo1);
        ecuaciones.agregarPrerrequisito(calculo2);

        catalogoMaterias.addAll(Arrays.asList(algebra, calculo1, calculo2, fisica1, ecuaciones));

        // Docente que administra el banco (segunda subclase de Usuario)
        docenteSistema = gestorPerfil.registrarDocente(
                "Paulo Guerra", "docente@udla.edu.ec", "docente2024", "Ciencias Exactas");

        // Banco de ejercicios: cada registro exige un Docente (autorizacion)
        registrar(new Ejercicio(1, "Sistemas de ecuaciones", Ejercicio.DIFICULTAD_BAJO,
                "Resuelva: x + 2 = 5. Cuanto vale x?", "3", algebra));
        registrar(new Ejercicio(2, "Matrices", Ejercicio.DIFICULTAD_MEDIO,
                "Determinante de [[1,2],[3,4]] =", "-2", algebra));
        registrar(new Ejercicio(3, "Vectores", Ejercicio.DIFICULTAD_ALTO,
                "Producto punto de (1,2,3) y (4,5,6) =", "32", algebra));
        registrar(new Ejercicio(4, "Limites", Ejercicio.DIFICULTAD_BAJO,
                "lim x->2 de (x+3) =", "5", calculo1));
        registrar(new Ejercicio(5, "Derivadas", Ejercicio.DIFICULTAD_MEDIO,
                "Derivada de x^2 con respecto a x =", "2x", calculo1));
        registrar(new Ejercicio(6, "Regla de la cadena", Ejercicio.DIFICULTAD_ALTO,
                "Derivada de sin(x^2) (escriba cos(x^2)*2x)", "cos(x^2)*2x", calculo1));
        registrar(new Ejercicio(7, "Integrales", Ejercicio.DIFICULTAD_BAJO,
                "Integral indefinida de 2 dx (primitiva sin constante)", "2x", calculo2));
        registrar(new Ejercicio(8, "Integrales definidas", Ejercicio.DIFICULTAD_MEDIO,
                "Integral de 0 a 2 de x dx =", "2", calculo2));
        registrar(new Ejercicio(9, "Cinematica", Ejercicio.DIFICULTAD_BAJO,
                "Velocidad si recorre 100 m en 10 s (en m/s) =", "10", fisica1));
        registrar(new Ejercicio(10, "Dinamica", Ejercicio.DIFICULTAD_MEDIO,
                "F=ma, si m=5kg y a=2m/s^2, F en Newtons =", "10", fisica1));
        registrar(new Ejercicio(11, "EDOs de primer orden", Ejercicio.DIFICULTAD_MEDIO,
                "Solucion general de dy/dx = 0 (escriba C)", "C", ecuaciones));

        // Estudiante de prueba: pasa por la validacion real (cedula valida, etc.)
        gestorPerfil.registrarEstudiante("1712345675", "Estudiante Demo",
                "demo@udla.edu.ec", "udla2024", 3);
    }

    private void registrar(Ejercicio e) throws ReglaNegocioException {
        gestorEjercicios.registrarEjercicio(docenteSistema, e);
    }
}
