package ui;

import base.Medico;
import exceptions.DAOException;
import exceptions.DatosInvalidosException;
import service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel que muestra el listado de médicos y permite CRUD
 * (solo accesible para ADMIN).
 */
public class PanelTablaMedicos extends JPanel {
    private static final long serialVersionUID = 1L;

    private final UsuarioService usuarioService;
    private final JTable tabla;
    private final DefaultTableModel modelo;
    private final BotonPanel botonPanel;

    public PanelTablaMedicos(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

        setLayout(new BorderLayout(10, 10));

        // Columnas: ID, DNI, Nombre, Apellido, Email, Honorarios
        modelo = new DefaultTableModel(
            new String[]{"DNI", "Nombre", "Apellido", "Email", "Honorarios"},
            0) 
        {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones: Agregar, Editar, Eliminar
        botonPanel = new BotonPanel();
        add(botonPanel, BorderLayout.SOUTH);

        // Oculto los botones que no uso en este panel
        botonPanel.getBtnLimpiar().setVisible(false);
        botonPanel.getBtnCancelar().setVisible(false);
        
        botonPanel.addActionAgregar(e -> abrirFormulario(null));
        botonPanel.addActionEditar(e -> editarSeleccionado());
        botonPanel.addActionEliminar(e -> eliminarSeleccionado());

        // Carga inicial
        recargarDatos();
    }

    /** Recarga la lista de médicos desde la base de datos */
    public void recargarDatos() {
        modelo.setRowCount(0);
        try {
            List<Medico> medicos = usuarioService.listarMedicos();
            for (Medico m : medicos) {
                modelo.addRow(new Object[]{
                    m.getDni(),
                    m.getNombre(),
                    m.getApellido(),
                    m.getEmail(),
                    m.getHonorariosPorConsulta()
                });
            }
        } catch (DAOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al cargar médicos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre el formulario para alta (id == null) o edición (id != null)
     * y luego recarga la tabla.
     */
    private void abrirFormulario(String dniOriginal) {
        try {
            FormularioMedicos dlg = new FormularioMedicos(
                SwingUtilities.getWindowAncestor(this),
                usuarioService,
                dniOriginal    // ahora es String, no Integer
            );
            dlg.setModal(true);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
            recargarDatos();

        } catch (DatosInvalidosException ex) {
            JOptionPane.showMessageDialog(this,
                "No se encontró médico con DNI " + dniOriginal,
                "Error", JOptionPane.ERROR_MESSAGE);

        }
    }
    
    private void editarSeleccionado()
    {
    	int idx = tabla.getSelectedRow();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un médico para editar.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 1) Leo el DNI de la columna 0:
        String dni = (String) modelo.getValueAt(idx, 0);
        // 2) Abro el formulario pasando el DNI:
        abrirFormulario(dni);
    }

    /** Elimina el médico seleccionado tras confirmación */
    private void eliminarSeleccionado() {
    	int idx = tabla.getSelectedRow();
	    if (idx < 0) {
	        JOptionPane.showMessageDialog(this,
	            "Seleccione un médico para eliminar.",
	            "Validación", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
	    String dni = (String) modelo.getValueAt(idx, 0);
	    int confirm = JOptionPane.showConfirmDialog(this,
	        "¿Confirma la eliminación del médico con DNI " + dni + "?",
	        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
	    if (confirm != JOptionPane.YES_OPTION) return;

	    try {
	        // Busco el usuario por DNI para obtener su ID interno
	        int id = usuarioService.buscarPorDni(dni).getId();
	        usuarioService.eliminarUsuario(id);
	        recargarDatos();
	    } catch (Exception ex) {
	        JOptionPane.showMessageDialog(this,
	            "Error al eliminar médico: " + ex.getMessage(),
	            "Error", JOptionPane.ERROR_MESSAGE);
	    }
    }

}
