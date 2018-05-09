package png.manager.gui;

import java.awt.Font;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;

import png.manager.decoder.Decoder;
import png.manager.decoder.DecoderType;
import png.manager.entity.Chunk;
import png.manager.exception.DecodingException;
import png.manager.exception.PNGStructureException;
import png.manager.miscellaneous.PNGConstants;
import png.manager.miscellaneous.Utility;

/**
 * Fornisce funzionalità necessarie all'interfaccia grafica.
 */
public class UITools {

    /**
     * Costruttore privato dell'oggetto.
     */
    private UITools() {
    }

    /**
     * Crea un'immagine in bianco e nero a partire da quella colori.
     */
    static void buildBlackAndWhiteImage() {
        BufferedImage imageToConvert = UI.currImage;

        //Verifica se deve essere convertita l'immagine coi metadati
        if (UI.enableMetaBox.isSelected()) {
            imageToConvert = UI.currImageWithMeta;
        }

        UI.blackAndWhiteImage = Utility.copyImage(imageToConvert);
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        //Applico il filtro bianco e nero
        op.filter(UI.blackAndWhiteImage, UI.blackAndWhiteImage);
    }

    /**
     * Crea una immagine utilizzando tutti i metadati dei chunk ausiliari.
     *
     * @throws DecodingException - se occorrono errori in fase di decodifica dei
     * metadati
     * @throws PNGStructureException - se l'immagine non rispetta i vincoli del
     * formato
     */
    static void buildImageWhithMeta() throws DecodingException, PNGStructureException {
        UI.currImageWithMeta = Decoder.getImage(UI.fileChosen, UI.parsedImage, DecoderType.EXTERNAL);
    }

    /**
     * Inserisce tutte le informazioni sull'immagine corrente nell'apposito
     * pannello dell'interfaccia grafica.
     */
    static void updateImageInfo() {
        Font font = UI.imageInfo.getFont();
        Font newFont = new Font(font.getFontName(), Font.PLAIN, font.getSize());
        UI.imageInfo.setFont(newFont);
        UI.imageInfo.setText(getInformation());
    }

    /**
     * Ritorna una descrizione dell'immagine corrente, analizzandone tutte le
     * caratteristiche.
     * <p>
     * La descrizione è formattata con codice HTML.
     *
     * @return stringa contenente le informazioni estratte
     */
    static String getInformation() {
        int ct = UI.parsedImage.getColorType(),
                cm = UI.parsedImage.getCompressionMethod(),
                fm = UI.parsedImage.getFilteringMethod(),
                im = UI.parsedImage.getInterlacingMethod();

        DecimalFormat df = new DecimalFormat("#.###");

        String val = "<html><br><b>Percorso file:</b> " + UI.fileChosen + "<br><b>Dimensione file:</b> "
                + df.format(((double) new File(UI.fileChosen).length() / 1024)) + " KB<br><b>Dimensioni immagine:</b> "
                + UI.parsedImage.getWidth() + "x" + UI.parsedImage.getHeight() + " px<br><b>Profondita':</b> "
                + UI.parsedImage.getBitDepth() + " bit<br><b>Tipo di colore:</b> " + ct + " (" + getColorTypeMeaning(ct)
                + ")<br><b>Metodo di compressione:</b> " + cm + " (" + getCompressionMethodMeaning(cm) + ")<br><b>Metodo di filtraggio:</b> "
                + fm + " (" + getFilteringMethodMeaning(fm) + ")<br><b>Metodo di interlacciamento:</b> " + im + " ("
                + getInterlacingMethodMeaning(im) + ")<br>";

        val += "<br><b>Numero di chunk:</b> " + UI.parsedImage.getChunks().size();
        val += "<br><b>Chunk presenti:</b> ";

        //Memorizzo per ogni tipo di chunk le sue occorrenze
        HashMap<String, Integer> chunkMap = new HashMap<>();
        for (Chunk c : UI.parsedImage.getChunks()) {
            String type = c.getTypeAsString();
            if (chunkMap.containsKey(type)) {
                chunkMap.put(type, chunkMap.get(type) + 1);
            } else {
                chunkMap.put(type, 1);
            }
        }

        boolean firstChunk = true;
        for (String s : chunkMap.keySet()) {
            if (firstChunk) {
                val += s + " x" + chunkMap.get(s) + "<br>";
                firstChunk = false;
            } else {
                val += "&#9;" + s + " x" + chunkMap.get(s) + "<br>";
            }
        }

        if (UI.parsedImage.getColorType() == PNGConstants.COLOR_TYPE_PALETTE) {
            val += ("<b>Campioni tavolozza:</b> " + (UI.parsedImage.getChunk("PLTE").getLengthAsInt() / 3) + "<br>");
        }

        val += extractMetadata() + "</html>";
        return val;
    }

