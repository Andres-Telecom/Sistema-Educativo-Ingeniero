package interfaz;

import modelo.Materia;
import modelo.ReporteProgreso;
import negocio.excepciones.ReglaNegocioException;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * MODULO 4 (vista). Dashboard de progreso: barras por materia, estado
 * academico y alertas de riesgo. Los calculos y umbrales viven en
 * GestorDashboard; esta clase solo dibuja los datos que recibe.
 */
public class VentanaDashboard extends JDialog {

    private final ContextoApp ctx;
    private final ReporteProgreso reporte;
    private final JPanel contenido = new JPanel();

    private JComboBox<Materia> comboMateria;
    private JSpinner spAvance;
    private JSpinner spDias;

    public VentanaDashboard(JFrame owner, ContextoApp ctx, ReporteProgreso reporte) {
        super(owner, "M4 - Dashboard de progreso", true);
        this.ctx = ctx;
        this.reporte = reporte;
        setSize(640, 620);
        setLocationRelativeTo(owner);
        setContentPane(construir());
        refrescar();
    }

    private JPanel construir() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(UITheme.pad(16));
        JLabel t = new JLabel("Dashboard de progreso academico");
        t.setFont(UITheme.H2);
        t.setForeground(Color.WHITE);
        header.add(t, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(UITheme.BG);
        contenido.setBorder(UITheme.pad(16));
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        root.add(construirControles(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel construirControles() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 6));
        panel.setBackground(new Color(0xEC, 0xEF, 0xF3));
        panel.setBorder(UITheme.pad(12));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila1.setOpaque(false);
        comboMateria = new JComboBox<>(ctx.catalogoMaterias.toArray(new Materia[0]));
        spAvance = new JSpinner(new SpinnerNumberModel(50, 0, 100, 5));
        JButton btnAvance = new JButton("Registrar avance");
        UITheme.accent(btnAvance);
        btnAvance.addActionListener(e -> registrarAvance());
        fila1.add(new JLabel("Materia:"));
        fila1.add(comboMateria);
        fila1.add(new JLabel("Avance %:"));
        fila1.add(spAvance);
        fila1.add(btnAvance);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila2.setOpaque(false);
        spDias = new JSpinner(new SpinnerNumberModel(0, 0, 60, 1));
        JButton btnDias = new JButton("Simular dias sin practicar");
        UITheme.secondary(btnDias);
        btnDias.addActionListener(e -> simularInactividad());
        fila2.add(new JLabel("Dias:"));
        fila2.add(spDias);
        fila2.add(btnDias);

        panel.add(fila1);
        panel.add(fila2);
        return panel;
    }

    private void refrescar() {
        contenido.removeAll();

        String estado = ctx.gestorDashboard.clasificarEstado(reporte);
        Color colorEstado = "DOMINIO".equals(estado) ? UITheme.OK
                : "EN PROGRESO".equals(estado) ? UITheme.WARN : UITheme.DANGER;

        JPanel resumen = UITheme.card();
        resumen.setLayout(new GridLayout(0, 1, 0, 4));
        resumen.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel rend = new JLabel(String.format("Rendimiento global: %.1f%%", reporte.getPorcentajeRendimiento()));
        rend.setFont(UITheme.TITLE);
        rend.setForeground(UITheme.TEXT);
        JLabel lblEstado = new JLabel("Estado: " + estado);
        lblEstado.setFont(UITheme.BODY_BOLD);
        lblEstado.setForeground(colorEstado);
        JLabel lblDias = new JLabel("Dias sin practicar: " + reporte.getDiasSinPracticar());
        lblDias.setFont(UITheme.BODY);
        resumen.add(rend);
        resumen.add(lblEstado);
        resumen.add(lblDias);
        contenido.add(resumen);
        contenido.add(Box.createVerticalStrut(12));

        if (ctx.gestorDashboard.estaEnRiesgo(reporte)) {
            JPanel alerta = new JPanel(new BorderLayout());
            alerta.setBackground(new Color(0xFD, 0xEC, 0xEA));
            alerta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xF2, 0xB8, 0xB5)),
                    UITheme.pad(12)));
            alerta.setAlignmentX(Component.LEFT_ALIGNMENT);
            String texto = reporte.generarAlertaRiesgo().replace("\n", "<br>");
            JLabel l = new JLabel("<html><b>ALERTA: estudiante en riesgo academico</b><br>" + texto + "</html>");
            l.setForeground(UITheme.DANGER);
            alerta.add(l, BorderLayout.CENTER);
            contenido.add(alerta);
            contenido.add(Box.createVerticalStrut(12));
        }

        JLabel tituloBarras = UITheme.h2("Progreso por materia");
        tituloBarras.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(tituloBarras);
        contenido.add(Box.createVerticalStrut(6));

        Map<Materia, Double> avances = reporte.getAvancePorMateria();
        if (avances.isEmpty()) {
            JLabel vacio = UITheme.muted("Aun no hay avances registrados. Realiza un diagnostico, practica o registra un avance manual.");
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            contenido.add(vacio);
        } else {
            for (Map.Entry<Materia, Double> en : avances.entrySet()) {
                contenido.add(filaBarra(en.getKey().getNombre(), en.getValue()));
                contenido.add(Box.createVerticalStrut(8));
            }
        }
        contenido.revalidate();
        contenido.repaint();
    }

    private JPanel filaBarra(String nombre, double valor) {
        JPanel fila = UITheme.card();
        fila.setLayout(new BorderLayout(10, 0));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        JLabel l = new JLabel(nombre);
        l.setFont(UITheme.BODY_BOLD);
        l.setPreferredSize(new Dimension(180, 24));
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) Math.round(valor));
        bar.setStringPainted(true);
        bar.setForeground(valor >= 60 ? UITheme.OK : UITheme.WARN);
        fila.add(l, BorderLayout.WEST);
        fila.add(bar, BorderLayout.CENTER);
        return fila;
    }

    private void registrarAvance() {
        Materia m = (Materia) comboMateria.getSelectedItem();
        double v = ((Number) spAvance.getValue()).doubleValue();
        try {
            ctx.gestorDashboard.registrarAvance(reporte, m, v);
            refrescar();
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void simularInactividad() {
        int dias = ((Number) spDias.getValue()).intValue();
        try {
            ctx.gestorDashboard.actualizarInactividad(reporte, dias);
            refrescar();
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
