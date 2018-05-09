package png.manager.decoder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import png.manager.entity.PNGImage;
import png.manager.exception.DecodingException;
import png.manager.miscellaneous.PNGConstants;

/**
 * Decoder personale per la visualizzazione delle immagini.
 */
class CustomDecoder {

    /**
     * Dimensione del buffer di decompressione.
     */
    private static final int BUFFER_SIZE = 8192;
    /* Per motivi di performance si sceglie una dimensione multipla di 512 byte e,
     * preferibilmente, multipla della dimensione del cluster del disco */
    
    /**
     * Modalità bianco e nero.
     */
    public static final byte BW_MODE = 0;
    /**
     * Modalità scala di grigio.
     */
    public static final byte GREYSCALE_MODE = 1;
    /**
     * Modalità fullcolor.
     */
    public static final byte COLOR_MODE = 2;

    /**
     * Costruttore privato della classe.
     */
    private CustomDecoder() {
    }

    /**
     * Genera l'immagine da visualizzare a partire dal risultato del parser.
     *
     * @param image immagine risultato del parsing
     * @return immagine pronta per la visualizzazione
     * @throws DecodingException - se vengono riscontrati errori in fase di
     * decodifica
     */
    static BufferedImage getBufferedImage(PNGImage image) throws DecodingException {
        if (image.getColorType() == PNGConstants.COLOR_TYPE_PALETTE) {
            try {
                ColorModel cm = generateColorModel(image);
                WritableRaster raster = generateRaster(image);
                return new BufferedImage(cm, raster, false, null);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                throw new DecodingException("Impossibile decodificare l'immagine.");
            }
        } else { //Grayscale (bitDepth = 1 o 8) || RGB (bitDepth = 8)
            return decodeImage(image);
        }
    }

    /**
     * Genera il modello di colori per le immagini con tavolozza.
     *
     * @param image immagine per cui generare il modello
     * @return modello per l'immagine
     * @throws DecodingException - se non è presente il chunk PLTE
     */
    private static ColorModel generateColorModel(PNGImage image) throws DecodingException {
        if (!image.containsChunk(PNGConstants.PALETTE_CHUNK_NAME)) {
            throw new DecodingException("Unable to locate " + PNGConstants.PALETTE_CHUNK_NAME + " chunk.");
        }

        byte[] paletteData = image.getChunk(PNGConstants.PALETTE_CHUNK_NAME).getData();
        int paletteLength = paletteData.length / 3;
        return new IndexColorModel(image.getBitDepth(), paletteLength, paletteData, 0, false);
    }

    /**
     * Genera il raster per le immagini con tavolozza.
     *
     * @param image immagine per cui generare il raster
     * @return raster dell'immagine
     * @throws DecodingException - se occorrono errori in fase di decodifica
     */
    private static WritableRaster generateRaster(PNGImage image) throws DecodingException {
        byte[] imageData = decompressData(image);
        DataBuffer db = new DataBufferByte(imageData, imageData.length);
        WritableRaster raster = Raster.createPackedRaster(db, image.getWidth(), image.getHeight(), image.getBitDepth(), null);
        return raster;
    }