    /**
     * Interpreta l'identificativo del tipo di colore.
     *
     * @param colorType identificativo del tipo di colore
     * @return interpretazione testuale del tipo di colore
     */
    private static String getColorTypeMeaning(int colorType) {
        switch (colorType) {
            case PNGConstants.COLOR_TYPE_GRAYSCALE:
                return "Grayscale";
            case PNGConstants.COLOR_TYPE_RGB:
                return "RGB";
            case PNGConstants.COLOR_TYPE_PALETTE:
                return "Palette";
            case PNGConstants.COLOR_TYPE_GRAYSCALE_ALPHA:
                return "Grayscale + Alpha";
            case PNGConstants.COLOR_TYPE_RGB_ALPHA:
                return "RGB + Alpha";
            default:
                return "";
        }
    }

    /**
     * Interpreta l'identificativo del metodo di compressione.
     *
     * @param method identificativo del metodo di compressione
     * @return interpretazione testuale del metodo di compressione
     */
    private static String getCompressionMethodMeaning(int method) {
        return "Deflate/Inflate";
    }

    /**
     * Interpreta l'identificativo del metodo di filtraggio.
     *
     * @param method identificativo del metodo di filtraggio
     * @return interpretazione testuale del metodo di filtraggio
     */
    private static String getFilteringMethodMeaning(int method) {
        return "Adaptive";
    }

    /**
     * Interpreta l'identificativo del metodo di interlacciamento.
     *
     * @param method identificativo del metodo di interlacciamento
     * @return interpretazione testuale del metodo di interlacciamento
     */
    private static String getInterlacingMethodMeaning(int method) {
        switch (method) {
            case PNGConstants.NO_INTERLACE:
                return "Nessuno";
            case PNGConstants.ADAM7_INTERLACE:
                return "Adam 7";
            default:
                return "";
        }
    }

    /**
     * Ritorna una rappresentazione testuale dei chunk ausiliari dell'immagine
     * corrente.
     * <p>
     * La descrizione è formattata con codice HTML.
     *
     * @return interpretazione testuale dei chunk ausiliari
     */
    private static String extractMetadata() {
        String metadati = "";

        for (Chunk c : UI.parsedImage.getChunks()) {
            if (c.isAncillary()) {
                metadati += extractInfo(c);
            }
        }

        if (!metadati.equals("")) {
            return "<br><br><b><u>Metadati estratti dai chunk ausiliari:</u></b><br>" + metadati;
        }

        return metadati;
    }

