package controller;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import model.ModeloParrafo;
import view.VentanaGUI;

public class ControladorParrafo {
    private VentanaGUI view;
    private ControladorGUI controladorGUI;
    private ModeloParrafo modeloParrafo;
    
    private int inicio, fin;
    private Color btnOn = new Color(197, 197, 197), 
            btnOff = new Color(245, 245, 245);
    
    public ControladorParrafo(VentanaGUI view, ControladorGUI controladorGUI){
        this.view = view;
        this.controladorGUI = controladorGUI;
        this.modeloParrafo = new ModeloParrafo();
        
        initController();
    }
    
    private void initController(){
        // Boton Viñeta
        view.btnViñeta.addActionListener(e -> {
            try {
                boolean activo = modeloParrafo.toggleVignette();

                JTextPane area = controladorGUI.getAreaActiva();
                if (area == null) return;

                StyledDocument doc = area.getStyledDocument();

                int pos = area.getCaretPosition();
                String texto = area.getText();

                modeloParrafo.aplicarViñeta(doc, view.attributes, pos, texto);

                view.btnViñeta.setBackground(colorBotonClicked(activo));
                area.requestFocusInWindow();

            } catch (Exception ex) {
                System.err.println("Error al insertar viñeta");
            }
        });
        
        // Boton Numeracion
        view.btnNumeracion.addActionListener(e -> {
            try {
                boolean activo = modeloParrafo.toggleNumeration();

                if(activo) modeloParrafo.resetContadorNumeracion();

                JTextPane area = controladorGUI.getAreaActiva();
                if (area == null) return;

                StyledDocument doc = area.getStyledDocument();

                int pos = area.getCaretPosition();
                String texto = area.getText();

                modeloParrafo.aplicarNumeracion(doc, pos, texto);

                view.btnNumeracion.setBackground(colorBotonClicked(activo));
                area.requestFocusInWindow();

            } catch (Exception ex) {
                System.err.println("Error al insertar numeracion");
            }
        });
        
        // Boton Reducir Sangria
        view.btnDisminuirSangria.addActionListener(e -> {
            obtenerTextoSeleccionado();    
            
            aplicarSangria(-20);
        });
        
        // Boton Aumentar Sangria
        view.btnAumentarSangria.addActionListener(e -> {
            obtenerTextoSeleccionado();
            
            aplicarSangria(20);
        });
        
        // Boton Interlineado 
        view.btnInterlineado.addActionListener(e -> {
            view.menuInterlineado.show(view.btnInterlineado, 0, view.btnInterlineado.getHeight());
        });
        
        view.interlineado1_0.addActionListener(e -> aplicarInterlineado(1.0f));
        view.interlineado1_15.addActionListener(e -> aplicarInterlineado(1.15f));
        view.interlineado1_5.addActionListener(e -> aplicarInterlineado(1.5f));
        view.interlineado2_0.addActionListener(e -> aplicarInterlineado(2.0f));
        view.interlineado2_5.addActionListener(e -> aplicarInterlineado(2.5f));
        view.interlineado3_0.addActionListener(e -> aplicarInterlineado(3.0f));
        view.sinInterlineado.addActionListener(e -> aplicarInterlineado(0.0f));
        
        // Boton Alinear Izq.
        view.btnAlinearIzq.addActionListener(e -> {            
            aplicarAlineacionTexto(StyleConstants.ALIGN_LEFT);   
            colorBotonesAlineacion(0);
        });
        
        // Boton Alinear Cen.
        view.btnAlinearCen.addActionListener(e -> {
            aplicarAlineacionTexto(StyleConstants.ALIGN_CENTER);
            colorBotonesAlineacion(1);
        });
        
        // Boton Alinear Der.
        view.btnAlinearDer.addActionListener(e -> {
            aplicarAlineacionTexto(StyleConstants.ALIGN_RIGHT);       
            colorBotonesAlineacion(2);
        });
        
        // Boton Justificar
        view.btnJustificar.addActionListener(e -> {
            aplicarAlineacionTexto(StyleConstants.ALIGN_JUSTIFIED);
            colorBotonesAlineacion(3);
        });
        
        //-----------Eventos complementarios------------------
        for (JTextPane area : view.areasTexto) {
            area.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {

                    JTextPane areaActiva = controladorGUI.getAreaActiva();
                    if (areaActiva == null) return;

                    StyledDocument doc = areaActiva.getStyledDocument();
                    if (doc == null) return;

                    if (e.getKeyCode() == KeyEvent.VK_ENTER && modeloParrafo.isVignette()) {
                        try {
                            e.consume();
                            int pos = areaActiva.getCaretPosition();
                            doc.insertString(pos, "\n• ", null);
                            areaActiva.setCaretPosition(pos + 3);
                        } catch (Exception ex) {
                            System.err.println("Error viñeta ENTER");
                        }
                    }

                    if (e.getKeyCode() == KeyEvent.VK_ENTER && modeloParrafo.isNumeration()) {
                        try {
                            e.consume();
                            int pos = areaActiva.getCaretPosition();
                            int n = modeloParrafo.incrementarContadorNumeracion();
                            String linea = "\n" + n + ". ";
                            doc.insertString(pos, linea, null);
                            areaActiva.setCaretPosition(pos + linea.length());
                        } catch (Exception ex) {
                            System.err.println("Error numeración ENTER");
                        }
                    }
                }
            });
        }
                
    }    
    
    //------------Metodos para el controlador de eventos-------------------
    private void obtenerTextoSeleccionado(){
        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;

        this.inicio = area.getSelectionStart();
        this.fin = area.getSelectionEnd();
    }

    private void aplicarAtributosDeParrafo(SimpleAttributeSet attr){
        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;

        StyledDocument doc = area.getStyledDocument();
        if (doc == null) return;

        if(inicio == fin)
            area.setParagraphAttributes(attr, false);
        else
            doc.setParagraphAttributes(inicio, fin - inicio, attr, false);

        area.requestFocusInWindow();
    }

    private void aplicarSangria(int valor){
        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;

        StyledDocument doc = area.getStyledDocument();

        obtenerTextoSeleccionado();

        SimpleAttributeSet attr = modeloParrafo.sangriaActualizada(doc, inicio, fin, valor);

        aplicarAtributosDeParrafo(attr);
    }
    
    private void aplicarInterlineado(float interlineado){
        obtenerTextoSeleccionado();
        //StyledDocument doc = view.areaTexto.getStyledDocument();
        
        SimpleAttributeSet attr = modeloParrafo.interlineadoActualizada(interlineado);
        
        aplicarAtributosDeParrafo(attr);
    }

    private void aplicarAlineacionTexto(int align){
        JTextPane area = controladorGUI.getAreaActiva();
        if (area == null) return;

        StyledDocument doc = area.getStyledDocument();

        obtenerTextoSeleccionado();

        SimpleAttributeSet attr = modeloParrafo.alineacionTextoActualizada(doc, inicio, fin, align);

        aplicarAtributosDeParrafo(attr);
    }
    
    private Color colorBotonClicked(boolean btnActived){
        Color c = (btnActived)? btnOn : btnOff;
        return c;
    }
    
    private void colorBotonesAlineacion(int numBotonActivo){
            boolean[] botonesActivados = new boolean[4];
            for (int i = 0; i < 4; i++) {            
                if(i == numBotonActivo) botonesActivados[i] = true;
                else botonesActivados[i] = false;
            }
            view.btnAlinearIzq.setBackground(colorBotonClicked(botonesActivados[0]));
            view.btnAlinearCen.setBackground(colorBotonClicked(botonesActivados[1]));
            view.btnAlinearDer.setBackground(colorBotonClicked(botonesActivados[2]));
            view.btnJustificar.setBackground(colorBotonClicked(botonesActivados[3]));        
    }
        
}
