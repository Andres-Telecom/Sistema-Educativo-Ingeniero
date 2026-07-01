package interfaz;

import modelo.Estudiante;
import negocio.excepciones.ReglaNegocioException;

import javax.swing.*;
import java.awt.*;

/**
 * Dialogo modal de registro de estudiantes (capa de INTERFAZ). Solo
 * recoge los datos y los envia al GestorPerfil, que aplica todas las
 * validaciones. Cualquier ReglaNegocioException se muestra al usuario.
 */
public class VentanaRegistro extends JDialog {

    private final ContextoApp ctx;
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtCorreo;
    private JPasswordField txtClave;
    private JTextField txtSemestre;

    public VentanaRegistro(JFrame owner, ContextoApp ctx) {
        super(owner, "Registro de estudiante", true);
        this.ctx = ctx;
        setSize(460, 470);
        setLocationRelativeTo(owner);
        setContentPane(construir());
    }

    private JPanel construir() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.setBorder(UITheme.pad(18));

        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0;
        card.add(UITheme.h2("Datos del nuevo estudiante"), g);

        txtCedula = new JTextField(20);
        txtNombres = new JTextField(20);
        txtCorreo = new JTextField(20);
        txtClave = new JPasswordField(20);
        txtSemestre = new JTextField(20);

        int y = 1;
        y = fila(card, g, y, "Cedula (10 digitos)", txtCedula);
        y = fila(card, g, y, "Nombres completos", txtNombres);
        y = fila(card, g, y, "Correo (@udla.edu.ec)", txtCorreo);
        y = fila(card, g, y, "Contrasena (min. 6, letra y numero)", txtClave);
        y = fila(card, g, y, "Semestre (1-4)", txtSemestre);

        g.gridy = y;
        g.insets = new Insets(16, 8, 6, 8);
        JButton btn = new JButton("Registrar");
        UITheme.primary(btn);
        btn.addActionListener(e -> registrar());
        card.add(btn, g);

        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private int fila(JPanel card, GridBagConstraints g, int y, String etiqueta, JComponent campo) {
        g.gridy = y;
        card.add(new JLabel(etiqueta), g);
        g.gridy = y + 1;
        campo.setFont(UITheme.BODY);
        card.add(campo, g);
        return y + 2;
    }

    private void registrar() {
        String cedula = txtCedula.getText().trim();
        String nombres = txtNombres.getText().trim();
        String correo = txtCorreo.getText().trim();
        String clave = new String(txtClave.getPassword());
        String semTexto = txtSemestre.getText().trim();

        int semestre;
        try {
            semestre = Integer.parseInt(semTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El semestre debe ser un numero entero (1-4).",
                    "Dato invalido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Estudiante e = ctx.gestorPerfil.registrarEstudiante(cedula, nombres, correo, clave, semestre);
            JOptionPane.showMessageDialog(this,
                    "Estudiante registrado exitosamente.\nID asignado: " + e.getIdUsuario()
                            + "\nYa puede iniciar sesion.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo registrar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