    /**
     * Ritorna una rappresentazione testuale del chunk in input.
     * <p>
     * La descrizione è formattata con codice HTML.
     *
     * @param c chunk da analizzare
     * @return interpretazione testuale del chunk
     */
    private static String extractInfo(Chunk c) {

        //Vedere le specifiche dei chunk per comprendere come vengono interpretati i dati
        switch (c.getTypeAsString()) {
            case PNGConstants.GAMMA_CHUNK_NAME:
                double gama = new Double(Utility.hexStringToInt("" + Utility.bytesToHexString(c.getData()))) / 100000;
                return "<br><b>[gAMA]:</b> " + gama;
            case PNGConstants.PHYSICAL_PIXEL_DIMENSION_CHUNK_NAME:
                int x = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 0)));
                int y = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 4)));
                int measure = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 1, 8)));
                String ret = "<br><b>[pHYs]:</b> X axis: " + x + "; Y axis: " + y;
                ret += (measure == 0) ? " (pixel size unspecified)" : " m";
                return ret;
            case PNGConstants.TRANSPARENCY_CHUNK_NAME:
                ret = "<br><b>[tRNS]:</b> ";
                switch (UI.parsedImage.getColorType()) {
                    case PNGConstants.COLOR_TYPE_GRAYSCALE:
                        x = Utility.hexStringToInt(Utility.bytesToHexString(c.getData()));
                        ret += x;
                        break;
                    case PNGConstants.COLOR_TYPE_RGB:
                        int r = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 2, 0)));
                        int g = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 2, 2)));
                        int b = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 2, 4)));
                        ret += "R: " + r + "; G: " + g + " B: " + b;
                        break;
                    case PNGConstants.COLOR_TYPE_PALETTE:
                        ret += c.getData().length + " values found";
                        break;
                }
                return ret;
            case PNGConstants.BACKGROUND_CHUNK_NAME:
                ret = "<br><b>[bKGD]:</b> ";
                switch (UI.parsedImage.getColorType()) {
                    case PNGConstants.COLOR_TYPE_GRAYSCALE:
                    case PNGConstants.COLOR_TYPE_GRAYSCALE_ALPHA:
                        x = Utility.hexStringToInt(Utility.bytesToHexString(c.getData()));
                        ret += x;
                        break;
                    case PNGConstants.COLOR_TYPE_RGB:
                    case PNGConstants.COLOR_TYPE_RGB_ALPHA:
                        int r = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 2, 0)));
                        int g = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 2, 2)));
                        int b = Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 2, 4)));
                        ret += "R: " + r + "; G: " + g + " B: " + b;
                        break;
                    case PNGConstants.COLOR_TYPE_PALETTE:
                        ret += "Entry #" + Utility.hexStringToInt(Utility.bytesToHexString(c.getData()));
                        break;
                }
                return ret;
            case PNGConstants.CHROMACITIES_CHUNK_NAME:
                ret = "<br><b>[cHRM]:</b> ";
                double wpx = new Double(Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 0)))) / 100000;
                double wpy = new Double(Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 4)))) / 100000;
                double rx = new Double(Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 8)))) / 100000;
                double ry = new Double(Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 12)))) / 100000;
                double gx = new Double(Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 16)))) / 100000;
                double gy = new Double(Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 20)))) / 100000;
                double bx = new Double(Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 24)))) / 100000;
                double by = new Double(Utility.hexStringToInt(Utility.bytesToHexString(Utility.getBytesFromArray(c.getData(), 4, 28)))) / 100000;
                ret += "White Point x: " + wpx + "; White Point y: " + wpy
                        + "<br>&#9;Red x: " + rx + "; Red y: " + ry
                        + "<br>&#9;Green x: " + gx + "; Green y: " + gy
                        + "<br>&#9;Blue x: " + bx + "; Blue y: " + by;
                return ret;
            case PNGConstants.TEXT_CHUNK_NAME:
                ret = "<br><b>[tEXt]:</b> " + Utility.hexStringToASCIIString(Utility.bytesToHexString(c.getData()));
                return ret;
            case PNGConstants.STANDARD_RGB_COLOR_SPACE_CHUNK_NAME:
                ret = "<br><b>[sRGB]:</b> ";
                x = Utility.hexStringToInt(Utility.bytesToHexString(c.getData()));
                switch (x) {
                    case 0:
                        ret += "0 (Perceptual)";
                        break;
                    case 1:
                        ret += "1 (Relative colorimetric)";
                        break;
                    case 2:
                        ret += "2 (Saturation)";
                        break;
                    case 3:
                        ret += "3 (Absolute colorimetric)";
                        break;
                }
                return ret;
            default:
                return "";
        }
    }
}
