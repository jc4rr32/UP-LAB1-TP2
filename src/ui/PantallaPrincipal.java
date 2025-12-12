package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import base.Rol;
import base.Usuario;
import service.UsuarioService;
import exceptions.DAOException;

/**
 * Pantalla principal tras el login
 * 
 */
public class PantallaPrincipal extends JFrame {
	//LoggedUser pasa info del usuario actual en cada panel
	private final Usuario loggedUser;
	private final JDesktopPane desktopPane;
	private final UsuarioService usuarioService;
    //private final TurnoService turnoService;

	// Menú “Ver”
	private JMenuItem miPacientes;
	private JMenuItem miMedicos;
	//private JMenuItem miTurnos;
	//private JMenuItem miReportes;
	private JMenuItem miSalir;

	public PantallaPrincipal(Usuario usuario) {
		super("Panel Principal - "
	              + usuario.getApellido() + ", " + usuario.getNombre()
	              + " (" + usuario.getRol() + ")");
	        this.loggedUser = usuario;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

//        // Etiqueta de bienvenida
//        JLabel lblBienvenido = new JLabel(
//            "Bienvenido, " + usuario.getNombre() + " " + usuario.getApellido(),
//            SwingConstants.CENTER);
//        lblBienvenido.setFont(lblBienvenido.getFont().deriveFont(Font.BOLD, 16f));
//        add(lblBienvenido, BorderLayout.NORTH);

		// Crear barra de menú estilo Office 2003
		JMenuBar menuBar = new JMenuBar();

		// Menú "Ver" con todas las opciones, incluyendo Salir
		JMenu menuVer = new JMenu("Ver");

		// Ítems de menú
		miMedicos = new JMenuItem("Medicos");
		miPacientes = new JMenuItem("Pacientes");
		//miTurnos = new JMenuItem("Turnos");
		miReportes = new JMenuItem("Reportes");
		miSalir = new JMenuItem("Salir");

		menuVer.add(miMedicos);
		menuVer.add(miPacientes);
		//menuVer.add(miTurnos);
		menuVer.add(miReportes);
		menuVer.addSeparator();
		menuVer.add(miSalir);

		menuBar.add(menuVer);
		setJMenuBar(menuBar);

		// Ajustar visibilidad de menús según rol
		if (usuario.getRol() == Rol.PACIENTE) {
			miPacientes.setVisible(false);
			miMedicos.setVisible(false);
			//miReportes.setVisible(false);
		// Medicos solo ven pacientes
		} else if (usuario.getRol() == Rol.MEDICO) 
			miMedicos.setVisible(false);
	
		// Admins ven todo
	

		// Contenedor MDI
		desktopPane = new JDesktopPane();
		add(desktopPane, BorderLayout.CENTER);

		// Listeners de menú
		miMedicos.addActionListener(e -> abrirMedicos());
		miPacientes.addActionListener(e -> abrirPacientes());
		//miTurnos.addActionListener(e -> abrirTurnos());
		miReportes.addActionListener(e -> abrirReportes());
		miSalir.addActionListener(e -> System.exit(0));

		// Tamaño inicial y mínimos
		setSize(1024, 768);
		setMinimumSize(new Dimension(1024, 768));
		setLocationRelativeTo(null);
		setResizable(true);
		

        // Iniciar servicios
        try {
            usuarioService = new UsuarioService();
            //turnoService   = new TurnoService();
        } catch (DAOException | SQLException ex) {
            throw new RuntimeException("No se pudo inicializar los servicios", ex);
        }
		
	}

	private void abrirPacientes() {
	    try {
	        JInternalFrame jf = new JInternalFrame(
	            "ABM Pacientes", 
	            true,   // resizable
	            true,   // closable
	            true,   // maximizable
	            true    // iconifiable
	        );

	        PanelTablaPacientes panel = new PanelTablaPacientes(usuarioService);
	        jf.setContentPane(panel);
	        jf.pack();

	        // Añade al desktop y muestra
	        desktopPane.add(jf);
	        jf.setVisible(true);

	    } catch (Exception ex) {
	        JOptionPane.showMessageDialog(this,
	            "Error al abrir ABM de pacientes: " + ex.getMessage(),
	            "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	private void abrirMedicos() {
	    try {
	        JInternalFrame jf = new JInternalFrame(
	            "ABM Médicos",   // Título de la ventana interna
	            true,   // resizable
	            true,   // closable
	            true,   // maximizable
	            true    // iconifiable
	        );

	        // Panel que listará los médicos y permitirá CRUD
	        PanelTablaMedicos panel = new PanelTablaMedicos(usuarioService);
	        jf.setContentPane(panel);
	        jf.pack();

	        // Añadir la ventana interna al desktop y mostrarla
	        desktopPane.add(jf);
	        jf.setVisible(true);

	    } catch (Exception ex) {
	        JOptionPane.showMessageDialog(this,
	            "Error al abrir ABM de médicos: " + ex.getMessage(),
	            "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	
	private void abrirTurnos() {
	    try {
	        JInternalFrame jf = new JInternalFrame("Gestión de Turnos", true, true, true, true);
	        PanelTurnos panel = new PanelTurnos();
	        jf.setContentPane(panel);
	        jf.pack();
	        // Ajuste de tamaño mínimo para que se vea bien
	        jf.setSize(600, 400); 
	        desktopPane.add(jf);
	        jf.setVisible(true);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

	private void abrirReportes() {
		try {
	        JInternalFrame jf = new JInternalFrame("Reportes", true, true, true, true);
	        PanelReportes panel = new PanelReportes();
	        jf.setContentPane(panel);
	        jf.setSize(500, 300);
	        desktopPane.add(jf);
	        jf.setVisible(true);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
}