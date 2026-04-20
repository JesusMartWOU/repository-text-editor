package controller;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import view.VentanaApertura;
import view.VentanaGUI;

public class ControladorGUI {
    private VentanaGUI view;
    public File currentFile = null;
    private HashMap<JTextPane, UndoManager> undoManagers = new HashMap<>();
    public CompoundEdit compoundEdit = null;
    private int inicio, fin;
    private String textCopied = "";
    JTextPane principal;
    private JTextPane areaActiva;

    public ControladorGUI(VentanaGUI view) {
        this.view = view;
        this.principal = view.areasTexto.get(0);

        for (JTextPane area : view.areasTexto) {
            agregarListenersANuevaArea(area);
        }

        this.areaActiva = principal;
        this.view.controlador = this;

        initController();

        // Inicializamos los otros controladores
        new ControladorFuente(view, this);
        new ControladorParrafo(view, this);
    }

    private UndoManager getUndoManagerActivo() {
        return undoManagers.get(areaActiva);
    }

    private void initController() {

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
            if (archivo != null) {
                VentanaGUI makeNewView = new VentanaGUI();
                ControladorGUI makeNewController = new ControladorGUI(makeNewView);

                makeNewView.setVisible(true);
                makeNewController.currentFile = archivo;
                abrirArchivo(archivo, makeNewView);
            } else {
                JOptionPane.showMessageDialog(view, "Error al abrir el archivo", "Abrir archivo", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Boton Guardar
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
        view.guardado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hacerGuardadoDeArchivo();
            }
        });

