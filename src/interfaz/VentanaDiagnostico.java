package interfaz;

import modelo.Ejercicio;
import modelo.Estudiante;
import modelo.EvaluacionDiagnostico;
import modelo.Materia;
import modelo.ReporteProgreso;
import modelo.RutaCritica;
import negocio.excepciones.ReglaNegocioException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MODULO 2 (vista). Aplica la evaluacion diagnostica y muestra la ruta
 * critica generada. Toda la logica (calificar, generar ruta, insertar
 * refuerzos) vive en GestorRutas; esta clase solo presenta y recoge.
 */
public class VentanaDiagnostico extends JDialog {

    private final ContextoApp ctx;
    private final Estudiante estudiante;
    private final ReporteProgreso reporte;
    private final JPanel centro = new JPanel(new BorderLayout());

    private JComboBox<Materia> comboMateria;
    private Materia materiaSel;
    private EvaluacionDiagnostico ev;
    private List<JTextField> campos = new ArrayList<>();

    public VentanaDiagnostico(JFrame owner, ContextoApp ctx, Estudiante estudiante, ReporteProgreso reporte) {
        super(owner, "M2 - Evaluacion diagnostica y ruta critica", true);
        this.ctx = ctx;
        this.estudiante = estudiante;
        this.reporte = reporte;
        setSize(640, 600);
        setLocationRelativeTo(owner);
        setContentPane(construir());
        mostrarSeleccion();
    }

    private JPanel construir() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(UITheme.pad(16));
        JLabel t = new JLabel("Diagnostico y generacion de ruta");
        t.setFont(UITheme.H2);
        t.setForeground(Color.WHITE);
        header.add(t, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);
        centro.setBackground(UITheme.BG);
        centro.setBorder(UITheme.pad(16));
        root.add(centro, BorderLayout.CENTER);
        return root;
    }

    private void setCentro(JComponent c) {
        centro.removeAll();
        centro.add(c, BorderLayout.CENTER);
        centro.revalidate();
        centro.repaint();
    }

    private void mostrarSeleccion() {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0;
        card.add(UITheme.h2("Elige la materia principal de tu ruta"), g);
        g.gridy = 1;
        comboMateria = new JComboBox<>(ctx.catalogoMaterias.toArray(new Materia[0]));
        comboMateria.setFont(UITheme.BODY);
        card.add(comboMateria, g);
        g.gridy = 2;
        card.add(UITheme.muted("Se aplicaran 5 preguntas aleatorias del banco. Si tu puntaje es menor a 60/100, la ruta insertara refuerzos de prerrequisitos."), g);
        g.gridy = 3;
        g.insets = new Insets(16, 8, 8, 8);
        JButton iniciar = new JButton("Iniciar diagnostico");
        UITheme.primary(iniciar);
        iniciar.addActionListener(e -> iniciar());
        card.add(iniciar, g);
        setCentro(card);
    }

    private void iniciar() {
        materiaSel = (Materia) comboMateria.getSelectedItem();
        try {
            ev = ctx.gestorRutas.prepararDiagnostico(estudiante, ctx.gestorEjercicios.getBanco(), 5);
            mostrarPreguntas();
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarPreguntas() {
        campos = new ArrayList<>();
        JPanel lista = new JPanel();
        lista.setBackground(UITheme.BG);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        int n = 1;
        for (Ejercicio e : ev.getPreguntas()) {
            JPanel card = UITheme.card();
            card.setLayout(new BorderLayout(0, 6));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel cab = new JLabel("Pregunta " + n + "  [" + e.getNivelDificultad() + "]  -  " + e.getTema());
            cab.setFont(UITheme.BODY_BOLD);
            JTextArea enun = new JTextArea(e.getEnunciado());
            enun.setEditable(false);
            enun.setLineWrap(true);
            enun.setWrapStyleWord(true);
            enun.setOpaque(false);
            enun.setFont(UITheme.BODY);
            JTextField resp = new JTextField();
            resp.setFont(UITheme.BODY);
            campos.add(resp);
            card.add(cab, BorderLayout.NORTH);
            card.add(enun, BorderLayout.CENTER);
            card.add(resp, BorderLayout.SOUTH);
            lista.add(card);
            lista.add(Box.createVerticalStrut(10));
            n++;
        }
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton calificar = new JButton("Calificar y generar ruta");
        UITheme.primary(calificar);
        calificar.addActionListener(e -> calificar());

        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setBackground(UITheme.BG);
        wrap.add(scroll, BorderLayout.CENTER);
        wrap.add(calificar, BorderLayout.SOUTH);
        setCentro(wrap);
    }

    private void calificar() {
        List<String> respuestas = new ArrayList<>();
        for (JTextField f : campos) {
            respuestas.add(f.getText());
        }
        ev.evaluarConocimientos(respuestas);
        try {
            RutaCritica ruta = ctx.gestorRutas.generarRuta(estudiante, ev, materiaSel);
            ctx.gestorDashboard.registrarAvance(reporte, materiaSel, ev.getPuntajeGlobal());
            mostrarResultado(ruta);
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarResultado(RutaCritica ruta) {
        StringBuilder sb = new StringBuilder();
        sb.append("Materia principal: ").append(materiaSel.getNombre()).append("\n");
        sb.append(String.format("Puntaje del diagnostico: %.1f/100%n", ev.getPuntajeGlobal()));
        sb.append("Total de pasos en la ruta: ").append(ruta.getSecuencia().size()).append("\n\n");
        sb.append("Secuencia recomendada:\n");
        int i = 1;
        for (Ejercicio e : ruta.getSecuencia()) {
            String prefijo = e.getTema().startsWith("Refuerzo:") ? "(*) REFUERZO" : "(>)";
            sb.append("  ").append(prefijo).append("  Paso ").append(i).append(" - [")
              .append(e.getNivelDificultad()).append("] ").append(e.getTema()).append("\n");
            i++;
        }
        if (ev.getPuntajeGlobal() < 60) {
            sb.append("\nComo tu puntaje fue menor a 60, se insertaron refuerzos de prerrequisitos al inicio (*).");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(UITheme.MONO);
        area.setBackground(Color.WHITE);
        area.setBorder(UITheme.pad(12));
        JScrollPane scroll = new JScrollPane(area);

        JButton cerrar = new JButton("Cerrar");
        UITheme.secondary(cerrar);
        cerrar.addActionListener(e -> dispose());

        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setBackground(UITheme.BG);
        JLabel ok = new JLabel("Ruta critica generada y avance registrado en el dashboard.");
        ok.setForeground(UITheme.OK);
        ok.setFont(UITheme.BODY_BOLD);
        wrap.add(ok, BorderLayout.NORTH);
        wrap.add(scroll, BorderLayout.CENTER);
        wrap.add(cerrar, BorderLayout.SOUTH);
        setCentro(wrap);
    }
}
