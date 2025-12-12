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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class PanelTurnos extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final TurnoService turnoService;
    private final UsuarioService usuarioService;
    private final Usuario usuarioActual;
    
    // Componentes gráficos
    private JComboBox<Medico> cmbMedicos;
    private JComboBox<Paciente> cmbPacientes;
    
    // Nuevos componentes para Fecha y Hora
    private JSpinner spinnerFecha;
    private JComboBox<String> comboHora;
    
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelTurnos(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        try {
            this.turnoService = new TurnoService();
            this.usuarioService = new UsuarioService();
        } catch (Exception e) { 
            throw new RuntimeException("Error iniciando servicios en PanelTurnos", e); 
        }

        setLayout(new BorderLayout(10, 10));
        
        // --- 1. SECCIÓN FORMULARIO (ARRIBA) ---
        JPanel panelForm = new JPanel(new GridLayout(0, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Agendar Nuevo Turno"));
        
        // -- Selector de Médico --
        panelForm.add(new JLabel("Médico:"));
        cmbMedicos = new JComboBox<>();
        cargarMedicos();
        panelForm.add(cmbMedicos);

        // -- Selector de Paciente --
        if (usuarioActual.getRol() != Rol.PACIENTE) {
            panelForm.add(new JLabel("Paciente:"));
            cmbPacientes = new JComboBox<>();
            cargarPacientes();
            panelForm.add(cmbPacientes);
        }

        // -- Selector de FECHA (Calendario simple con Spinner) --
        panelForm.add(new JLabel("Fecha del Turno:"));
        // Configuramos el Spinner para manejar fechas
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spinnerFecha = new JSpinner(dateModel);
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(de);
        spinnerFecha.setValue(new Date()); // Seteamos hoy por defecto
        panelForm.add(spinnerFecha);

        // -- Selector de HORA (Intervalos de 30 mins) --
        panelForm.add(new JLabel("Horario (30 min):"));
        comboHora = new JComboBox<>();
        cargarHorariosDisponibles();
        panelForm.add(comboHora);

        // -- Botón Guardar --
        JButton btnGuardar = new JButton("Confirmar Turno");
        btnGuardar.addActionListener(e -> guardarTurno());
        
        panelForm.add(new JLabel("")); // Espaciador
        panelForm.add(btnGuardar);

        add(panelForm, BorderLayout.NORTH);

        // --- 2. SECCIÓN TABLA (CENTRO) ---
        String[] columnas;
        if (usuarioActual.getRol() == Rol.PACIENTE) {
            columnas = new String[]{"Fecha y Hora", "Médico", "Costo"};
        } else {
            columnas = new String[]{"Fecha y Hora", "Médico", "Paciente", "Costo"};
        }

        modelo = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        
        recargarTabla();
    }

    /** Carga horarios de 08:00 a 20:00 cada 30 minutos */
    private void cargarHorariosDisponibles() {
        for (int hora = 8; hora < 20; hora++) {
            comboHora.addItem(String.format("%02d:00", hora));
            comboHora.addItem(String.format("%02d:30", hora));
        }
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
            Paciente p;

            if (usuarioActual.getRol() == Rol.PACIENTE) {
                p = (Paciente) usuarioActual;
            } else {
                p = (Paciente) cmbPacientes.getSelectedItem();
            }

            if (m == null || p == null) {
                throw new DatosInvalidosException("Debe seleccionar médico y paciente.");
            }

            // 1. Obtener FECHA del Spinner
            Date fechaDate = (Date) spinnerFecha.getValue();
            // Convertir a LocalDate (nueva API de Java 8)
            LocalDate fecha = fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // 2. Obtener HORA del Combo
            String horaStr = (String) comboHora.getSelectedItem();
            LocalTime hora = LocalTime.parse(horaStr);

            // 3. Combinar en LocalDateTime
            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
            
            // 4. Validar que sea futura
            ValidationUtils.validarFechaFutura(fechaHora);

            Turno t = new Turno(fechaHora, m, p);
            turnoService.registrarTurno(t);
            
            JOptionPane.showMessageDialog(this, "Turno registrado exitosamente.");
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
            List<Turno> turnos;
            
            // Los turnos ya vienen ordenados desde el DAO (ORDER BY fechaHora ASC)
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