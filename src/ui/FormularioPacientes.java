package ui;

import base.ObraSocial;
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
 */
public class FormularioPacientes extends JDialog {

    private static final long serialVersionUID = 1L;
    private final UsuarioService usuarioService;
    private final String dniOriginal; 

    private CampoPanel cpDni;
    private CampoPanel cpNombre;
    private CampoPanel cpApellido;
    private CampoPanel cpEmail;
    
    // NUEVO: Combo para Obra Social
    private JComboBox<ObraSocial> cmbObraSocial;
    
    private BotonPanel botonPanel;

    public FormularioPacientes(Window owner, UsuarioService usuarioService, String dni) {
        super(owner, dni == null ? "Nuevo Paciente" : "Editar Paciente", ModalityType.APPLICATION_MODAL);
        this.usuarioService = usuarioService;
        this.dniOriginal = dni;

        initComponents();
        if (dni != null) {
            botonPanel.getBtnLimpiar().setVisible(false);
            cargarDatos(dni);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel vertical
        JPanel panelCampos = new JPanel(new GridLayout(0, 1, 5, 5));
        
        cpDni     = new CampoPanel("DNI:",     8, false);
        cpNombre  = new CampoPanel("Nombre:",  20, false);
        cpApellido= new CampoPanel("Apellido:",20, false);
        cpEmail   = new CampoPanel("Email:",   20, false);

        // Panel para el combo
        JPanel panelOS = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelOS.add(new JLabel("Obra Social:"));
        cmbObraSocial = new JComboBox<>(ObraSocial.values());
        panelOS.add(cmbObraSocial);

        panelCampos.add(cpDni);
        panelCampos.add(cpNombre);
        panelCampos.add(cpApellido);
        panelCampos.add(cpEmail);
        panelCampos.add(panelOS); // Agregado
        
        add(panelCampos, BorderLayout.CENTER);

        botonPanel = new BotonPanel();
        botonPanel.getBtnAgregar().setText("Guardar");
        botonPanel.getBtnLimpiar().setText("Limpiar");
        botonPanel.getBtnCancelar().setText("Cancelar");
        add(botonPanel, BorderLayout.SOUTH);
        
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
            
            // Cargar OS
            if (u.getObraSocial() != null) {
                cmbObraSocial.setSelectedItem(u.getObraSocial());
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarPaciente() {
        try {
            ValidationUtils.validarNoVacio(
                cpDni.getTexto(),
                cpNombre.getTexto(),
                cpApellido.getTexto(),
                cpEmail.getTexto()
            );
            ValidationUtils.validarDni(cpDni.getTexto());
            
            ObraSocial os = (ObraSocial) cmbObraSocial.getSelectedItem();
            
            if (dniOriginal == null) {
                // ALTA
                Paciente paciente = new Paciente(
                  cpDni.getTexto(),
                  cpNombre.getTexto(),
                  cpApellido.getTexto(),
                  cpEmail.getTexto(),
                  os // <---
                );
                usuarioService.crearUsuario(paciente);
                JOptionPane.showMessageDialog(this, "Paciente creado con éxito.");
            } else {
                // EDICIÓN
                int id = usuarioService.buscarPorDni(dniOriginal).getId();
                Paciente paciente = new Paciente(
                    id, 
                    cpDni.getTexto(), 
                    cpNombre.getTexto(),
                    cpApellido.getTexto(), 
                    cpEmail.getTexto(),
                    os // <---
                );
                usuarioService.actualizarUsuario(paciente);
                JOptionPane.showMessageDialog(this, "Paciente actualizado con éxito.");
            }
            dispose();
            
        } catch (DatosInvalidosException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (DAOException | ConnectionException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        cpDni.limpiar();
        cpNombre.limpiar();
        cpApellido.limpiar();
        cpEmail.limpiar();
        cmbObraSocial.setSelectedIndex(0);
    }
}