package ui;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

/**
 * REUTILIZABLE
 * Panel que agrupa los botones comunes de los ABM: Agregar, Editar, Eliminar,
 * Limpiar y Cancelar.
 */
public class BotonPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JButton btnAgregar;
    private final JButton btnEditar;
    private final JButton btnEliminar;
    private final JButton btnLimpiar;
    private final JButton btnCancelar;

    public BotonPanel() {
        super(new FlowLayout(FlowLayout.CENTER, 10, 10));
        // Creamos los botones
        btnAgregar  = new JButton("Agregar");
        btnEditar   = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar  = new JButton("Limpiar");
        btnCancelar = new JButton("Cancelar");

        // Los añadimos al panel
        add(btnAgregar);
        add(btnEditar);
        add(btnEliminar);
        add(btnLimpiar);
        add(btnCancelar);
    }

    // Métodos para asignar ActionListeners a cada botón
    public void addActionAgregar(ActionListener listener) {
        btnAgregar.addActionListener(listener);
    }

    public void addActionEditar(ActionListener listener) {
        btnEditar.addActionListener(listener);
    }

    public void addActionEliminar(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }

    public void addActionLimpiar(ActionListener listener) {
        btnLimpiar.addActionListener(listener);
    }

    public void addActionCancelar(ActionListener listener) {
        btnCancelar.addActionListener(listener);
    }

    // Getters en caso de necesitarlos directamente
    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }
}
