 package ui;

import javax.swing.*;
import base.Usuario;
import exceptions.DAOException;
import exceptions.DatosInvalidosException;
import service.UsuarioService;
import java.awt.*;

/**
 * Ventana principal con pantalla de login. TODO: Contraseñas.
 */
public class Login extends JFrame {
	private JTextField tfDni;
	private JButton btnLogin;
	private UsuarioService usuarioService;

	public Login() {
		setTitle("Turnero Medico UP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());


		// Inicializar servicio de usuarios
		try {
			usuarioService = new UsuarioService();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error al inicializar servicio de usuarios: " + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		// Panel central para login
		JPanel panelLogin = new JPanel(new GridBagLayout());
		panelLogin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);

		// Título de sección
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		JLabel lblSection = new JLabel("Turnero Médico - Inicie Sesión", SwingConstants.CENTER);
		lblSection.setFont(lblSection.getFont().deriveFont(Font.BOLD, 14f));
		panelLogin.add(lblSection, gbc);

		// Reset grid
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.WEST;

		// DNI
		gbc.gridy = 1;
		gbc.gridx = 0;
		panelLogin.add(new JLabel("DNI:"), gbc);
		gbc.gridx = 1;
		tfDni = new JTextField(12);
		panelLogin.add(tfDni, gbc);

		// Botón Login
		gbc.gridy = 3;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		btnLogin = new JButton("Login");
		panelLogin.add(btnLogin, gbc);

		add(panelLogin, BorderLayout.CENTER);

		// Ajustes finales
		pack();
		setResizable(false);
		setLocationRelativeTo(null);

		// listener para login
		btnLogin.addActionListener(e -> handleLogin());

	}

	private void handleLogin() { //
		String dni = tfDni.getText().trim();
		try {
			Usuario u = usuarioService.buscarPorDni(dni);
			if (u == null) {
				JOptionPane.showMessageDialog(this, "Usuario no registrado.", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				dispose();
				PantallaPrincipal pp = new PantallaPrincipal(u);
				pp.setVisible(true);
			}
		} catch (DAOException | DatosInvalidosException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
