package interfaz;

import modelo.Docente;
import modelo.Ejercicio;
import modelo.Materia;
import negocio.excepciones.ReglaNegocioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel del DOCENTE (capa de INTERFAZ). Muestra el banco de ejercicios y
 * permite registrar nuevos. Demuestra la regla de AUTORIZACION: como el
 * usuario autenticado es un Docente, GestorEjercicios acepta el registro;
 * un estudiante recibiria AutorizacionException.
 */
public class VentanaDocente extends JFrame {

    private final ContextoApp ctx;
    private final Docente docente;
    private DefaultTableModel modelo;
    private JTextField txtTema;
    private JTextField txtEnunciado;
    private JTextField txtSolucion;
    private JComboBox<Materia> comboMateria;
    private JComboBox<String> comboNivel;

    public VentanaDocente(ContextoApp ctx, Docente docente) {
        this.ctx = ctx;
        this.docente = docente;
        setTitle("Panel del docente - Banco de ejercicios");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setContentPane(construir());
        refrescarTabla();
    }

    private JPanel construir() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(UITheme.pad(18));
        JPanel htxt = new JPanel();
        htxt.setOpaque(false);
        htxt.setLayout(new BoxLayout(htxt, BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel("Docente: " + docente.getNombres());
        t1.setFont(UITheme.TITLE);
        t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("Departamento: " + docente.getDepartamento() + "   |   Rol: " + docente.getRol());
        t2.setFont(UITheme.SUBTITLE);
        t2.setForeground(new Color(0xF1, 0xD9, 0xE0));
        htxt.add(t1);
        htxt.add(Box.createVerticalStrut(4));
        htxt.add(t2);
        header.add(htxt, BorderLayout.WEST);
        JButton salir = new JButton("Cerrar sesion");
        UITheme.secondary(salir);
        salir.addActionListener(e -> {
            ctx.gestorPerfil.cerrarSesion();
            dispose();
            new VentanaLogin(ctx).setVisible(true);
        });
        JPanel salirWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salirWrap.setOpaque(false);
        salirWrap.add(salir);
        header.add(salirWrap, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID", "Materia", "Tema", "Nivel", "Enunciado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(UITheme.BODY_BOLD);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Banco de ejercicios"));

        JPanel centro = new JPanel(new BorderLayout(14, 0));
        centro.setBackground(UITheme.BG);
        centro.setBorder(UITheme.pad(16));
        centro.add(scroll, BorderLayout.CENTER);
        centro.add(construirFormulario(), BorderLayout.EAST);
        root.add(centro, BorderLayout.CENTER);
        return root;
    }

    private JPanel construirFormulario() {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(320, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0;
        card.add(UITheme.h2("Registrar nuevo ejercicio"), g);

        txtTema = new JTextField(18);
        txtEnunciado = new JTextField(18);
        txtSolucion = new JTextField(18);
        comboMateria = new JComboBox<>(ctx.catalogoMaterias.toArray(new Materia[0]));
        comboNivel = new JComboBox<>(new String[]{
                Ejercicio.DIFICULTAD_BAJO, Ejercicio.DIFICULTAD_MEDIO, Ejercicio.DIFICULTAD_ALTO});

        int y = 1;
        y = fila(card, g, y, "Tema", txtTema);
        y = fila(card, g, y, "Materia", comboMateria);
        y = fila(card, g, y, "Nivel", comboNivel);
        y = fila(card, g, y, "Enunciado", txtEnunciado);
        y = fila(card, g, y, "Solucion correcta", txtSolucion);

        g.gridy = y;
        g.insets = new Insets(14, 6, 6, 6);
        JButton btn = new JButton("Registrar ejercicio");
        UITheme.primary(btn);
        btn.addActionListener(e -> registrar());
        card.add(btn, g);
        return card;
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
        Materia m = (Materia) comboMateria.getSelectedItem();
        String nivel = (String) comboNivel.getSelectedItem();
        Ejercicio e = new Ejercicio(100 + ctx.gestorEjercicios.getBanco().size(),
                txtTema.getText().trim(), nivel,
                txtEnunciado.getText().trim(), txtSolucion.getText().trim(), m);
        try {
            ctx.gestorEjercicios.registrarEjercicio(docente, e);
            refrescarTabla();
            txtTema.setText("");
            txtEnunciado.setText("");
            txtSolucion.setText("");
            JOptionPane.showMessageDialog(this, "Ejercicio registrado en el banco.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
        } catch (ReglaNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        for (Ejercicio e : ctx.gestorEjercicios.getBanco()) {
            modelo.addRow(new Object[]{
                    e.getIdEjercicio(),
                    e.getMateria() != null ? e.getMateria().getNombre() : "-",
                    e.getTema(),
                    e.getNivelDificultad(),
                    e.getEnunciado()});
        }
    }
}
