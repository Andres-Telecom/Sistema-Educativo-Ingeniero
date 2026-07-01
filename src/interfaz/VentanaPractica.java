package interfaz;

import modelo.Ejercicio;
import modelo.Estudiante;
import modelo.Materia;
import modelo.ReporteProgreso;
import negocio.excepciones.ReglaNegocioException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * MODULO 3 (vista). Sesion de practica adaptativa: presenta ejercicios y
 * muestra como sube o baja la dificultad. La regla de ajuste vive en
 * GestorEjercicios; esta clase solo orquesta la interaccion.
 */
public class VentanaPractica extends JDialog {

    private static final int TOTAL = 5;

    private final ContextoApp ctx;
    private final Estudiante estudiante;
    private final ReporteProgreso reporte;
    private final JPanel centro = new JPanel(new BorderLayout());

    private JComboBox<Materia> comboMateria;
    private Materia materiaSel;
    private String nivelActual;
    private int resueltos;
    private Ejercicio actual;
    private boolean modoResponder = true;

    private JLabel lblCabecera;
    private JTextArea lblEnunciado;
    private JTextField txtResp;
    private JLabel lblFeedback;
    private JButton btnAccion;

    public VentanaPractica(JFrame owner, ContextoApp ctx, Estudiante estudiante, ReporteProgreso reporte) {
        super(owner, "M3 - Practica adaptativa", true);
        this.ctx = ctx;
        this.estudiante = estudiante;
        this.reporte = reporte;
        setSize(600, 520);
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
        JLabel t = new JLabel("Practica con ajuste dinamico de dificultad");
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
        card.add(UITheme.h2("Elige la materia a practicar"), g);
        g.gridy = 1;
        comboMateria = new JComboBox<>(ctx.catalogoMaterias.toArray(new Materia[0]));
        comboMateria.setFont(UITheme.BODY);
        card.add(comboMateria, g);
        g.gridy = 2;
        card.add(UITheme.muted("Se inicia en nivel BAJO. Con 3 aciertos seguidos sube; con 2 fallos seguidos baja."), g);
        g.gridy = 3;
        g.insets = new Insets(16, 8, 8, 8);
        JButton iniciar = new JButton("Iniciar practica");
        UITheme.primary(iniciar);
        iniciar.addActionListener(e -> iniciar());
        card.add(iniciar, g);
        setCentro(card);
    }

    private void iniciar() {
        materiaSel = (Materia) comboMateria.getSelectedItem();
        ctx.gestorEjercicios.reiniciarSesion();
        nivelActual = Ejercicio.DIFICULTAD_BAJO;
        resueltos = 0;
        construirPanelPractica();
        siguienteEjercicio();
    }

    private void construirPanelPractica() {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(0, 10));
        lblCabecera = new JLabel(" ");
        lblCabecera.setFont(UITheme.BODY_BOLD);
        lblEnunciado = new JTextArea();
        lblEnunciado.setEditable(false);
        lblEnunciado.setLineWrap(true);
        lblEnunciado.setWrapStyleWord(true);
        lblEnunciado.setOpaque(false);
        lblEnunciado.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtResp = new JTextField();
        txtResp.setFont(UITheme.BODY);
        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(UITheme.BODY_BOLD);
        btnAccion = new JButton("Responder");
        UITheme.primary(btnAccion);
        btnAccion.addActionListener(e -> onAccion());

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(lblCabecera, BorderLayout.NORTH);
        top.add(lblEnunciado, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(txtResp, BorderLayout.NORTH);
        bottom.add(lblFeedback, BorderLayout.CENTER);
        bottom.add(btnAccion, BorderLayout.SOUTH);

        card.add(top, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        setCentro(card);
        getRootPane().setDefaultButton(btnAccion);
    }

    private void siguienteEjercicio() {
        List<Ejercicio> disponibles = ctx.gestorEjercicios.filtrar(materiaSel, nivelActual);
        if (disponibles.isEmpty()) {
            disponibles = ctx.gestorEjercicios.filtrar(materiaSel, null);
        }
        if (disponibles.isEmpty()) {
            lblCabecera.setText("No hay ejercicios para " + materiaSel.getNombre());
            lblEnunciado.setText("");
            btnAccion.setEnabled(false);
            return;
        }
        actual = disponibles.get(resueltos % disponibles.size());
        lblCabecera.setText("Ejercicio " + (resueltos + 1) + "/" + TOTAL + "   [" + actual.getNivelDificultad() + "]   " + actual.getTema());
        lblEnunciado.setText(actual.getEnunciado());
        txtResp.setText("");
        txtResp.setEditable(true);
        lblFeedback.setText(" ");
        txtResp.requestFocusInWindow();
        modoResponder = true;
        btnAccion.setText("Responder");
    }

    private void onAccion() {
        if (modoResponder) {
            responder();
        } else {
            avanzar();
        }
    }

    private void responder() {
        boolean correcta = ctx.gestorEjercicios.evaluarRespuesta(actual, txtResp.getText());
        StringBuilder fb = new StringBuilder();
        if (correcta) {
            fb.append("Correcto!");
            lblFeedback.setForeground(UITheme.OK);
        } else {
            fb.append("Incorrecto. Respuesta correcta: ").append(actual.getSolucionCorrecta());
            lblFeedback.setForeground(UITheme.DANGER);
        }
        String nuevoNivel = ctx.gestorEjercicios.calcularSiguienteDificultad(nivelActual);
        if (!nuevoNivel.equalsIgnoreCase(nivelActual)) {
            fb.append("   |   Nivel ajustado: ").append(nivelActual).append(" -> ").append(nuevoNivel);
            nivelActual = nuevoNivel;
        }
        lblFeedback.setText(fb.toString());
        txtResp.setEditable(false);
        modoResponder = false;
        btnAccion.setText(resueltos + 1 >= TOTAL ? "Ver resumen" : "Siguiente");
    }

    private void avanzar() {
        resueltos++;
        if (resueltos >= TOTAL) {
            finalizar();
        } else {
            siguienteEjercicio();
        }
    }

    private void finalizar() {
        double porc = ctx.gestorEjercicios.getPorcentajeAciertos();
        try {
            ctx.gestorDashboard.registrarAvance(reporte, materiaSel, porc);
        } catch (ReglaNegocioException ignored) {
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Resumen de la sesion de practica\n\n");
        sb.append("Materia: ").append(materiaSel.getNombre()).append("\n");
        sb.append("Total respondidos: ").append(ctx.gestorEjercicios.getTotalRespuestas()).append("\n");
        sb.append("Aciertos: ").append(ctx.gestorEjercicios.getTotalAciertos()).append("\n");
        sb.append(String.format("Porcentaje de aciertos: %.1f%%%n", porc));
        sb.append("\nEl avance se registro en tu dashboard de progreso.");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(UITheme.MONO);
        area.setBorder(UITheme.pad(12));
        JButton cerrar = new JButton("Cerrar");
        UITheme.secondary(cerrar);
        cerrar.addActionListener(e -> dispose());
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setBackground(UITheme.BG);
        wrap.add(new JScrollPane(area), BorderLayout.CENTER);
        wrap.add(cerrar, BorderLayout.SOUTH);
        setCentro(wrap);
    }
}
