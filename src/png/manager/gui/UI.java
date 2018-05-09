package png.manager.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import png.manager.decoder.Decoder;
import png.manager.decoder.DecoderType;
import png.manager.entity.PNGImage;
import png.manager.exception.ChunkParserException;
import png.manager.exception.DecodingException;
import png.manager.exception.PNGStructureException;
import png.manager.miscellaneous.Utility;

/**
 * Interfaccia grafica del programma. Contiene il metodo <code>main</code> da
 * cui parte il programma.
 */
public class UI {

    /**
     * Larghezza della finestra principale.
     */
    private static final int WINDOW_WIDTH = Utility.getScreenWidth() * 3 / 5;
    /**
     * Altezza della finestra principale.
     */
    private static final int WINDOW_HEIGHT = Utility.getScreenHeight() * 3 / 4;
    /**
     * Larghezza dei popup.
     */
    private static final int POPUP_WINDOW_WIDTH = 400;
    /**
     * Altezza dei popup.
     */
    private static final int POPUP_WINDOW_HEIGHT = 250;
    /**
     * Velocità di scrolling verticale del pannello contenente l'immagine.
     */
    private static final int SCROLLBAR_VERTICAL_SPEED = 16;
    /**
     * Velocità di scrolling orizzontale del pannello contenente l'immagine.
     */
    private static final int SCROLLBAR_HORIZONTAL_SPEED = 16;

    /**
     * Finestra principale.
     */
    private static final JFrame mainWindow = new JFrame("PNG Manager");

    /**
     * Voce del menu File.
     */
    private static final JMenuItem openOption = new JMenuItem("Apri...");
    /**
     * Voce del menu File.
     */
    private static final JMenuItem exportOption = new JMenuItem("Salva senza metadati...");
    /**
     * Voce del menu File.
     */
    private static final JMenuItem convertOption = new JMenuItem("Converti in JPEG...");    
    /**
     * Voce del menu File.
     */
    private static final JMenuItem aboutOption = new JMenuItem("Informazioni");
    /**
     * Voce del menu File.
     */
    private static final JMenuItem exitOption = new JMenuItem("Esci");

    /**
     * Nome del bottone.
     */
    private static final String enableBlackAndWhiteLabel = "Attiva modalita' bianco e nero";
    /**
     * Nome del bottone.
     */
    private static final String disableBlackAndWhiteLabel = "Disattiva modalita' bianco e nero";
    /**
     * Bottone per attivare la conversione dell'immagine.
     */
    private static final JButton blackAndWhiteButton = new JButton(enableBlackAndWhiteLabel, null);
    /**
     * Nome del bottone.
     */
    private static final JButton exportChunkButton = new JButton("Esporta report su file di testo", null);

    /**
     * Pannello contenente l'immagine e le sue informazioni.
     */
    private static final JPanel mainPanel = new JPanel();
    /**
     * Pannello contenente l'immagine e la check box dei metadati.
     */
    private static final JPanel imagePanel = new JPanel();
    /**
     * Pannello dell'immagine da visualizzare.
     */
    private static ImagePanel imageArea;
    /**
     * Pannello per lo scorrimento di immagini troppo grandi.
     */
    private static JScrollPane scrollPane;
    /**
     * Check box per attivare la visualizzazione dell'immagine con metadati.
     */
    static final JCheckBox enableMetaBox = new JCheckBox("Attiva metadati / Correggi immagine");

    /**
     * Informazioni sull'immagine corrente.
     */
    static final JLabel imageInfo = new JLabel("");

    /**
     * Finestra con le informazioni sul programma.
     */
    private static final JDialog aboutWindow = new JDialog(mainWindow, "Informazioni");

    /**
     * Finestra per la richiesta di inserimento di un nome di file.
     */
    private static final JDialog nameRequestWindow = new JDialog(mainWindow, "Salva con nome...");
    /**
     * Pannello della finestra per la richiesta di inserimento di un nome di
     * file.
     */
    private static final JPanel nameRequestPanel = new JPanel();
    /**
     * Casella d'inserimento del nome di file.
     */
    private static final JTextField nameRequestValue = new JTextField();
    /**
     * Bottone per confermare l'inserimento del nome di file.
     */
    private static final JButton nameRequestButton = new JButton("Salva!");
    /**
     * Flag che indica se il nome del file è stato inserito correttamente.
     */
    private static boolean requestCompleted = false;

