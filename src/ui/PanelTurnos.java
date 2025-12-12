package ui;

import base.Medico;
import base.Paciente;
import base.Rol;
import base.Turno;
import base.Usuario;
import service.TurnoService;
import service.UsuarioService;
import utils.ValidationUtils;
import exceptions.DatosInvalidosException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelTurnos extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final TurnoService turnoService;
    private final UsuarioService usuarioService;
    private final Usuario usuarioActual;
    
    private JComboBox<Medico> cmbMedicos;
    private JComboBox<Paciente> cmbPacientes;
    private CampoPanel cpFecha; 
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelTurnos(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        try {
            this.turnoService = new TurnoService();
            this.usuarioService = new UsuarioService();
        } catch (Exception e) { 
            throw new RuntimeException("Error iniciando servicios", e); 
        }

        setLayout(new BorderLayout(10, 10));
        
        // --- FORMULARIO ---
        JPanel panelForm = new JPanel(new GridLayout(0, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Agendar Nuevo Turno"));
        
        // 1. Selector de Médico (Visible solo si NO soy Médico)
        if (usuarioActual.getRol() != Rol.MEDICO) {
            panelForm.add(new JLabel("Médico:"));
            cmbMedicos = new JComboBox<>();
            cargarMedicos();
            panelForm.add(cmbMedicos);
        }

        // 2. Selector de Paciente (Visible solo si NO soy Paciente)
        if (usuarioActual.getRol() != Rol.PACIENTE) {
            panelForm.add(new JLabel("Paciente:"));
            cmbPacientes = new JComboBox<>();
            cargarPacientes();
            panelForm.add(cmbPacientes);
        }

        // Fecha
        cpFecha = new CampoPanel("Fecha (yyyy-MM-dd HH:mm):", 15, false);
        panelForm.add(cpFecha.getLabel()); 
        panelForm.add(cpFecha.getField());

        // Botón
        JButton btnGuardar = new JButton("Confirmar Turno");
        btnGuardar.addActionListener(e -> guardarTurno());
        panelForm.add(new JLabel("")); 
        panelForm.add(btnGuardar);

        add(panelForm, BorderLayout.NORTH);

        // --- TABLA ---
        // Definimos columnas según el rol
        String[] columnas;
        if (usuarioActual.getRol() == Rol.PACIENTE) {
            // Paciente ve al Médico
            columnas = new String[]{"Fecha y Hora", "Médico", "Costo"};
        } else if (usuarioActual.getRol() == Rol.MEDICO) {
            // Médico ve al Paciente
            columnas = new String[]{"Fecha y Hora", "Paciente", "Costo"};
        } else {
            // Admin ve a ambos
            columnas = new String[]{"Fecha y Hora", "Médico", "Paciente", "Costo"};
        }

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        
        recargarTabla();
    }

    private void cargarMedicos() {
        try {
            for (Medico m : usuarioService.listarMedicos()) cmbMedicos.addItem(m);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarPacientes() {
        try {
            for (Paciente p : usuarioService.listarPacientes()) cmbPacientes.addItem(p);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void guardarTurno() {
        try {
            // Determinamos Médico
            Medico m;
            if (usuarioActual.getRol() == Rol.MEDICO) {
                m = (Medico) usuarioActual;
            } else {
                m = (Medico) cmbMedicos.getSelectedItem();
            }

            // Determinamos Paciente
            Paciente p;
            if (usuarioActual.getRol() == Rol.PACIENTE) {
                p = (Paciente) usuarioActual;
            } else {
                p = (Paciente) cmbPacientes.getSelectedItem();
            }

            if (m == null || p == null) {
                throw new DatosInvalidosException("Faltan datos (médico o paciente).");
            }

            String fechaStr = cpFecha.getTexto();
            ValidationUtils.validarNoVacio(fechaStr);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaStr, formatter);
            ValidationUtils.validarFechaFutura(fechaHora);

            Turno t = new Turno(fechaHora, m, p);
            turnoService.registrarTurno(t);
            
            JOptionPane.showMessageDialog(this, "Turno registrado exitosamente.");
            cpFecha.limpiar();
            recargarTabla();
            
        } catch (DatosInvalidosException ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (ex instanceof java.time.format.DateTimeParseException) {
                msg = "Formato de fecha inválido. Use yyyy-MM-dd HH:mm";
            }
            JOptionPane.showMessageDialog(this, "Error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recargarTabla() {
        modelo.setRowCount(0);
        try {
            List<Turno> turnos;
            
            // Selección de método de búsqueda según rol
            if (usuarioActual.getRol() == Rol.PACIENTE) {
                turnos = turnoService.listarTurnosPorPaciente(usuarioActual.getId());
            } else if (usuarioActual.getRol() == Rol.MEDICO) {
                turnos = turnoService.listarTurnosPorMedico(usuarioActual.getId());
            } else {
                turnos = turnoService.listarTurnos();
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Turno t : turnos) {
                if (usuarioActual.getRol() == Rol.PACIENTE) {
                    modelo.addRow(new Object[]{
                        t.getFechaHora().format(fmt),
                        t.getMedico().getApellido() + " " + t.getMedico().getNombre(),
                        "$" + t.getCosto()
                    });
                } else if (usuarioActual.getRol() == Rol.MEDICO) {
                    modelo.addRow(new Object[]{
                        t.getFechaHora().format(fmt),
                        t.getPaciente().getApellido() + " " + t.getPaciente().getNombre(),
                        "$" + t.getCosto()
                    });
                } else {
                    modelo.addRow(new Object[]{
                        t.getFechaHora().format(fmt),
                        t.getMedico().getApellido(),
                        t.getPaciente().getApellido(),
                        "$" + t.getCosto()
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}