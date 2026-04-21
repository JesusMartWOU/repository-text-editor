package view;

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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class VentanaGUI extends JFrame {
    public JTextPane areaTexto;
    public JLayeredPane panelEditor;
    private JPanel panelMenu, panelHeader, panelCartas, panelInicio, 
            panelInsertar, panelFormato, panelFooter, 
            panelFuente;
    public JPanel panelAreaHoja;
    public JLabel lblContadorPalabras, lblContadorCaracteres, lblListo;
    public JButton btnAumentarTamaño, btnReducirTamaño, btnCambiarMayusMinusc, 
            btnBorrarFormato, btnNegritas, btnCursiva, btnSubrayar, btnTachado, 
            btnSubindice, btnSuperindice, btnTipografia, btnResaltarTexto, 
            btnColorFuente, btnViñeta, btnNumeracion, btnDisminuirSangria,
            btnAumentarSangria, btnInterlineado, btnAlinearIzq, btnAlinearCen, 
            btnAlinearDer, btnJustificar;
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
        setTitle("*Archivo sin Nombre");       
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
        // Panel fondo (gris)
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(new Color(230, 230, 230));

        // Panel hoja (blanco)
        JPanel hoja = new JPanel(new BorderLayout());
        hoja.setBackground(Color.WHITE);
        //hoja.setPreferredSize(new Dimension(600, 800)); // tamaño tipo hoja
        hoja.setPreferredSize(new Dimension(566, 736)); // tamaño tipo hoja word

        // JTextPane (area para escribir)
        areaTexto = new JTextPane();
        //areaTexto.setMargin(new Insets(20, 20, 20, 20)); // Margen de hoja
        areaTexto.setMargin(new Insets(65, 75, 65, 75)); // Margen tipo word
        areaTexto.setEditorKit(new StyledEditorKit());
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT); // Inicia con Alineacion Izq.
        areaTexto.setParagraphAttributes(attributes, false);

        hoja.add(areaTexto, BorderLayout.CENTER);
        fondo.add(hoja); // centrado automático
        
        return fondo;
    } 
    */
    private JPanel crearAreaTexto() {
        // Panel fondo (gris)
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(new Color(230, 230, 230));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(566, 736));

        // JTextPane
        areaTexto = new JTextPane();
        areaTexto.setBounds(0, 0, 566, 736);
        areaTexto.setMargin(new Insets(65, 75, 65, 75));
        areaTexto.setEditorKit(new StyledEditorKit());

        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);
        areaTexto.setParagraphAttributes(attributes, false);
        
        layeredPane.add(areaTexto, JLayeredPane.DEFAULT_LAYER);       
        this.panelEditor = layeredPane;

        fondo.add(layeredPane);

        return fondo;
    }
    
    /*
    private JPanel crearFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(241, 241, 241));
        
        lblContadorCaracteres = new JLabel("Caracteres: 0");                
        lblContadorPalabras = new JLabel("Palabras: 0");
        lblListo = new JLabel("Listo");
        
        panel.add(lblContadorCaracteres);
        panel.add(lblContadorPalabras);       
        panel.add(lblListo);
        
        return panel;
    }*/
    
    private JPanel crearFooter() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));  
        panel.setBackground(new Color(241, 241, 241));

        lblContadorCaracteres = new JLabel("Caracteres: 0");
        lblContadorPalabras = new JLabel("Palabras: 0");
        lblListo = new JLabel("Listo");
        lblListo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 25));
        

        // Panel izquierdo para los contadores
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelIzquierdo.setOpaque(false);  // para que se vea el fondo del footer
        panelIzquierdo.add(lblContadorCaracteres);
        panelIzquierdo.add(lblContadorPalabras);

        panel.add(panelIzquierdo, BorderLayout.WEST); 
        panel.add(lblListo, BorderLayout.EAST);        

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
        
}
