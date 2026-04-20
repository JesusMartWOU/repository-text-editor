package model;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ModeloParrafo {
    
    private int contadorNumeracion = 0;    
    private boolean toVignette = false, toNumeration = false;
    
    public boolean toggleVignette() { return toVignette = !toVignette; }
    public boolean toggleNumeration() { return toNumeration = !toNumeration; }
    
    public boolean isVignette() { return toVignette; }
    public boolean isNumeration() { return toNumeration; }

    public int getContadorNumeracion() { return contadorNumeracion; }
    public int incrementarContadorNumeracion() { return ++contadorNumeracion; }
    public void resetContadorNumeracion() { contadorNumeracion = 0; }
    
    public void aplicarViñeta(StyledDocument doc, SimpleAttributeSet attr, 
                                int pos, String texto){       
        try {
            if(toVignette){            
                if (pos != 0 && !texto.substring(pos - 1, pos).equals("\n")) {                    
                    doc.insertString(pos, "\n• ", null);
                } else {                    
                    doc.insertString(pos, "• ", null);
                }                   
            }
        } catch (Exception ex) {
            System.err.println("Error al insertar viñeta");
        }                
    }
    
    public void aplicarNumeracion(StyledDocument doc, int pos, String texto){        
        try {
            contadorNumeracion = 0;
            if(toNumeration){                                        
                if (pos != 0 && !texto.substring(pos - 1, pos).equals("\n")) {
                    doc.insertString(pos, "\n" + incrementarContadorNumeracion() + ". ", null);
                } else {
                    doc.insertString(pos, incrementarContadorNumeracion() + ". ", null);
                }                                    
            }   
        } catch (Exception e) {
            System.err.println("Error al insertar numeracion");
        }                                    
    }        
    
    public SimpleAttributeSet sangriaActualizada(StyledDocument doc, int inicio, int fin, int sangria){
        Element paragraph = doc.getParagraphElement(inicio);
        AttributeSet attrActual = paragraph.getAttributes();
            
        float sangriaActual = StyleConstants.getLeftIndent(attrActual);
        //float nuevaSangria = Math.max(0, sangriaActual + valor);
        float nuevaSangria = Math.max(0, Math.min(400, sangriaActual + sangria)); // Modificar 200 si no alcanza final de la hoja
            
        SimpleAttributeSet attr = new SimpleAttributeSet(attrActual);
        StyleConstants.setLeftIndent(attr, nuevaSangria);
            
        return attr;
    }
    
    public SimpleAttributeSet interlineadoActualizada(float interlineado){        
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(attrs, interlineado);
        
        return attrs;
    }    
    
    public SimpleAttributeSet alineacionTextoActualizada(StyledDocument doc, int inicio, int fin, int align){
        Element paragraph = doc.getParagraphElement(inicio);
        AttributeSet attrActual = paragraph.getAttributes();
        SimpleAttributeSet attr = new SimpleAttributeSet(attrActual);
        
        StyleConstants.setAlignment(attr, align);
        
        return attr;
    }
    
}
