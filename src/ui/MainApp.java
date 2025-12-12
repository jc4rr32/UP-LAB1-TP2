/*
 * MAINAPP.JAVA
 * Entry point del sistema.
 * Líneas comentadas, las uso una sola vez para crear un administrador inicial. 
 * 
 */
package ui;

import java.sql.SQLException;


import javax.swing.SwingUtilities;

import base.Rol;
import base.Usuario;
import service.UsuarioService;
import base.Administrador;
import base.Medico;
import dao.UsuarioDAO;
import exceptions.ConnectionException;

public class MainApp {
	public static void main(String[] args) {
		// Población inicial de datos de prueba
//		try {
//		    UsuarioService userSvc = new UsuarioService();
	//    Usuario admin = new Administrador(
//	        0,
//	        "40123456",
//	        "Admin",
//	        "Istrador",
//	        "administrador@admin.com"
//	    );
//	    userSvc.crearUsuario(admin);

//		} catch (Exception e) {
//		    e.printStackTrace();
//		    // Si hay algún error en la inicialización, lo registramos pero seguimos adelante
//		}
//		
		SwingUtilities.invokeLater(() -> { //Later para que la invoque cuando pueda. Java no es thread-safe
			Login ventana = new Login();
			ventana.setVisible(true); //las ventanas nacen invisibles, hay que hacerla visible
		});
	}
}
