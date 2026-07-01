package interfaz;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/**
 * Utilidades de estilo (paleta, tipografias y ayudas de componentes)
 * para dar un aspecto consistente y profesional a la interfaz grafica.
 * Pertenece a la capa de INTERFAZ (presentacion): no contiene logica de
 * negocio, solo apariencia.
 */
public final class UITheme {

    public static final Color PRIMARY = new Color(0x8A, 0x15, 0x38);      // vino UDLA
    public static final Color PRIMARY_DARK = new Color(0x66, 0x10, 0x2A);
    public static final Color ACCENT = new Color(0x1F, 0x6F, 0xEB);
    public static final Color BG = new Color(0xF4, 0xF5, 0xF7);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(0x22, 0x26, 0x2B);
    public static final Color MUTED = new Color(0x6B, 0x72, 0x80);
    public static final Color OK = new Color(0x1E, 0x8E, 0x3E);
    public static final Color WARN = new Color(0xB9, 0x7A, 0x00);
    public static final Color DANGER = new Color(0xC0, 0x39, 0x2B);
    public static final Color BORDER = new Color(0xDD, 0xE1, 0xE6);

    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font SUBTITLE = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font H2 = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font MONO = new Font("Monospaced", Font.PLAIN, 13);

    private UITheme() {
    }

    public static void primary(JButton b) { styleButton(b, PRIMARY, Color.WHITE); }
    public static void accent(JButton b) { styleButton(b, ACCENT, Color.WHITE); }
    public static void secondary(JButton b) { styleButton(b, new Color(0xE7, 0xEA, 0xEE), TEXT); }
    public static void success(JButton b) { styleButton(b, OK, Color.WHITE); }

    private static void styleButton(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(BODY_BOLD);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorderPainted(false);
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(18, 18, 18, 18)));
        return p;
    }

    public static JLabel h2(String t) {
        JLabel l = new JLabel(t);
        l.setFont(H2);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel muted(String t) {
        JLabel l = new JLabel(t);
        l.setFont(SMALL);
        l.setForeground(MUTED);
        return l;
    }

    public static Border pad(int a) {
        return new EmptyBorder(a, a, a, a);
    }
}
