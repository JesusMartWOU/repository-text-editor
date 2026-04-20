package view;

import controller.ControladorGUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.*;

public class VentanaGUI extends JFrame {
    public JTextPane areaTexto;
    public JLayeredPane panelEditor;
    private JPanel panelMenu, panelHeader, panelCartas, panelInicio, 
            panelInsertar, panelFormato, panelFooter, 
            panelFuente;
    public JPanel panelAreaHoja;
    public ArrayList<JTextPane> areasTexto = new ArrayList<>();
    public JLabel lblContadorPalabras;
    public JButton btnAumentarTamaño, btnReducirTamaño, btnCambiarMayusMinusc, 
            btnBorrarFormato, btnNegritas, btnCursiva, btnSubrayar, btnTachado, 
            btnSubindice, btnSuperindice, btnTipografia, btnResaltarTexto, 
            btnColorFuente, btnViñeta, btnNumeracion, btnDisminuirSangria,
            btnAumentarSangria, btnInterlineado, btnAlinearIzq, btnAlinearCen, 
            btnAlinearDer, btnJustificar, btnZoomMas, btnZoomMenos, btnAñadirPagina;
    public JComboBox<String> comboTipoFuente;
    public JComboBox<Integer> comboTamañoFuente;   
    private CardLayout cardLayout;
    private JMenuBar menuBar;
    public JMenu menuArchivo, menuInicio, menuInsertar, menuFormato, guardado,
            deshacer, rehacer;
    public JMenuItem nuevo, abrir, guardar, guardarComo, imprimir, opciones, 
            cerrar, negro, gris, rojo, naranja, verde, cyan, azul, magenta,
            otroColor, sinColor, interlineado1_0, interlineado1_15, 
            interlineado1_5, interlineado2_0, interlineado2_5, 
            interlineado3_0, sinInterlineado, cortar, copiar, pegar;
    public JPopupMenu menuColores, menuInterlineado, menuHoja;
    public SimpleAttributeSet attributes;
    public StyledDocument doc;
    public JPanel contenedorHojas;
    public float zoomFactor = 1.0f;
    public boolean autoCrearPaginas = true;
    public ControladorGUI controlador;
    
