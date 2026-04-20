package model;

import java.awt.Color;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ModeloFuente {
    
    private boolean toUpperCase = false, toBold = false, toItalic = false, 
            toUnderline = false, toStrikeThrough = false, toSubscript = false,
            toSuperscript = false;
    public Color currentColor = Color.BLACK;        

    // Metodos para hacer toggle los botones (esto para no cambiar el tipo de botones)
    public boolean ToggleUpperCase() { return toUpperCase = !toUpperCase; }
    public boolean ToggleBold() { return toBold = !toBold; }
    public boolean ToggleItalic() { return toItalic = !toItalic; }
    public boolean ToggleUnderline() { return toUnderline = !toUnderline; }
    public boolean ToggleStrikeThrough() { return toStrikeThrough = !toStrikeThrough; }
    public boolean ToggleSubscript() { return toSubscript = !toSubscript; }
    public boolean ToggleSuperscript() { return toSuperscript = !toSuperscript; }
    
    
    public boolean isSubscript()   { return toSubscript;   }
    public boolean isSuperscript() { return toSuperscript; }

    public void desactivarSubscript()   { toSubscript   = false; }
    public void desactivarSuperscript() { toSuperscript = false; }
    
    // Metodos para controladorFuente
    public void aplicarFamiliaFuente(StyledDocument doc, SimpleAttributeSet attr, 
                                        int inicio, int fin, String familia){
        StyleConstants.setFontFamily(attr, familia);
        aplicarAtributos(doc, attr, inicio, fin);
    }
    
    public void aplicarTamañoFuente(StyledDocument doc, SimpleAttributeSet attr, 
                                         int inicio, int fin, int tamaño){
        StyleConstants.setFontSize(attr, tamaño);
        aplicarAtributos(doc, attr, inicio, fin);
    }      

    public String textoAMayusMinus(String texto){         
         return toUpperCase? texto.toUpperCase() : texto.toLowerCase();
     }

    public void limpiarFormatoTexto(StyledDocument doc, int inicio, int fin){
        SimpleAttributeSet limpio = new SimpleAttributeSet();
        StyleConstants.setFontFamily(limpio, "Arial");
        StyleConstants.setFontSize(limpio, 12);

        if (inicio == fin)
             doc.setCharacterAttributes(0, doc.getLength(), limpio, true);
         else
             doc.setCharacterAttributes(inicio, fin - inicio, limpio, true);
    }

    public void aplicarNegritas(StyledDocument doc, SimpleAttributeSet attr, int inicio, int fin){
        StyleConstants.setBold(attr, toBold);
        aplicarAtributos(doc, attr, inicio, fin);
    }

    public void aplicarCursiva(StyledDocument doc, SimpleAttributeSet attr, int inicio, int fin){
        StyleConstants.setItalic(attr, toItalic);
        aplicarAtributos(doc, attr, inicio, fin);
    }

    public void aplicarSubrayar(StyledDocument doc, SimpleAttributeSet attr, int inicio, int fin){
        StyleConstants.setUnderline(attr, toUnderline);
        aplicarAtributos(doc, attr, inicio, fin);
    }

    public void aplicarTachado(StyledDocument doc, SimpleAttributeSet attr, int inicio, int fin){
        StyleConstants.setStrikeThrough(attr, toStrikeThrough);
        aplicarAtributos(doc, attr, inicio, fin);
    }

    public void aplicarSubindice(StyledDocument doc, SimpleAttributeSet attr, int inicio, int fin){
        StyleConstants.setSubscript(attr, toSubscript);
        aplicarAtributos(doc, attr, inicio, fin);
    }

    public void aplicarSuperindice(StyledDocument doc, SimpleAttributeSet attr, int inicio, int fin){
        StyleConstants.setSuperscript(attr, toSuperscript);
        aplicarAtributos(doc, attr, inicio, fin);
    }   

    public void aplicarColorTexto(StyledDocument doc, SimpleAttributeSet attr, int inicio, int fin, Color color){
        currentColor = color;
        StyleConstants.setForeground(attr, color);
        aplicarAtributos(doc, attr, inicio, fin);
    }
   
    public void aplicarFondoTexto(StyledDocument doc, SimpleAttributeSet attr, int inicio, int fin, Color color){
        currentColor = color;
        StyleConstants.setBackground(attr, color);
        aplicarAtributos(doc, attr, inicio, fin);
    }
       
    //------------Metodos auxiliares para esta clase--------------
    private void aplicarAtributos(StyledDocument doc, SimpleAttributeSet attr, 
                                    int inicio, int fin){
        if(inicio == fin)
            //Esto va en el controlador
            //view.areaTexto.setCharacterAttributes(view.attributes, false);            
            return;
        doc.setCharacterAttributes(inicio, fin - inicio, attr, false);                        
    }
            
    public ImageIcon resizeImage(String resource, int width, int height){
        ImageIcon icono = new ImageIcon(resource);
        Image imagenOriginal = icono.getImage();
        Image nuevaImagen = imagenOriginal.getScaledInstance(width, height, Image.SCALE_SMOOTH);        
        icono = new ImageIcon(nuevaImagen);        
        return icono;
    }       
    
    
}
