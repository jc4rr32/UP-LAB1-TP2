package ui;

//import java.sql.SQLException;

//import java.sql.SQLException;

import javax.swing.SwingUtilities;

//import base.Rol;
//import base.Usuario;
//import service.UsuarioService;

//import base.Medico;
//import dao.UsuarioDAO;
//import exceptions.ConnectionException;

public class MainApp {
	public static void main(String[] args) {
		// Población inicial de datos de prueba
//		try {
//		    UsuarioService userSvc = new UsuarioService();
//
//		    Usuario admin = new Usuario(
//		        0,
//		        "11223344",
//		        "Federico",
//		        "Wegener",
//		        "fwegener@abstergo.com",
//		        Rol.ADMIN
//		    );
//		    userSvc.crearUsuario(admin);
//
//		} catch (SQLException e) {
//		    e.printStackTrace();
//		    // Si hay algún error en la inicialización, lo registramos pero seguimos adelante
//		}
//		
		SwingUtilities.invokeLater(() -> {
			Login ventana = new Login();
			ventana.setVisible(true);
		});
	}
}
