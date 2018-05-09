package png.manager.decoder;

import java.awt.image.BufferedImage;
import java.io.IOException;

import png.manager.checker.CRC32Checker;
import png.manager.checker.Checker;
import png.manager.entity.PNGImage;
import png.manager.exception.ChunkParserException;
import png.manager.exception.DecodingException;
import png.manager.exception.PNGStructureException;
import png.manager.miscellaneous.PNGConstants;

/**
 * Decodifica l'immagine per visualizzarla nell'interfaccia grafica.
 */
public class Decoder {

    /**
     * Costruttore privato dell'oggetto.
     */
    private Decoder() {
    }

    /**
     * Invoca il parser sull'immagine, il cui percorso viene fornito in input.
     *
     * @param path percorso dell'immagine da visualizzare
     * @return immagine pronta per la visualizzazione
     * @throws ChunkParserException - se occorrono errori in fase di parsing
     */
    public static PNGImage getPNGImageFromFile(String path) throws ChunkParserException {
        return new PNGImage(ChunkParser.parseImage(path));
    }

    /**
     * Seleziona il decoder appropriato per l'immagine.
     *
     * @param image immagine risultato del parsing
     * @return decoder specifico per l'immagine
     */
    public static DecoderType getDecoderType(PNGImage image) {
        int colorType = image.getColorType();
        int bitDepth = image.getBitDepth();
        int interlaceMethod = image.getInterlacingMethod();

        if (interlaceMethod == PNGConstants.ADAM7_INTERLACE) {
            return DecoderType.EXTERNAL;
        }
        if (colorType == PNGConstants.COLOR_TYPE_GRAYSCALE_ALPHA || colorType == PNGConstants.COLOR_TYPE_RGB_ALPHA) {
            return DecoderType.EXTERNAL;
        }
        if (colorType == PNGConstants.COLOR_TYPE_GRAYSCALE && bitDepth == 2) {
            return DecoderType.EXTERNAL;
        }
        if (colorType == PNGConstants.COLOR_TYPE_GRAYSCALE && bitDepth == 4) {
            return DecoderType.EXTERNAL;
        }
        if (colorType == PNGConstants.COLOR_TYPE_GRAYSCALE && bitDepth == 16) {
            return DecoderType.EXTERNAL;
        }
        if (colorType == PNGConstants.COLOR_TYPE_RGB && bitDepth == 16) {
            return DecoderType.EXTERNAL;
        }

        return DecoderType.CUSTOM;
    }

    /**
     * Richiama il decoder appropriato per l'immagine in input, in accordo col
     * decoder in input.
     *
     * @param path percorso dell'immagine da visualizzare
     * @param pngImage immagine risultato del parsing
     * @param decoder decoder da utilizzare per la decodifica
     * @return immagine pronta per la visualizzazione
     * @throws PNGStructureException - se l'immagine non rispetta i requisiti
     * del formato
     * @throws DecodingException - se occorrono errori in fase di parsing
     */
    public static BufferedImage getImage(String path, PNGImage pngImage, DecoderType decoder) throws PNGStructureException, DecodingException {
        if (!CRC32Checker.checkChunksCRC(pngImage.getChunks())) {
            throw new PNGStructureException("Controllo di integrita' CRC non superato: i dati sono corrotti.");
        }

        if (!Checker.checkDimension(pngImage)) {
            throw new PNGStructureException("Dimensioni invalide per l'immagine: altezza o larghezza nulle.");
        }

        if (!Checker.checkColorTypeBitDepthCombination(pngImage)) {
            throw new PNGStructureException("Immagine non conforme al formato.\nCombinazione invalida di tipo di colore e profondita' di bit.");
        }

        System.out.println(pngImage);
        System.out.println(pngImage.getInfo());

        if (decoder == DecoderType.EXTERNAL) {
            try {
                return ExternalDecoder.getBufferedImage(path);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new DecodingException("Impossibile decodificare l'immagine.");
            }
        }

        try {
            return CustomDecoder.getBufferedImage(pngImage);
        } catch (Exception e) {
            if (e instanceof DecodingException) {
                throw e;
            } else {
                System.err.println(e.getMessage());
                throw new DecodingException("Impossibile decodificare l'immagine.");
            }
        }
    }
}
