package ui;

import base.Medico;
import base.Paciente;
import base.Turno;
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
    
    private JComboBox<Medico> cmbMedicos;
    private JComboBox<Paciente> cmbPacientes;
    private CampoPanel cpFecha; // Reutilizamos tu componente CampoPanel
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelTurnos() {
    	try {
            this.turnoService = new TurnoService();   // <--- AHORA ESTÁ PROTEGIDO
            this.usuarioService = new UsuarioService();
        } catch (Exception e) { 
            throw new RuntimeException("Error iniciando servicios", e); 
        }

        setLayout(new BorderLayout(10, 10));
        
        // --- Formulario Superior ---
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        panelForm.add(new JLabel("Médico:"));
        cmbMedicos = new JComboBox<>();
        cargarMedicos();
        panelForm.add(cmbMedicos);

        panelForm.add(new JLabel("Paciente:"));
        cmbPacientes = new JComboBox<>();
        cargarPacientes();
        panelForm.add(cmbPacientes);

        // Usamos tu componente reutilizable
        cpFecha = new CampoPanel("Fecha (yyyy-MM-dd HH:mm):", 15, false);
        // Agregamos label y campo por separado porque el GridLayout lo pide así, 
        // o agregamos el panel entero si el layout lo permite.
        // Para simplificar en este GridLayout 4x2:
        panelForm.add(cpFecha.getLabel()); 
        panelForm.add(cpFecha.getField());

        JButton btnGuardar = new JButton("Agendar Turno");
        btnGuardar.addActionListener(e -> guardarTurno());
        panelForm.add(new JLabel("")); 
        panelForm.add(btnGuardar);

        add(panelForm, BorderLayout.NORTH);

        // --- Tabla Central ---
        modelo = new DefaultTableModel(new String[]{"Fecha", "Médico", "Paciente", "Costo"}, 0);
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
            Medico m = (Medico) cmbMedicos.getSelectedItem();
            Paciente p = (Paciente) cmbPacientes.getSelectedItem();
            String fechaStr = cpFecha.getTexto();
            
            ValidationUtils.validarNoVacio(fechaStr);
            
            // Parsear fecha 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaStr, formatter);
            
            ValidationUtils.validarFechaFutura(fechaHora);

            Turno t = new Turno(fechaHora, m, p);
            turnoService.registrarTurno(t);
            
            JOptionPane.showMessageDialog(this, "Turno agendado con éxito!");
            recargarTabla();
            
        } catch (DatosInvalidosException ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recargarTabla() {
        modelo.setRowCount(0);
        try {
            List<Turno> turnos = turnoService.listarTurnos();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Turno t : turnos) {
                modelo.addRow(new Object[]{
                    t.getFechaHora().format(fmt),
                    t.getMedico().getApellido(),
                    t.getPaciente().getApellido(),
                    "$" + t.getCosto()
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}