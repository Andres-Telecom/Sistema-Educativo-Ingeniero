package interfaz;

import modelo.Estudiante;
import modelo.Materia;
import negocio.excepciones.ReglaNegocioException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MODULO 1 (vista). Permite seleccionar las materias inscritas y calcular
 * el indice de carga academica. Delega el calculo al GestorPerfil.
 */
public class DialogoCargaAcademica extends JDialog {

    private final ContextoApp ctx;
    private final Estudiante estudiante;
    private final List<JCheckBox> checks = new ArrayList<>();
    private JLabel resultado;

    public DialogoCargaAcademica(JFrame owner, ContextoApp ctx, Estudiante estudiante) {
        super(owner, "M1 - Calculo de carga academica", true);
        this.ctx = ctx;
        this.estudiante = estudiante;
        setSize(520, 480);
        setLocationRelativeTo(owner);
        setContentPane(construir());
    }

    private JPanel construir() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(UITheme.BG);
        root.setBorder(UITheme.pad(18));

        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(UITheme.h2("Selecciona tus materias inscritas este semestre"));
        card.add(Box.createVerticalStrut(10));
        for (Materia m : ctx.catalogoMaterias) {
            JCheckBox cb = new JCheckBox(m.toString());
            cb.setOpaque(false);
            cb.setFont(UITheme.BODY);
            cb.putClientProperty("materia", m);
            checks.add(cb);
            card.add(cb);
        }
        root.add(card, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 10));
        south.setOpaque(false);
        resultado = new JLabel(" ");
        resultado.setFont(UITheme.BODY_BOLD);
        JButton calcular = new JButton("Calcular indice de carga");
        UITheme.primary(calcular);
        calcular.addActionListener(e -> calcular());
        south.add(resultado, BorderLayout.CENTER);
        south.add(calcular, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);
        return root;
    }

    private void calcular() {
        List<Materia> seleccion = new ArrayList<>();
        for (JCheckBox cb : checks) {
            if (cb.isSelected()) {
                seleccion.add((Materia) cb.getClientProperty("materia"));
            }
        }
        try {
            int indice = ctx.gestorPerfil.calcularCarga(estudiante, seleccion);
            String reco;
            if (indice >= 8) {
                reco = "Carga ALTA: se reduce la cantidad de temas semanales recomendados.";
                resultado.setForeground(UITheme.DANGER);
            } else if (indice >= 5) {
                reco = "Carga MEDIA: cantidad estandar de temas recomendados.";
                resultado.setForeground(UITheme.WARN);
            } else {
                reco = "Carga BAJA: puedes tomar temas adicionales.";
                resultado.setForeground(UITheme.OK);
            }
            resultado.setText("<html>Indice de carga: " + indice + "/10<br>" + reco + "</html>");
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dato invalido", JOptionPane.WARNING_MESSAGE);
        }
    }
}
