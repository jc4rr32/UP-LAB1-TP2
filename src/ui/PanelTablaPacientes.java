package ui;

import base.Rol;
import base.Usuario;
import service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel que muestra el ABM de pacientes:
 * - Lista los pacientes en una JTable.
 * - Botones para Agregar, Editar y Eliminar.
 */
public class PanelTablaPacientes extends JPanel {
    private final UsuarioService usuarioService;
    private final JTable tabla;
    private final DefaultTableModel modelo;
    private final BotonPanel botonPanel;

    public PanelTablaPacientes(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

        setLayout(new BorderLayout(10, 10));

        // Modelo de tabla con columnas: DNI, Nombre, Apellido, Email
        modelo = new DefaultTableModel(new String[]{"DNI", "Nombre", "Apellido", "Email"}, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // no editable directamente
            }
        };
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel de botones reutilizable
        botonPanel = new BotonPanel();
        add(botonPanel, BorderLayout.SOUTH);
        
        // Oculto los botones que no uso en este panel
        botonPanel.getBtnLimpiar().setVisible(false);
        botonPanel.getBtnCancelar().setVisible(false);

        // Listeners de botones
        botonPanel.addActionAgregar(e -> abrirFormulario(null));
        
        botonPanel.addActionEditar(e -> {
            int idx = tabla.getSelectedRow();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un paciente para editar.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String dni = (String) modelo.getValueAt(idx, 0);
            abrirFormulario(dni);
        });
        
        botonPanel.addActionEliminar(e -> {
            int idx = tabla.getSelectedRow();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un paciente para eliminar.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Obtengo el DNI y luego el objeto para extraer el ID:
            String dni = (String) modelo.getValueAt(idx, 0);
            try {
                Usuario u = usuarioService.buscarPorDni(dni);
                int id = u.getId();
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirma la eliminación del paciente con DNI " + dni + "?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    usuarioService.eliminarUsuario(id);  // método que recibe el id
                    recargarDatos();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar paciente: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Carga inicial
        recargarDatos();
    }

    /**  
     * Recarga la tabla con todos los pacientes (rol PACIENTE).
     */
    public void recargarDatos() {
        modelo.setRowCount(0);
        try {
            List<Usuario> todos = usuarioService.listarUsuarios();
            for (Usuario u : todos) {
                if (u.getRol() == Rol.PACIENTE) {
                    modelo.addRow(new Object[]{
                        u.getDni(),
                        u.getNombre(),
                        u.getApellido(),
                        u.getEmail()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar pacientes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre el formulario para crear o editar un paciente.
     * @param dni existente para edición, o null para alta.
     */
    private void abrirFormulario(String dni) {
        // Obtengo la ventana que contiene este panel
        Window owner = SwingUtilities.getWindowAncestor(this);
        // Llamo al constructor correcto: (dueño, service, dni)
        FormularioPacientes formulario = new FormularioPacientes(owner, usuarioService, dni);
        formulario.setModal(true);
        formulario.setLocationRelativeTo(owner);
        formulario.setVisible(true);
        recargarDatos();
    }
}
