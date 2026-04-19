package controller;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
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
                
                int pos = view.areaTexto.getCaretPosition();
                String texto = view.areaTexto.getText();
                view.doc = view.areaTexto.getStyledDocument();
                
                modeloParrafo.aplicarViñeta(view.doc, view.attributes, pos, texto);
                
                view.btnViñeta.setBackground(colorBotonClicked(activo));
                view.areaTexto.requestFocusInWindow();                
            } catch (Exception ex) {
                 System.err.println("Error al insertar viñeta");
            }
        });
        
        // Boton Numeracion
        view.btnNumeracion.addActionListener(e -> {            
            try {
                boolean activo = modeloParrafo.toggleNumeration();
                
                if(activo) modeloParrafo.resetContadorNumeracion();
                
                int pos = view.areaTexto.getCaretPosition();
                String texto = view.areaTexto.getText();
                view.doc = view.areaTexto.getStyledDocument();
                
                modeloParrafo.aplicarNumeracion(view.doc, pos, texto);
                
                view.btnNumeracion.setBackground(colorBotonClicked(activo));
                view.areaTexto.requestFocusInWindow();
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
        view.areaTexto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && modeloParrafo.isVignette()) {
                    try {
                        e.consume();
                        int pos = view.areaTexto.getCaretPosition();
                        view.doc.insertString(pos, "\n• ", null);
                        view.areaTexto.setCaretPosition(pos + 3);
                    } catch (Exception ex) {                        
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && modeloParrafo.isNumeration()) {
                    try {
                        e.consume();
                        int pos = view.areaTexto.getCaretPosition();
                        int n = modeloParrafo.incrementarContadorNumeracion();
                        String linea = "\n" + n + ". ";
                        view.doc.insertString(pos, linea, null);
                        view.areaTexto.setCaretPosition(pos + linea.length());
                    } catch (Exception ex) {                        
                    }
                }
            }
        });
                
    }    
    
    //------------Metodos para el controlador de eventos-------------------
    private void obtenerTextoSeleccionado(){
        this.inicio = view.areaTexto.getSelectionStart();
        this.fin = view.areaTexto.getSelectionEnd();
    }        
    
    private void aplicarAtributosDeParrafo(SimpleAttributeSet attr){
        if(inicio == fin)
            view.areaTexto.setParagraphAttributes(attr, false);
        else
            view.doc.setParagraphAttributes(inicio, fin - inicio, attr, false);

        view.areaTexto.requestFocusInWindow();
    }
    
    private void aplicarSangria(int valor){
        obtenerTextoSeleccionado();
        StyledDocument doc = view.areaTexto.getStyledDocument();
        
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
        obtenerTextoSeleccionado();
        StyledDocument doc = view.areaTexto.getStyledDocument();
        
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
