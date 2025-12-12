/*
 * MAINAPP.JAVA
 * Entry point del sistema.
 * 
 * 
 */
package ui;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Ya NO inicializamos la DB aca. 
                // Asumo que la tabla existe.
                
                Login loginFrame = new Login();
                loginFrame.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error al iniciar la aplicación:\n" + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}