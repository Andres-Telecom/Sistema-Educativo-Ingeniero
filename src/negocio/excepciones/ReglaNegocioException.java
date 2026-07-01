package negocio.excepciones;

/**
 * Excepcion base (checked) para TODA violacion de una regla de negocio
 * del sistema. Al ser una excepcion verificada, el compilador OBLIGA a
 * la capa de interfaz a capturarla, garantizando que ningun error de
 * regla llegue sin manejo al usuario.
 *
 * Sus subclases especializan el tipo de violacion (datos invalidos,
 * autenticacion, duplicado, autorizacion) permitiendo un manejo
 * polimorfico: la vista puede capturar ReglaNegocioException y mostrar
 * getMessage(), o capturar un tipo concreto para reaccionar distinto.
 */
public class ReglaNegocioException extends Exception {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