    /**
     * Percorso dell'immagine correntemente visualizzata.
     */
    static String fileChosen = null;
    /**
     * Immagine correntemente visualizzata, risultato del processo di parsing.
     */
    static PNGImage parsedImage = null;
    /**
     * Immagine correntemente visualizzata, risultato del processo di
     * decodifica.
     */
    static BufferedImage currImage = null;
    /**
     * Immagine visualizzata con l'impiego dei metadati.
     */
    static BufferedImage currImageWithMeta = null;
    /**
     * Immagine in bianco e nero.
     */
    static BufferedImage blackAndWhiteImage = null;
    /**
     * Flag che indica se l'interfaccia grafica sta mostrando l'immagine in
     * bianco e nero invece dell'originale.
     */
    private static boolean blackAndWhiteInUse = false;

    /**
     * Metodo di avvio del programma. Disegna l'interfaccia grafica.
     *
     * @param args parametri di input della console
     */
    public static void main(String[] args) {
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        mainWindow.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        mainWindow.setResizable(false);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setLayout(new BoxLayout(mainWindow.getContentPane(), BoxLayout.Y_AXIS));

        JMenu fileDropdownMenu = new JMenu("File");
        fileDropdownMenu.add(openOption);
        fileDropdownMenu.add(exportOption);
        fileDropdownMenu.add(convertOption);
        fileDropdownMenu.add(aboutOption);
        fileDropdownMenu.addSeparator();
        fileDropdownMenu.add(exitOption);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileDropdownMenu);
        mainWindow.setJMenuBar(menuBar);

        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        BufferedImage icon = new UI().loadIcon("/moon.png");
        if (icon != null) {
            blackAndWhiteButton.setIcon(new ImageIcon(icon));
        }
        icon = new UI().loadIcon("/note.png");
        if (icon != null) {
            exportChunkButton.setIcon(new ImageIcon(icon));
        }

        JToolBar toolBar = new JToolBar("Barra degli strumenti");
        toolBar.add(blackAndWhiteButton);
        toolBar.add(exportChunkButton);
        toolbarPanel.add(toolBar);

