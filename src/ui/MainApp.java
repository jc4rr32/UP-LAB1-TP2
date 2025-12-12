/*
 * MAINAPP.JAVA
 * Entry point del sistema.
 * Lo unico que hace es iniciar el programa de forma segura
 * 
 */
package ui;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //uso invokeLater porque las interfaces no son seguras para hilos, si no lo uso se puede crashear la jvm.
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