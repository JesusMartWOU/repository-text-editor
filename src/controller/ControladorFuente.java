package controller;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import model.ModeloFuente;
import view.VentanaGUI;

public class ControladorFuente {
    private VentanaGUI view;
    private ControladorGUI controladorGUI;
    private ModeloFuente modeloFuente;
    
    private int inicio, fin;    
    private boolean modoResaltado;
    private Color btnOn = new Color(197, 197, 197), 
            btnOff = new Color(245, 245, 245);                
    
    public ControladorFuente(VentanaGUI view, ControladorGUI controladorGUI){
        this.view = view;
        this.controladorGUI = controladorGUI;
        this.modeloFuente = new ModeloFuente();
        
        initController();
    }    
    
    private void initController(){
        
        // Evento cambiar tipo fuente
        view.comboTipoFuente.addActionListener(e -> {       
            //------Lo cambiamos por el metodo getCursorTextPosition()-------
            //int inicio = view.areaTexto.getSelectionStart();
            //int fin = view.areaTexto.getSelectionEnd();       
            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();
            
            String seleccionado = (String) view.comboTipoFuente.getSelectedItem();            
            view.doc = view.areaTexto.getStyledDocument();
                        
            modeloFuente.aplicarFamiliaFuente(view.doc, view.attributes, inicio, fin, seleccionado);
            
            cursorSinTextoSeleccionado(seleccionado);
        });
        
        // Evento cambiar tamaño texto
        view.comboTamañoFuente.addActionListener(e -> {
            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();
            
            int tamaño = (int) view.comboTamañoFuente.getSelectedItem();            
            view.doc = view.areaTexto.getStyledDocument();
            
            modeloFuente.aplicarTamañoFuente(view.doc, view.attributes, inicio, fin, tamaño);
            cursorSinTextoSeleccionado(tamaño);            
        });
        
        // Evento boton aumentar tamaño
        view.btnAumentarTamaño.addActionListener(e -> {
            try {
                controladorGUI.cerrarBloque();
                obtenerTextoSeleccionado();
                
                int tamaño = (int) view.comboTamañoFuente.getItemAt(view.comboTamañoFuente.getSelectedIndex() + 1);                        
                //System.out.println("Tamaño fuente: " + tamaño);
                view.doc = view.areaTexto.getStyledDocument();
                
                modeloFuente.aplicarTamañoFuente(view.doc, view.attributes, inicio, fin, tamaño);
                cursorSinTextoSeleccionado(tamaño);
            
                view.comboTamañoFuente.setSelectedIndex(view.comboTamañoFuente.getSelectedIndex() + 1);
            } catch (Exception ex) {
                System.err.println("Limite de tamaño de texto");
                view.areaTexto.requestFocusInWindow();
            }            
        });
        
        // Evento boton reducir tamaño
        view.btnReducirTamaño.addActionListener(e -> {
            try {
                controladorGUI.cerrarBloque();
                obtenerTextoSeleccionado();
                
                int tamaño = (int) view.comboTamañoFuente.getItemAt(view.comboTamañoFuente.getSelectedIndex() - 1);                        
                view.doc = view.areaTexto.getStyledDocument();
                
                modeloFuente.aplicarTamañoFuente(view.doc, view.attributes, inicio, fin, tamaño);
                cursorSinTextoSeleccionado(tamaño);
            
                view.comboTamañoFuente.setSelectedIndex(view.comboTamañoFuente.getSelectedIndex() - 1);
            } catch (Exception ex) {
                System.err.println("Limite de tamaño de texto");
                view.areaTexto.requestFocusInWindow();
            }            
        });
        
        // Evento boton MayusculasMinusculas
        view.btnCambiarMayusMinusc.addActionListener(e -> {
            modeloFuente.ToggleUpperCase();
            view.areaTexto.setText(modeloFuente.textoAMayusMinus(view.areaTexto.getText()));
                        
            view.areaTexto.requestFocusInWindow();            
        });
        
        //Evento boton borrar formato texto
        view.btnBorrarFormato.addActionListener(e -> {   
            obtenerTextoSeleccionado();
            view.doc = view.areaTexto.getStyledDocument();
            
            modeloFuente.limpiarFormatoTexto(view.doc, inicio, fin);            
                        
            view.attributes = new SimpleAttributeSet();
            view.areaTexto.requestFocusInWindow();
        });
        
        // Evento boton Negritas
        view.btnNegritas.addActionListener(e -> {
            // Activar/Desactivar la funcion
            boolean activo = modeloFuente.ToggleBold();
            
            // Cerramos bloque para guardarlo en el historial undo/redo
            controladorGUI.cerrarBloque();
            
            obtenerTextoSeleccionado();
            view.doc = view.areaTexto.getStyledDocument();
            
            modeloFuente.aplicarNegritas(view.doc, view.attributes, inicio, fin);
            cursorSinTextoSelecionado();
            
            view.btnNegritas.setBackground(colorBotonClicked(activo));                 
        });
        
        // Evento para Cursiva
        view.btnCursiva.addActionListener(e -> {
            boolean activo = modeloFuente.ToggleItalic();
            
            controladorGUI.cerrarBloque();
            
            obtenerTextoSeleccionado();
            view.doc = view.areaTexto.getStyledDocument();
            
            modeloFuente.aplicarCursiva(view.doc, view.attributes, inicio, fin);
            cursorSinTextoSelecionado();
            
            view.btnCursiva.setBackground(colorBotonClicked(activo));                        
        });
        
        // Evento boton Subrayar
        view.btnSubrayar.addActionListener(e -> {
            boolean activo = modeloFuente.ToggleUnderline();
            
            controladorGUI.cerrarBloque();
            
            obtenerTextoSeleccionado();
            view.doc = view.areaTexto.getStyledDocument();
            
            modeloFuente.aplicarSubrayar(view.doc, view.attributes, inicio, fin);
            cursorSinTextoSelecionado();
            
            view.btnSubrayar.setBackground(colorBotonClicked(activo));            
        });
        
        // Evento boton Tachado
        view.btnTachado.addActionListener(e -> {
            boolean activo = modeloFuente.ToggleStrikeThrough();
            
            controladorGUI.cerrarBloque();
            
            obtenerTextoSeleccionado();
            view.doc = view.areaTexto.getStyledDocument();
            
            modeloFuente.aplicarTachado(view.doc, view.attributes, inicio, fin);
            cursorSinTextoSelecionado();
            
            view.btnTachado.setBackground(colorBotonClicked(activo));                        
        });
        
        // Evento boton Subindice
        view.btnSubindice.addActionListener(e -> {                        
            if(modeloFuente.isSuperscript()) {
                modeloFuente.desactivarSuperscript();   
                view.btnSuperindice.setBackground(btnOff);
                
                StyleConstants.setSuperscript(view.attributes, false);
                cursorSinTextoSelecionado();
            }                        
            
            boolean activo = modeloFuente.ToggleSubscript();            
            
            controladorGUI.cerrarBloque();
            
            obtenerTextoSeleccionado();
            view.doc = view.areaTexto.getStyledDocument();  
            
            modeloFuente.aplicarSubindice(view.doc, view.attributes, inicio, fin);            
            cursorSinTextoSelecionado();
            
            view.btnSubindice.setBackground(colorBotonClicked(activo));
        });
        
        // Evento boton Superindice
        view.btnSuperindice.addActionListener(e -> {
            if(modeloFuente.isSubscript()) {
                modeloFuente.desactivarSubscript();
                view.btnSubindice.setBackground(btnOff);
                                
                StyleConstants.setSubscript(view.attributes, false);
                cursorSinTextoSelecionado();
            }
            
            boolean activo = modeloFuente.ToggleSuperscript();
            
            controladorGUI.cerrarBloque();
            
            obtenerTextoSeleccionado();
            view.doc = view.areaTexto.getStyledDocument();
            
            modeloFuente.aplicarSuperindice(view.doc, view.attributes, inicio, fin);
            cursorSinTextoSelecionado();
            
            view.btnSuperindice.setBackground(colorBotonClicked(activo));
        });
        
        // Evento boton ResaltarTexto
        view.btnResaltarTexto.addActionListener(e -> {
            modoResaltado = true;
            view.sinColor.setVisible(true);
            view.menuColores.show(view.btnResaltarTexto, 0, view.btnResaltarTexto.getHeight());            
        });       
        
        // Evento boton Color Texto
        view.btnColorFuente.addActionListener(e -> {
            modoResaltado = false;
            view.sinColor.setVisible(false);
            view.menuColores.show(view.btnColorFuente, 0, view.btnColorFuente.getHeight());
        });
        
        view.negro.addActionListener(e -> aplicarColorTexto(Color.black));
        view.gris.addActionListener(e -> aplicarColorTexto(Color.lightGray));
        view.rojo.addActionListener(e -> aplicarColorTexto(Color.red));
        view.naranja.addActionListener(e -> aplicarColorTexto(Color.orange));
        view.verde.addActionListener(e -> aplicarColorTexto(Color.green));
        view.cyan.addActionListener(e -> aplicarColorTexto(Color.cyan));
        view.azul.addActionListener(e -> aplicarColorTexto(Color.blue));
        view.magenta.addActionListener(e -> aplicarColorTexto(Color.magenta));
        view.otroColor.addActionListener(e -> aplicarColorTexto(escogerColor()));
        view.sinColor.addActionListener(e -> aplicarColorTexto(Color.white));
                
        // Boton añadir imagen
        view.btnTipografia.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de Imagen", "jpg", "png", "jpeg");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(view);

            if (result != JFileChooser.APPROVE_OPTION) return;

            controladorGUI.cerrarBloque();

            File selectedFile = fileChooser.getSelectedFile();
            String ruta = selectedFile.getAbsolutePath();

            JLabel imgLabel = new JLabel(modeloFuente.resizeImage(ruta, 240, 120));
            imgLabel.setBounds(50, 50, 240, 120);
            imgLabel.setOpaque(false);

            agregarEventosImagen(imgLabel, ruta);

            view.panelEditor.add(imgLabel, JLayeredPane.PALETTE_LAYER);
            view.panelEditor.repaint();
        });

    }
    
    //------------Metodos para el controlador de eventos-------------------
    private void obtenerTextoSeleccionado(){
        this.inicio = view.areaTexto.getSelectionStart();
        this.fin = view.areaTexto.getSelectionEnd();
    }
    
    // Para todos
    private void cursorSinTextoSelecionado(){
        if(inicio == fin)
            view.areaTexto.setCharacterAttributes(view.attributes, false);
            
        view.areaTexto.requestFocusInWindow();
    }
    
    // Para tipo fuente
    private void cursorSinTextoSeleccionado(String familia){
        if(inicio == fin){
            StyleConstants.setFontFamily(view.attributes, familia);
            view.areaTexto.setCharacterAttributes(view.attributes, false);            
        }            
            
        view.areaTexto.requestFocusInWindow();
    }
        
    // Para tamaño fuente
    private void cursorSinTextoSeleccionado(int tamaño){
        if(inicio == fin){
            StyleConstants.setFontSize(view.attributes, tamaño);
            view.areaTexto.setCharacterAttributes(view.attributes, false);            
        }            
            
        view.areaTexto.requestFocusInWindow();
    }
                    
    private void aplicarColorTexto(Color color){
        controladorGUI.cerrarBloque();
        
        obtenerTextoSeleccionado();
        
        view.doc = view.areaTexto.getStyledDocument();         
        
        if(modoResaltado){
            modeloFuente.aplicarFondoTexto(view.doc, view.attributes, inicio, fin, color);
            boolean actived = !color.equals(Color.white);
            view.btnResaltarTexto.setBackground(colorBotonClicked(actived));
        } else {
            modeloFuente.aplicarColorTexto(view.doc, view.attributes, inicio, fin, color);
        }
        cursorSinTextoSelecionado();
        view.areaTexto.requestFocusInWindow();
    }                
    
    private Color escogerColor(){
        Color c = JColorChooser.showDialog(null, "Selecciona un color", modeloFuente.currentColor);
        if (c != null) {
            return c;
        }else{
            return modeloFuente.currentColor;
        }
    }
    
    private Color colorBotonClicked(boolean btnActived){
        Color c = (btnActived)? btnOn : btnOff;
        return c;
    }
    
    private void agregarEventosImagen(JLabel imgLabel, String ruta) {

        final Point[] inicio = {null};

        imgLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                inicio[0] = e.getPoint();
                imgLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                imgLabel.setBorder(null);
            }
        });

        imgLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                if (inicio[0] == null) return;

                if (e.isShiftDown()) {
                    int newWidth = Math.max(50, e.getX());
                    int newHeight = Math.max(50, e.getY());

                    imgLabel.setSize(newWidth, newHeight);
                    imgLabel.setIcon(modeloFuente.resizeImage(ruta, newWidth, newHeight));

                } else {
                    int x = imgLabel.getX() + e.getX() - inicio[0].x;
                    int y = imgLabel.getY() + e.getY() - inicio[0].y;
                    imgLabel.setLocation(x, y);
                }
            }
        });
    }        
    
}