        mainPanel.setLayout(new FlowLayout());
        mainPanel.setSize(new Dimension(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 100));
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 100));
        imagePanel.setSize(new Dimension(WINDOW_WIDTH * 2 / 3 - 50, WINDOW_HEIGHT - 120));
        imagePanel.setPreferredSize(new Dimension(WINDOW_WIDTH * 2 / 3 - 50, WINDOW_HEIGHT - 120));
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        setBorderTitle(imagePanel, "Visualizzatore");
        imageArea = new ImagePanel(null);
        scrollPane = new JScrollPane(imageArea);
        configureScrollPane();
        imagePanel.add(scrollPane);
        imagePanel.add(enableMetaBox);
        enableMetaBox.setEnabled(false);
        JPanel informationPanel = new JPanel();
        informationPanel.setSize(new Dimension(WINDOW_WIDTH / 3, WINDOW_HEIGHT - 120));
        informationPanel.setPreferredSize(new Dimension(WINDOW_WIDTH / 3, WINDOW_HEIGHT - 120));
        informationPanel.setLayout(new BoxLayout(informationPanel, BoxLayout.Y_AXIS));
        setBorderTitle(informationPanel, "Informazioni");
        informationPanel.add(imageInfo);
        mainPanel.add(imagePanel);
        mainPanel.add(informationPanel);

        mainWindow.add(toolbarPanel);
        mainWindow.add(mainPanel);
        mainWindow.pack();
        addListeners();
        mainWindow.setVisible(true);
        buildAboutWindow();
        buildNameRequestWindow();
    }

    /**
     * Aggiunge gli ascoltatori sugli elementi dell'interfaccia grafica.
     */
    private static void addListeners() {

        openOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIManager.put("FileChooser.readOnly", Boolean.TRUE);
                FileFilter customFilter = new ExtensionFilter();
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                //Permette all'utente solo di selezionare un file
                disableTextField(fileChooser.getComponents());
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(customFilter);
                int rVal = fileChooser.showOpenDialog(mainWindow);
                if (rVal == JFileChooser.APPROVE_OPTION) { //Se viene scelto un file dalla finestra di dialogo
                    //Inizializza tutte le varabili
                    blackAndWhiteImage = null;
                    currImageWithMeta = null;
                    currImage = null;
                    addImage(null);
                    enableMetaBox.setSelected(false);
                    enableMetaBox.setEnabled(false);
                    blackAndWhiteInUse = false;
                    blackAndWhiteButton.setText(enableBlackAndWhiteLabel);
                    nameRequestValue.setText("");
                    imageInfo.setText("");
                    fileChosen = fileChooser.getSelectedFile().toString();
                    try {
                        //Prova a decodificare l'immagine
                        parsedImage = Decoder.getPNGImageFromFile(fileChosen);
                        DecoderType decoderUsed = Decoder.getDecoderType(parsedImage);
                        currImage = Decoder.getImage(fileChosen, parsedImage, decoderUsed);
                        addImage(currImage);
                        UITools.updateImageInfo();

                        if (decoderUsed == DecoderType.EXTERNAL) {
                            currImageWithMeta = currImage;
                        } else {
                            enableMetaBox.setEnabled(true);
                        }

                    } catch (DecodingException | ChunkParserException | PNGStructureException ex) {
                        System.err.println(ex.getMessage());
                        JOptionPane.showMessageDialog(null, "Impossibile visualizzare l'immagine.\n" + ex.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        exportOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currImage == null) {
                    return;
                }

                nameRequestWindow.setVisible(true);
                if (requestCompleted) {
                    String path = System.getProperty("user.dir") + File.separator + nameRequestValue.getText() + ".png";
                    boolean success = Utility.exportImage(path, parsedImage);

                    if (!success) {
                        JOptionPane.showMessageDialog(null, "Impossibile esportare l'immagine senza metadati.", "ERRORE", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Immagine senza metadati esportata correttamente in" + System.getProperty("line.separator") + path,
                                "Operazione completata", JOptionPane.INFORMATION_MESSAGE);
                    }

                }
                nameRequestValue.setText("");

            }
        });
        
        convertOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currImage == null) {
                    return;
                }

                nameRequestWindow.setVisible(true);
                if (requestCompleted) {
                    String path = System.getProperty("user.dir") + File.separator + nameRequestValue.getText() + ".jpg";
                    boolean success = Utility.convertImage(path, currImage);

                    if (!success) {
                        JOptionPane.showMessageDialog(null, "Impossibile convertire l'immagine in JPEG.", "ERRORE", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Immagine convertita in JPEG correttamente in" + System.getProperty("line.separator") + path,
                                "Operazione completata", JOptionPane.INFORMATION_MESSAGE);
                    }

                }
                nameRequestValue.setText("");

            }
        });

        aboutOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutWindow.setVisible(true);
            }
        });

        exitOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        blackAndWhiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currImage == null) {
                    return;
                }

                UITools.buildBlackAndWhiteImage();
                if (!blackAndWhiteInUse) {
                    addImage(blackAndWhiteImage);
                    blackAndWhiteInUse = true;
                    blackAndWhiteButton.setText(disableBlackAndWhiteLabel);
                } else {
                    if (enableMetaBox.isSelected()) {
                        addImage(currImageWithMeta);
                    } else {
                        addImage(currImage);
                    }
                    blackAndWhiteInUse = false;
                    blackAndWhiteButton.setText(enableBlackAndWhiteLabel);
                }
            }
        });

        exportChunkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currImage == null) {
                    return;
                }

                nameRequestWindow.setVisible(true);
                if (requestCompleted) {
                    String path = System.getProperty("user.dir") + File.separator + nameRequestValue.getText() + ".txt";
                    boolean success = Utility.createReport(path, UITools.getInformation());

                    if (!success) {
                        JOptionPane.showMessageDialog(null, "Impossibile esportare le informazioni sul file.", "ERRORE", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Informazioni sul file esportate correttamente in" + System.getProperty("line.separator") + path,
                                "Operazione completata", JOptionPane.INFORMATION_MESSAGE);
                    }

                }
                nameRequestValue.setText("");
            }
        });

        enableMetaBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currImage == null) {
                    return;
                }

                if (enableMetaBox.isSelected()) {
                    if (currImageWithMeta == null) {
                        try {
                            UITools.buildImageWhithMeta();
                        } catch (DecodingException | PNGStructureException ex) {
                            System.err.println(ex.getMessage());
                            JOptionPane.showMessageDialog(null, "Impossibile attivare metadati o correggere l'immagine.", "ERRORE", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if (currImageWithMeta != null) {
                        addImage(currImageWithMeta);
                    }

                } else {
                    addImage(currImage);
                }

                blackAndWhiteInUse = false;
                blackAndWhiteButton.setText(enableBlackAndWhiteLabel);
            }
        });

        nameRequestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameRequestWindow.setVisible(false);
                requestCompleted = !nameRequestValue.getText().equals("");
            }
        });

    }

    /**
     * Costruisce la finestra con le informazioni sul programma.
     */
    private static void buildAboutWindow() {
        aboutWindow.setSize(new Dimension(POPUP_WINDOW_WIDTH, POPUP_WINDOW_HEIGHT));
        aboutWindow.setLocationRelativeTo(null);
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        aboutPanel.add(new JLabel("<html>PNG Manager - v. 1.0<br>Autore: Gianvito Taneburgo<br><br>"
                + "Il programma e' un visualizzatore di immagini in formato PNG.<br>Alcuni tipi di immagini possono essere visualizzati con o senza<br>metadati."
                + " Per attivare/disattivare tale opzione e' presente una<br>check box in basso a sinistra: se la casella non e' selezionabile<br>allora l'opzione"
                + " e' già attiva.<br><br>Per segnalare errori scrivetemi "
                + "al seguente indirizzo email:<br><a href=\"mailto:taneburgo@gmail.com\"><b>taneburgo@gmail.com</b></a></html>"));
        aboutWindow.add(aboutPanel);
    }

    /**
     * Costruisce la finestra per la richiesta di inserimento di un nome di file
     * all'utente.
     */
    private static void buildNameRequestWindow() {
        nameRequestWindow.setModal(true);
        nameRequestWindow.setSize(new Dimension(POPUP_WINDOW_WIDTH, POPUP_WINDOW_HEIGHT));
        nameRequestWindow.setLocationRelativeTo(null);
        nameRequestPanel.setLayout(new GridLayout(3, 1));
        nameRequestPanel.setSize(new Dimension(POPUP_WINDOW_WIDTH - 100, POPUP_WINDOW_HEIGHT - 180));
        nameRequestPanel.setPreferredSize(new Dimension(POPUP_WINDOW_WIDTH - 100, POPUP_WINDOW_HEIGHT - 180));
        nameRequestPanel.add(new JLabel("<html>Inserisci il nome del file:<br></html>"));
        nameRequestPanel.add(nameRequestValue);
        nameRequestPanel.add(nameRequestButton);
        nameRequestWindow.add(nameRequestPanel);
        nameRequestWindow.pack();
    }

    /**
     * Inserisce intorno al pannello un box con un titolo
     *
     * @param panel pannello intorno al quale disegnare il box
     * @param title titolo del box
     */
    private static void setBorderTitle(JPanel panel, String title) {
        Border border = BorderFactory.createTitledBorder(title);
        panel.setBorder(border);
    }

    /**
     * Mostra l'immagine in input nell'interfaccia grafica.
     *
     * @param imageToAdd immagine da visualizzare
     */
    private static void addImage(BufferedImage imageToAdd) {
        imagePanel.removeAll();
        imageArea = new ImagePanel(imageToAdd);
        scrollPane = new JScrollPane(imageArea);
        configureScrollPane();
        imagePanel.add(scrollPane);
        imagePanel.add(enableMetaBox);
        mainPanel.updateUI();
    }

    /**
     * Configura le proprietà della finestra di scorrimento dell'immagine.
     */
    private static void configureScrollPane() {
        scrollPane.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        scrollPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLLBAR_VERTICAL_SPEED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLLBAR_HORIZONTAL_SPEED);
        scrollPane.setBorder(null);
    }

    /**
     * Disabilita tutte le caselle di testo in un componente ed i suoi
     * sotto-componenti.
     *
     * @param comp Il componente nel quale le caselle di testo vanno
     * disabilitate
     */
    private static void disableTextField(Component[] comp) {
        for (Component aComp : comp) {
            if (aComp instanceof JPanel) {
                disableTextField(((JPanel) aComp).getComponents());
            } else if (aComp instanceof JTextField) {
                ((JTextField) aComp).setEditable(false);
                return;
            }
        }
    }

    /**
     * Carica una icona da un file.
     *
     * @param filename percorso dell'immagine da aprire
     * @return immagine caricata
     */
    private BufferedImage loadIcon(String filename) {
        try {
            return ImageIO.read(getClass().getResource(filename));
        } catch (IOException e) {
            System.err.println("Unable to load icon: " + filename + "\n" + e.getMessage());
            return null;
        }
    }
}
