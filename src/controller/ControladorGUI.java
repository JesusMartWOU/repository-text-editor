package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import static org.apache.poi.xwpf.usermodel.ParagraphAlignment.BOTH;
import static org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER;
import static org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import view.VentanaApertura;
import view.VentanaGUI;

public class ControladorGUI {
    private VentanaGUI view;    
    public File currentFile = null;
    public UndoManager undoManager = new UndoManager();
    public CompoundEdit compoundEdit = null;
    private int inicio, fin;
    
    public ControladorGUI(VentanaGUI view){
        this.view = view;        
        initController();
        
        // Inicializamos los otros controladores
        new ControladorFuente(view, this); 
        new ControladorParrafo(view, this);
    }
    
    private void initController(){                                        
        //---------Eventos al presionar un boton del menu----------        
        // Boton Nuevo
        view.nuevo.addActionListener(e -> {
            VentanaGUI nuevaVentana = new VentanaGUI();
            new ControladorGUI(nuevaVentana);
            nuevaVentana.setVisible(true);            
        });
        
        // Boton Abrir
        view.abrir.addActionListener(e -> {
            File archivo = escogerRutaAbrir();            
            if(archivo != null){
                /*------Este bloque remplaza el doc actual por el abierto----
                currentFile = archivo; //actualizar el archivo actual
                abrirArchivo(archivo);
                */
                VentanaGUI makeNewView = new VentanaGUI();
                ControladorGUI makeNewController = new ControladorGUI(makeNewView);
                
                makeNewView.setTitle(archivo.getName());
                makeNewView.setVisible(true);
                makeNewController.currentFile = archivo;
                abrirArchivo(archivo, makeNewView);
            }else{
                JOptionPane.showMessageDialog(view, "Error al abrir el archivo", "Abrir archivo", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Boton Guardar (temporal en formato .rtf) 
        view.guardar.addActionListener(e -> hacerGuardadoDeArchivo());                
        
        // Boton GuardarComo
        view.guardarComo.addActionListener(e -> archivoGuardarComo());
        
        // Boton Imprimir
        view.imprimir.addActionListener(e -> imprimirTexto());
        
        // Boton cerrar
        view.cerrar.addActionListener(e -> {
            VentanaApertura aperturaView = new VentanaApertura();
            new ControladorApertura(aperturaView);
            aperturaView.setVisible(true);
            view.dispose();
        });
        
        //---------Eventos al presionar un boton del FrameMenu----------
        // Boton guardado (sobreescribir)
        view.guardado.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                hacerGuardadoDeArchivo();
            }        
        });
        //---------Eventos deshacer y rehacer--------------------
        view.areaTexto.getDocument().addUndoableEditListener(e -> {
            if (compoundEdit == null) {
                compoundEdit = new CompoundEdit();
            }            
            compoundEdit.addEdit(e.getEdit());            
            
            timer.restart();
        });
                
        // Boton deshacer
        view.deshacer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    if (undoManager.canUndo()) undoManager.undo();
                    habilitarDeshacerRehacer();
                } catch (Exception ex) {
                    System.err.println("Error en undo");
                }                    
            }        
        });
        
        // Boton rehacer
        view.rehacer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    if (undoManager.canRedo()) undoManager.redo();
                    habilitarDeshacerRehacer();
                } catch (Exception ex) {
                    System.err.println("Error en redo");
                }                             
            }        
        });
        
        //------------------Eventos complementarios------------------
        // Evento para contar numero de palabras
        view.areaTexto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {                
                actualizarContadorPalabras();      
                actualizarContadorCaracteres();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizarContadorPalabras();
                actualizarContadorCaracteres();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizarContadorPalabras();
                actualizarContadorCaracteres();
            }
        });               

        
        //----------------Eventos de atajos del teclado----------------
        // Atajo Ctrl + Z
        view.areaTexto.getInputMap().put(
            KeyStroke.getKeyStroke("control Z"), "undo"
        );
        view.areaTexto.getActionMap().put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canUndo()) undoManager.undo();
                    habilitarDeshacerRehacer();
                } catch (Exception ex) {
                    System.err.println("Error en undo");
                }
            }
        });
        
        // Atajo Ctrl + Y
        view.areaTexto.getInputMap().put(
            KeyStroke.getKeyStroke("control Y"), "redo"
        );
        view.areaTexto.getActionMap().put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canRedo()) undoManager.redo();
                    habilitarDeshacerRehacer();
                } catch (Exception ex) {
                    System.err.println("Error en redo");
                }
            }
        });
        
        //Atajo Ctrl + C
        view.areaTexto.getInputMap().put(
            KeyStroke.getKeyStroke("control C"), DefaultEditorKit.copyAction
        );
        
        //Atajo Ctrl + X
        view.areaTexto.getInputMap().put(
            KeyStroke.getKeyStroke("control X"), DefaultEditorKit.cutAction
        );
        
        //Atajo Ctrl + V
        view.areaTexto.getInputMap().put(
            KeyStroke.getKeyStroke("control V"), DefaultEditorKit.pasteAction
        );
        
        //Atajo Ctrl + S
        view.areaTexto.getInputMap().put(
                KeyStroke.getKeyStroke("control S"), "save"
        );
        view.areaTexto.getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hacerGuardadoDeArchivo();
            }            
        });
        
        //Atajo Ctrl + P
        view.areaTexto.getInputMap().put(
            KeyStroke.getKeyStroke("control P"), "imprimir"
        );
        view.areaTexto.getActionMap().put("imprimir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imprimirTexto();
            }            
        });
        
        
        //----------------Eventos del popupMenu en la hoja----------------
        // Click derecho sobre el areaTexto (hoja del doc)
        view.areaTexto.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {                    
                    view.menuHoja.show(view.areaTexto, e.getX(), e.getY());
                }
            }
        });
        
        // Click derecho sobre el panelAreaHoja (hoja del doc)
        view.panelAreaHoja.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {                    
                    view.menuHoja.show(view.panelAreaHoja, e.getX(), e.getY());
                }
            }
        });
        
        // Item Cortar
        view.cortar.addActionListener(e -> view.areaTexto.cut());
        
        // Item Copiar
        view.copiar.addActionListener(e -> view.areaTexto.copy());
        
        // Item Pegar
        view.pegar.addActionListener(e -> view.areaTexto.paste());
    }
    
    //------------Metodos para initController()-----------------    
    // Contador palabras
    private void actualizarContadorPalabras(){
        String texto = view.areaTexto.getText().trim();
        if(texto.isEmpty()) return;
        String[] palabras = texto.split("\\s+"); // Uno o más espacios en blanco seguidos
        view.lblContadorPalabras.setText("Palabras: " + palabras.length);
    }          
    
    // Contador caracteres
    private void actualizarContadorCaracteres() {
        String texto = view.areaTexto.getText();
        if(texto.isEmpty()) return;
        int caracteres = texto.length();
        view.lblContadorCaracteres.setText("Caracteres: " + caracteres);
    }

    // Metodo reutilizable para los botones de guardado
    private void hacerGuardadoDeArchivo(){
        if(currentFile != null){
            guardarArchivo(currentFile);
            JOptionPane.showMessageDialog(view, "Guardado");
        }else {
            File archivo = escogerRutaGuardado();
            if(archivo != null){
                currentFile = archivo;
                guardarArchivo(archivo);                
                view.setTitle(archivo.getName());
                JOptionPane.showMessageDialog(view, "Archivo Guardado", "Guardar archivo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Funcion cancelada", "Guardar archivo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void archivoGuardarComo() {
        File archivo = escogerRutaGuardado();
        if(archivo != null){
            currentFile = archivo;
            guardarArchivo(archivo);
            view.setTitle(archivo.getName());
            JOptionPane.showMessageDialog(view, "Archivo Guardado", "Guardar archivo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(view, "Funcion cancelada", "Guardar archivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Timer para guardar un bloque de texto cuando se deja de escribir
    private Timer timer = new Timer(1000, e -> {
        if (compoundEdit != null) {            
            compoundEdit.end();
            undoManager.addEdit(compoundEdit);
            compoundEdit = null;
            habilitarDeshacerRehacer();
        }
    });
    
    // Activar / Desactivar botones deshacer y rehacer
    private void habilitarDeshacerRehacer(){
        view.deshacer.setEnabled(undoManager.canUndo());
        view.rehacer.setEnabled(undoManager.canRedo());
    }
       
    // Metodo para cerrar un bloque manualmente
    public void cerrarBloque() {
        if (compoundEdit != null) {
            try {
                timer.stop();
                compoundEdit.end();
                undoManager.addEdit(compoundEdit);
            } catch (Exception e) {
                System.err.println("Error cerrando bloque");
            }
            compoundEdit = null;
        }
    }
    
    public void imprimirTexto() {
        try {
            boolean completado = view.areaTexto.print();  // Muestra el diálogo de impresión automáticamente

            if (completado) {
                JOptionPane.showMessageDialog(view, "Documento enviado a la impresora correctamente.", 
                                            "Imprimir", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(view, "Error al imprimir:\n" + ex.getMessage(), 
                                        "Error de impresión", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    
    
    //-----------METODOS PARA ABRIR Y GUARDAR EN FORMATO .RTF--------------    
    /*
    // Elegir ubicacion para guardar archivo (Formato .rtf)
    private File escogerRutaGuardado(){ 
        JFileChooser fileChooser = new JFileChooser(); 
        fileChooser.setDialogTitle("Guardar archivo"); 
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Documento Word(.rtf)", "rtf"); 
        fileChooser.setFileFilter(filter); 
        
        int result = fileChooser.showSaveDialog(view); 
        
        if(result == JFileChooser.APPROVE_OPTION){ 
            File archivo = fileChooser.getSelectedFile(); 
            
            if (!archivo.getName().endsWith(".rtf")) { 
                archivo = new File(archivo.getAbsolutePath() + ".rtf"); 
            } 
            
            return archivo; 
        } 
        return null; 
    }
    */
    /*
    // Guardar archivo (temporal en formato .rtf)
    private void guardarArchivo(File archivo){
        try (FileOutputStream out = new FileOutputStream(archivo)){ //Cambio en el try            
            new javax.swing.text.rtf.RTFEditorKit().write(out, view.areaTexto.getDocument(), 0, view.areaTexto.getDocument().getLength());
            out.close();            
        } catch (Exception e) {
            System.err.println("Error al guardar archivo");
        }       
    }
    */
    // Elegir ubicacion para abrir un archivo formato .rtf
    /*
    private File escogerRutaAbrir(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir archivo");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo Word(.rtf)", "rtf");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(view);
        
        if(result == JFileChooser.APPROVE_OPTION){
            File archivo = fileChooser.getSelectedFile();            
            return archivo;
        }
        return null;
    }
    */
    /*    
    // Metodo abrir archivo en formato .rtf
    private void abrirArchivo(File archivo, VentanaGUI view){
        try (FileInputStream in = new FileInputStream(archivo)){// Cambio del try            
            RTFEditorKit rtf = new RTFEditorKit();
            Document doc = rtf.createDefaultDocument();

            rtf.read(in, doc, 0);
            view.areaTexto.setDocument(doc);

            
            undoManager.discardAllEdits();      
            compoundEdit = null;
            //doc.addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
            doc.addUndoableEditListener(e -> {
                if (compoundEdit == null) {
                    compoundEdit = new CompoundEdit();
                }
                compoundEdit.addEdit(e.getEdit());
                timer.restart();
            });
            
            in.close();
            view.areaTexto.requestFocusInWindow();
        } catch (Exception ex) {
            System.err.println("Error al abrir archivo");
        }
    }
    */
    //------------------------FIN METODOS RTF---------------------------------
    
    
    
    
    //-----------METODOS PARA ABRIR Y GUARDAR EN FORMATO .DOCX--------------    
    // Elegir ubicacion para guardar archivo en formato .docx      
    private File escogerRutaGuardado(){        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Documento Word (.docx)", "docx");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showSaveDialog(view);

        if(result == JFileChooser.APPROVE_OPTION){
            File archivo = fileChooser.getSelectedFile();

            if (!archivo.getName().endsWith(".docx")) {
                archivo = new File(archivo.getAbsolutePath() + ".docx");
            }

            return archivo;
        }  
        return null;
    }
    // Guardar archivo en formato .docx
    private void guardarArchivo(File archivo) {
        try (FileOutputStream out = new FileOutputStream(archivo)) {

            XWPFDocument docx = new XWPFDocument();
            StyledDocument doc = view.areaTexto.getStyledDocument();

            int length = doc.getLength();
            int index = 0;

            XWPFParagraph parrafo = docx.createParagraph();

            // ================= TEXTO =================
            while (index < length) {

                Element elem = doc.getCharacterElement(index);
                AttributeSet attrs = elem.getAttributes();

                int start = elem.getStartOffset();
                int end = elem.getEndOffset();

                String texto = doc.getText(start, end - start);

                String[] lineas = texto.split("\n", -1);

                for (int i = 0; i < lineas.length; i++) {

                    if (i > 0) {
                        parrafo = docx.createParagraph();
                    }

                    aplicarAlineacion(parrafo, attrs);

                    XWPFRun run = parrafo.createRun();
                    run.setText(lineas[i]);

                    aplicarEstilos(run, attrs);
                }

                index = end;
            }

            // ================= IMÁGENES =================
            for (Component comp : view.panelEditor.getComponents()) {

                if (comp instanceof JLabel) {

                    JLabel label = (JLabel) comp;

                    if (label.getIcon() != null) {

                        XWPFParagraph pImg = docx.createParagraph();
                        XWPFRun runImg = pImg.createRun();

                        insertarImagenDesdeLabel(runImg, label);
                    }
                }
            }

            docx.write(out);
            docx.close();

            System.out.println("DOCX completo guardado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Elegir ubicacion para abrir un archivo formato .docx
    private File escogerRutaAbrir(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir archivo");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Documento Word (.docx)", "docx");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(view);

        if(result == JFileChooser.APPROVE_OPTION){
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    // Metodo abrir archivo en formato .docx
    private void abrirArchivo(File archivo, VentanaGUI view){
        try (FileInputStream fis = new FileInputStream(archivo)) {

            XWPFDocument docx = new XWPFDocument(fis);

            StyledDocument styledDoc = view.areaTexto.getStyledDocument();
            styledDoc.remove(0, styledDoc.getLength()); // limpiar

            for (XWPFParagraph p : docx.getParagraphs()) {

                // 🔹 Alineación
                SimpleAttributeSet attr = new SimpleAttributeSet();
                switch (p.getAlignment()) {
                    case CENTER:
                        StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
                        break;
                    case RIGHT:
                        StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
                        break;
                    case BOTH:
                        StyleConstants.setAlignment(attr, StyleConstants.ALIGN_JUSTIFIED);
                        break;
                    default:
                        StyleConstants.setAlignment(attr, StyleConstants.ALIGN_LEFT);
                }

                for (XWPFRun run : p.getRuns()) {

                    SimpleAttributeSet runAttr = new SimpleAttributeSet(attr);

                    // 🔸 TEXTO
                    String text = run.getText(0);
                    if (text == null) text = "";

                    // 🔸 ESTILOS
                    StyleConstants.setBold(runAttr, run.isBold());
                    StyleConstants.setItalic(runAttr, run.isItalic());
                    StyleConstants.setStrikeThrough(runAttr, run.isStrikeThrough());

                    if (run.getUnderline() != UnderlinePatterns.NONE) {
                        StyleConstants.setUnderline(runAttr, true);
                    }

                    // 🔸 FUENTE
                    if (run.getFontFamily() != null)
                        StyleConstants.setFontFamily(runAttr, run.getFontFamily());

                    if (run.getFontSize() > 0)
                        StyleConstants.setFontSize(runAttr, run.getFontSize());

                    // 🔸 COLOR
                    if (run.getColor() != null) {
                        StyleConstants.setForeground(runAttr,
                            Color.decode("#" + run.getColor()));
                    }

                    styledDoc.insertString(styledDoc.getLength(), text, runAttr);
                }

                // salto de línea por párrafo
                styledDoc.insertString(styledDoc.getLength(), "\n", null);
            }

            // 🔥 IMÁGENES (OPCIÓN 1: agregarlas al panel como ya haces)
            for (XWPFPictureData pic : docx.getAllPictures()) {

                byte[] data = pic.getData();
                InputStream is = new ByteArrayInputStream(data);
                BufferedImage img = ImageIO.read(is);

                ImageIcon icon = new ImageIcon(img);

                JLabel imgLabel = new JLabel(icon);
                imgLabel.setBounds(50, 50, icon.getIconWidth(), icon.getIconHeight());

                view.panelEditor.add(imgLabel, JLayeredPane.PALETTE_LAYER);
            }

            view.panelEditor.repaint();

            // 🔁 Reset undo
            undoManager.discardAllEdits();
            compoundEdit = null;

            styledDoc.addUndoableEditListener(e -> {
                if (compoundEdit == null) {
                    compoundEdit = new CompoundEdit();
                }
                compoundEdit.addEdit(e.getEdit());
                timer.restart();
            });

            view.areaTexto.requestFocusInWindow();

            System.out.println("DOCX abierto correctamente");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error al abrir DOCX");
        }
    }
    // Metodos para el guardado en formato .docx
    private void aplicarEstilos(XWPFRun run, AttributeSet attrs) {

        run.setBold(StyleConstants.isBold(attrs));
        run.setItalic(StyleConstants.isItalic(attrs));
        run.setStrikeThrough(StyleConstants.isStrikeThrough(attrs));

        if (StyleConstants.isUnderline(attrs)) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }

        // Fuente
        String font = StyleConstants.getFontFamily(attrs);
        if (font != null) run.setFontFamily(font);

        // Tamaño
        int size = StyleConstants.getFontSize(attrs);
        if (size > 0) run.setFontSize(size);

        // Color texto
        Color fg = StyleConstants.getForeground(attrs);
        if (fg != null) {
            run.setColor(toHex(fg));
        }

        // Fondo (highlight)
        Color bg = StyleConstants.getBackground(attrs);
        if (bg != null) {
            run.setTextHighlightColor("yellow"); // Word limitado
        }
    }
    
    private void aplicarAlineacion(XWPFParagraph p, AttributeSet attrs) {

        int align = StyleConstants.getAlignment(attrs);

        switch (align) {
            case StyleConstants.ALIGN_CENTER:
                p.setAlignment(ParagraphAlignment.CENTER);
                break;
            case StyleConstants.ALIGN_RIGHT:
                p.setAlignment(ParagraphAlignment.RIGHT);
                break;
            case StyleConstants.ALIGN_JUSTIFIED:
                p.setAlignment(ParagraphAlignment.BOTH);
                break;
            default:
                p.setAlignment(ParagraphAlignment.LEFT);
        }
    }
    
    private void insertarImagen(XWPFParagraph p, Icon icon) {
        try {
            BufferedImage bimg = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            Graphics g = bimg.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bimg, "png", baos);

            InputStream is = new ByteArrayInputStream(baos.toByteArray());

            XWPFRun run = p.createRun();
            run.addPicture(
                    is,
                    XWPFDocument.PICTURE_TYPE_PNG,
                    "img.png",
                    Units.toEMU(200),
                    Units.toEMU(200)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String toHex(Color c) {
        return String.format("%02X%02X%02X",
                c.getRed(), c.getGreen(), c.getBlue());
    }
    
    private void insertarImagenDesdeLabel(XWPFRun run, JLabel label) {
        try {
            Icon icon = label.getIcon();

            BufferedImage bimg = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            Graphics g = bimg.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bimg, "png", baos);

            InputStream is = new ByteArrayInputStream(baos.toByteArray());

            run.addPicture(
                    is,
                    XWPFDocument.PICTURE_TYPE_PNG,
                    "imagen.png",
                    Units.toEMU(label.getWidth()),
                    Units.toEMU(label.getHeight())
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //------------------------FIN METODOS WORD---------------------------------
    
    
    
    
    
}
