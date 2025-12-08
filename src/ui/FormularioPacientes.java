package ui;

import base.Paciente;
import base.Usuario;
import service.UsuarioService;
import exceptions.ConnectionException;
import exceptions.DAOException;
import exceptions.DatosInvalidosException;
import utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario para crear o editar un Paciente.
 * Utiliza UsuarioService para persistir los datos y ValidationUtils para validaciones.
 */
public class FormularioPacientes extends JDialog {

    private static final long serialVersionUID = 1L;
    private final UsuarioService usuarioService;
    private final String dniOriginal; // null para alta, DNI existente para edición

    private CampoPanel cpDni;
    private CampoPanel cpNombre;
    private CampoPanel cpApellido;
    private CampoPanel cpEmail;
    private BotonPanel botonPanel;

    public FormularioPacientes(Window owner, UsuarioService usuarioService, String dni) {
        super(owner, dni == null ? "Nuevo Paciente" : "Editar Paciente", ModalityType.APPLICATION_MODAL);
        this.usuarioService = usuarioService;
        this.dniOriginal = dni;

        initComponents();
        if (dni != null) {
        	// modo edición: no permito limpiar campos
            botonPanel.getBtnLimpiar().setVisible(false);
            cargarDatos(dni);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel panelCampos = new JPanel(new GridLayout(4, 1, 5, 5));
        cpDni     = new CampoPanel("DNI:",     8, false);
        cpNombre  = new CampoPanel("Nombre:",  20, false);
        cpApellido= new CampoPanel("Apellido:",20, false);
        cpEmail   = new CampoPanel("Email:",   20, false);

        panelCampos.add(cpDni);
        panelCampos.add(cpNombre);
        panelCampos.add(cpApellido);
        panelCampos.add(cpEmail);
        add(panelCampos, BorderLayout.CENTER);

        botonPanel = new BotonPanel();
        botonPanel.getBtnAgregar().setText("Guardar");
        botonPanel.getBtnLimpiar().setText("Limpiar");
        botonPanel.getBtnCancelar().setText("Cancelar");
        add(botonPanel, BorderLayout.SOUTH);
        
        // Oculto los botones que no uso en este panel
        botonPanel.getBtnEditar().setVisible(false);
        botonPanel.getBtnEliminar().setVisible(false);

        botonPanel.addActionAgregar(e -> guardarPaciente());
        botonPanel.addActionLimpiar(e -> limpiarCampos());
        botonPanel.addActionCancelar(e -> dispose());

        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private void cargarDatos(String dni) {
    	try {
            Usuario u = usuarioService.buscarPorDni(dni);
            cpDni.setTexto(u.getDni());
            cpDni.getField().setEnabled(false);

            cpNombre.setTexto(u.getNombre());
            cpApellido.setTexto(u.getApellido());
            cpEmail.setTexto(u.getEmail());
        } catch (DAOException | DatosInvalidosException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarPaciente() {
        // Validaciones previas
        try {
            ValidationUtils.validarNoVacio(
                cpDni.getTexto(),
                cpNombre.getTexto(),
                cpApellido.getTexto(),
                cpEmail.getTexto()
            );
            ValidationUtils.validarDni(cpDni.getTexto());
        } catch (DatosInvalidosException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Validación",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
      
        // Armo el paciente según modo alta o edición
        if (dniOriginal == null) {
            // ALTA
        	Paciente paciente = new Paciente(
	          cpDni.getTexto(),
	          cpNombre.getTexto(),
	          cpApellido.getTexto(),
	          cpEmail.getTexto()
    		);
            try {
                usuarioService.crearUsuario(paciente);
                JOptionPane.showMessageDialog(this, "Paciente creado con éxito.");
                dispose();
            } catch (DatosInvalidosException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
                );
            } catch (DAOException | ConnectionException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al crear paciente: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            // EDICIÓN
            try {
            	
                
                int id = usuarioService.buscarPorDni(dniOriginal).getId();
                Paciente paciente = new Paciente(id, cpDni.getTexto(), cpNombre.getTexto(),
                                                 cpApellido.getTexto(), cpEmail.getTexto());
                usuarioService.actualizarUsuario(paciente);
                JOptionPane.showMessageDialog(this, "Paciente actualizado con éxito.");
                dispose();
            } catch (DatosInvalidosException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
                );
            } catch (DAOException | ConnectionException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar paciente: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void limpiarCampos() {
        cpDni.limpiar();
        cpNombre.limpiar();
        cpApellido.limpiar();
        cpEmail.limpiar();
    }
}
