package ui;

import base.Medico;
import base.ObraSocial;
import exceptions.ConnectionException;
import exceptions.DAOException;
import exceptions.DatosInvalidosException;
import service.UsuarioService;
import utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario para alta/edición de Médicos.
 * Solo accesible para ADMIN.
 */
public class FormularioMedicos extends JDialog {
    private static final long serialVersionUID = 1L;

    private final UsuarioService usuarioService;
    // null = alta, no null = edición
    private final String medicoDni;  

    private CampoPanel cpDni;
    private CampoPanel cpNombre;
    private CampoPanel cpApellido;
    private CampoPanel cpEmail;
    private CampoPanel cpHonorarios;
    
    // NUEVO: Combo para Obra Social
    private JComboBox<ObraSocial> cmbObraSocial;
    
    private BotonPanel botonPanel;

    public FormularioMedicos(Window owner,
                            UsuarioService usuarioService,
                            String medicoDni) {
          super(owner,
                  medicoDni == null ? "Nuevo Médico" : "Editar Médico",
                  ModalityType.APPLICATION_MODAL);
            this.usuarioService = usuarioService;
            this.medicoDni      = medicoDni;
            try {
                initComponents();
                if (medicoDni != null) {
                    // modo edición: no permito limpiar campos
                    botonPanel.getBtnLimpiar().setVisible(false);
                    cargarDatos(medicoDni);                
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al inicializar formulario:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
    }
  
    private void initComponents() {
        // padding exterior
        ((JComponent)getContentPane())
            .setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setLayout(new BorderLayout(10,10));

        // Panel de campos (Cambié a 0 filas para que crezca dinámicamente)
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        cpDni       = new CampoPanel("DNI:",       10, false);
        cpNombre    = new CampoPanel("Nombre:",    15, false);
        cpApellido  = new CampoPanel("Apellido:",  15, false);
        cpEmail     = new CampoPanel("Email:",     20, false);
        cpHonorarios= new CampoPanel("Honorarios:", 10, false);

        // NUEVO: Panel para el Combo de Obra Social (simulando estilo de CampoPanel)
        JPanel panelOS = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelOS.add(new JLabel("Obra Social:"));
        cmbObraSocial = new JComboBox<>(ObraSocial.values());
        panelOS.add(cmbObraSocial);

        panel.add(cpDni);
        panel.add(cpNombre);
        panel.add(cpApellido);
        panel.add(cpEmail);
        panel.add(cpHonorarios);
        panel.add(panelOS); // Agregamos el combo al formulario

        add(panel, BorderLayout.CENTER);

        // Botones
        botonPanel = new BotonPanel();
        botonPanel.getBtnAgregar().setText("Guardar");
        botonPanel.getBtnEditar().setVisible(false);
        botonPanel.getBtnEliminar().setVisible(false);
        
        botonPanel.addActionAgregar(e -> guardarMedico());
        botonPanel.addActionLimpiar(e -> limpiarCampos());
        botonPanel.addActionCancelar(e -> dispose());
        
        add(botonPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private void cargarDatos(String dni) throws DAOException, DatosInvalidosException {
        // Buscar el usuario por DNI y castear a Medico
        Medico m = (Medico) usuarioService.buscarPorDni(dni);

        // Rellenar los campos
        cpDni.setTexto(m.getDni());
        cpDni.getField().setEnabled(false); // No se puede cambiar el DNI al editar
        cpNombre.setTexto(m.getNombre());
        cpApellido.setTexto(m.getApellido());
        cpEmail.setTexto(m.getEmail());
        cpHonorarios.setTexto(Double.toString(m.getHonorariosPorConsulta()));
        
        // NUEVO: Seleccionar la obra social guardada
        if (m.getObraSocial() != null) {
            cmbObraSocial.setSelectedItem(m.getObraSocial());
        }
    }

    private void guardarMedico() {
        try {
            // Validaciones
            ValidationUtils.validarNoVacio(cpDni.getTexto(),        "DNI");
            ValidationUtils.validarNoVacio(cpNombre.getTexto(),     "Nombre");
            ValidationUtils.validarNoVacio(cpApellido.getTexto(),   "Apellido");
            ValidationUtils.validarNoVacio(cpEmail.getTexto(),      "Email");
            ValidationUtils.validarNoVacio(cpHonorarios.getTexto(), "Honorarios");

            double honor;
            try {
                honor = Double.parseDouble(cpHonorarios.getTexto());
            } catch (NumberFormatException ex) {
                throw new DatosInvalidosException("Honorarios debe ser un número válido.");
            }
            if (honor <= 0) {
                throw new DatosInvalidosException("Honorarios debe ser mayor que cero.");
            }

            // Recuperamos la Obra Social seleccionada
            ObraSocial os = (ObraSocial) cmbObraSocial.getSelectedItem();

            Medico m;
            String dni = cpDni.getTexto();
            
            if (medicoDni == null) {
                // Alta (Constructor con Obra Social)
                m = new Medico(
                    dni,
                    cpNombre.getTexto(),
                    cpApellido.getTexto(),
                    cpEmail.getTexto(),
                    honor,
                    os // <--- Pasamos la Obra Social
                );
                usuarioService.crearUsuario(m);
                JOptionPane.showMessageDialog(this, "Médico creado con éxito.");
            } else {
                // Edición
                Medico existente = (Medico) usuarioService.buscarPorDni(medicoDni);
                m = new Medico(
                    existente.getId(),
                    dni,
                    cpNombre.getTexto(),
                    cpApellido.getTexto(),
                    cpEmail.getTexto(),
                    honor,
                    os // <--- Pasamos la Obra Social
                );
                usuarioService.actualizarUsuario(m);
                JOptionPane.showMessageDialog(this, "Médico actualizado con éxito.");
            }

            dispose();

        } catch (DatosInvalidosException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (DAOException | ConnectionException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar médico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        if (medicoDni == null) {
            cpDni.limpiar();
        }
        cpNombre.limpiar();
        cpApellido.limpiar();
        cpEmail.limpiar();
        cpHonorarios.limpiar();
        cmbObraSocial.setSelectedIndex(0);
    }
}