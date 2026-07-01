package interfaz;

import modelo.Estudiante;
import modelo.ReporteProgreso;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del ESTUDIANTE (capa de INTERFAZ). Es el hub que da
 * acceso a los cuatro modulos del sistema. Mantiene el reporte de
 * progreso del estudiante (composicion) y lo comparte con los modulos
 * para que los avances se acumulen durante la sesion.
 */
public class VentanaPrincipal extends JFrame {

    private final ContextoApp ctx;
    private final Estudiante estudiante;
    private final ReporteProgreso reporte;

    public VentanaPrincipal(ContextoApp ctx, Estudiante estudiante) {
        this.ctx = ctx;
        this.estudiante = estudiante;
        this.reporte = ctx.gestorDashboard.crearReporte(estudiante);
        setTitle("Sistema Educativo de Ingenieria UDLA - Panel del estudiante");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(760, 560);
        setLocationRelativeTo(null);
        setContentPane(construir());
    }

    private JPanel construir() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(UITheme.pad(20));
        JPanel htxt = new JPanel();
        htxt.setOpaque(false);
        htxt.setLayout(new BoxLayout(htxt, BoxLayout.Y_AXIS));
        JLabel bienvenida = new JLabel("Hola, " + estudiante.getNombres());
        bienvenida.setFont(UITheme.TITLE);
        bienvenida.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Rol: " + estudiante.getRol()
                + "   |   Semestre " + estudiante.getSemestreActual()
                + "   |   Ciencias Basicas");
        sub.setFont(UITheme.SUBTITLE);
        sub.setForeground(new Color(0xF1, 0xD9, 0xE0));
        htxt.add(bienvenida);
        htxt.add(Box.createVerticalStrut(4));
        htxt.add(sub);
        header.add(htxt, BorderLayout.WEST);

        JButton salir = new JButton("Cerrar sesion");
        UITheme.secondary(salir);
        salir.addActionListener(e -> cerrarSesion());
        JPanel salirWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salirWrap.setOpaque(false);
        salirWrap.add(salir);
        header.add(salirWrap, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(UITheme.BG);
        grid.setBorder(UITheme.pad(20));
        grid.add(modulo("M1 - Perfil academico",
                "Selecciona tus materias inscritas y calcula tu indice de carga (1-10) con recomendacion semanal.",
                () -> new DialogoCargaAcademica(this, ctx, estudiante).setVisible(true)));
        grid.add(modulo("M2 - Diagnostico y ruta",
                "Realiza la evaluacion diagnostica y genera tu ruta critica; si tu puntaje es bajo se agregan refuerzos.",
                () -> new VentanaDiagnostico(this, ctx, estudiante, reporte).setVisible(true)));
        grid.add(modulo("M3 - Practica adaptativa",
                "Resuelve ejercicios que suben o bajan de dificultad segun tu desempeno en tiempo real.",
                () -> new VentanaPractica(this, ctx, estudiante, reporte).setVisible(true)));
        grid.add(modulo("M4 - Dashboard de progreso",
                "Visualiza tu avance por materia, tu estado academico y las alertas de riesgo tempranas.",
                () -> new VentanaDashboard(this, ctx, reporte).setVisible(true)));
        root.add(grid, BorderLayout.CENTER);
        return root;
    }

    private JPanel modulo(String titulo, String desc, Runnable accion) {
        JPanel c = UITheme.card();
        c.setLayout(new BorderLayout(0, 10));
        JLabel t = UITheme.h2(titulo);
        JTextArea d = new JTextArea(desc);
        d.setEditable(false);
        d.setLineWrap(true);
        d.setWrapStyleWord(true);
        d.setOpaque(false);
        d.setFont(UITheme.SMALL);
        d.setForeground(UITheme.MUTED);
        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(t, BorderLayout.NORTH);
        top.add(d, BorderLayout.CENTER);
        c.add(top, BorderLayout.CENTER);
        JButton b = new JButton("Abrir modulo");
        UITheme.accent(b);
        b.addActionListener(e -> accion.run());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.setOpaque(false);
        south.add(b);
        c.add(south, BorderLayout.SOUTH);
        return c;
    }

    private void cerrarSesion() {
        ctx.gestorPerfil.cerrarSesion();
        dispose();
        new VentanaLogin(ctx).setVisible(true);
    }
}
