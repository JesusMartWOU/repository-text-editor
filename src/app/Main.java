package app;

import controller.ControladorApertura;
import javax.swing.SwingUtilities;
import view.VentanaApertura;

public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            VentanaApertura view = new VentanaApertura();      
            new ControladorApertura(view);
            
            view.setVisible(true);
            
        });
    }    
}
