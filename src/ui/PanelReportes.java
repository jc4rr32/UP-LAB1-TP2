package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

import base.Medico;
import base.Rol;
import base.Usuario;
import service.TurnoService;
import service.UsuarioService;

/**
 * Pantalla de Reportes de Recaudación.
 * Permite filtrar por rango de fechas y por médico.
 * * Lógica de seguridad:
 * - ADMIN: Puede ver "Todos los médicos" (reporte general) o seleccionar uno específico.
 * - MÉDICO: Solo puede verse a sí mismo (el combo aparece bloqueado o con una única opción).
 */
public class PanelReportes extends JPanel {
    private static final long serialVersionUID = 1L;

    private TurnoService turnoService;
    private UsuarioService usuarioService;
    private Usuario usuarioActual; // Usuario logueado para validar permisos

    // Componentes gráficos
    private JSpinner spinnerDesde;
    private JSpinner spinnerHasta;
    private JComboBox<Medico> cmbMedicos;
    private JTable tabla;
    private DefaultTableModel modelo;

    /**
     * Constructor modificado para recibir el usuario actual.
     * @param usuarioActual El usuario que está usando el sistema (logueado).
     */
    public PanelReportes(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        try {
            // Inicializamos servicios (Mismo patrón que en otros paneles)
            this.turnoService = new TurnoService();
            this.usuarioService = new UsuarioService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error iniciando servicios: " + e.getMessage());
        }

        setLayout(new BorderLayout(10, 10));
        
        // --- 1. PANEL DE FILTROS (Norte) ---
        JPanel panelFiltros = new JPanel(new GridLayout(3, 2, 5, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Reporte"));

        // Filtro: Fecha Desde
        panelFiltros.add(new JLabel("Fecha Desde:"));
        spinnerDesde = crearSpinnerFecha();
        panelFiltros.add(spinnerDesde);

        // Filtro: Fecha Hasta
        panelFiltros.add(new JLabel("Fecha Hasta:"));
        spinnerHasta = crearSpinnerFecha();
        panelFiltros.add(spinnerHasta);

        // Filtro: Selección de Médico
        panelFiltros.add(new JLabel("Médico:"));
        cmbMedicos = new JComboBox<>();
        cargarMedicosEnCombo(); // Lógica inteligente de carga según rol
        panelFiltros.add(cmbMedicos);

        add(panelFiltros, BorderLayout.NORTH);

        // --- 2. TABLA DE RESULTADOS (Centro) ---
        // Definimos las columnas: Nombre del Médico, Cantidad de Turnos, Total Recaudado
        modelo = new DefaultTableModel(
            new String[]{"Médico", "Turnos Atendidos", "Total Recaudado"}, 0
        ) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos que la tabla sea de solo lectura
            }
        };
        
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- 3. BOTONERA (Sur) ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGenerar = new JButton("Generar Reporte");
        btnGenerar.addActionListener(e -> generarReporte());
        
        panelBotones.add(btnGenerar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Crea un JSpinner configurado como calendario (dd/MM/yyyy).
     */
    private JSpinner crearSpinnerFecha() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        spinner.setValue(new Date()); // Fecha por defecto: Hoy
        return spinner;
    }

    /**
     * Carga el ComboBox de médicos aplicando reglas de negocio según el rol.
     */
    private void cargarMedicosEnCombo() {
        cmbMedicos.removeAllItems();
        try {
            if (usuarioActual.getRol() == Rol.ADMIN) {
                // CASO ADMIN: Ve la opción "Todos" y luego la lista de médicos
                
                // 1. Crear opción "cualquiera" para representar a todos
                // Usamos una clase anónima para sobrescribir toString() solo visualmente TODO: investigar si esto se puede hacer mas limpio
                Medico opcionTodos = new Medico(-1, "", "", "", "", 0, null) {
                    @Override
                    public String toString() {
                        return "--- Todos los Médicos ---";
                    }
                };
                cmbMedicos.addItem(opcionTodos);

                // 2. Cargar médicos reales desde la base de datos
                for (Medico m : usuarioService.listarMedicos()) {
                    cmbMedicos.addItem(m);
                }
                
            } else if (usuarioActual.getRol() == Rol.MEDICO) {
                // CASO MÉDICO: Solo se ve a sí mismo
                // Casteamos el usuario actual a Medico y lo agregamos como única opción
                cmbMedicos.addItem((Medico) usuarioActual);
                cmbMedicos.setEnabled(false);
            }
            // Si es PACIENTE no debería entrar a esta pantalla, pero por seguridad el combo quedaría vacío.
            
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Ejecuta la lógica de reporte llamando al servicio y actualizando la tabla.
     */
    private void generarReporte() {
        try {
            // 1. Obtener fechas de los Spinners
            LocalDate desde = ((Date)spinnerDesde.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = ((Date)spinnerHasta.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // 2. Obtener el ID del médico seleccionado
            Medico seleccionado = (Medico) cmbMedicos.getSelectedItem();
            if (seleccionado == null) return;

            // Si el ID es -1 (objeto cualquiera "Todos"), el servicio sabrá que es un reporte general.
            int idMedico = seleccionado.getId();

            // 3. Llamada al servicio (devuelve lista de Object[] para no usar DTOs)
            // [0]=Nombre, [1]=Apellido, [2]=Cantidad(int), [3]=Total(double)
            List<Object[]> resultados = turnoService.generarReporte(desde, hasta, idMedico);

            // 4. Llenar la tabla
            modelo.setRowCount(0);
            double granTotal = 0;
            
            for (Object[] fila : resultados) {
                String nombre = (String) fila[0];
                String apellido = (String) fila[1];
                int cantidad = (Integer) fila[2];
                double total = (Double) fila[3];

                modelo.addRow(new Object[] {
                    apellido + ", " + nombre,
                    cantidad,
                    String.format("$ %.2f", total) // Formato moneda
                });
                granTotal += total;
            }

            // 5. Feedback visual
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron datos para el período seleccionado.");
            } else if (idMedico == -1) {
                // Si es reporte general ("Todos"), agregamos una fila final con la suma total
                modelo.addRow(new Object[]{"--- TOTAL GRAL ---", "", String.format("$ %.2f", granTotal)});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}