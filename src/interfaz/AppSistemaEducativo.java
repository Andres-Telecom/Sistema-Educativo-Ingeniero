package interfaz;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase PRINCIPAL con el metodo main(). Arranca la aplicacion con
 * interfaz grafica (Swing):
 *   1) construye el contexto y carga los datos iniciales;
 *   2) abre la ventana de inicio de sesion en el hilo de eventos (EDT).
 *
 * Reemplaza a la antigua clase de consola. Gracias a la separacion MVC,
 * NO fue necesario modificar el modelo ni el negocio para cambiar de
 * consola a interfaz grafica: solo cambio la capa de presentacion.
 */
public class AppSistemaEducativo {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        final ContextoApp ctx = new ContextoApp();
        try {
            ctx.cargarDatosIniciales();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al iniciar el sistema: " + e.getMessage(),
                    "Error de arranque", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new VentanaLogin(ctx).setVisible(true));
    }
}
