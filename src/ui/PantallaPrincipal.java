package ui;

import base.Rol;
import base.Usuario;
import exceptions.DAOException;
import service.UsuarioService;
import service.TurnoService;

import javax.swing.*;
import java.awt.BorderLayout; // Import necesario para el diseño
import java.awt.Color;        // Para estética (opcional)
import java.awt.FlowLayout;   // Para centrar el texto
import java.awt.Font;         // Para darle estilo a la fuente
import java.sql.SQLException;

public class PantallaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    // Eliminamos 'contentPane' si no se usa o lo usamos como el panel principal
    private JDesktopPane desktopPane;
    private Usuario loggedUser;
    
    // Servicios
    private UsuarioService usuarioService;
    private final TurnoService turnoService;

    // Menús
    private JMenuItem miMedicos;
    private JMenuItem miPacientes;
    private JMenuItem miTurnos;
    private JMenuItem miReportes;
    private JMenuItem miSalir;

    public PantallaPrincipal(Usuario usuario) {
        this.loggedUser = usuario;
        setTitle("Turnera Médica - Usuario: " + usuario.getNombre() + " " + usuario.getApellido() + " (" + usuario.getRol() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);

        // Inicializar Servicios
        try {
            usuarioService = new UsuarioService();
            turnoService = new TurnoService(); 
        } catch (DAOException | SQLException ex) {
            throw new RuntimeException("No se pudo inicializar los servicios", ex);
        }

        // --- BARRA DE MENÚ ---
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnArchivo = new JMenu("Archivo");
        menuBar.add(mnArchivo);

        miSalir = new JMenuItem("Salir");
        miSalir.addActionListener(e -> System.exit(0));
        mnArchivo.add(miSalir);

        JMenu mnGestion = new JMenu("Gestión");
        menuBar.add(mnGestion);

        miMedicos = new JMenuItem("Médicos");
        miPacientes = new JMenuItem("Pacientes");
        miTurnos = new JMenuItem("Turnos");
        miReportes = new JMenuItem("Reportes");

        // Lógica de roles
        if (loggedUser.getRol() == Rol.ADMIN) {
            mnGestion.add(miMedicos);
            mnGestion.add(miPacientes);
            mnGestion.addSeparator();
            mnGestion.add(miReportes);
        }
        mnGestion.add(miTurnos); 

        // Listeners
        miMedicos.addActionListener(e -> abrirMedicos());
        miPacientes.addActionListener(e -> abrirPacientes());
        miTurnos.addActionListener(e -> abrirTurnos());
        miReportes.addActionListener(e -> abrirReportes());

        // --- ESTRUCTURA PRINCIPAL (LAYOUT) ---
        
        // 1. Panel contenedor principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 2. El escritorio va al CENTRO (Ocupa todo el espacio disponible)
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.LIGHT_GRAY); // Un color de fondo suave
        mainPanel.add(desktopPane, BorderLayout.CENTER);
        
        // 3. BARRA INFERIOR (Footer) va al SUR
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBorder(BorderFactory.createEtchedBorder()); // Un borde sutil
        
        JLabel lblFooter = new JLabel("TP - UP - Joaquin Carruego - 2025");
        lblFooter.setFont(new Font("Arial", Font.BOLD, 12));
        footerPanel.add(lblFooter);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Asignamos el panel principal a la ventana
        setContentPane(mainPanel);
    }

    private void abrirMedicos() {
        try {
            JInternalFrame jf = new JInternalFrame("Gestión de Médicos", true, true, true, true);
            PanelTablaMedicos panel = new PanelTablaMedicos(usuarioService);
            jf.setContentPane(panel);
            jf.pack();
            jf.setSize(600, 400);
            desktopPane.add(jf);
            jf.setVisible(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void abrirPacientes() {
        try {
            JInternalFrame jf = new JInternalFrame("Gestión de Pacientes", true, true, true, true);
            PanelTablaPacientes panel = new PanelTablaPacientes(usuarioService);
            jf.setContentPane(panel);
            jf.pack();
            jf.setSize(600, 400);
            desktopPane.add(jf);
            jf.setVisible(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void abrirTurnos() {
        try {
            JInternalFrame jf = new JInternalFrame("Turnos", true, true, true, true);
            PanelTurnos panel = new PanelTurnos(loggedUser); 
            jf.setContentPane(panel);
            jf.pack();
            jf.setSize(700, 500);
            desktopPane.add(jf);
            jf.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al abrir turnos: " + ex.getMessage());
        }
    }
    
    private void abrirReportes() {
        try {
            JInternalFrame jf = new JInternalFrame("Reportes de Recaudación", true, true, true, true);
            PanelReportes panel = new PanelReportes(loggedUser);
            jf.setContentPane(panel);
            jf.pack();
            jf.setSize(600, 450);
            desktopPane.add(jf);
            jf.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}