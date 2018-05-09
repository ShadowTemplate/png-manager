package png.manager.decoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Decoder di Java.
 */
class ExternalDecoder {
    
    /**
     * Costruttore privato dell'oggetto.
     */
    private ExternalDecoder() {}
    
    /**
     * Genera l'immagine da visualizzare nell'interfaccia grafica, invocando il metodo della libreria.
     * 
     * @param path percorso dell'immagine da visualizzare
     * @return immagine pronta per la visualizzazione
     * @throws IOException - se occorrono errori in fase di decodifica
     */
    static BufferedImage getBufferedImage(String path) throws IOException {  
       return ImageIO.read(new File(path));   
    }
}
