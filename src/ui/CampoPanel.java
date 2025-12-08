package ui;

import javax.swing.*;
import java.awt.FlowLayout;

/**
 * Panel que agrupa un JLabel y un campo de texto o contraseña.
 * Permite obtener o limpiar el contenido, comprobar si está vacío,
 * y acceder/desactivar el campo programáticamente.
 */
public class CampoPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel label;
    private JTextField textField;
    private JPasswordField passwordField;
    private final boolean esPassword;

    /**
     * @param textoEtiqueta Texto que mostrará la etiqueta.
     * @param columnas      Número de columnas del campo.
     * @param esPassword    true para JPasswordField, false para JTextField.
     */
    public CampoPanel(String textoEtiqueta, int columnas, boolean esPassword) {
        super(new FlowLayout(FlowLayout.LEFT, 5, 5));
        this.esPassword = esPassword;
        label = new JLabel(textoEtiqueta);
        add(label);

        if (esPassword) {
            passwordField = new JPasswordField(columnas);
            add(passwordField);
        } else {
            textField = new JTextField(columnas);
            add(textField);
        }
    }

    /** Devuelve el texto ingresado (o la contraseña como String). */
    public String getTexto() {
        if (esPassword) {
            return new String(passwordField.getPassword());
        } else {
            return textField.getText();
        }
    }

    /** Fija el texto del campo (útil en edición). */
    public void setTexto(String texto) {
        if (esPassword) {
            passwordField.setText(texto);
        } else {
            textField.setText(texto);
        }
    }

    /** Limpia el contenido del campo. */
    public void limpiar() {
        if (esPassword) {
            passwordField.setText("");
        } else {
            textField.setText("");
        }
    }

    /** Indica si el campo está vacío (espacios contados como vacíos). */
    public boolean esVacio() {
        return getTexto().trim().isEmpty();
    }

    /**
     * Obtiene el componente de entrada (JTextField o JPasswordField)
     * para configurarlo (habilitar, deshabilitar, etc.).
     * @return el componente de entrada subyacente.
     */
    public JComponent getField() {
        return esPassword ? passwordField : textField;
    }

    /**
     * Habilita o deshabilita este panel y su campo de entrada.
     * @param enabled true para habilitar, false para deshabilitar.
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getField().setEnabled(enabled);
        label.setEnabled(enabled);
    }

    /**
     * Obtiene la etiqueta asociada al campo.
     * @return el JLabel de descripción del campo.
     */
    public JLabel getLabel() {
        return label;
    }
}
