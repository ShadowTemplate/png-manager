package png.manager.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Modella il pannello che contiene, all'interno dell'interfaccia grafica,
 * l'immagine PNG decodificata.
 */
public class ImagePanel extends JPanel {

    /**
     * Immagine da contenere.
     */
    private final BufferedImage image;

    /**
     * Costruisce l'oggetto memorizzando l'immagine in input.
     *
     * @param image immagine da visualizzare
     */
    public ImagePanel(BufferedImage image) {
        this.image = image;
        if (image != null) {
            this.setSize(new Dimension(image.getWidth(), image.getHeight()));
            this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        }
    }

    /**
     * Disegna il pannello e l'immagine al suo interno.
     * <p>
     * Se non vi è alcuna immagine crea un pannello vuoto.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }
}