        //---------Eventos deshacer y rehacer-------------------
        // Boton deshacer
        view.deshacer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UndoManager um = getUndoManagerActivo();
                if (um != null && um.canUndo()) um.undo();
                habilitarDeshacerRehacer();
            }
        });

        // Boton rehacer
        view.rehacer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UndoManager um = getUndoManagerActivo();
                if (um != null && um.canRedo()) um.redo();
                habilitarDeshacerRehacer();
            }
        });

        // Click derecho por area
        for (JTextPane area : view.areasTexto) {
            area.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        view.menuHoja.show(area, e.getX(), e.getY());
                    }
                }
            });
        }

        //------------------Eventos complementarios------------------


        //----------------Eventos de atajos del teclado----------------
        for (JTextPane area : view.areasTexto) {

            // Ctrl + Z
            area.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "undo");
            area.getActionMap().put("undo", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    UndoManager um = getUndoManagerActivo();
                    if (um != null && um.canUndo()) um.undo();
                    habilitarDeshacerRehacer();
                }
            });

            // Ctrl + Y
            area.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "redo");
            area.getActionMap().put("redo", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    UndoManager um = getUndoManagerActivo();
                    if (um != null && um.canRedo()) um.redo();
                    habilitarDeshacerRehacer();
                }
            });

            // Ctrl + S
            area.getInputMap().put(KeyStroke.getKeyStroke("control S"), "save");
            area.getActionMap().put("save", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    hacerGuardadoDeArchivo();
                }
            });

            // Copy / Cut / Paste
            area.getInputMap().put(KeyStroke.getKeyStroke("control C"), DefaultEditorKit.copyAction);
            area.getInputMap().put(KeyStroke.getKeyStroke("control X"), DefaultEditorKit.cutAction);
            area.getInputMap().put(KeyStroke.getKeyStroke("control V"), DefaultEditorKit.pasteAction);
        }

        //----------------Eventos del popupMenu----------------
        view.panelAreaHoja.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    view.menuHoja.show(view.panelAreaHoja, e.getX(), e.getY());
                }
            }
        });

        // Detectar area activa
        for (JTextPane area : view.areasTexto) {
            area.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    areaActiva = area;
                }
            });
        }

        // Copy / Cut / Paste menú
        view.copiar.addActionListener(e -> {
            if (areaActiva != null) areaActiva.copy();
        });
        view.cortar.addActionListener(e -> {
            if (areaActiva != null) areaActiva.cut();
        });
        view.pegar.addActionListener(e -> {
            if (areaActiva != null) areaActiva.paste();
        });

        // Zoom
        view.btnZoomMas.addActionListener(e -> {
            view.zoomFactor += 0.1f;
            view.aplicarZoomGlobal(view.zoomFactor);
        });

        view.btnZoomMenos.addActionListener(e -> {
            view.zoomFactor -= 0.1f;
            if (view.zoomFactor < 0.2f) view.zoomFactor = 0.2f;

            view.aplicarZoomGlobal(view.zoomFactor);
        });

        view.btnAñadirPagina.addActionListener(e -> {
            agregarNuevaPagina();
        });
    }

    // Contador palabras
    private void actualizarContadorPalabras() {
        int total = 0;
        for (JTextPane area : view.areasTexto) {
            String texto = area.getText().trim();
            if (!texto.isEmpty()) {
                total += texto.split("\\s+").length;
            }
        }
        view.lblContadorPalabras.setText("Palabras: " + total);
    }

    private void guardarArchivo(File archivo) {
        try {
            JTextPane objetivo = (areaActiva != null) ? areaActiva : principal;
            FileOutputStream out = new FileOutputStream(archivo);
            new RTFEditorKit().write(out, objetivo.getDocument(), 0, objetivo.getDocument().getLength());
            out.close();
        } catch (Exception e) {
            System.err.println("Error al guardar archivo");
        }
    }

    private void hacerGuardadoDeArchivo() {
        File archivo = escogerRutaGuardado();
        if (archivo != null) {
            guardarArchivo(archivo);
            JOptionPane.showMessageDialog(view, "Guardado");
        } else {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Documento Word(.rtf)", "rtf"));
            if (fc.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                archivo = fc.getSelectedFile();
                guardarArchivo(archivo);
            }
        }
    }

    private File escogerRutaAbrir(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir archivo");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo Word (.rtf)", "rtf");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(view);

        if(result == JFileChooser.APPROVE_OPTION){
            File archivo = fileChooser.getSelectedFile();

            if (!archivo.getName().toLowerCase().endsWith(".rtf")) {
                JOptionPane.showMessageDialog(view,
                        "Solo se permiten archivos .rtf",
                        "Archivo inválido",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return archivo;
        }

        return null;
    }

    public void aplicarMargen(int izquierda, int derecha) {

        if (areaActiva == null) return;
        cerrarBloque();

        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = areaActiva.getStyledDocument();

                int start = areaActiva.getSelectionStart();
                int end = areaActiva.getSelectionEnd();

                if (start < 0 || end > doc.getLength() || start >= end) {
                    start = 0;
                    end = doc.getLength();
                }

                SimpleAttributeSet attrs = new SimpleAttributeSet();

                StyleConstants.setLeftIndent(attrs, izquierda);
                StyleConstants.setRightIndent(attrs, derecha);

                doc.setParagraphAttributes(start, end - start, attrs, false);

                areaActiva.revalidate();
                areaActiva.repaint();

            } catch (Exception e) {
                System.err.println("Error aplicando margen");
            }
        });
    }

    private void abrirArchivo(File archivo, VentanaGUI view) {
        try {
            FileInputStream in = new FileInputStream(archivo);
            RTFEditorKit rtf = new RTFEditorKit();
            Document doc = rtf.createDefaultDocument();
            rtf.read(in, doc, 0);

            JTextPane area = view.areasTexto.get(0);
            area.setDocument(doc);
            area.requestFocusInWindow();

            UndoManager um = undoManagers.get(area);
            if (um != null) um.discardAllEdits();

            compoundEdit = null;

            doc.addUndoableEditListener(e -> {
                if (compoundEdit == null) compoundEdit = new CompoundEdit();
                compoundEdit.addEdit(e.getEdit());
                timer.restart();
            });

            in.close();
        } catch (Exception ex) {
            System.err.println("Error al abrir archivo");
        }
    }

    private Timer timer = new Timer(1000, e -> {
        if (compoundEdit != null) {
            compoundEdit.end();

            UndoManager um = getUndoManagerActivo();
            if (um != null) um.addEdit(compoundEdit);

            compoundEdit = null;
            habilitarDeshacerRehacer();
        }
    });

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

    private void habilitarDeshacerRehacer() {
        UndoManager um = getUndoManagerActivo();
        if (um != null) {
            view.deshacer.setEnabled(um.canUndo());
            view.rehacer.setEnabled(um.canRedo());
        }
    }

    // Metodo para cerrar un bloque manualmente
    public void cerrarBloque() {
        if (compoundEdit != null) {
            try {
                timer.stop();
                compoundEdit.end();

                UndoManager um = getUndoManagerActivo();
                if (um != null) um.addEdit(compoundEdit);

            } catch (Exception e) {
                System.err.println("Error cerrando bloque");
            }
            compoundEdit = null;
        }
    }

    public void agregarNuevaPagina() {
        view.autoCrearPaginas = false;

        JPanel nuevaHoja = view.crearHoja();
        view.contenedorHojas.add(Box.createVerticalStrut(20));
        view.contenedorHojas.add(nuevaHoja);

        view.contenedorHojas.revalidate();
        view.contenedorHojas.repaint();

        JTextPane nuevaArea = view.areasTexto.get(view.areasTexto.size() - 1);
        agregarListenersANuevaArea(nuevaArea);

        new ControladorFuente(view, this);
        new ControladorParrafo(view, this);

        view.autoCrearPaginas = true;
    }

    public void agregarListenersANuevaArea(JTextPane area) {

        UndoManager um = new UndoManager();
        undoManagers.put(area, um);

        area.getDocument().addUndoableEditListener(e -> {
            if (compoundEdit == null) {
                compoundEdit = new CompoundEdit();
            }
            compoundEdit.addEdit(e.getEdit());
            timer.restart();
        });

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

        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                areaActiva = area;
            }
        });
    }

    public JTextPane getAreaActiva() {
        return areaActiva;
    }

    public void setAreaActiva(JTextPane area) {
        this.areaActiva = area;
    }
}