    /**
     * Decodifica l'immagine con il metodo idoneo al suo tipo.
     * <p>
     * Il metodo effettua anche decompressione e defiltering.
     *
     * @param image immagine risultato del parsing
     * @return immagine pronta per la visualizzazione
     * @throws DecodingException - se occorrono errori in fase di decodifica
     */
    private static BufferedImage decodeImage(PNGImage image) throws DecodingException {
        int width = image.getWidth();
        int height = image.getHeight();
        int colorType = image.getColorType();
        int bitDepth = image.getBitDepth();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int mode;
        if (colorType == PNGConstants.COLOR_TYPE_GRAYSCALE && bitDepth == 1) {
            mode = BW_MODE;
        } else if (colorType == PNGConstants.COLOR_TYPE_GRAYSCALE && bitDepth == 8) {
            mode = GREYSCALE_MODE;
        } else { // ColorType == PNGConstants.COLOR_TYPE_RGB
            mode = COLOR_MODE;
        }

        byte[] data = image.getCompressedData();
        int size = data.length;
        Inflater inflater = new Inflater();
        inflater.setInput(data, 0, size);

        int color;

        try {
            switch (mode) {
                case BW_MODE:
                    int bytes = (int) (width / 8);
                    if ((width % 8) != 0) {
                        bytes++;
                    }

                    byte[] decompressedData = new byte[height * bytes + height];
                    inflater.inflate(decompressedData);
                    byte[] defilteredData = CustomDefilterer.defilterImage(image, decompressedData);

                    byte[][] rows = new byte[height][bytes];
                    for (int i = 0; i < height; i++) {
                        System.arraycopy(defilteredData, i * bytes, rows[i], 0, bytes);
                    }

                    byte colorset;

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < bytes; x++) {
                            colorset = rows[y][x];
                            for (int sh = 0; sh < 8; sh++) {
                                if (x * 8 + sh >= width) {
                                    break;
                                }
                                if ((colorset & 0x80) == 0x80) {
                                    result.setRGB(x * 8 + sh, y, Color.white.getRGB());
                                } else {
                                    result.setRGB(x * 8 + sh, y, Color.black.getRGB());
                                }
                                colorset <<= 1;
                            }
                        }
                    }
                    break;
                case GREYSCALE_MODE:
                    decompressedData = new byte[width * height + height];
                    inflater.inflate(decompressedData);
                    rows = new byte[height][width];
                    defilteredData = CustomDefilterer.defilterImage(image, decompressedData);

                    for (int i = 0; i < height; i++) {
                        System.arraycopy(defilteredData, i * width, rows[i], 0, width);
                    }

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            color = rows[y][x];
                            result.setRGB(x, y, (color << 16) + (color << 8) + color);
                        }
                    }
                    break;
                case COLOR_MODE:
                    decompressedData = new byte[image.getHeight() * image.getWidth() * 3 + image.getHeight()];
                    inflater.inflate(decompressedData);
                    defilteredData = CustomDefilterer.defilterImage(image, decompressedData);

                    rows = new byte[height][width * 3];

                    for (int i = 0; i < height; i++) {
                        System.arraycopy(defilteredData, i * (width * 3), rows[i], 0, width * 3);
                    }

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            result.setRGB(x, y,
                                    ((rows[y][x * 3 + 0] & 0xff) << 16)
                                    + ((rows[y][x * 3 + 1] & 0xff) << 8)
                                    + ((rows[y][x * 3 + 2] & 0xff)));
                        }
                    }
                    break;
            }
        } catch (DataFormatException e) {
            System.err.println(e.getMessage());
            throw new DecodingException("Errore nella decompressione dei dati.");
        }

        return result;
    }

    /**
     * Decomprime i dati delle imamgini con tavolozza.
     *
     * @param image risultato del parsing
     * @return dati decompressi
     * @throws DecodingException - se occorrono errori in fase di decompressione
     */
    private static byte[] decompressData(PNGImage image) throws DecodingException {
        try {
            InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(image.getCompressedData()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int readLength;
            byte[] block = new byte[BUFFER_SIZE];
            while ((readLength = in.read(block)) != -1) {
                out.write(block, 0, readLength);
            }
            out.flush();
            byte[] imageData = out.toByteArray();

            int width = image.getWidth();
            int height = image.getHeight();
            int bitsPerPixel = image.getBitDepth();
            int length = width * height * bitsPerPixel / 8;

            byte[] prunedData = new byte[length];

            if (image.getInterlacingMethod() == PNGConstants.NO_INTERLACE) {
                int index = 0;
                for (int i = 0; i < length; i++) {
                    if ((i * 8 / bitsPerPixel) % width == 0) {
                        index++; //Salta il byte del filtro
                    }
                    prunedData[i] = imageData[index++];
                }
            }
            return prunedData;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new DecodingException("Errore nella decompressione dei dati.");
        }
    }
}
