package ui;

import base.Medico;
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
    private BotonPanel botonPanel;

    public FormularioMedicos(Window owner,
				            UsuarioService usuarioService,
				            String medicoDni) throws DatosInvalidosException {
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
            } catch (DatosInvalidosException | DAOException ex) {
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

        // Panel de campos
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        cpDni       = new CampoPanel("DNI:",       10, false);
        cpNombre    = new CampoPanel("Nombre:",    15, false);
        cpApellido  = new CampoPanel("Apellido:",  15, false);
        cpEmail     = new CampoPanel("Email:",     20, false);
        cpHonorarios= new CampoPanel("Honorarios:", 10, false);

        panel.add(cpDni);
        panel.add(cpNombre);
        panel.add(cpApellido);
        panel.add(cpEmail);
        panel.add(cpHonorarios);

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

        // Rellenar los campos y deshabilitar el DNI
        cpDni.setTexto(m.getDni());
        cpDni.getField().setEnabled(false);
        cpNombre.setTexto(m.getNombre());
        cpApellido.setTexto(m.getApellido());
        cpEmail.setTexto(m.getEmail());
        cpHonorarios.setTexto(Double.toString(m.getHonorariosPorConsulta()));
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

            Medico m;
            String dni = cpDni.getTexto();
            if (medicoDni == null) {
                // Alta
                m = new Medico(
                    dni,
                    cpNombre.getTexto(),
                    cpApellido.getTexto(),
                    cpEmail.getTexto(),
                    honor
                );
                usuarioService.crearUsuario(m);
                JOptionPane.showMessageDialog(this,
                    "Médico creado con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Edición
                // obtenemos el usuario existente para recuperar su ID
                Medico existente = (Medico) usuarioService.buscarPorDni(medicoDni);
                m = new Medico(
                    existente.getId(),
                    dni,
                    cpNombre.getTexto(),
                    cpApellido.getTexto(),
                    cpEmail.getTexto(),
                    honor
                );
                usuarioService.actualizarUsuario(m);
                JOptionPane.showMessageDialog(this,
                    "Médico actualizado con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();

        } catch (DatosInvalidosException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Validación", JOptionPane.WARNING_MESSAGE);


        } catch (DAOException | ConnectionException ex) {
            // Ahora atrapamos también ConnectionException
            JOptionPane.showMessageDialog(this,
                "Error al guardar médico: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
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
    }
}