    private String[] fuentes = { "Arial", "Calibri", "Times New Roman", "Algerian" ,"Consolas", "Papyrus", 
        "Georgia", "Impact", "Tahoma", "Segoe UI", "SansSerif", "Edwardian Script ITC" };
    private Integer[] tamaños = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 
        26, 28, 36, 48, 76 };    
    private Color[] colores = { Color.BLACK, Color.GRAY, Color.RED, 
        Color.ORANGE, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA };    
    private Float[] espaciados = { 0.0f, 1.0f, 1.15f, 1.5f, 2.0f, 2.5f, 3.0f };
    
    public VentanaGUI() {
        configFrame();
        initComponents();
    }
    
    private void configFrame() {
        setLayout(new BorderLayout());        
        setTitle("Procesador de Texto");       
        setIconImage(new ImageIcon(getClass().getResource("/resources/editor-texto.png")).getImage());
        setJMenuBar(frameMenuBar());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);  
    }
    
    private void initComponents() {
        attributes = new SimpleAttributeSet();        
        menuHoja = crearPopupMenuHoja();
        
        //Creamos los paneles
        panelMenu = crearMenuBar();     
        panelCartas = crearPanelCartas();
        panelAreaHoja = crearAreaTexto();
        panelFooter = crearFooter();

        //Personalizamos panelAreaHoja:
        panelAreaHoja.setLayout(new BoxLayout(panelAreaHoja, BoxLayout.Y_AXIS));
        panelAreaHoja.setBackground(new Color(200, 200, 200));
        
        //Construimos la interfaz (header)
        panelHeader = new JPanel(new BorderLayout());             
        panelHeader.add(panelMenu, BorderLayout.NORTH);
        panelHeader.add(panelCartas, BorderLayout.CENTER);        
        
        //Añadimos al Frame
        add(panelHeader, BorderLayout.NORTH);      
        add(panelAreaHoja, BorderLayout.CENTER);
        add(panelFooter, BorderLayout.SOUTH);
    }
    
    private JMenuBar frameMenuBar(){
        JMenuBar barra = new JMenuBar();
        barra.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        barra.setBorderPainted(false);
        barra.setBackground(new Color(156, 137, 184));
        barra.setBorder(null);
                
        guardado = crearJMenu("Guardar");
        deshacer = crearJMenu("Deshacer");
        rehacer = crearJMenu("Rehacer");
        
        deshacer.setEnabled(false);
        rehacer.setEnabled(false);
        
        barra.add(guardado);
        barra.add(deshacer);
        barra.add(rehacer);                
        
        return barra;
    }
    
    private JPanel crearPanelCartas() {
        cardLayout = new CardLayout();
        JPanel panel = new JPanel(cardLayout);
        panel.setPreferredSize(new Dimension(0, 100));
        
        //Panel Inicio
        panelInicio = crearPanelInicio();        
        
        //Panel Insertar
        panelInsertar = new JPanel();
        panelInsertar.setBackground(Color.green);
        panelInsertar.add(new JLabel("Panel Insertar"));
        
        //Panel Formato
        panelFormato = new JPanel();
        panelFormato.setBackground(Color.cyan);
        panelFormato.add(new JLabel("Panel Formato"));
        
        panel.add(panelInicio, "INICIO");
        panel.add(panelInsertar, "INSERTAR");
        panel.add(panelFormato, "FORMATO");
        
        return panel;
    }
    
    private JPanel crearPanelInicio(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));        
        panel.setBackground(new Color(241, 241, 241));
        
        //---------Panel para herramientas de Fuente----------------
        JPanel herramientas = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Espaciado entre componentes
        //gbc.insets = new Insets(5, 5, 5, 5);

        // Botones Zoom
        btnZoomMas = crearBoton("Zoom +");
        btnZoomMenos = crearBoton("Zoom -");

        // Boton Añadir Pagina
        btnAñadirPagina = new JButton("Nueva Pagina");

        panel.add(btnZoomMas);
        panel.add(btnZoomMenos);
        panel.add(btnAñadirPagina);
        
        // ComboBox Fuentes
        comboTipoFuente = new JComboBox(fuentes); // Se crea el componente 
        customCombo(comboTipoFuente);
        //Se define su posicion gbc en el panel
        gbc.gridx = 0; // Posicion x del componente
        gbc.gridy = 0; // Posicion y del componente
        gbc.gridwidth = 4; // Tamaño (ocupa 4 columnas)
        gbc.fill = GridBagConstraints.HORIZONTAL; //Rellena espacios en blanco del componente
        gbc.anchor = GridBagConstraints.WEST; // Alineacion del componente
        herramientas.add(comboTipoFuente, gbc); // Añadir componente al panel
                
        // ComboBox TamañoFuente
        comboTamañoFuente = new JComboBox(tamaños);
        customCombo(comboTamañoFuente);
        gbc.gridx = 4;
        gbc.gridwidth = 1;        
        herramientas.add(comboTamañoFuente, gbc);
        
        // Boton AumentarTamaño
        btnAumentarTamaño = crearBoton("+Tam");
        gbc.gridx = 5;
        herramientas.add(btnAumentarTamaño, gbc);
        
        // Boton ReducirTamaño
        btnReducirTamaño = crearBoton("-Tam");
        gbc.gridx = 6;
        herramientas.add(btnReducirTamaño, gbc);
        
        // Boton CambiarMayusMinusc
        btnCambiarMayusMinusc = crearBoton("Aa");
        gbc.gridx = 7;
        herramientas.add(btnCambiarMayusMinusc, gbc);
        
        // Boton BorrarFormato
        btnBorrarFormato = crearBoton("A->x");
        gbc.gridx = 8;
        herramientas.add(btnBorrarFormato, gbc);
        
        // Boton Negritas
        btnNegritas = crearBoton("N");
        gbc.gridx = 0;
        gbc.gridy = 1;      
        herramientas.add(btnNegritas, gbc);
        
        // Boton Cursiva
        btnCursiva = crearBoton("K");
        gbc.gridx = 1;
        herramientas.add(btnCursiva, gbc);
        
        // Boton Subrayar
        btnSubrayar = crearBoton("_S_");
        gbc.gridx = 2;
        herramientas.add(btnSubrayar, gbc);
        
        // Boton Tachado
        btnTachado = crearBoton("-abc-");
        gbc.gridx = 3;
        herramientas.add(btnTachado, gbc);
        
        // Boton Subindice
        btnSubindice = crearBoton("X,");
        gbc.gridx = 4;
        herramientas.add(btnSubindice, gbc);
        
        // Boton Superindice
        btnSuperindice = crearBoton("X´");
        gbc.gridx = 5;
        herramientas.add(btnSuperindice, gbc);
        
        // Boton Tipografia
        btnTipografia = crearBoton("Image");
        gbc.gridx = 6;
        herramientas.add(btnTipografia, gbc);
        
        // Boton ResaltarTexto
        btnResaltarTexto = crearBoton("Resaltado");
        gbc.gridx = 7;
        herramientas.add(btnResaltarTexto, gbc);
        
        // Boton ColorFuente
        menuColores = crearMenuColores();
        btnColorFuente = crearBoton("Color");
        btnColorFuente.addActionListener(e -> {
            menuColores.show(btnColorFuente, 0, btnColorFuente.getHeight());
        });
        gbc.gridx = 8;
        herramientas.add(btnColorFuente, gbc);
        
        // Label Categoria
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        herramientas.add(new JLabel("Fuente"), gbc);
                        
        //----------Panel para herramientas de Parrafo---------------
        JPanel parrafo = new JPanel(new GridBagLayout());                
        
        btnViñeta = crearBoton("Viñeta");        
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.WEST; 
        parrafo.add(btnViñeta, gbc);
        
        btnNumeracion = crearBoton("Numeracion");
        gbc.gridx = 1;
        parrafo.add(btnNumeracion, gbc);
        
        btnDisminuirSangria = crearBoton("-Sangria");
        gbc.gridx = 2;
        parrafo.add(btnDisminuirSangria, gbc);
        
        btnAumentarSangria = crearBoton("+Sangria");
        gbc.gridx = 3;
        parrafo.add(btnAumentarSangria, gbc);
        
        /*
        comboInterlineado = new JComboBox(espaciados);        
        customCombo(comboInterlineado);*/
        menuInterlineado = crearMenuInterlineado();
        btnInterlineado = crearBoton("Interlineado");
        gbc.gridx = 4;
        parrafo.add(btnInterlineado, gbc);
        
        btnAlinearIzq = crearBoton("Alinear Izq.");
        btnAlinearIzq.setBackground(new Color(197, 197, 197));
        gbc.gridx = 0;
        gbc.gridy = 1;
        parrafo.add(btnAlinearIzq, gbc);
        
        btnAlinearCen = crearBoton("Alinear Cen.");
        gbc.gridx = 1;
        parrafo.add(btnAlinearCen, gbc);
        
        btnAlinearDer = crearBoton("Alinear Der.");
        gbc.gridx = 2;
        parrafo.add(btnAlinearDer, gbc);
        
        btnJustificar = crearBoton("Justificar");
        gbc.gridx = 3;
        parrafo.add(btnJustificar, gbc);        
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        parrafo.add(new JLabel("Parrafo"), gbc);
        
        panel.add(herramientas);      
        JSeparator separador1 = new JSeparator(JSeparator.VERTICAL);
        separador1.setPreferredSize(new Dimension(1, 75));
        separador1.setForeground(Color.lightGray);
        panel.add(separador1);
        panel.add(parrafo);
        
        return panel;
    }        
    
    private JPanel crearMenuBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(156, 137, 184));
        
        //Creamos el menuBar
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(156, 137, 184));
        
        //Opciones del menuBar
        menuArchivo = crearJMenu("Archivo");               
        menuInicio = crearJMenu("Inicio");
        menuInsertar = crearJMenu("Insertar");
        menuFormato = crearJMenu("Formato");
        
        //Items del menu Archivo
        nuevo = new JMenuItem("Nuevo");
        abrir = new JMenuItem("Abrir");
        guardar = new JMenuItem("Guardar");
        guardarComo = new JMenuItem("Guardar Como");
        imprimir = new JMenuItem("Imprimir");
        opciones = new JMenuItem("Opciones");
        cerrar = new JMenuItem("Cerrar");
        
                
        menuArchivo.add(nuevo);
        menuArchivo.add(abrir);
        menuArchivo.add(guardar);
        menuArchivo.add(guardarComo);
        menuArchivo.addSeparator();
        menuArchivo.add(imprimir);
        menuArchivo.add(opciones);
        menuArchivo.add(cerrar);   
       
        //Añadimos las opciones de menu a la barra
        menuBar.add(menuArchivo);
        menuBar.add(menuInicio);
        menuBar.add(menuInsertar);
        menuBar.add(menuFormato);
        
        //Añadimos el menuBar al panelMenu
        panel.add(menuBar);      
        
        //-----Eventos solo visual asi que se queda en la vista-------
        //-----------Se activa al hacer click en el menu--------------        
        menuInicio.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(panelCartas, "INICIO");
            }
        });
        menuInsertar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(panelCartas, "INSERTAR");
            }
        });
        menuFormato.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(panelCartas, "FORMATO");
            }
        });
        
        return panel;
    }

    /*
    private JPanel crearAreaTexto() {
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(new Color(230, 230, 230));

        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setOpaque(false);

        JPanel hoja = new JPanel(new BorderLayout());
        hoja.setBackground(Color.WHITE);
        hoja.setPreferredSize(new Dimension(600, 800));
        hoja.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        areaTexto = new JTextPane();
        areaTexto.setMargin(new Insets(65, 75, 65, 75));
        areaTexto.setEditorKit(new StyledEditorKit());

        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);
        areaTexto.setParagraphAttributes(attributes, false);

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(null);

        hoja.add(scroll, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        contenedor.add(hoja, gbc);

        fondo.add(contenedor, BorderLayout.CENTER);

        return fondo;
    }
     */

    public JPanel crearHoja() {
        JPanel hoja = new JPanel(new BorderLayout());
        hoja.setBackground(Color.WHITE);
        hoja.setPreferredSize(new Dimension(
                (int)(794 * zoomFactor),
                (int)(1123 * zoomFactor)
        ));
        hoja.setMaximumSize(new Dimension(794, 1123));
        hoja.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JTextPane area = new JTextPane();
        area.setMargin(new Insets(65, 75, 65, 75));
        areasTexto.add(area);

        if (areaTexto == null) {
            areaTexto = area;
        }

        if (controlador != null) {
            controlador.agregarListenersANuevaArea(area);
        }

        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (controlador != null) {
                    controlador.setAreaActiva(area);
                }
            }
        });

        hoja.add(area, BorderLayout.CENTER);

        // Listener inteligente
        area.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { verificar(area); }
            public void removeUpdate(DocumentEvent e) {}
            public void changedUpdate(DocumentEvent e) {}

            private void verificar(JTextPane area) {
                int altura = area.getPreferredSize().height;

                if (autoCrearPaginas && altura > 900) { // límite realista
                    // Evita crear infinitas hojas
                    if (contenedorHojas.getComponentCount() < 10) {
                        contenedorHojas.add(Box.createVerticalStrut(20));
                        contenedorHojas.add(crearHoja());
                        contenedorHojas.revalidate();
                    }
                }
            }
        });

        return hoja;
    }


    private JPanel crearAreaTexto() {

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(new Color(230, 230, 230));

        contenedorHojas = new JPanel();
        contenedorHojas.setLayout(new BoxLayout(contenedorHojas, BoxLayout.Y_AXIS));
        contenedorHojas.setBackground(new Color(230, 230, 230));

        // Primera hoja
        contenedorHojas.add(Box.createVerticalStrut(20));
        contenedorHojas.add(crearHoja());

        JScrollPane scroll = new JScrollPane(contenedorHojas);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        fondo.add(scroll, BorderLayout.CENTER);

        return fondo;
    }

    
    private JPanel crearFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(241, 241, 241));
        
        lblContadorPalabras = new JLabel("Palabras: 0");
        panel.add(lblContadorPalabras);
        
        return panel;
    }        
    
    private JPopupMenu crearMenuColores(){
        JPopupMenu popMenu = new JPopupMenu();
        popMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        negro = crearMenuItem(Color.black);
        gris = crearMenuItem(Color.lightGray);
        rojo = crearMenuItem(Color.red);
        naranja = crearMenuItem(Color.orange);
        verde = crearMenuItem(Color.green);
        cyan = crearMenuItem(Color.cyan);
        azul = crearMenuItem(Color.blue);
        magenta = crearMenuItem(Color.magenta);
        otroColor = crearMenuItem("Otro Color");
        sinColor = crearMenuItem("Sin Color");
                
        popMenu.add(negro);
        popMenu.add(gris);
        popMenu.add(rojo);
        popMenu.add(naranja);
        popMenu.add(verde);
        popMenu.add(cyan);
        popMenu.add(azul);
        popMenu.add(magenta);
        popMenu.addSeparator();
        popMenu.add(otroColor);
        popMenu.add(sinColor);
        
        return popMenu;
    }
    
    private JPopupMenu crearMenuInterlineado(){
        JPopupMenu popMenu = new JPopupMenu();
        popMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        interlineado1_0 = crearMenuItem("1.0");
        interlineado1_15 = crearMenuItem("1.15");
        interlineado1_5 = crearMenuItem("1.5");
        interlineado2_0 = crearMenuItem("2.0");
        interlineado2_5 = crearMenuItem("2.5");
        interlineado3_0 = crearMenuItem("3.0");
        sinInterlineado = crearMenuItem("Sin interlineado");
        
        popMenu.add(interlineado1_0);
        popMenu.add(interlineado1_15);
        popMenu.add(interlineado1_5);
        popMenu.add(interlineado2_0);
        popMenu.add(interlineado2_5);
        popMenu.add(interlineado3_0);
        popMenu.addSeparator();
        popMenu.add(sinInterlineado);
        
        return popMenu;
    }
    
    private JPopupMenu crearPopupMenuHoja(){
        JPopupMenu popMenu = new JPopupMenu();
        popMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cortar = crearMenuItem("Cortar");
        copiar = crearMenuItem("copiar");
        pegar = crearMenuItem("Pegar");
        
        popMenu.add(cortar);
        popMenu.add(copiar);
        popMenu.add(pegar);
        
        return popMenu;
    }
    
    private void customCombo(JComboBox combo){
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        combo.setBackground(new Color(245, 245, 245));
    }
    
    private JMenu crearJMenu(String s){
        JMenu m = new JMenu(s);
        m.setForeground(Color.white);
        m.setCursor(new Cursor(Cursor.HAND_CURSOR));
        m.setBorder(null);
        
        return m;
    }
    
    private JButton crearBoton(String s){
        JButton btn = new JButton(s);
        btn.setFocusPainted(false);
        //btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(new Color(245, 245, 245));
        
        return btn;
    }
    
    private JMenuItem crearMenuItem(Color c){
        JMenuItem item = new JMenuItem();
        item.setBackground(c);
        item.setOpaque(true);
        //item.setPreferredSize(new Dimension(40, 20));     
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return item;
    }
    
    private JMenuItem crearMenuItem(String s){
        JMenuItem item = new JMenuItem(s);        
        item.setOpaque(true);
        //item.setPreferredSize(new Dimension(40, 20));     
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return item;
    }

    public void aplicarZoom(JTextPane area, float factor) {
        StyledDocument doc = area.getStyledDocument();

        for (int i = 0; i < doc.getLength(); i++) {
            javax.swing.text.Element element = doc.getCharacterElement(i);
            SimpleAttributeSet attr = new SimpleAttributeSet(element.getAttributes());

            int baseSize = StyleConstants.getFontSize(attr);

            // Evitar que se acumule infinito
            int newSize = Math.max(1, Math.round(baseSize * factor));

            StyleConstants.setFontSize(attr, newSize);

            doc.setCharacterAttributes(i, 1, attr, true);
        }
    }

    public void aplicarZoomGlobal(float factor) {
        for (JTextPane area : areasTexto) {
            aplicarZoom(area, factor);
        }
    }

}
