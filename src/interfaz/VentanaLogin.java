package interfaz;

import modelo.Usuario;
import negocio.excepciones.ReglaNegocioException;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesion (capa de INTERFAZ). Captura credenciales y
 * DELEGA la autenticacion al GestorPerfil (controlador). Segun el rol
 * del usuario autenticado (polimorfismo) abre la ventana del estudiante
 * o el panel del docente. No contiene ninguna regla de negocio.
 */
public class VentanaLogin extends JFrame {

    private final ContextoApp ctx;
    private JTextField txtCorreo;
    private JPasswordField txtClave;

    public VentanaLogin(ContextoApp ctx) {
        this.ctx = ctx;
        setTitle("Sistema Educativo de Ingenieria UDLA - Ingreso");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(470, 580);
        setLocationRelativeTo(null);
        setContentPane(construir());
    }

    private JPanel construir() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);

        // Cabecera
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(UITheme.pad(26));
        JLabel t1 = new JLabel("Sistema Educativo de Ingenieria");
        t1.setFont(UITheme.TITLE);
        t1.setForeground(Color.WHITE);
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel t2 = new JLabel("Ruta Critica Adaptativa en Ciencias Basicas (1-4)");
        t2.setFont(UITheme.SUBTITLE);
        t2.setForeground(new Color(0xF1, 0xD9, 0xE0));
        t2.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(t1);
        header.add(Box.createVerticalStrut(6));
        header.add(t2);
        root.add(header, BorderLayout.NORTH);

        // Formulario
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.BG);
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        card.add(UITheme.h2("Iniciar sesion"), g);

        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1;
        card.add(new JLabel("Correo institucional"), g);
        g.gridx = 0; g.gridy = 2;
        txtCorreo = new JTextField("demo@udla.edu.ec", 20);
        txtCorreo.setFont(UITheme.BODY);
        card.add(txtCorreo, g);

        g.gridx = 0; g.gridy = 3;
        card.add(new JLabel("Contrasena"), g);
        g.gridx = 0; g.gridy = 4;
        txtClave = new JPasswordField("udla2024", 20);
        txtClave.setFont(UITheme.BODY);
        card.add(txtClave, g);

        g.gridx = 0; g.gridy = 5;
        g.insets = new Insets(16, 8, 6, 8);
        JButton btnIngresar = new JButton("Ingresar");
        UITheme.primary(btnIngresar);
        btnIngresar.addActionListener(e -> ingresar());
        card.add(btnIngresar, g);

        g.gridx = 0; g.gridy = 6;
        g.insets = new Insets(2, 8, 8, 8);
        JButton btnRegistrar = new JButton("Registrarse como estudiante");
        UITheme.secondary(btnRegistrar);
        btnRegistrar.addActionListener(e -> new VentanaRegistro(this, ctx).setVisible(true));
        card.add(btnRegistrar, g);

        g.gridx = 0; g.gridy = 7;
        JPanel hint = new JPanel(new GridLayout(0, 1));
        hint.setBackground(new Color(0xEF, 0xF4, 0xFF));
        hint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCF, 0xDD, 0xF7)),
                UITheme.pad(10)));
        hint.add(UITheme.muted("Cuentas de prueba:"));
        hint.add(UITheme.muted("Estudiante:  demo@udla.edu.ec  /  udla2024"));
        hint.add(UITheme.muted("Docente:  docente@udla.edu.ec  /  docente2024"));
        card.add(hint, g);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(20, 20, 20, 20);
        center.add(card, gc);
        root.add(center, BorderLayout.CENTER);

        getRootPane().setDefaultButton(btnIngresar);
        return root;
    }

    private void ingresar() {
        String correo = txtCorreo.getText();
        String clave = new String(txtClave.getPassword());
        try {
            Usuario u = ctx.gestorPerfil.autenticar(correo, clave);
            if ("DOCENTE".equals(u.getRol())) {
                new VentanaDocente(ctx, (modelo.Docente) u).setVisible(true);
            } else {
                new VentanaPrincipal(ctx, (modelo.Estudiante) u).setVisible(true);
            }
            dispose();
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo iniciar sesion", JOptionPane.ERROR_MESSAGE);
        }
    }
}
