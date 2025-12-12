package ui;

import base.Medico;
import service.ReporteService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class PanelReportes extends JPanel {
    private static final long serialVersionUID = 1L;

    public PanelReportes() {
        setLayout(new BorderLayout());
        
        JLabel titulo = new JLabel("Reporte de Recaudación por Médico", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Médico", "Recaudación Total"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            ReporteService service = new ReporteService();
            Map<Medico, Double> data = service.calcularRecaudacionTotal();
            
            for (Map.Entry<Medico, Double> entry : data.entrySet()) {
                Medico m = entry.getKey();
                model.addRow(new Object[]{
                    m.getApellido() + ", " + m.getNombre() + " (DNI: " + m.getDni() + ")",
                    "$" + entry.getValue()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage());
        }
    }
}