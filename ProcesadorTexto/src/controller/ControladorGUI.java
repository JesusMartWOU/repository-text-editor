package controller;

import java.awt.Frame;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import view.VentanaApertura;
import view.VentanaGUI;

public class ControladorGUI {
    private VentanaGUI view;    
    public File currentFile = null;
    public UndoManager undoManager = new UndoManager();
    public CompoundEdit compoundEdit = null;
    private int inicio, fin;
    private String textCopied = "";
    JTextPane principal;
    private JTextPane areaActiva;
    
    public ControladorGUI(VentanaGUI view){
        this.view = view;
        this.principal = view.areasTexto.get(0);

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
                
                makeNewView.setVisible(true);
                makeNewController.currentFile = archivo;
                abrirArchivo(archivo, makeNewView);
            }else{
                JOptionPane.showMessageDialog(view, "Error al abrir el archivo", "Abrir archivo", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Boton Guardar (temporal en formato .rtf) 
        view.guardar.addActionListener(e -> {
            hacerGuardadoDeArchivo();
        });                
        
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
        for (JTextPane area : view.areasTexto) {
            area.getDocument().addUndoableEditListener(e -> {
                if (compoundEdit == null) {
                    compoundEdit = new CompoundEdit();
                }
                compoundEdit.addEdit(e.getEdit());
                timer.restart();
            });
        }
                
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

        for (JTextPane area : view.areasTexto) {
            area.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        view.menuHoja.show(area, e.getX(), e.getY());
                    }
                }
            });
        }
        
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
        for (JTextPane area : view.areasTexto) {
            area.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    actualizarContadorPalabras();
                }
                public void removeUpdate(DocumentEvent e) {
                    actualizarContadorPalabras();
                }
                public void changedUpdate(DocumentEvent e) {
                    actualizarContadorPalabras();
                }
            });
        }

        
        //----------------Eventos de atajos del teclado----------------
        // Atajo Ctrl + Z
        view.areaTexto.getInputMap().put(
                KeyStroke.getKeyStroke("control Z"), "undo"
        );

        view.areaTexto.getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });
        
        // Atajo Ctrl + Y
        view.areaTexto.getInputMap().put(
                KeyStroke.getKeyStroke("control Y"), "redo"
        );

        view.areaTexto.getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()){
                    undoManager.redo();
                }
            }
        });

        for (JTextPane area : view.areasTexto) {
            //Atajo Ctrl+C
            area.getInputMap().put(KeyStroke.getKeyStroke("control C"), DefaultEditorKit.copyAction);
            //Atajo Ctrl+X
            area.getInputMap().put(KeyStroke.getKeyStroke("control X"), DefaultEditorKit.cutAction);
            //Atajo Ctrl+V
            area.getInputMap().put(KeyStroke.getKeyStroke("control V"), DefaultEditorKit.pasteAction);
        }

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

        for (JTextPane area : view.areasTexto) {
            area.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    areaActiva = area;
                }
            });
        }

        // Item Copy
        view.copiar.addActionListener(e -> areaActiva.copy());
        // Item Cut
        view.cortar.addActionListener(e -> areaActiva.cut());
        // Item Paste
        view.pegar.addActionListener(e -> areaActiva.paste());

        // Boton Zoom +
        view.btnZoomMas.addActionListener(e -> {
            if (view.zoomFactor < 2.0f) {
                view.zoomFactor += 0.1f;
                view.aplicarZoomGlobal(view.zoomFactor);
            }
        });

        // Boton Zoom -
        view.btnZoomMenos.addActionListener(e -> {
            if (view.zoomFactor > 0.5f) {
                view.zoomFactor -= 0.1f;
                view.aplicarZoomGlobal(view.zoomFactor);
            }
        });
    }
    
    //------------Metodos para initController()----------------
    /*private void actualizarContadorPalabras(){
        String texto = view.areaTexto.getText().trim();
        if(texto.isEmpty()) return;
        String[] palabras = texto.split("\\s+");
        view.lblContadorPalabras.setText("Palabras: " + palabras.length);
    }*/
    // Contador palabras
    private void actualizarContadorPalabras(){
        int total = 0;

        for (JTextPane area : view.areasTexto) {
            String texto = area.getText().trim();
            if (!texto.isEmpty()) {
                total += texto.split("\\s+").length;
            }
        }

        view.lblContadorPalabras.setText("Palabras: " + total);
    }
    
    // Elegir ubicacion para guardar archivo
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
    
    // Guardar archivo (temporal en formato .rtf)
    private void guardarArchivo(File archivo){
        try {
            FileOutputStream out = new FileOutputStream(archivo);
            new javax.swing.text.rtf.RTFEditorKit().write(out, view.areaTexto.getDocument(), 0, view.areaTexto.getDocument().getLength());
            out.close();            
        } catch (Exception e) {
            System.err.println("Error al guardar archivo");
        }       
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
                JOptionPane.showMessageDialog(view, "Archivo Guardado", "Guardar archivo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Funcion cancelada", "Guardar archivo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Elegir ubicacion para abrir un archivo
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
    
    private void abrirArchivo(File archivo, VentanaGUI view){
        try {
            FileInputStream in = new FileInputStream(archivo);

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
  
    private void getCursorTextPosition(){
        this.inicio = view.areaTexto.getSelectionStart();
        this.fin = view.areaTexto.getSelectionEnd();
    }
    
    private void setCharAttrOnText(){
        if(inicio == fin)
            view.areaTexto.setCharacterAttributes(view.attributes, false);
        else
            view.doc.setCharacterAttributes(inicio, fin - inicio, view.attributes, false);
        
        view.areaTexto.requestFocusInWindow();
    }
    
}
