package view;

import controller.ControladorFuente;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VentanaApertura extends JFrame{
    private JLabel lblTitulo;
    public JButton btnNuevo, btnAbrir, btnCerrar;
    private JPanel panelTitulo, panelBotones;
    
    public VentanaApertura(){
        configFrame();
        initComponents();
    }
    
    private void configFrame(){                
        //setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));        
        setLayout(new BorderLayout());
        setTitle("Aciones del procesador de texto");
        setIconImage(new ImageIcon(getClass().getResource("/resources/editor-texto.png")).getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //setSize(400, 300);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);  
    }
    
    private void initComponents(){
        //Construir GUI
        panelTitulo = crearPanelTitulo();
        panelBotones = crearPanelBotones();
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
    }        
    
    private JPanel crearPanelTitulo(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(156, 137, 184)); // Color purpura
        
        lblTitulo = new JLabel("Procesador de Texto");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 48));
        lblTitulo.setForeground(Color.white);
        
        panel.add(lblTitulo);
        
        return panel;
    }
    
    private JPanel crearPanelBotones(){        
        //Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(new Color(241, 241, 241)); // Color blanco
        
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(new Color(241, 241, 241)); // Color blanco
        area.setPreferredSize(new Dimension(900, 600));
        
        JPanel botones = new JPanel(new GridBagLayout());
        botones.setBackground(new Color(241, 241, 241)); // Color blanco        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        btnNuevo = crearBoton(resizeImage("registro", 200, 200));
        btnAbrir = crearBoton(resizeImage("carpeta", 200, 200));
        btnCerrar = crearBoton(resizeImage("cerrar-sesion", 200, 200));
        
        JLabel lblNuevo = new JLabel("Nuevo Documento", SwingConstants.CENTER);
        JLabel lblAbrir = new JLabel("Abrir Archivo", SwingConstants.CENTER);
        JLabel lblCerrar = new JLabel("Cerrar App", SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        //gbc.gridwidth = 4; // Tamaño 
        gbc.fill = GridBagConstraints.HORIZONTAL; //Relleno
        gbc.anchor = GridBagConstraints.CENTER; // Alineacion
        botones.add(btnNuevo, gbc);
        
        gbc.gridx = 1;
        botones.add(btnAbrir, gbc);
        
        gbc.gridx = 2;
        botones.add(btnCerrar, gbc);
        
        gbc.gridy = 1;
        gbc.gridx = 0;
        botones.add(lblNuevo, gbc);        
        
        gbc.gridx = 1;
        botones.add(lblAbrir, gbc);
        
        gbc.gridx = 2;
        botones.add(lblCerrar, gbc);
        
        area.add(botones, BorderLayout.CENTER);
        fondo.add(area);
        
        return fondo;
    }
    
    private JButton crearBoton(ImageIcon icon){
        JButton b = new JButton(icon);
        
        b.setFocusPainted(false);
        b.setBackground(new Color(245, 245, 245));
        
        return b;
    }
    
    private ImageIcon resizeImage(String resource, int width, int height){
        ImageIcon icono = new ImageIcon(getClass().getResource("/resources/" + resource + ".png"));
        Image imagenOriginal = icono.getImage();
        Image nuevaImagen = imagenOriginal.getScaledInstance(width, height, Image.SCALE_SMOOTH);        
        icono = new ImageIcon(nuevaImagen);        
        return icono;
    }
    
}
