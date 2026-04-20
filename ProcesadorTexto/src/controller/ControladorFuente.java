package controller;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

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
            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            StyledDocument doc = area.getStyledDocument();

            String seleccionado = (String) view.comboTipoFuente.getSelectedItem();

            modeloFuente.aplicarFamiliaFuente(doc, view.attributes, inicio, fin, seleccionado);

            cursorSinTextoSeleccionado(seleccionado);
        });
        
        // Evento cambiar tamaño texto
        view.comboTamañoFuente.addActionListener(e -> {
            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            StyledDocument doc = area.getStyledDocument();

            int tamaño = (int) view.comboTamañoFuente.getSelectedItem();

            modeloFuente.aplicarTamañoFuente(doc, view.attributes, inicio, fin, tamaño);

            cursorSinTextoSeleccionado(tamaño);
        });
        
        // Evento boton aumentar tamaño
        view.btnAumentarTamaño.addActionListener(e -> {
            try {
                controladorGUI.cerrarBloque();
                obtenerTextoSeleccionado();

                JTextPane area = controladorGUI.getAreaActiva();
                if (area == null) return;

                StyledDocument doc = area.getStyledDocument();

                int tamaño = (int) view.comboTamañoFuente
                        .getItemAt(view.comboTamañoFuente.getSelectedIndex() + 1);

                modeloFuente.aplicarTamañoFuente(doc, view.attributes, inicio, fin, tamaño);

                cursorSinTextoSeleccionado(tamaño);

                view.comboTamañoFuente.setSelectedIndex(
                        view.comboTamañoFuente.getSelectedIndex() + 1);

            } catch (Exception ex) {
                System.err.println("Limite de tamaño de texto");
            }
        });
        
        // Evento boton reducir tamaño
        view.btnReducirTamaño.addActionListener(e -> {
            try {
                controladorGUI.cerrarBloque();
                obtenerTextoSeleccionado();

                JTextPane area = controladorGUI.getAreaActiva();
                if (area == null) return;

                StyledDocument doc = area.getStyledDocument();

                int tamaño = (int) view.comboTamañoFuente
                        .getItemAt(view.comboTamañoFuente.getSelectedIndex() - 1);

                modeloFuente.aplicarTamañoFuente(doc, view.attributes, inicio, fin, tamaño);

                cursorSinTextoSeleccionado(tamaño);

                view.comboTamañoFuente.setSelectedIndex(
                        view.comboTamañoFuente.getSelectedIndex() - 1);

            } catch (Exception ex) {
                System.err.println("Limite de tamaño de texto");
            }
        });
        
        // Evento boton MayusculasMinusculas
        view.btnCambiarMayusMinusc.addActionListener(e -> {
            modeloFuente.ToggleUpperCase();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            area.setText(modeloFuente.textoAMayusMinus(area.getText()));
                        
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
            boolean activo = modeloFuente.ToggleBold();

            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            StyledDocument doc = area.getStyledDocument();

            modeloFuente.aplicarNegritas(doc, view.attributes, inicio, fin);

            cursorSinTextoSelecionado();

            view.btnNegritas.setBackground(colorBotonClicked(activo));
        });
        
        // Evento para Cursiva
        view.btnCursiva.addActionListener(e -> {
            boolean activo = modeloFuente.ToggleItalic();

            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            StyledDocument doc = area.getStyledDocument();

            modeloFuente.aplicarCursiva(doc, view.attributes, inicio, fin);

            cursorSinTextoSelecionado();

            view.btnCursiva.setBackground(colorBotonClicked(activo));
        });
        
        // Evento boton Subrayar
        view.btnSubrayar.addActionListener(e -> {
            boolean activo = modeloFuente.ToggleUnderline();

            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            StyledDocument doc = area.getStyledDocument();

            modeloFuente.aplicarSubrayar(doc, view.attributes, inicio, fin);

            cursorSinTextoSelecionado();

            view.btnSubrayar.setBackground(colorBotonClicked(activo));
        });
        
        // Evento boton Tachado
        view.btnTachado.addActionListener(e -> {
            boolean activo = modeloFuente.ToggleStrikeThrough();

            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            StyledDocument doc = area.getStyledDocument();

            modeloFuente.aplicarTachado(doc, view.attributes, inicio, fin);

            cursorSinTextoSelecionado();

            view.btnTachado.setBackground(colorBotonClicked(activo));
        });
        
        // Evento boton Subindice
        view.btnSubindice.addActionListener(e -> {
            if (modeloFuente.isSuperscript()) {
                modeloFuente.desactivarSuperscript();
                view.btnSuperindice.setBackground(btnOff);
                StyleConstants.setSuperscript(view.attributes, false);
            }

            boolean activo = modeloFuente.ToggleSubscript();

            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            StyledDocument doc = area.getStyledDocument();

            modeloFuente.aplicarSubindice(doc, view.attributes, inicio, fin);

            cursorSinTextoSelecionado();

            view.btnSubindice.setBackground(colorBotonClicked(activo));
        });
        
        // Evento boton Superindice
        view.btnSuperindice.addActionListener(e -> {
            if (modeloFuente.isSubscript()) {
                modeloFuente.desactivarSubscript();
                view.btnSubindice.setBackground(btnOff);
                StyleConstants.setSubscript(view.attributes, false);
            }

            boolean activo = modeloFuente.ToggleSuperscript();

            controladorGUI.cerrarBloque();
            obtenerTextoSeleccionado();

            JTextPane area = controladorGUI.getAreaActiva();
            if (area == null) return;

            StyledDocument doc = area.getStyledDocument();

            modeloFuente.aplicarSuperindice(doc, view.attributes, inicio, fin);

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
        
        view.negro.addActionListener(e -> aplicarColorTexto(Color.BLACK));
        view.gris.addActionListener(e -> aplicarColorTexto(Color.LIGHT_GRAY));
        view.rojo.addActionListener(e -> aplicarColorTexto(Color.RED));
        view.naranja.addActionListener(e -> aplicarColorTexto(Color.ORANGE));
        view.verde.addActionListener(e -> aplicarColorTexto(Color.GREEN));
        view.cyan.addActionListener(e -> aplicarColorTexto(Color.CYAN));
        view.azul.addActionListener(e -> aplicarColorTexto(Color.BLUE));
        view.magenta.addActionListener(e -> aplicarColorTexto(Color.MAGENTA));
        view.otroColor.addActionListener(e -> aplicarColorTexto(escogerColor()));
        view.sinColor.addActionListener(e -> aplicarColorTexto(Color.WHITE));
                
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
        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;

        this.inicio = area.getSelectionStart();
        this.fin = area.getSelectionEnd();
    }
    
    // Para todos
    private void cursorSinTextoSelecionado(){
        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;
        if(inicio == fin)
            area.setCharacterAttributes(view.attributes, false);
            
        area.requestFocusInWindow();
    }
    
    // Para tipo fuente
    private void cursorSinTextoSeleccionado(String familia){
        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;
        if(inicio == fin){
            StyleConstants.setFontFamily(view.attributes, familia);
            area.setCharacterAttributes(view.attributes, false);
        }            
            
        area.requestFocusInWindow();
    }
        
    // Para tamaño fuente
    private void cursorSinTextoSeleccionado(int tamaño){
        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;
        if(inicio == fin){
            StyleConstants.setFontSize(view.attributes, tamaño);
            area.setCharacterAttributes(view.attributes, false);
        }

        area.requestFocusInWindow();
    }

    private void aplicarColorTexto(Color color){
        controladorGUI.cerrarBloque();
        obtenerTextoSeleccionado();

        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;

        StyledDocument doc = area.getStyledDocument();

        if(modoResaltado){
            modeloFuente.aplicarFondoTexto(doc, view.attributes, inicio, fin, color);
            boolean actived = !color.equals(Color.white);
            view.btnResaltarTexto.setBackground(colorBotonClicked(actived));
        } else {
            modeloFuente.aplicarColorTexto(doc, view.attributes, inicio, fin, color);
        }

        cursorSinTextoSelecionado();
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
