package controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import view.VentanaApertura;
import view.VentanaGUI;

public class ControladorApertura {

    private VentanaApertura aperturaView;

    public ControladorApertura(VentanaApertura aperturaView) {
        this.aperturaView = aperturaView;
        initController();
    }

    private void initController() {

        // 🔹 BOTÓN NUEVO
        aperturaView.btnNuevo.addActionListener(e -> {
            VentanaGUI view = new VentanaGUI();
            new ControladorGUI(view);

            view.setVisible(true);
            view.areaTexto.requestFocusInWindow();

            aperturaView.dispose();
        });

        // 🔹 BOTÓN ABRIR
        aperturaView.btnAbrir.addActionListener(e -> {
            File archivo = escogerRutaAbrir();

            if (archivo != null) {

                VentanaGUI view = new VentanaGUI();
                ControladorGUI controlador = new ControladorGUI(view);

                abrirArchivoDOCX(archivo, view);

                controlador.currentFile = archivo;

                view.setTitle(archivo.getName());
                view.setVisible(true);
                view.areaTexto.requestFocusInWindow();

                aperturaView.dispose();

            } else {
                JOptionPane.showMessageDialog(aperturaView,
                        "Error al abrir el archivo",
                        "Abrir archivo",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // 🔹 BOTÓN CERRAR
        aperturaView.btnCerrar.addActionListener(e -> aperturaView.dispose());
    }
    
    //-----------------METODOS PARA ABRIR ARCHIVO .DOCX------------------------

    // ==============================
    // 📂 FILE CHOOSER DOCX
    // ==============================
    private File escogerRutaAbrir() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir archivo");

        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("Documento Word (.docx)", "docx");

        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(aperturaView);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    // ==============================
    // 📄 ABRIR DOCX (SIN UNDO)
    // ==============================
    private void abrirArchivoDOCX(File archivo, VentanaGUI view) {

        try (FileInputStream fis = new FileInputStream(archivo)) {

            XWPFDocument docx = new XWPFDocument(fis);

            // 🔹 usar documento existente
            StyledDocument styledDoc = view.areaTexto.getStyledDocument();
            styledDoc.remove(0, styledDoc.getLength());

            // ========= TEXTO =========
            for (XWPFParagraph p : docx.getParagraphs()) {

                SimpleAttributeSet parrafoAttr = new SimpleAttributeSet();

                switch (p.getAlignment()) {
                    case CENTER:
                        StyleConstants.setAlignment(parrafoAttr, StyleConstants.ALIGN_CENTER);
                        break;
                    case RIGHT:
                        StyleConstants.setAlignment(parrafoAttr, StyleConstants.ALIGN_RIGHT);
                        break;
                    case BOTH:
                        StyleConstants.setAlignment(parrafoAttr, StyleConstants.ALIGN_JUSTIFIED);
                        break;
                    default:
                        StyleConstants.setAlignment(parrafoAttr, StyleConstants.ALIGN_LEFT);
                }

                for (XWPFRun run : p.getRuns()) {

                    SimpleAttributeSet attr = new SimpleAttributeSet(parrafoAttr);

                    String texto = run.getText(0);
                    if (texto == null) continue;

                    StyleConstants.setBold(attr, run.isBold());
                    StyleConstants.setItalic(attr, run.isItalic());
                    StyleConstants.setStrikeThrough(attr, run.isStrikeThrough());

                    if (run.getUnderline() != UnderlinePatterns.NONE) {
                        StyleConstants.setUnderline(attr, true);
                    }

                    if (run.getFontFamily() != null)
                        StyleConstants.setFontFamily(attr, run.getFontFamily());

                    if (run.getFontSize() > 0)
                        StyleConstants.setFontSize(attr, run.getFontSize());

                    if (run.getColor() != null) {
                        try {
                            StyleConstants.setForeground(attr,
                                    Color.decode("#" + run.getColor()));
                        } catch (Exception ignored) {}
                    }

                    styledDoc.insertString(styledDoc.getLength(), texto, attr);
                }

                styledDoc.insertString(styledDoc.getLength(), "\n", null);
            }

            // ========= IMÁGENES =========
            view.panelEditor.removeAll();

            int offsetX = 50;
            int offsetY = 50;

            for (XWPFPictureData pic : docx.getAllPictures()) {

                byte[] data = pic.getData();
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));

                ImageIcon icon = new ImageIcon(img);
                JLabel label = new JLabel(icon);

                label.setBounds(offsetX, offsetY,
                        icon.getIconWidth(), icon.getIconHeight());

                offsetY += icon.getIconHeight() + 20;

                view.panelEditor.add(label, JLayeredPane.PALETTE_LAYER);
            }

           view.panelEditor.repaint();

            // aplicar documento
            view.areaTexto.setDocument(styledDoc);

            // 🔹 asegurar que areaTexto esté dentro del panel (sin duplicar)
            if (view.areaTexto.getParent() == null) {
                view.panelEditor.add(view.areaTexto, JLayeredPane.DEFAULT_LAYER);
            }

            SwingUtilities.invokeLater(() -> {
                view.areaTexto.setBounds(0, 0, 566, 736); // tamaño fijo tipo hoja

                view.panelEditor.revalidate();
                view.panelEditor.repaint();
            });

            view.areaTexto.revalidate();
            view.areaTexto.repaint();

            // 🔍 DEBUG
            /*
            System.out.println("areaTexto visible: " + view.areaTexto.isVisible());
            System.out.println("areaTexto tamaño: " + view.areaTexto.getWidth() + "x" + view.areaTexto.getHeight());
            System.out.println("areaTexto parent: " + view.areaTexto.getParent());
            */

            System.out.println("DOCX abierto correctamente");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(aperturaView,
                    "Error al abrir archivo DOCX");
        }
    }
}