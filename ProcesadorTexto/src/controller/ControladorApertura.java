package controller;

import java.io.File;
import java.io.FileInputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import view.VentanaApertura;
import view.VentanaGUI;

public class ControladorApertura {
    private VentanaApertura aperturaView;
    
    public ControladorApertura(VentanaApertura aperturaView){
        this.aperturaView = aperturaView;
        
        initController();
    }
    
    private void initController(){
        //---------Eventos de los botones---------------
        // Evento boton Nuevo
        aperturaView.btnNuevo.addActionListener(e -> {
            VentanaGUI view = new VentanaGUI();
            new ControladorGUI(view);
            view.setVisible(true);         
            view.areaTexto.requestFocusInWindow();
            aperturaView.dispose();            
        });
        
        // Evento boton Abrir
        aperturaView.btnAbrir.addActionListener(e -> {
            File archivo = escogerRutaAbrir();
            if(archivo != null){
                abrirArchivo(archivo);
            }else {
                JOptionPane.showMessageDialog(aperturaView, "Error al abrir el archivo", "Abrir archivo", JOptionPane.ERROR_MESSAGE);
            }           
        });
        
        // Evento boton Cerrar 
        aperturaView.btnCerrar.addActionListener(e -> aperturaView.dispose());
        
    }
    
    //------------Metodos para initController()-----------------
    // Elegir ubicacion para abrir un archivo
    private File escogerRutaAbrir(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir archivo");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo Word(.rtf)", "rtf");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(aperturaView);
        
        if(result == JFileChooser.APPROVE_OPTION){
            File archivo = fileChooser.getSelectedFile();            
            return archivo;
        }
        return null;
    }
    
    // Abrir archivo    
    private void abrirArchivo(File archivo){
        try {
            VentanaGUI view = new VentanaGUI(); 
            ControladorGUI controladorGUI = new ControladorGUI(view);
            
            FileInputStream in = new FileInputStream(archivo);

            RTFEditorKit rtf = new RTFEditorKit();
            Document doc = rtf.createDefaultDocument();

            rtf.read(in, doc, 0);
            view.areaTexto.setDocument(doc);

            in.close();
            
            view.setVisible(true);
            controladorGUI.currentFile = archivo;
            view.areaTexto.requestFocusInWindow();
            aperturaView.dispose();

        } catch (Exception ex) {
            System.err.println("Error al abrir archivo");
        }
   }
    
}
